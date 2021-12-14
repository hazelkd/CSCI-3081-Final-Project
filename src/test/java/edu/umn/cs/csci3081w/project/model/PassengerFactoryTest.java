package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class PassengerFactoryTest {

  /*/**
   * Tests generate function.
   */
  //@Test
  /*public void testGenerate() {
    PassengerFactory.DETERMINISTIC = true;
    assertEquals(3, PassengerFactory.generate(1, 10).getDestination());
  }*/

  /**
   * Tests generate function.
   */
  @Test
  public void nameGeneration() {

    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;

    assertEquals("Goldy", PassengerFactory.nameGeneration());

  }

  @Test
  public void generateNonDeterm() {

    PassengerFactory.DETERMINISTIC = false;
    assertEquals(0, PassengerFactory.generate(1, 10).getTimeOnVehicle());

  }


  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    PassengerFactory.DETERMINISTIC = false;
  }


}
