package edu.umn.cs.csci3081w.project.model;

public abstract class VehicleDecorator extends Vehicle {

  protected Vehicle vehicle;

  /**
   * Constructor for a vehicle.
   *
   * @param id       vehicle identifier
   * @param line     line
   * @param capacity vehicle capacity
   * @param speed    vehicle speed
   * @param loader   passenger loader for vehicle
   * @param unloader passenger unloader for vehicle
   */
  public VehicleDecorator(int id, Line line, int capacity, double speed,
                          PassengerLoader loader, PassengerUnloader unloader) {
    super(id, line, capacity, speed, loader, unloader);

    this.vehicle = vehicle;
  }

}
