package edu.umn.cs.csci3081w.project.model;

import com.google.gson.JsonObject;
import java.io.PrintStream;

public class VehicleTestImpl extends Vehicle {

  public VehicleTestImpl(int id, Line line, int capacity, double speed, PassengerLoader loader,
                         PassengerUnloader unloader) {
    super(id, line, capacity, speed, loader, unloader);
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
    colorJsonObject.addProperty("r", 255);
    colorJsonObject.addProperty("g", 255);
    colorJsonObject.addProperty("b", 255);
    colorJsonObject.addProperty("alpha", 255);
    return colorJsonObject;
  }
}
