import RestaurantBackend.Cook;
import RestaurantBackend.Ingredient;
import RestaurantBackend.Kitchen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * The application GUI for Cooks.
 */
class CookPane extends AnchorPane implements Observer {
  private Cook cook;

  private static final String FXML_PATH = "CookPane.fxml";

  @FXML private Label nextDish;
  @FXML private Label currentDish;
  @FXML private ListView<Ingredient> ingredientsDisplay;
  @FXML private Button markSeen;
  @FXML private Button completeDish;

  /**
   * Constructor for the new CookPane
   */
  CookPane() {
    cook = null;
    this.setStyle("-fx-background-color: rgb(255,255,255);");
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

  /** initialize all CookPanel controls */
  private void initControls() {
    markSeen.setOnAction(
        event -> {
          if (nextDish.getText().length() > 0) {
            cook.markSeenAndParse();
            if (cook.getCurrentDish() != null) {
              currentDish.setText(this.cook.getCurrentDish().toString());
              ingredientsDisplay.getItems().setAll(cook.getCurrentDish().getIngredients());
            }
          }
        });
    completeDish.setOnAction(
        event -> {
          cook.completeDish();
          currentDish.setText(null);
          ingredientsDisplay.getItems().clear();
        });
  }

  /**
   * Sets the cook that this CookPane is responsible for to the given Cook.
   *
   * @param cook The new Cook that this CookPane will be tied to.
   */
  void setCook(Cook cook) {
    this.cook = cook;
  }

  /** Update the nextDish label to display the ordered dish the kitchen passes to this CookPane */
  @Override
  public void update(Observable o, Object arg) {
    if ((o instanceof Kitchen))
      if ((arg instanceof String)) {
        this.nextDish.setText((String) arg);
      }
  }
}
