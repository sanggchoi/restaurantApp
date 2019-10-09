import RestaurantBackend.*;
import RestaurantBackend.Menu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * A ServerPane
 *
 * <p>ServerPanes are GUI components through which servers can manage their orders
 */
public class ServerPane extends SplitPane implements Observer {

  private static final String FXML_PATH = "ServerPane.fxml";
  private Server server;

  @FXML private ListView<Integer> tableList;
  @FXML private TextField addTableInput;
  @FXML private Button addTableButton;

  @FXML private ListView<Integer> orderList;
  @FXML private Button addOrderButton;
  @FXML private Button resolve;

  @FXML private TextArea notificationBox;

  @FXML private ListView<OrderedDish> pendingDeliveries;
  @FXML private Button confirmDelivery;

  @FXML private ListView<OrderedDish> tentativeDishListView;
  @FXML private ChoiceBox<Dish> newDishSelector;
  @FXML private Button addNewDish;
  @FXML private Button removeDishFromOrder;
  @FXML private Button confirmDishes;
  @FXML private ListView<OrderedDish> orderedDishListView;
  @FXML private Button removeOrderedDish;

  @FXML private ListView<Ingredient> ingredientListView;
  @FXML private Button incrementIngredient;
  @FXML private Button decrementIngredient;

  @FXML ListView<OrderedDish> deliveredDishView;
  @FXML Button sendBack;

  @FXML TextArea billArea;
  @FXML Button showIndividual;
  @FXML Button showCombined;

  private Menu menu;

  private static final double INGREDIENT_STEP = 0.5;

  /**
   * Create a new ServerPane instance
   *
   * <p>Precondition: the restaurant has at least one server.
   */
  ServerPane(Menu menu, Restaurant restaurant) {
    this.setStyle("-fx-background-color: rgb(255,255,255);");
    this.server = restaurant.getServers().get(0);
    this.menu = menu;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    initControls();
  }

  /** initialize all controls */
  private void initControls() {
    initTableControls();
    initOrderControls();
    initDishControls();
    initIngredientControls();
    initDeliveryControls();
    initDeliveredDishControls();
    initBillControls();
  }

  /** initialize delivered dish controls */
  private void initDeliveredDishControls() {
    sendBack.setOnAction(
        event -> {
          OrderedDish dish = deliveredDishView.getSelectionModel().getSelectedItem();
          if (dish != null) {
            int tableNumber = tableList.getSelectionModel().getSelectedItem();
            int orderNumber = orderList.getSelectionModel().getSelectedItem();
            server.returnDish(tableNumber, orderNumber, dish.getID());
            Order r = server.getOrder(tableNumber, orderNumber);
            refreshDeliveredDishView(r);
            refreshTentativeDishes(r);
            refreshOrderedDishListView(r);
          }
        });
  }

  /** initialize bill controls */
  private void initBillControls() {
    showIndividual.setOnAction(
        event -> {
          Integer tableNumber = tableList.getSelectionModel().getSelectedItem();
          if (tableNumber != null) {
            Integer orderNumber = orderList.getSelectionModel().getSelectedItem();
            if (orderNumber != null) {
              billArea.setText(server.getIndividualBill(tableNumber, orderNumber));
            }
          }
        });
    showCombined.setOnAction(
        event -> {
          Integer tableNumber = tableList.getSelectionModel().getSelectedItem();
          if (tableNumber != null) billArea.setText(server.getBill(tableNumber));
        });
  }

  /** initialize the tableList controls */
  private void initTableControls() {
    tableList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((tableNumber, oldVal, newVal) -> refreshOrderList(newVal));
    addTableButton.setOnAction(
        event -> {
          String input = addTableInput.getText();
          if (input.matches("^[0-9]+") && input.length() < 8) {
            // realistically, you won't have more than 10^3 tables but whatever
            server.addTable(Integer.valueOf(input));
            refreshTableList();
            billArea.clear();
          }
        });
  }

