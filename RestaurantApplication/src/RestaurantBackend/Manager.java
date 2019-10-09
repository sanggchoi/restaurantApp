package RestaurantBackend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

/**
 * A Manager
 *
 * <p>Managers can view the inventory.
 */
public class Manager extends Employee implements Observer {

  private static final String REQUESTS_PATH = "requests.txt";

  /**
   * A new manager
   *
   * @param name: name of manager
   * @param r: manager's restaurant
   */
  Manager(String name, Restaurant r) {
    super(name, r);
  }

  /**
   * Writes a request for more of a kitchen ingredient to the requests.txt file
   *
   * @param i: the kitchen ingredient being requested
   */
  private void writeRequest(KitchenIngredient i) {
    try {
      PrintWriter pw = new PrintWriter(new FileOutputStream(new File(REQUESTS_PATH), true));
      pw.println(String.format("I'd like to Order 20 more %s", i.getName()));
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Returns the restaurant inventory */
  public String checkInventory() {
    LogWriter.getInstance().write(String.format("%s requests an inventory printout.", this.name));
    return super.kitchen.toString();
  }

  /** @return A String representation of all orders in progress */
  public String getOrdersInProgress() {
    return super.kitchen.inProgress();
  }

  /**
   * Updates this Observer according to the given arguments.
   *
   * @param o The Observable Object(Kitchen).
   * @param arg The argument(KitchenIngredient) that is passed to all observers.
   */
  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Kitchen && arg instanceof KitchenIngredient) {
      LogWriter.getInstance().write(
          String.format("Added a request for %s", ((KitchenIngredient) arg).getName()));
      this.writeRequest((KitchenIngredient) arg);
    }
  }
}
