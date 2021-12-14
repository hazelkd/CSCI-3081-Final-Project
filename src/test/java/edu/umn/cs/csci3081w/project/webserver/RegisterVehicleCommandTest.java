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

public class RegisterVehicleCommandTest {

  /**
   * Tests the execute method for the register vehicles command with vehicles.
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
    commandFromClient.addProperty("command", "registerVehicle");
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
   * Tests the execute method for the register vehicles command without vehicles.
   */

  @Test
  public void testExecuteRegisterVehiclesCommandWithoutVehicles() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    RegisterVehicleCommand registerVehicleCommandDummy = mock(RegisterVehicleCommand.class);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "start");
    commandFromClient.addProperty("numTimeSteps", 50);
    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(5);
    timeBetweenVehicles.add(5);
    commandFromClient.add("timeBetweenVehicles", timeBetweenVehicles);
    commandFromClient.addProperty("command", "registerVehicle");
    commandFromClient.addProperty("id", 1);
    registerVehicleCommandDummy.execute(webServerSessionSpy, commandFromClient);
    webServerSessionSpy.onMessage(commandFromClient.toString());
    webServerSessionSpy.sendJson(commandFromClient);
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("1", commandToClient.get("id").toString());
  }
}
