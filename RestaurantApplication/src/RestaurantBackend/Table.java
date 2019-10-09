package RestaurantBackend;

import java.util.ArrayList;

/**
 * A table at a restaurant *
 *
 * <p>Contains the table number this table corresponds to, and any orders pertaining to this table
 */
public class Table {

  private int tableNumber;
  private ArrayList<Order> orders;

  private static final int BILL_WIDTH = 30;
  private static final double TAX = 0.13;
  private static final double GRATUITY = 0.15;
  private static final int GRATUITY_THRESHOLD = 8;

  public Table(int tableNumber) {
    this.tableNumber = tableNumber;
    orders = new ArrayList<>();
  }

  public int getTableNumber() {
    return tableNumber;
  }

  /**
   * Add the given order to this table
   *
   * @param r the order to be added
   */
  void addOrder(Order r) {
    orders.add(r);
  }

  /** @return a deep copy of the orders pertaining to this Table */
  public ArrayList<Order> getOrders() {
    return new ArrayList<>(orders);
  }

  /**
   * Get an order with a given order number if it exists
   *
   * @param orderNumber the order number to search for
   * @return the order, or null if it doesn't exist
   */
  Order getOrder(int orderNumber) {
    for (Order r : orders) {
      if (r.getOrderNumber() == orderNumber) return r;
    }
    return null;
  }

  /** @return A formatted bill for all orders at this table */
  String getBill() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < BILL_WIDTH; i++) sb.append(".");
    sb.append("\n");
    sb.append(getBillHelper(0, orders.size() - 1, 0, new StringBuilder()));
    for (int i = 0; i < BILL_WIDTH; i++) sb.append(".");
    return sb.toString();
  }

  /**
   * Returns a formatted bill for a given order
   *
   * @param orderNumber: the order number pertaining to the order in question
   * @return a formatted bill given an order number
   */
  String getBill(int orderNumber) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < BILL_WIDTH; i++) sb.append(".");
    sb.append("\n");
    for (int i = 0; i < orders.size(); i++) {
      Order r = orders.get(i);
      if (r.getOrderNumber() == orderNumber) {
        sb.append(getBillHelper(i, i, 0, new StringBuilder()));
      }
    }
    for (int i = 0; i < BILL_WIDTH; i++) sb.append(".");
    return sb.toString();
  }

  /**
   * Recursive helper method for getting the bill
   *
   * @param start: starting index
   * @param end: end index
   * @param currentTotal: the current total
   * @param sb: a StringBuilder used to memoize the contents of the bill
   * @return the bill containing all the items ordered at this table, excluding the header
   */
  private String getBillHelper(int start, int end, double currentTotal, StringBuilder sb) {
    if (start <= end && start >= 0) {
      double total = currentTotal;
      for (OrderedDish dish : orders.get(start).getOrderItems()) {
        if (dish.served()) {
          sb.append(billLineFormat(dish.getName(), dish.getCost())).append("\n");
          total += dish.getCost();
        }
      }
      return getBillHelper(start + 1, end, total, sb);
    } else {
      double tax = currentTotal * TAX;
      double gratuity = 0;
      sb.append(billLineFormat("SUBTOTAL:", currentTotal)).append("\n");
      sb.append(billLineFormat("TAX:", tax));
      if (orders.size() >= GRATUITY_THRESHOLD) {
        gratuity = currentTotal * GRATUITY;
        sb.append(billLineFormat("GRATUITY:", gratuity)).append("\n");
      }
      double total = currentTotal + tax + gratuity;
      sb.append(billLineFormat("TOTAL:", total)).append("\n");
      return sb.toString();
    }
  }

  /**
   * Format a line of the bill
   *
   * @param label the label associated with the value; e.g. TAX, SUBTOTAL etc
   * @param value the value associated with the label
   * @return a formatted line of the bill, formatted as follows: label ......... value
   */
  private String billLineFormat(String label, double value) {
    StringBuilder sb = new StringBuilder();
    sb.append(label);
    String valueText = String.format("$%.2f%n", value);
    for (int i = 0; i < BILL_WIDTH - label.length() - valueText.length(); i++) {
      sb.append(".");
    }
    sb.append(valueText);
    return sb.toString();
  }

  /**
   * Resolve the given order and remove it from the list of orders if all of it's dishes have been
   * served
   *
   * <p>Write the bill to payment records
   *
   * @param orderNumber the order number to be resolved
   * @return true if an order was removed; false otherwise
   */
  boolean resolveOrder(int orderNumber) {
    for (int i = 0; i < orders.size(); i++) {
      Order r = orders.get(i);
      if (r.getOrderNumber() == orderNumber
          && r.getOrderItems().size() == r.getDeliveredItems().size()) {
        LogWriter.getInstance().writeToPaymentRecords(getBill(orderNumber));
        orders.remove(i);
        return true;
      }
    }
    return false;
  }
}
