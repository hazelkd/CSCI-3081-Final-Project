package edu.umn.cs.csci3081w.project.model;

import com.google.gson.JsonObject;

public class LargeBusDecorator extends LargeBus {
  /**
   * Constructor for a bus.
   *
   * @param id       bus identifier
   * @param line     route of in/out bound
   * @param capacity capacity of bus
   * @param speed    speed of bus
   */
  public LargeBusDecorator(int id, Line line, int capacity, double speed) {
    super(id, line, capacity, speed);
  }

  /**
   * decorate vehicles with proper colors, and add
   * transparency if there is a line issue flagged.
   *
   * @return The correct color for specific vehicle
   */
  public JsonObject getColor() {
    if (getLine().isIssueExist()) {
      JsonObject colorJsonObject = new JsonObject();
      colorJsonObject.addProperty("r", 239);
      colorJsonObject.addProperty("g", 130);
      colorJsonObject.addProperty("b", 238);
      colorJsonObject.addProperty("alpha", 155);
      return colorJsonObject;
    } else {
      JsonObject colorJsonObject = new JsonObject();
      colorJsonObject.addProperty("r", 239);
      colorJsonObject.addProperty("g", 130);
      colorJsonObject.addProperty("b", 238);
      colorJsonObject.addProperty("alpha", 255);
      return colorJsonObject;
    }
  }
}
