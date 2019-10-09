package RestaurantBackend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * A server at a Restaurant.
 *
 * <p>Has a name,
 *
 * <p>Can access the bill on demand, take orders and manage them.
 *
 * <p>Servers cannot add more active tables (orders) if there are dishes waiting to be delivered
 */
public class Server extends Employee implements Observer {

  // active tables this server handles
  private ArrayList<Table> activeTables;

  private LinkedList<String> activeNotifications;

  private static final int MAX_NOTIFICATIONS = 20;

  // the number of dishes waiting to be delivered
  private int numWaiting;

  /**
   * A new server
   *
   * @param name: name of server
   * @param r: server's restaurant
   */
  Server(String name, Restaurant r) {
    super(name, r);
    activeTables = new ArrayList<>();
    activeNotifications = new LinkedList<>();
    numWaiting = 0;
  }

  /** @return a deep copy of active notifications */
  public LinkedList<String> getActiveNotifications() {
    return new LinkedList<>(activeNotifications);
  }

  /**
   * Add the given notification to the list of active notifications and pop off any stale
   * notifications
   */
  private void addNotification(String notification) {
    activeNotifications.add(notification);
    if (activeNotifications.size() > MAX_NOTIFICATIONS) {
      activeNotifications.removeFirst();
    }
  }

  /**
   * Create a new order and add it to the given table
   *
   * @param tableNumber: the table number of the table to be associated with the order
   */
  public void addOrder(int tableNumber) {
    Table table = getTable(tableNumber);
    if (table != null) {
      Order r = new Order(tableNumber);
      r.addObserver(this);
      table.addOrder(r);
      LogWriter.getInstance()
          .write(
              String.format(
                  "%s added order #%d to table #%d.", this.name, r.getOrderNumber(), tableNumber));
    }
  }

  /** @return a list of all active table numbers */
  public ArrayList<Integer> getActiveTableNumbers() {
    ArrayList<Integer> ret = new ArrayList<>();
    for (Table t : activeTables) ret.add(t.getTableNumber());
    return ret;
  }

  /**
   * add the given table to the active tables if it doesn't already exist, and there are no dishes
   * waiting to be delivered
   *
   * @param tableNumber the table to be added
   */
  public void addTable(int tableNumber) {
    if (numWaiting <= 0) {
      Table active = new Table(tableNumber);
      for (Table t : activeTables) if (t.getTableNumber() == tableNumber) return;
      activeTables.add(active);
      LogWriter.getInstance()
          .write(
              String.format(
                  "%s added table number #%d to the active tables.", this.name, tableNumber));
    }
  }

  /**
   * @param tableNumber the table number pertaining to the table
   * @return a list of all order numbers pertaining to a table
   */
  public ArrayList<Integer> getActiveOrdersAtTable(int tableNumber) {
    ArrayList<Integer> ret = new ArrayList<>();
    Table table = this.getTable(tableNumber);
    if(table != null) {
      for (Order r : table.getOrders()) ret.add(r.getOrderNumber());
    }
    return ret;
  }

  /**
   * Finalizes all tentative items in all orders for a table
   *
   * @param tableNumber: the tableNumber corresponding with the order to finalize
   */
  public void finalizeOrdersForTable(int tableNumber) {
    Table active = getTable(tableNumber);
    if (active != null) {
      for (Order order : active.getOrders())
        super.kitchen.addOrderedDishesToMake(order.finalizeTentativeItems());
      LogWriter.getInstance()
          .write(
              String.format(
                  "%s sent the orders for table #%d to the kitchen.", this.name, tableNumber));
    }
  }

