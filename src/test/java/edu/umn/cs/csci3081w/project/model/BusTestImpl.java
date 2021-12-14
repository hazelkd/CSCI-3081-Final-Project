package edu.umn.cs.csci3081w.project.model;

import com.google.gson.JsonObject;
import java.io.PrintStream;

public class BusTestImpl extends Bus {
  public BusTestImpl(int id, Line line, int capacity, double speed) {
    super(id, line, capacity, speed);
  }

  /**
   * Method created for testing purposes.
   *
   * @param out output stream
   */
  @Override
  public void report(PrintStream out) {

  }

  /**
   * Method created for testing purposes.
   *
   * @return
   */
  @Override
  public int getCurrentCO2Emission() {
    return 0;
  }

  @Override
  public JsonObject getColor() {
    JsonObject colorJsonObject = new JsonObject();
    colorJsonObject.addProperty("r", 122);
    colorJsonObject.addProperty("g", 0);
    colorJsonObject.addProperty("b", 25);
    colorJsonObject.addProperty("alpha", 255);
    return colorJsonObject;
  }
}
