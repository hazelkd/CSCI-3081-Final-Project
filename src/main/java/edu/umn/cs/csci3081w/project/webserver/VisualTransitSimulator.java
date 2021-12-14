package edu.umn.cs.csci3081w.project.webserver;

import edu.umn.cs.csci3081w.project.model.Bus;
import edu.umn.cs.csci3081w.project.model.BusFactory;
import edu.umn.cs.csci3081w.project.model.Counter;
import edu.umn.cs.csci3081w.project.model.DieselTrain;
import edu.umn.cs.csci3081w.project.model.ElectricTrain;
import edu.umn.cs.csci3081w.project.model.Issue;
import edu.umn.cs.csci3081w.project.model.LargeBus;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.PassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Position;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Route;
import edu.umn.cs.csci3081w.project.model.SmallBus;
import edu.umn.cs.csci3081w.project.model.Stop;
import edu.umn.cs.csci3081w.project.model.StorageFacility;
import edu.umn.cs.csci3081w.project.model.Train;
import edu.umn.cs.csci3081w.project.model.TrainFactory;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import edu.umn.cs.csci3081w.project.model.VehicleConcreteSubject;
import edu.umn.cs.csci3081w.project.model.VehicleFactory;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class VisualTransitSimulator {

  private static boolean LOGGING = false;
  private int numTimeSteps = 0;
  private int simulationTimeElapsed = 0;
  private Counter counter;
  private List<Line> lines;
  private List<Vehicle> activeVehicles;
  private List<Vehicle> completedTripVehicles;
  private List<Integer> vehicleStartTimings;
  private List<Integer> timeSinceLastVehicle;
  private StorageFacility storageFacility;
  private WebServerSession webServerSession;
  private VehicleFactory busFactory;
  private VehicleFactory trainFactory;
  private VehicleConcreteSubject vehicleConcreteSubject;

  /**
   * Constructor for Simulation.
   *
   * @param configFile       file containing the simulation configuration
   * @param webServerSession session associated with the simulation
   */
  public VisualTransitSimulator(String configFile, WebServerSession webServerSession) {
    this.webServerSession = webServerSession;
    this.counter = new Counter();
    ConfigManager configManager = new ConfigManager();
    configManager.readConfig(counter, configFile);
    this.lines = configManager.getLines();
    this.activeVehicles = new ArrayList<Vehicle>();
    this.completedTripVehicles = new ArrayList<Vehicle>();
    this.vehicleStartTimings = new ArrayList<Integer>();
    this.timeSinceLastVehicle = new ArrayList<Integer>();
    this.storageFacility = configManager.getStorageFacility();
    if (this.storageFacility == null) {
      this.storageFacility = new StorageFacility(Integer.MAX_VALUE, Integer.MAX_VALUE,
          Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    vehicleConcreteSubject = new VehicleConcreteSubject(webServerSession);

    if (VisualTransitSimulator.LOGGING) {
      System.out.println("////Simulation Lines////");
      for (int i = 0; i < lines.size(); i++) {
        lines.get(i).report(System.out);
      }
    }
  }

  /**
   * Initializes vehicle factory classes for the simulation.
   *
   * @param time time when the simulation was started
   */
  public void setVehicleFactories(int time) {
    this.busFactory = new BusFactory(storageFacility, counter, time);
    this.trainFactory = new TrainFactory(storageFacility, counter, time);
  }

  /**
   * Starts the simulation.
   *
   * @param vehicleStartTimings start timings of bus
   * @param numTimeSteps        number of time steps
   */
  public void start(List<Integer> vehicleStartTimings, int numTimeSteps) {
    this.vehicleStartTimings = vehicleStartTimings;
    this.numTimeSteps = numTimeSteps;
    for (int i = 0; i < vehicleStartTimings.size(); i++) {
      this.timeSinceLastVehicle.add(i, 0);
    }
    simulationTimeElapsed = 0;
  }

  /**
   * Updates the simulation at each step.
   */
  public void update() {
    simulationTimeElapsed++;
    if (simulationTimeElapsed > numTimeSteps) {
      return;
    }
    System.out.println("~~~~The simulation time is now at time step "
        + simulationTimeElapsed + "~~~~");
    // generate vehicles
    for (int i = 0; i < timeSinceLastVehicle.size(); i++) {
      Line line = lines.get(i);
      if (timeSinceLastVehicle.get(i) <= 0) {
        Vehicle generatedVehicle = null;
        if (line.getType().equals(Line.BUS_LINE) && !line.isIssueExist()) {
          generatedVehicle = busFactory.generateVehicle(line.shallowCopy());
        } else if (line.getType().equals(Line.TRAIN_LINE) && !line.isIssueExist()) {
          generatedVehicle = trainFactory.generateVehicle(line.shallowCopy());
        }
        if (line.getType().equals(Line.TRAIN_LINE) || line.getType().equals(Line.BUS_LINE)) {
          if (generatedVehicle != null && !line.isIssueExist()) {
            activeVehicles.add(generatedVehicle);
          }
          timeSinceLastVehicle.set(i, vehicleStartTimings.get(i));
          timeSinceLastVehicle.set(i, timeSinceLastVehicle.get(i) - 1);
        }
      } else {
        if (!line.isIssueExist()) {
          timeSinceLastVehicle.set(i, timeSinceLastVehicle.get(i) - 1);
        }
      }
    }
    // update vehicles
    for (int i = activeVehicles.size() - 1; i >= 0; i--) {
      Vehicle currVehicle = activeVehicles.get(i);
      currVehicle.update();
      if (currVehicle.isTripComplete()) {
        Vehicle completedTripVehicle = activeVehicles.remove(i);
        completedTripVehicles.add(completedTripVehicle);
        if (completedTripVehicle instanceof Bus) {
          busFactory.returnVehicle(completedTripVehicle);
        } else if (completedTripVehicle instanceof Train) {
          trainFactory.returnVehicle(completedTripVehicle);
        }
      } else {
        if (VisualTransitSimulator.LOGGING) {
          currVehicle.report(System.out);
        }
      }
    }
    // update lines
    for (int i = 0; i < lines.size(); i++) {
      Line currLine = lines.get(i);
      currLine.update();
      if (VisualTransitSimulator.LOGGING) {
        currLine.report(System.out);
      }
    }
    vehicleConcreteSubject.notifyObservers();
  }

  public List<Line> getLines() {
    return lines;
  }

  /**
   * Added section for testing
   * status of active vehicles.
   *
   * @return list of vehicles
   */
  public List<Vehicle> getActiveVehicles() {

    Vehicle testVehicleSB;
    Vehicle testVehicleLB;
    Vehicle testVehicleET;
    Vehicle testVehicleD;
    Route testRouteIn;
    Route testRouteOut;

    if (numTimeSteps == 0) {
      //Testing data
      List<Stop> stopsIn = new ArrayList<Stop>();
      Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
      Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
      stopsIn.add(stop1);
      stopsIn.add(stop2);
      List<Double> distancesIn = new ArrayList<>();
      distancesIn.add(0.843774422231134);
      List<Double> probabilitiesIn = new ArrayList<Double>();
      probabilitiesIn.add(.025);
      probabilitiesIn.add(0.3);
      PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

      testRouteIn = new Route(0, "testRouteIn",
          stopsIn, distancesIn, generatorIn);

      List<Stop> stopsOut = new ArrayList<Stop>();
      stopsOut.add(stop2);
      stopsOut.add(stop1);
      List<Double> distancesOut = new ArrayList<>();
      distancesOut.add(0.843774422231134);
      List<Double> probabilitiesOut = new ArrayList<Double>();
      probabilitiesOut.add(0.3);
      probabilitiesOut.add(.025);
      PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

      testRouteOut = new Route(1, "testRouteOut",
          stopsOut, distancesOut, generatorOut);

      testVehicleSB = new SmallBus(1, new Line(10000, "testLine",
          "SMALL_BUS", testRouteOut, testRouteIn,
          new Issue()), 3, 1.0) {
        @Override
        public void report(PrintStream out) {
        }

        @Override
        public int getCurrentCO2Emission() {
          return 0;
        }
      };
      testVehicleET = new ElectricTrain(2, new Line(10000, "testLine",
          "ELECTRIC_TRAIN", testRouteOut, testRouteIn,
          new Issue()), 3, 1.0) {
        @Override
        public void report(PrintStream out) {
        }

        @Override
        public int getCurrentCO2Emission() {
          return 0;
        }
      };
      testVehicleD = new DieselTrain(3, new Line(10000, "testLine",
          "DIESEL_TRAIN", testRouteOut, testRouteIn,
          new Issue()), 3, 1.0) {
        @Override
        public void report(PrintStream out) {
        }

        @Override
        public int getCurrentCO2Emission() {
          return 0;
        }
      };
      testVehicleLB = new LargeBus(4, new Line(10000, "testLine",
          "LARGE_BUS", testRouteOut, testRouteIn,
          new Issue()), 3, 1.0) {
        @Override
        public void report(PrintStream out) {
        }

        @Override
        public int getCurrentCO2Emission() {
          return 0;
        }
      };

      activeVehicles.add(testVehicleSB);
      activeVehicles.add(testVehicleD);
      activeVehicles.add(testVehicleET);
      activeVehicles.add(testVehicleLB);

      return activeVehicles;
      // Active lines
    } else {
      return activeVehicles;
    }
  }

  public boolean getLogging() {
    return LOGGING;
  }

  /**
   * Registers an observer into the vehicle subject.
   *
   * @param vehicle the vehicle that starts observing
   */
  public void addObserver(Vehicle vehicle) {
    vehicleConcreteSubject.attachObserver(vehicle);
  }
}