  /**
   * Return a dish back to the kitchen
   *
   * @param tableNumber the table number corresponding to this table
   * @param orderNumber the order number corresponding to this table
   * @param dishID the dishID of the dish to be sent back
   */
  public void returnDish(int tableNumber, int orderNumber, int dishID) {
    Order order = getOrder(tableNumber, orderNumber);
    if (order != null) {
      OrderedDish dish = order.getOrderedDish(dishID);
      if (dish.served()) {
        order.removeOrderedDish(dishID);
        dish.resetStatus();
        order.addTentativeOrderedDish(dish);
        super.kitchen.addOrderedDishesToMake(order.finalizeTentativeItems());
        LogWriter.getInstance()
            .write(
                String.format(
                    "%s found something wrong with dish #%d, and sent it back to the kitchen.",
                    this.name, dishID));
      }
    }
  }

  /**
   * Add the given OrderedDish to a list of tentative orders if possible
   *
   * <p>A dish is tentative if it hasn't been finalized in the order.
   *
   * @param tableNumber the tableNumber pertaining to the order
   * @param orderNumber the orderNumber pertaining to the order
   * @param dishToAdd the dish to be added
   */
  public void addToOrder(int tableNumber, int orderNumber, OrderedDish dishToAdd) {
    if (numWaiting <= 0) {
      Order addTo = getOrder(tableNumber, orderNumber);
      if (addTo != null) {
        if (super.kitchen.sufficientIngredients(dishToAdd.ingredients)) {
          addTo.addTentativeOrderedDish(dishToAdd);
          LogWriter.getInstance()
              .write(
                  String.format(
                      "%s added %s to table #%d, order #%d's tentative order",
                      this.name, dishToAdd.toString(), tableNumber, orderNumber));
        }
        LogWriter.getInstance()
            .write(
                String.format(
                    "%s tried to add %s to table #%d's, order #%d tentative order, but there were insufficient ingredients",
                    this.name, dishToAdd.toString(), tableNumber, orderNumber));
      }
    }
  }

  /**
   * Remove an ordered dish that was sent to the kitchen and rejected
   *
   * <p>A dish is tentative if it hasn't been finalized in the order.
   *
   * @param tableNumber the tableNumber corresponding to the order
   * @param orderNumber the orderNumber pertaining to this order
   * @param dishId the id of the dish you are trying to remove
   */
  public boolean removeOrderedDish(int tableNumber, int orderNumber, int dishId) {
    Order order = getOrder(tableNumber, orderNumber);
    if (order != null) {
      if (order.getOrderedDish(dishId).shouldCancel() && order.removeOrderedDish(dishId))
        LogWriter.getInstance()
            .write(
                String.format(
                    "%s removed ordered dish #%d from table #%d's order.",
                    this.name, dishId, tableNumber));
      return true;
    }
    return false;
  }
  /**
   * Remove a tentative ordered dish from an order;
   *
   * <p>A dish is tentative if it hasn't been finalized in the order.
   *
   * @param tableNumber the tableNumber corresponding to the order
   * @param orderNumber the orderNumber pertaining to the order
   * @param dishId the id of the dish you are trying to remove
   */
  public void removeTentativeDish(int tableNumber, int orderNumber, int dishId) {
    Order order = getOrder(tableNumber, orderNumber);
    if (order != null) {
      if (order.removeTentativeOrderedDish(dishId))
        LogWriter.getInstance()
            .write(
                String.format(
                    "%s removed ordered dish #%d from table #%d's tentative order.",
                    this.name, dishId, tableNumber));
    }
  }

  /**
   * Get a bill consisting of all orders at the table
   *
   * @param tableNumber: the table number corresponding to this table
   * @return A String representation of the Bill corresponding to a table.
   */
  public String getBill(int tableNumber) {
    Table t = getTable(tableNumber);
    return t != null ? t.getBill() : null;
  }

  /**
   * Return an individual bill for a given order
   *
   * @param tableNumber the table number corresponding to an order
   * @param orderNumber the order number corrsesponding to an order
   * @return an individual bill for a given order
   */
  public String getIndividualBill(int tableNumber, int orderNumber) {
    Table t = getTable(tableNumber);
    return t != null ? t.getBill(orderNumber) : null;
  }

