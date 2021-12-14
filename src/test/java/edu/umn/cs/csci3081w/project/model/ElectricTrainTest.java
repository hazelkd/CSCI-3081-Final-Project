package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ElectricTrainTest {

  private Train testTrain;
  private Train testTrain2;
  private Route testRouteIn;
  private Route testRouteOut;


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

    testTrain = new ElectricTrain(1,
        new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, new Issue()),
        3, 1.0);

    testTrain2 = new ElectricTrainDecorator(1,
        new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, new Issue()),
        3, 1.0);
  }

  /**
   * Tests constructor.
   */
  @Test
  public void testConstructor() {
    assertEquals(1, testTrain.getId());
    assertEquals("testRouteOut1", testTrain.getName());
    assertEquals(3, testTrain.getCapacity());
    assertEquals(1, testTrain.getSpeed());
    assertEquals(testRouteOut, testTrain.getLine().getOutboundRoute());
    assertEquals(testRouteIn, testTrain.getLine().getInboundRoute());
  }

  /**
   * Tests if updateDistance function works properly.
   */
  @Test
  public void testReport() {
    testTrain.move();
    try {
      final Charset charset = StandardCharsets.UTF_8;
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream testStream = new PrintStream(outputStream, true, charset.name());
      testTrain.report(testStream);
      outputStream.flush();
      String data = new String(outputStream.toByteArray(), charset);
      testStream.close();
      outputStream.close();
      String strToCompare =
          "####Electric Train Info Start####" + System.lineSeparator()
              + "ID: 1" + System.lineSeparator()
              + "Name: testRouteOut1" + System.lineSeparator()
              + "Speed: 1.0" + System.lineSeparator()
              + "Capacity: 3" + System.lineSeparator()
              + "Position: 44.97358,-93.235071" + System.lineSeparator()
              + "Distance to next stop: 0.843774422231134" + System.lineSeparator()
              + "****Passengers Info Start****" + System.lineSeparator()
              + "Num of passengers: 0" + System.lineSeparator()
              + "****Passengers Info End****" + System.lineSeparator()
              + "####Electric Train Info End####" + System.lineSeparator();
      assertEquals(strToCompare, data);
    } catch (IOException ioe) {
      fail();
    }
  }

  /**
   * Test the co2 calculation for a train.
   */
  @Test
  public void testCurrentCO2Emission() {
    assertEquals(0, testTrain.getCurrentCO2Emission());
    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    testTrain.loadPassenger(testPassenger1);
    assertEquals(0, testTrain.getCurrentCO2Emission());
  }

  /**
   * Test the color for an electric train.
   */
  @Test
  public void testGetColor() {
    JsonObject colorJsonObject = new JsonObject();
    colorJsonObject.addProperty("r", 60);
    colorJsonObject.addProperty("g", 179);
    colorJsonObject.addProperty("b", 113);
    colorJsonObject.addProperty("alpha", 255);
    assertEquals(colorJsonObject, testTrain.getColor());
  }

  /**
   * Test the transparent color for a electric train.
   */
  @Test
  public void testGetTransparentColor() {
    testTrain.getLine().createIssue();
    JsonObject colorJsonObject = new JsonObject();
    colorJsonObject.addProperty("r", 60);
    colorJsonObject.addProperty("g", 179);
    colorJsonObject.addProperty("b", 113);
    colorJsonObject.addProperty("alpha", 155);
    assertEquals(colorJsonObject, testTrain.getColor());
  }

  /**
   * Test the color decorator for color on electric train.
   */
  @Test
  public void testColorDecorator() {
    JsonObject colorJsonObject = new JsonObject();
    colorJsonObject.addProperty("r", 60);
    colorJsonObject.addProperty("g", 179);
    colorJsonObject.addProperty("b", 113);
    colorJsonObject.addProperty("alpha", 255);
    assertEquals(colorJsonObject, testTrain2.getColor());
  }

  /**
   * Test the color decorator for transparent color on electric train.
   */
  @Test
  public void testColorDecoratorTransparent() {
    testTrain2.getLine().createIssue();
    JsonObject colorJsonObject = new JsonObject();
    colorJsonObject.addProperty("r", 60);
    colorJsonObject.addProperty("g", 179);
    colorJsonObject.addProperty("b", 113);
    colorJsonObject.addProperty("alpha", 155);
    assertEquals(colorJsonObject, testTrain2.getColor());
  }

  @Test
  public void testReportWithPassengers() {
    testTrain.move();
    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    testTrain.loadPassenger(testPassenger1);
    testTrain.loadPassenger(testPassenger2);
    try {
      final Charset charset = StandardCharsets.UTF_8;
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream testStream = new PrintStream(outputStream, true, charset.name());
      testTrain.report(testStream);
      outputStream.flush();
      String data = new String(outputStream.toByteArray(), charset);
      testStream.close();
      outputStream.close();
      String strToCompare =
          "####Electric Train Info Start####" + System.lineSeparator()
              + "ID: 1" + System.lineSeparator()
              + "Name: testRouteOut1" + System.lineSeparator()
              + "Speed: 1.0" + System.lineSeparator()
              + "Capacity: 3" + System.lineSeparator()
              + "Position: 44.97358,-93.235071" + System.lineSeparator()
              + "Distance to next stop: 0.843774422231134" + System.lineSeparator()
              + "****Passengers Info Start****" + System.lineSeparator()
              + "Num of passengers: 2" + System.lineSeparator()
              + "####Passenger Info Start####" + System.lineSeparator()
              + "Name: testPassenger1" + System.lineSeparator()
              + "Destination: 3" + System.lineSeparator()
              + "Wait at stop: 0" + System.lineSeparator()
              + "Time on vehicle: 1" + System.lineSeparator()
              + "####Passenger Info End####" + System.lineSeparator()
              + "####Passenger Info Start####" + System.lineSeparator()
              + "Name: testPassenger2" + System.lineSeparator()
              + "Destination: 2" + System.lineSeparator()
              + "Wait at stop: 0" + System.lineSeparator()
              + "Time on vehicle: 1" + System.lineSeparator()
              + "####Passenger Info End####" + System.lineSeparator()
              + "****Passengers Info End****" + System.lineSeparator()
              + "####Electric Train Info End####" + System.lineSeparator();
      assertEquals(strToCompare, data);
    } catch (IOException ioe) {
      fail();
    }
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testTrain = null;
  }

}
