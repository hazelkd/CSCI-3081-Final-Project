package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.websocket.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.StringUtil;

public class WebServerSessionTest {

  private final PrintStream standardOut = System.out;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  /**
   * Setup deterministic operations before each test runs.
   */

  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  /**
   * Test command for initializing the simulation.
   */

  @Test
  public void testSimulationInitialization() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "initLines");
    webServerSessionSpy.onMessage(commandFromClient.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("2", commandToClient.get("numLines").getAsString());
  }

  /**
   * Test onMessage for branch of not a key value.
   */

  @Test
  public void testOnMessageNotKey() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "notACommand");
    webServerSessionSpy.onMessage(commandFromClient.toString());
    assertEquals("notACommand", commandFromClient.get("command").getAsString());
  }

  /**
   * Test onOpen to see if it prints "session opened".
   */

  @Test
  public void testOnOpen() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    String myString = outputStreamCaptor.toString();
    String stringOutput = StringUtil.removeFirstLine(myString);
    stringOutput = stringOutput.replaceAll("[\\n\\t]", "");
    String expected = "session opened";
    assertEquals(expected, stringOutput);
  }

  /**
   * Test onClose to see if it prints "session closed".
   */

  @Test
  public void testOnClose() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onClose(sessionDummy);
    String myString = outputStreamCaptor.toString();
    String stringOutput = StringUtil.removeFirstLine(myString);
    stringOutput = stringOutput.replaceAll("[\\n\\t]", "");
    String expected = "session closed";
    assertEquals(expected, stringOutput);
  }

  /**
   * Closes the printStream capture after each test.
   */
  @AfterEach
  public void teardown() {
    System.setOut(standardOut);
  }
}