package edu.umn.cs.csci3081w.project.model;

import com.google.gson.JsonObject;
import java.io.PrintStream;

public class TrainTestImpl extends Train {
  public TrainTestImpl(int id, Line line, int capacity, double speed) {
    super(id, line, capacity, speed);
  }

  @Override
  public void report(PrintStream out) {

  }

  @Override
  public int getCurrentCO2Emission() {
    return 0;
  }

  @Override
  public JsonObject getColor() {
    JsonObject colorJsonObject = new JsonObject();
    colorJsonObject.addProperty("r", 60);
    colorJsonObject.addProperty("g", 179);
    colorJsonObject.addProperty("b", 113);
    colorJsonObject.addProperty("alpha", 255);
    return colorJsonObject;
  }
}
