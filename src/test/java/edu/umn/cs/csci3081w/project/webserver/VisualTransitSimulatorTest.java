package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Issue;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.PassengerGenerator;
import edu.umn.cs.csci3081w.project.model.PassengerLoader;
import edu.umn.cs.csci3081w.project.model.PassengerUnloader;
import edu.umn.cs.csci3081w.project.model.Position;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Route;
import edu.umn.cs.csci3081w.project.model.Stop;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import edu.umn.cs.csci3081w.project.model.VehicleTestImpl;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.Session;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class VisualTransitSimulatorTest {

  private Route testRouteIn;
  private Route testRouteOut;
  private Vehicle testVehicle;

  /**
   * Tests the execute method for the register vehicles command with
   * numTimeSteps = 0.
   */

  @Test
  public void testExecuteRegisterVehiclesCommand() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    RegisterVehicleCommand registerVehicleCommandDummy = mock(RegisterVehicleCommand.class);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "start");
    commandFromClient.addProperty("numTimeSteps", 0);
    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(5);
    timeBetweenVehicles.add(5);
    commandFromClient.add("timeBetweenVehicles", timeBetweenVehicles);
    commandFromClient.addProperty("command", "update");
    commandFromClient.addProperty("id", 1);
    registerVehicleCommandDummy.execute(webServerSessionSpy, commandFromClient);
    webServerSessionSpy.onMessage(commandFromClient.toString());
    webServerSessionSpy.sendJson(commandFromClient);
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("1", commandToClient.get("id").toString());
  }

  /**
   * Tests the execute method for the register vehicles command with
   * numTimeSteps = 50.
   */

  @Test
  public void testUpdateCommandVts() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    GetVehiclesCommand getVehiclesCommandDummy = mock(GetVehiclesCommand.class);
    UpdateCommand updateCommandDummy = mock(UpdateCommand.class);
    webServerSessionSpy.onOpen(sessionDummy);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "start");
    commandFromClient.addProperty("numTimeSteps", 50);
    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(5);
    timeBetweenVehicles.add(5);
    commandFromClient.add("timeBetweenVehicles", timeBetweenVehicles);
    webServerSessionSpy.onMessage(commandFromClient.toString());
    updateCommandDummy.execute(webServerSessionSpy, commandFromClient);
    commandFromClient.addProperty("command", "update");
    updateCommandDummy.execute(webServerSessionSpy, commandFromClient);
    getVehiclesCommandDummy.execute(webServerSessionSpy, commandFromClient);
    webServerSessionSpy.onMessage(commandFromClient.toString());
    webServerSessionSpy.sendJson(commandFromClient);
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("update", commandToClient.get("command").getAsString());
  }

  /**
   * Test if update function works correctly.
   */
  @Test
  public void testUpdate() {
    WebServerSession webSes = new WebServerSession();
    VisualTransitSimulator visualSim = new VisualTransitSimulator("src/main/resources/config.txt",
        webSes);

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
        "BUS_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0, new PassengerLoader(), new PassengerUnloader());

    assertEquals(4, visualSim.getActiveVehicles().size());
    visualSim.addObserver(testVehicle);
    assertEquals(8, visualSim.getActiveVehicles().size());

    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();

    visualSim.update();

    assertEquals(true, testVehicle.isTripComplete());

  }
}
