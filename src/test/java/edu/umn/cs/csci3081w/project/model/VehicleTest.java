package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class VehicleTest {

  private Vehicle testVehicle;
  private Vehicle testVehicle2;
  private Vehicle testVehicle3;
  private Vehicle testVehicle4;
  private Route testRouteIn;
  private Route testRoute2In;
  private Route testRoute3In;
  private Route testRouteOut;
  private Route testRoute2Out;
  private Route testRoute3Out;


  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
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

    testVehicle = new VehicleTestImpl(1, new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0, new PassengerLoader(), new PassengerUnloader());

    testVehicle4 = new VehicleTestImpl(1, new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, -1.0, new PassengerLoader(), new PassengerUnloader());
  }

  /**
   * Tests constructor.
   */
  @Test
  public void testConstructor() {
    assertEquals(1, testVehicle.getId());
    assertEquals("testRouteOut1", testVehicle.getName());
    assertEquals(3, testVehicle.getCapacity());
    assertEquals(1, testVehicle.getSpeed());
    assertEquals(testRouteOut, testVehicle.getLine().getOutboundRoute());
    assertEquals(testRouteIn, testVehicle.getLine().getInboundRoute());
  }

  /**
   * Tests if testIsTripComplete function works properly.
   */
  @Test
  public void testIsTripComplete() {
    assertEquals(false, testVehicle.isTripComplete());
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    assertEquals(true, testVehicle.isTripComplete());

  }


  /**
   * Tests if loadPassenger function works properly.
   */
  @Test
  public void testLoadPassenger() {

    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    Passenger testPassenger3 = new Passenger(1, "testPassenger3");
    Passenger testPassenger4 = new Passenger(1, "testPassenger4");

    assertEquals(1, testVehicle.loadPassenger(testPassenger1));
    assertEquals(1, testVehicle.loadPassenger(testPassenger2));
    assertEquals(1, testVehicle.loadPassenger(testPassenger3));
    assertEquals(0, testVehicle.loadPassenger(testPassenger4));
  }


  /**
   * Tests if move function works properly.
   */
  @Test
  public void testMove() {

    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.move();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Tests if update function works properly.
   */
  @Test
  public void testUpdate() {

    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.update();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Test to see if test vehicles provide info.
   */
  @Test
  public void testProvideInfo() {
    WebServerSession webServerSessionStub = mock(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionStub.onOpen(sessionDummy);
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(webServerSessionStub);
    vehicleConcreteSubject.attachObserver(testVehicle);
    testVehicle.update();
    testVehicle.provideInfo();
    JsonObject testOutput = new JsonObject();
    testOutput.addProperty("command", "observedVehicle");
    webServerSessionStub.onMessage(testOutput.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionStub).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = commandToClient.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test to see if Passengers are handled at the stop.
   */
  @Test
  public void testPassengerAtHandleStop() {
    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    Passenger testPassenger3 = new Passenger(1, "testPassenger3");
    Passenger testPassenger4 = new Passenger(1, "testPassenger4");

    testVehicle.loadPassenger(testPassenger1);
    testVehicle.loadPassenger(testPassenger2);
    testVehicle.loadPassenger(testPassenger3);
    testVehicle.loadPassenger(testPassenger4);

    testVehicle.move();
    testVehicle.move();
    assertEquals(2, testVehicle.getPassengers().size());

  }

  /**
   * Test to see if Small Bus provides Info.
   */
  @Test
  public void testSmallBusProvideInfo() {
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

    testRoute3In = new Route(0, "testRouteIn",
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

    testRoute3Out = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testVehicle3 = new SmallBus(1, new Line(10000, "testLine", "BUS", testRouteOut, testRouteIn,
        new Issue()), 20, 1.0);

    Passenger passenger1 = new Passenger(0, "passenger1");
    testVehicle3.loadPassenger(passenger1);

    WebServerSession webServerSessionStub = mock(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionStub.onOpen(sessionDummy);
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(webServerSessionStub);
    vehicleConcreteSubject.attachObserver(testVehicle3);
    testVehicle3.update();
    testVehicle3.provideInfo();
    JsonObject testOutput = new JsonObject();
    testOutput.addProperty("command", "observedVehicle");
    webServerSessionStub.onMessage(testOutput.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionStub).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = commandToClient.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: SMALL_BUS_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 1" + System.lineSeparator()
        + "* CO2: 2" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  @Test
  public void testLargeBusProvideInfo() {
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

    testRoute3In = new Route(0, "testRouteIn",
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

    testRoute3Out = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testVehicle3 = new LargeBus(1, new Line(10000, "testLine", "BUS", testRouteOut, testRouteIn,
        new Issue()), 20, 1.0);

    Passenger passenger1 = new Passenger(0, "passenger1");
    testVehicle3.loadPassenger(passenger1);

    WebServerSession webServerSessionStub = mock(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionStub.onOpen(sessionDummy);
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(webServerSessionStub);
    vehicleConcreteSubject.attachObserver(testVehicle3);
    testVehicle3.move();
    testVehicle3.move();

    testVehicle3.update();
    testVehicle3.provideInfo();
    JsonObject testOutput = new JsonObject();
    testOutput.addProperty("command", "observedVehicle");
    webServerSessionStub.onMessage(testOutput.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionStub).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = commandToClient.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: LARGE_BUS_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.243774,44.972392)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 3" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  @Test
  public void testElectricTrainProvideInfo() {
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

    testRoute3In = new Route(0, "testRouteIn",
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

    testRoute3Out = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testVehicle3 = new ElectricTrain(1, new Line(10000, "testLine", "BUS", testRouteOut,
        testRouteIn, new Issue()), 20, 1.0);

    Passenger passenger1 = new Passenger(0, "passenger1");
    testVehicle3.loadPassenger(passenger1);

    WebServerSession webServerSessionStub = mock(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionStub.onOpen(sessionDummy);
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(webServerSessionStub);
    vehicleConcreteSubject.attachObserver(testVehicle3);
    testVehicle3.update();
    testVehicle3.provideInfo();
    JsonObject testOutput = new JsonObject();
    testOutput.addProperty("command", "observedVehicle");
    webServerSessionStub.onMessage(testOutput.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionStub).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = commandToClient.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: ELECTRIC_TRAIN_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 1" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  @Test
  public void testDieselTrainProvideInfo() {
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

    testRoute3In = new Route(0, "testRouteIn",
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

    testRoute3Out = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testVehicle3 = new DieselTrain(1, new Line(10000, "testLine", "BUS", testRouteOut, testRouteIn,
        new Issue()), 20, 1.0);

    Passenger passenger1 = new Passenger(0, "passenger1");
    testVehicle3.loadPassenger(passenger1);

    WebServerSession webServerSessionStub = mock(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionStub.onOpen(sessionDummy);
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(webServerSessionStub);
    vehicleConcreteSubject.attachObserver(testVehicle3);
    testVehicle3.update();
    testVehicle3.provideInfo();
    JsonObject testOutput = new JsonObject();
    testOutput.addProperty("command", "observedVehicle");
    webServerSessionStub.onMessage(testOutput.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionStub).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = commandToClient.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: DIESEL_TRAIN_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 1" + System.lineSeparator()
        + "* CO2: 8" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test to see if a Vehicle has the correct info in a loop.
   */
  @Test
  public void testProvideInfoForLoop() {
    WebServerSession webServerSessionStub = mock(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionStub.onOpen(sessionDummy);
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(webServerSessionStub);
    vehicleConcreteSubject.attachObserver(testVehicle);
    testVehicle.move();
    testVehicle.move();
    testVehicle.update();
    testVehicle.provideInfo();
    JsonObject testOutput = new JsonObject();
    testOutput.addProperty("command", "observedVehicle");
    webServerSessionStub.onMessage(testOutput.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionStub).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = commandToClient.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.243774,44.972392)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test to see if passengers get updated.
   */
  @Test
  public void testUpdatePassengers() {
    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    Passenger testPassenger3 = new Passenger(1, "testPassenger3");
    Passenger testPassenger4 = new Passenger(1, "testPassenger4");

    testVehicle.loadPassenger(testPassenger1);
    testVehicle.loadPassenger(testPassenger2);
    testVehicle.loadPassenger(testPassenger3);
    testVehicle.loadPassenger(testPassenger4);

    testVehicle.move();
    testVehicle.move();
    testVehicle.update();
    assertEquals(2, testVehicle.getPassengers().size());

  }

  /**
   * Test to see if information is provided on an incomplete trip.
   */
  @Test
  public void testProvideInfoTripIncomplete() {
    WebServerSession webServerSessionStub = mock(WebServerSession.class);
    Session sessionDummy = mock(Session.class);
    webServerSessionStub.onOpen(sessionDummy);
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(webServerSessionStub);
    vehicleConcreteSubject.attachObserver(testVehicle);
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.update();
    testVehicle.provideInfo();
    JsonObject testOutput = new JsonObject();
    testOutput.addProperty("command", "observedVehicle");
    webServerSessionStub.onMessage(testOutput.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionStub).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    String command = commandToClient.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    assertEquals(true, testVehicle.isTripComplete());
  }

  /**
   * Test to see if Update Distance works properly.
   */
  @Test
  public void updateDistanceComplete() {
    assertEquals(false, testVehicle.isTripComplete());
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.update();
    assertEquals(true, testVehicle.isTripComplete());
  }

  /**
   * test updateDistance with vehicle that has negative speed.
   */
  @Test
  public void updateDistanceNegativeSpeed() {
    assertEquals(false, testVehicle4.isTripComplete());
    testVehicle4.move();
    testVehicle4.move();
    testVehicle4.move();
    testVehicle4.move();
    testVehicle4.move();
    testVehicle4.update();
    assertEquals(false, testVehicle4.isTripComplete());
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testVehicle = null;
  }
}
