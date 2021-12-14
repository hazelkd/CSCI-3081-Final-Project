package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.websocket.Session;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class GetVehiclesCommandTest {

  /**
   * Tests the execute method for the get vehicles command with vehicles.
   */

  @Test
  public void testExecuteGetVehiclesCommand() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    GetVehiclesCommand getVehiclesCommandDummy = mock(GetVehiclesCommand.class);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "getVehicles");
    getVehiclesCommandDummy.execute(webServerSessionSpy, commandFromClient);
    webServerSessionSpy.onMessage(commandFromClient.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("[{\"id\":1,\"numPassengers\":0,\"capacity"
        + "\":3,\"type\":\"SMALL_BUS_VEHICLE\",\"co2\":0,\"position\":{\"longitude"
        + "\":-93.235071,\"latitude\":44.97358},\"color\":{\"r\":122,"
        + "\"g\":0,\"b\":25,\"alpha\":255}},{\"id\":3,\"numPassengers\""
        + ":0,\"capacity\":3,\"type\":\"DIESEL_TRAIN_VEHICLE\",\"co2\":0,"
        + "\"position\":{\"longitude\":-93.235071,\"latitude\":44.97358},"
        + "\"color\":{\"r\":255,\"g\":204,\"b\":51,\"alpha\":255}},{\"id"
        + "\":2,\"numPassengers\":0,\"capacity\":3,\"type\":\"ELECTRIC_TRAIN_VEHICLE"
        + "\",\"co2\":0,\"position\":{\"longitude\":-93.235071,\"latitude\":44.97358},"
        + "\"color\":{\"r\":60,\"g\":179,\"b\":113,\"alpha\":255}},{\"id\":4,"
        + "\"numPassengers\":0,\"capacity\":3,\"type\":\"LARGE_BUS_VEHICLE\","
        + "\"co2\":0,\"position\":{\"longitude\":-93.235071,\"latitude\":44.97358},"
        + "\"color\":{\"r\":239,\"g\":130,\"b\":238,\"alpha\":255}}]",
        commandToClient.get("vehicles").toString());
  }

  /**
   * Tests the execute method for the get vehicles command without vehicles.
   */

  @Test
  public void testExecuteGetVehiclesCommandNoVehicles() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    GetVehiclesCommand getVehiclesCommandDummy = mock(GetVehiclesCommand.class);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "start");
    commandFromClient.addProperty("numTimeSteps", 50);
    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(5);
    timeBetweenVehicles.add(5);
    commandFromClient.add("timeBetweenVehicles", timeBetweenVehicles);
    webServerSessionSpy.onMessage(commandFromClient.toString());
    JsonObject vehiclesCommand = new JsonObject();
    vehiclesCommand.addProperty("command", "getVehicles");
    getVehiclesCommandDummy.execute(webServerSessionSpy, vehiclesCommand);
    webServerSessionSpy.onMessage(vehiclesCommand.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("[]", commandToClient.get("vehicles").toString());
  }
}