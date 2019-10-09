import RestaurantBackend.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/** The controller for the Restaurant Application */
public class MainApplicationWindow extends AnchorPane implements Observer {

  private static final String FXML_PATH = "MainApplicationWindow.fxml";
  private static final String EMPLOYEE_PATH = "employees.txt";
  private static final String MENU_PATH = "menu.txt";
  private static final String INVENTORY_PATH = "inventory.txt";

  /**
   * An enum of all of the jobs in the Restaurant.
   */
  private enum Jobs {
    MANAGER("manager"),
    SERVER("server"),
    COOK("cook");

    private final String job;

    /**
     * Constructor for this enum.
     *
     * @param text The job title.
     */
    Jobs(final String text) {
      this.job = text;
    }

    /**
     * Returns the title of the job.
     *
     * @return the title of the job.
     */
    @Override
    public String toString() {
      return job;
    }
  }

  private Restaurant restaurant;
  @FXML private TextArea logArea;
  @FXML private ChoiceBox<Employee> employeeSelect;
  @FXML private ChoiceBox<Jobs> jobSelect;
  @FXML private StackPane employeePane;
  @FXML private TextField ingredientField;
  @FXML private TextField ingredientAmount;
  @FXML private Button addIngredient;

  private ServerPane serverPane;
  private ManagerPane managerPane;
  private CookPane cookPane;

  /** Constructor for the restaurant Controller */
  MainApplicationWindow() {
    restaurant = new Restaurant(EMPLOYEE_PATH, MENU_PATH, INVENTORY_PATH);
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    LogWriter.getInstance().addObserver(this);

    try {
      fxmlLoader.load();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }

    serverPane = new ServerPane(restaurant.getMenu(), restaurant);
    serverPane.setServer(restaurant.getServers().get(0));
    managerPane = new ManagerPane();
    managerPane.setManager(restaurant.getManagers().get(0));
    cookPane = new CookPane();
    restaurant.getKitchen().addObserver(cookPane);
    cookPane.setCook(restaurant.getCooks().get(0));

    employeePane.getChildren().addAll(serverPane, managerPane, cookPane);
    initJobSelect();
    initIngredientControls();
  }

  /** Initialize the jobSelect drop-down window */
  private void initJobSelect() {
    jobSelect.getItems().setAll(Jobs.MANAGER, Jobs.SERVER, Jobs.COOK);
    jobSelect
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((changed, oldValue, newValue) -> switchToPane(newValue));
    jobSelect.getSelectionModel().selectFirst();
    employeeSelect
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (value, old, newValue) -> {
              switch (jobSelect.getSelectionModel().getSelectedItem()) {
                case MANAGER:
                  if (newValue instanceof Manager) managerPane.setManager((Manager) newValue);
                  break;
                case COOK:
                  if (newValue instanceof Cook) cookPane.setCook((Cook) newValue);
                  break;
                case SERVER:
                  if (newValue instanceof Server) serverPane.setServer((Server) newValue);
                  break;
              }
            });
  }

  /** Initialize the ingredient controls */
  private void initIngredientControls() {
    addIngredient.setOnAction(
        event -> {
          String ingredient = ingredientField.getText();
          String amount = ingredientAmount.getText();
          if (ingredient.length() > 0
              && amount.length() > 0
              && amount.matches("^[0-9]*\\.?[0-9]+")) {
            employeeSelect
                .getSelectionModel()
                .getSelectedItem()
                .receiveShipment(new Ingredient(ingredient, Double.valueOf(amount)));
          }
        });
  }

  /**
   * Switch between panes based on the passed Job
   *
   * @param job: the job pertaining to the pane to be displayed
   */
  private void switchToPane(Jobs job) {
    switch (job) {
      case MANAGER:
        employeeSelect.getItems().setAll(restaurant.getManagers());
        managerPane.toFront();
        break;
      case SERVER:
        employeeSelect.getItems().setAll(restaurant.getServers());
        serverPane.toFront();
        break;
      case COOK:
        employeeSelect.getItems().setAll(restaurant.getCooks());
        cookPane.toFront();
        break;
    }
    employeeSelect.getSelectionModel().selectFirst();
  }

  /**
   * Updates this Observer according to the given arguments.
   *
   * @param o The Observable Object(LogWriter).
   * @param arg The argument(String) that is passed to all observers.
   */
  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof LogWriter && arg instanceof String) {
      logArea.appendText(arg + "\r\n");
    }
  }
}
