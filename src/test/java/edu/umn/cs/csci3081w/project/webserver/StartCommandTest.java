package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.websocket.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.StringUtil;

public class StartCommandTest {

  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  /**
   * Setup print stream capture before each test runs.
   */

  @BeforeEach
  public void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  /**
   * Tests the execute method for the start command.
   */

  @Test
  public void testStartCommandExecute() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "start");
    commandFromClient.addProperty("numTimeSteps", 50);
    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(5);
    timeBetweenVehicles.add(5);
    commandFromClient.add("timeBetweenVehicles", timeBetweenVehicles);
    webServerSessionSpy.onMessage(commandFromClient.toString());
    webServerSessionSpy.sendJson(commandFromClient);
    String myString = outputStreamCaptor.toString();
    String stringOutput = StringUtil.removeFirstLine(myString);
    stringOutput = stringOutput.replace("[\\n\\t]", "");
    String expectedText = "session opened" + System.lineSeparator()
        + "Time between vehicles for route  0: 5" + System.lineSeparator()
        + "Time between vehicles for route  1: 5" + System.lineSeparator()
        + "Number of time steps for simulation is: 50" + System.lineSeparator()
        + "Starting simulation" + System.lineSeparator();
    assertEquals(expectedText, stringOutput);
  }
}
