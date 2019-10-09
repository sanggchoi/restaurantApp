package RestaurantBackend;

import java.util.ArrayList;
import java.util.Observable;

/**
 * A Employee.
 *
 * <p>An Employee can receive and log shipments.
 */
public class Employee extends Observable{

  Kitchen kitchen;
  String name;

  Employee(String name, Restaurant restaurant) {
    this.kitchen = restaurant.getKitchen();
    this.name = name;
  }

  /**
   * Receive a shipment of a particular ingredient
   *
   * @param ing the ingredient being received
   */
  public void receiveShipment(Ingredient ing) {
    LogWriter.getInstance().write(String.format("%s received a shipment of %s", this.name, ing));
    kitchen.receiveShipment(ing);
  }

  @Override
  public String toString() {
    return this.name;
  }
}
