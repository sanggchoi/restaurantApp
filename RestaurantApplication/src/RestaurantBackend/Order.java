package RestaurantBackend;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * An order for an individual at this restaurant;
 *
 * <p>Contains the table number this
 * order corresponds to, any tentative order items, and any items
 * that have been sent to the kitchen.
 */
public class Order extends Observable implements Observer {
  //List of items already sent to kitchen
  private ArrayList<OrderedDish> orderItems;
  //List of tentative order items
  private ArrayList<OrderedDish> tentativeOrderItems;
  //the tableNumber corresponding to this order
  private int tableNumber;
  private int orderNumber;
  private static int numOrders = 0;

  Order(int tableNumber) {
    this.tableNumber = tableNumber;
    this.orderNumber = numOrders;
    orderItems = new ArrayList<>();
    tentativeOrderItems = new ArrayList<>();
    numOrders++;
  }

  /**
   * Returns the order number.
   *
   * @return The order number.
   */
  public int getOrderNumber() {
    return orderNumber;
  }

  /** Get a deep copy of the tentative items in this order */
  public ArrayList<OrderedDish> getTentative(){
    return new ArrayList<>(tentativeOrderItems);
  }

  /**
   * Add a tentative dish
   * @param dish: the ordered dish to be added
   */
  void addTentativeOrderedDish(OrderedDish dish) {
    dish.addObserver(this);
    this.tentativeOrderItems.add(dish);
  }

  /**
   * @return an ArrayList of ordered dishes that are pending delivery
   */
  public ArrayList<OrderedDish> pendingDelivery() {
    ArrayList<OrderedDish> ret = new ArrayList<>();
    for(OrderedDish d : orderItems) {
      if(d.canDeliver())
        ret.add(d);
    }
    return ret;
  }

  /**
   * @return an ArrayList of ordered dishes that have been delivered
   */
  public ArrayList<OrderedDish> getDeliveredItems() {
    ArrayList<OrderedDish> ret = new ArrayList<>();
    for(OrderedDish d : orderItems) {
      if(d.served())
        ret.add(d);
    }
    return ret;
  }

  /**
   * Remove a tentative dish from the order
   *
   * @param id: the id of the ordered dish to be removed
   * @return true if something was removed, false otherwise
   */
  boolean removeTentativeOrderedDish(int id) {
    return remove(id, this.tentativeOrderItems);
  }

  /**
   * Remove a dish that was deemed unavailable after it was finalized
   *
   * @param id: the id of the dish
   * @return true if something was removed, false otherwise
   */
  boolean removeOrderedDish(int id) {
    return remove(id, this.orderItems);
  }

  /**
   * Get an ordered dish given an id
   * @param id: the id of the ordered dish you're searching for
   * @return the ordered dish you're looking for
   */
  OrderedDish getOrderedDish(int id) {
    return this.orderItems.get(id);
  }

  /**
   * Remove the given orderedDish from a list of ordered dishes
   *
   * @param id the id of the orderedDish to be removed
   * @param toSearch the list of dishes to search through
   * @return true if the OrderedDish was removed
   */
  private boolean remove(int id, ArrayList<OrderedDish> toSearch) {
    for(int i = 0; i < toSearch.size(); i++) {
      if(toSearch.get(i).getID() == id) {
        toSearch.remove(i);
        return true;
      }
    }
    return false;
  }

  /**
   * Add the tentative items to the order, and return
   * an ArrayList of the added items
   * @return an ArrayList of the finalized tentative OrderedDishes
   */
  ArrayList<OrderedDish> finalizeTentativeItems() {
    ArrayList<OrderedDish> ret = new ArrayList<>();
    while(!this.tentativeOrderItems.isEmpty()) {
      OrderedDish toFinalize = tentativeOrderItems.remove(0);
      this.orderItems.add(toFinalize);
      ret.add(toFinalize);
    }
    return ret;
  }

  /**
   * @return a deep copy of the this Order's order items
   */
  public ArrayList<OrderedDish> getOrderItems() {
    return new ArrayList<>(orderItems);
  }

  /**
   * Updates this Observer according to the given arguments.
   *
   * @param o The Observable Object(OrderedDish).
   * @param arg The argument that is passed to all observers.
   */
  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof OrderedDish) {
      this.setChanged();
      this.notifyObservers(o);
    }
  }

  /**
   * Returns the String representation of this Order.
   *
   * @return The String representation of this Order.
   */
  @Override
  public String toString(){
    return String.format("Table #%d, Order #%d",tableNumber, orderNumber);
  }
}