  /**
   * Return a table corresponding to the given table number
   *
   * @param tableNumber the table number corresponding to the sought table
   * @return the table corresponding to the given table number
   */
  private Table getTable(int tableNumber) {
    for (Table t : this.activeTables) {
      if (t.getTableNumber() == tableNumber) {
        return t;
      }
    }
    return null;
  }

  /**
   * Confirm that the dish with the given dishID has been served to a table with a given table
   * number; decrement to number of dishes waiting to be delivered
   *
   * @param dishID the id of the dish that was served
   * @param tableNumber the table number of the table that was served
   * @param orderNumber the order number of the order pertaining to the dish
   */
  public void confirmServed(int dishID, int tableNumber, int orderNumber) {
    Order confirmOrder = getOrder(tableNumber, orderNumber);
    if (confirmOrder != null) {
      OrderedDish toConfirm = confirmOrder.getOrderedDish(dishID);
      if (toConfirm != null && toConfirm.canDeliver()) {
        LogWriter.getInstance()
            .write(
                String.format(
                    "%s delivered %s to %s",
                    this.name, toConfirm.toString(), confirmOrder.toString()));
        toConfirm.updateStatus(true);
        numWaiting--;
      }
    }
  }

  /**
   * Get an order given a table number and order number
   *
   * @param tableNumber the table number corresponding to the table pertaining to this order
   * @param orderNumber the order number corresponding to the order you are looking for
   * @return the order corresponding to the table number and order number, null if it doesn't exist
   */
  public Order getOrder(int tableNumber, int orderNumber) {
    Table t = getTable(tableNumber);
    if (t != null) {
      Order r = t.getOrder(orderNumber);
      if (r != null) return r;
    }
    return null;
  }

  /**
   * Resolves the Order by the given order number.
   *
   * @param orderNumber: the order number to be resolved
   * @return true if an order was resolved, false otherwise
   */
  public boolean resolveOrder(int orderNumber) {
    for (int i = 0; i < activeTables.size(); i++) {
      Table table = activeTables.get(i);
      if (table.resolveOrder(orderNumber)) {
        if (table.getOrders().isEmpty()) activeTables.remove(i);
        LogWriter.getInstance().write(String.format("%s resolved order #%d", this.name, orderNumber));
        return true;
      }
    }
    return false;
  }

  /**
   * The notification text that tells a server that a dish for an order is ready, increments the
   * total number of dishes to be delivered
   *
   * @param dish: the dish in question
   * @param order: the order associated with that dish
   * @return the notification
   */
  private String notifyServeToTable(OrderedDish dish, Order order) {
    numWaiting++;
    String notification =
        String.format("%s for %s is ready to be served", dish.toString(), order.toString());
    addNotification(notification);
    setChanged();
    notifyObservers(notification);
    return notification;
  }

  /**
   * The notification text that tells a server to tell a customer that a dish for an order is
   * unavailable
   *
   * @param dish: the dish in question
   * @param order: the order associated with that dish
   * @return the notification
   */
  private String notifyCancelled(OrderedDish dish, Order order) {
    String notification =
        String.format(
            "Tell %s that the %s is currently unavailable, and remove %s from their ordered dishes",
            order, dish.getName(), dish.toString());
    addNotification(notification);
    setChanged();
    notifyObservers(notification);
    return notification;
  }

  /**
   * Updates this Observer according to the given arguments.
   *
   * @param o The Observable Object(Order).
   * @param arg The argument(OrderedDish) that is passed to all observers.
   */
  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Order && arg instanceof OrderedDish) {
      Order order = (Order) o;
      OrderedDish dish = (OrderedDish) arg;
      if (dish.canDeliver()) {
        LogWriter.getInstance().write(notifyServeToTable(dish, order));
      } else if (dish.shouldCancel()) {
        LogWriter.getInstance().write(notifyCancelled(dish, order));
      }
    }
  }
}
