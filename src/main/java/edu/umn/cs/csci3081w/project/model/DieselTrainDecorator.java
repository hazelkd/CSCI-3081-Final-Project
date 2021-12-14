package edu.umn.cs.csci3081w.project.model;

import com.google.gson.JsonObject;

public class DieselTrainDecorator extends DieselTrain {


  /**
   * Constructor for a train.
   *
   * @param id       train identifier
   * @param line     route of in/out bound
   * @param capacity capacity of the train
   * @param speed    speed of the train
   */
  public DieselTrainDecorator(int id, Line line, int capacity, double speed) {
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
      colorJsonObject.addProperty("r", 255);
      colorJsonObject.addProperty("g", 204);
      colorJsonObject.addProperty("b", 51);
      colorJsonObject.addProperty("alpha", 155);
      return colorJsonObject;
    } else {
      JsonObject colorJsonObject = new JsonObject();
      colorJsonObject.addProperty("r", 255);
      colorJsonObject.addProperty("g", 204);
      colorJsonObject.addProperty("b", 51);
      colorJsonObject.addProperty("alpha", 255);
      return colorJsonObject;
    }
  }
}
