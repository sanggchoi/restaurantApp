import RestaurantBackend.Manager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * The application GUI for Manager.
 */
class ManagerPane extends AnchorPane {

  private Manager manager;

  private static final String FXML_PATH = "ManagerPane.fxml";
  @FXML private TextArea managerDisplay;
  @FXML private Button getInventory;
  @FXML private Button getInProgress;
  @FXML private DatePicker datePicker;
  @FXML private Button getPaymentRecords;

  /**
   * Constructor for a new ManagerPane.
   */
  ManagerPane() {
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

  /**
   * Initializes the controls of this ManagerPane
   */
  private void initControls(){
    datePicker.setValue(LocalDate.now());
    getInventory.setOnAction(event -> {
      managerDisplay.setText(manager.checkInventory());
    });
    getInProgress.setOnAction(event -> {
      managerDisplay.setText(manager.getOrdersInProgress());
    });
    getPaymentRecords.setOnAction(event -> {
      managerDisplay.clear();
      String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyy-MM-dd"));
      File f = new File(String.format("PaymentRecords\\%s.txt",date));
      if(f.exists()){
        try {
          Scanner sc = new Scanner(f);
          while(sc.hasNextLine()) {
            String line = sc.nextLine();
            managerDisplay.appendText(line+"\r\n");
          }
          sc.close();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Sets the Manager that this ManagerPane is responsible for to the given Manager.
   *
   * @param manager The new Manager that this ManagerPane will be tied to.
   */
  void setManager(Manager manager){
    this.manager = manager;
    managerDisplay.setText(null);
  }
}
