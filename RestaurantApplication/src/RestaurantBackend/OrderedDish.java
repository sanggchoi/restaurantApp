package RestaurantBackend;

import java.util.ArrayList;

/**
 * A ordered dish
 *
 * <p>OrderedDishes are like dishes, except they can be modified;
 *
 * <p>They have a status and an id which is determined by how many OrderedDishes have been created
 */
public class OrderedDish extends Dish {
  // the status of this dish
  private Status status;
  // the id of this dish
  private int id;

  // how many orders have been created
  private static int orderCount = 0;

  // the threshold to reset the order count
  private static final int RESET_ORDER_COUNT = 1000;

  /**
   * An enum representing the current status of this OrderedDish.
   */
  private enum Status {
    CANCELLED,
    ORDERED,
    SEEN,
    READY,
    SERVED;

    Status update() {
      return this.ordinal() < values().length ? values()[this.ordinal() + 1] : this;
    }
  }

  /**
   * Returns true if this OrderedDish can be cooked.
   *
   * @return true if this OrderedDish can be cooked.
   */
  boolean canCook() {
    return this.status == Status.SEEN;
  }

  /**
   * Returns true if this OrderedDish has been served.
   *
   * @return true if this OrderedDish has been served.
   */
  boolean served() {
    return this.status == Status.SERVED;
  }

  /**
   * Returns true if this OrderedDish is ready for delivery.
   *
   * @return true if this OrderedDish is ready for delivery.
   */
  boolean canDeliver() {
    return this.status == Status.READY;
  }

  /**
   * Returns true if this OrderedDish needs to be cancelled.
   *
   * @return true if this OrderedDish needs to be cancelled.
   */
  boolean shouldCancel() {
    return this.status == Status.CANCELLED;
  }

  /**
   * Resets the status of this OrderedDish to having just been ordered.
   */
  void resetStatus() {
    this.status = Status.ORDERED;
  }

  /**
   * A new ordered dish
   *
   * @param name: name of ordered dish
   * @param cost: cost of ordered dish
   * @param ingredients: ingredients used in ordered dish
   */
  private OrderedDish(String name, double cost, ArrayList<Ingredient> ingredients) {
    super(name, cost, ingredients);
    this.status = Status.ORDERED;
    this.id = orderCount;
    orderCount++;
    if (orderCount >= RESET_ORDER_COUNT) {
      orderCount = 0;
    }
  }

  public int getID() {
    return id;
  }

  /**
   * Return an ordered dish given a Dish
   *
   * @param d: the dish in question
   * @return an ordered dish version of a Dish
   */
  public static OrderedDish DishToOrderedDish(Dish d) {
    Dish deepCopy = d.deepDishCopy();
    return new OrderedDish(deepCopy.getName(), deepCopy.getCost(), deepCopy.ingredients);
  }

  public ArrayList<Ingredient> getIngredients(){
    return this.ingredients;
  }

  /**
   * update the status of this order
   *
   * @param proceed: true if this order should proceed; false if it should be cancelled
   */
  void updateStatus(boolean proceed) {
    if (proceed) {
      this.status = this.status.update();
      if (this.status == Status.READY) {
        setChanged();
        notifyObservers();
      }
    } else {
      this.status = Status.CANCELLED;
      setChanged();
      notifyObservers();
    }
  }

  /**
   * A string representation, formatted as follows: id number,name of dish, with amount1
   * ingredient1, amount2 ingredient2 ...
   *
   * @return A String representation of this dish
   */
  @Override
  public String toString() {
    return String.format("#%d, %s", getID(), super.toString());
  }
}