  /** initilaize the delivery confirmation controls */
  private void initDeliveryControls() {
    confirmDelivery.setOnAction(
        event -> {
          OrderedDish selected = pendingDeliveries.getSelectionModel().getSelectedItem();
          if (selected != null) {
            int tableNumber = tableList.getSelectionModel().getSelectedItem();
            int orderNumber = orderList.getSelectionModel().getSelectedItem();
            Order r = server.getOrder(tableNumber, orderNumber);
            server.confirmServed(selected.getID(), tableNumber, orderNumber);
            refreshPendingDeliveries(r);
            refreshDeliveredDishView(r);
          }
          billArea.clear();
        });
  }

  /** initialize the ingredient controls */
  private void initIngredientControls() {
    incrementIngredient.setOnAction(
        event -> {
          Ingredient ing = ingredientListView.getSelectionModel().getSelectedItem();
          if (ing != null) {
            ing.increment(INGREDIENT_STEP);
            ingredientListView.refresh();
          }
        });
    decrementIngredient.setOnAction(
        event -> {
          Ingredient ing = ingredientListView.getSelectionModel().getSelectedItem();
          if (ing != null) {
            ing.decrement(INGREDIENT_STEP);
            ingredientListView.refresh();
          }
        });
  }

  /** initialize the order controls */
  private void initOrderControls() {
    orderList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (order, oldVal, newVal) -> {
              handleSelectedOrder(newVal);
            });
    addOrderButton.setOnAction(
        event -> {
          Integer tableNumber = tableList.getSelectionModel().getSelectedItem();
          if (tableNumber != null) {
            server.addOrder(tableNumber);
            refreshOrderList(tableNumber);
            billArea.clear();
          }
        });
    resolve.setOnAction(
        event -> {
          Integer orderNumber = orderList.getSelectionModel().getSelectedItem();
          if (orderNumber != null)
            if (server.resolveOrder(orderNumber)) {
              orderList.getItems().remove(orderNumber);
              deliveredDishView.getItems().clear();
            }
          refreshTableList();
          billArea.clear();
        });
  }

  /** Initialize the Dish Interface */
  private void initDishControls() {
    newDishSelector.getItems().setAll(menu.getMenuItems());
    newDishSelector.setValue(menu.getMenuItems().get(0));
    tentativeDishListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((orderedDish, oldDish, newDish) -> refreshIngredientsList(newDish));
    orderedDishListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((orderedDish, oldDish, newDish) -> refreshIngredientsList(newDish));
    removeOrderedDish.setOnAction(
        event -> {
          OrderedDish selectedDish = orderedDishListView.getSelectionModel().getSelectedItem();
          if (selectedDish != null) {
            if (server.removeOrderedDish(
                tableList.getSelectionModel().getSelectedItem(),
                orderList.getSelectionModel().getSelectedItem(),
                selectedDish.getID())) orderedDishListView.getItems().remove(selectedDish);
          }
        });
    confirmDishes.setOnAction(
        event -> {
          Integer tableNumber = tableList.getSelectionModel().getSelectedItem();
          if (tableNumber != null) server.finalizeOrdersForTable(tableNumber);
          handleSelectedOrder(orderList.getSelectionModel().getSelectedItem());
        });
    removeDishFromOrder.setOnAction(
        event -> {
          OrderedDish selectedDish = tentativeDishListView.getSelectionModel().getSelectedItem();
          if (selectedDish != null) {
            server.removeTentativeDish(
                tableList.getSelectionModel().getSelectedItem(),
                orderList.getSelectionModel().getSelectedItem(),
                selectedDish.getID());
            tentativeDishListView.getItems().remove(selectedDish);
          }
        });
    addNewDish.setOnAction(
        event -> {
          Integer orderNumber = orderList.getSelectionModel().getSelectedItem();
          if (orderNumber != null) {
            server.addToOrder(
                tableList.getSelectionModel().getSelectedItem(),
                orderNumber,
                OrderedDish.DishToOrderedDish(newDishSelector.getValue()));
            handleSelectedOrder(orderNumber);
          }
        });
  }

  /**
   * Refreshes the tentative and ordered dish list views based on the given ordered number
   *
   * @param orderNumber: the passed order number
   */
  private void handleSelectedOrder(Integer orderNumber) {
    if (orderNumber != null) {
      Order working = server.getOrder(tableList.getSelectionModel().getSelectedItem(), orderNumber);
      refreshTentativeDishes(working);
      refreshOrderedDishListView(working);
      refreshPendingDeliveries(working);
      billArea.clear();
    }
  }

  /** refreshes the tableList contents */
  private void refreshTableList() {
    clearSelections(tableList);
    tableList.getItems().setAll(server.getActiveTableNumbers());
  }

  /**
   * Refresh the orderList contents
   *
   * @param tableNumber the table to pull orders from
   */
  private void refreshOrderList(Integer tableNumber) {
    clearSelections(orderList);
    if (tableNumber != null) {
      orderList.getItems().setAll(server.getActiveOrdersAtTable(tableNumber));
    } else {
      orderList.getItems().clear();
    }
    refreshTentativeDishes(null);
    refreshOrderedDishListView(null);
  }

  /**
   * Refresh the tentativeDishListView contents with the given Order
   *
   * @param order: the order which gives the context for the tentativeDishListView contents
   */
  private void refreshTentativeDishes(Order order) {
    clearSelections(tentativeDishListView);
    if (order != null) {
      tentativeDishListView.getItems().setAll(order.getTentative());
    } else tentativeDishListView.getItems().clear();
    refreshIngredientsList(null);
  }

  /**
   * Refresh the orderedDishListView contents with the given Order
   *
   * @param order: the order which gives the context for the orderedDishListView contents
   */
  private void refreshOrderedDishListView(Order order) {
    clearSelections(orderedDishListView);
    if (order != null) {
      orderedDishListView.getItems().setAll(order.getOrderItems());
    } else orderedDishListView.getItems().clear();
    refreshIngredientsList(null);
  }

  /**
   * Refresh the pendingDelivery listView contents with the given Order
   *
   * @param order: the order which gives the context for the orderedDishListView contents
   */
  private void refreshPendingDeliveries(Order order) {
    clearSelections(pendingDeliveries);
    if (order != null) {
      pendingDeliveries.getItems().setAll(order.pendingDelivery());
    } else pendingDeliveries.getItems().clear();
  }

    /**
     * Refreshes the delivery dish view.
     *
     * @param order the order that pertains to this delivered dish view
     */
  private void refreshDeliveredDishView(Order order) {
    clearSelections(deliveredDishView);
    if (order != null) {
      deliveredDishView.getItems().setAll(order.getDeliveredItems());
    } else deliveredDishView.getItems().clear();
  }

  /**
   * Refresh the ingredients list
   *
   * @param dish: the dish that gives the context for the list of ingredients
   */
  private void refreshIngredientsList(OrderedDish dish) {
    clearSelections(ingredientListView);
    if (dish != null) {
      ingredientListView.getItems().setAll(dish.getIngredients());
    } else ingredientListView.getItems().clear();
  }

  // refresh the notification box
  private void refreshNotificationBox() {
    StringBuilder sb = new StringBuilder();
    for (String notification : server.getActiveNotifications()) {
      sb.append(notification).append("\r\n");
    }
    notificationBox.setText(sb.toString());
    notificationBox.setScrollTop(Double.MAX_VALUE); // scroll to the bottom
  }

  /**
   * Clear the selection of a given listView
   *
   * @param l the listView to be cleared
   */
  private void clearSelections(ListView l) {
    l.getSelectionModel().select(-1);
  }

  /**
   * Set the Server
   *
   * <p>Precondition: the passed server is not null
   *
   * @param server the server to be set to
   */
  void setServer(Server server) {
    this.server = server;
    refreshTableList();
    refreshNotificationBox();
  }

    /**
     * Updates this Observer according to the given arguments.
     *
     * @param o The Observable Object(Server).
     * @param arg The argument(String) that is passed to all observers.
     */
  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Server && arg instanceof String)
      if (this.server == o) {
        refreshNotificationBox();
      }
  }
}
