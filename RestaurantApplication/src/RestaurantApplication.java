import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The application for the restaurant program.
 */
public class RestaurantApplication extends Application {

  private static final String TITLE = "RESTAURANT APPLICATION";

  /**
   * The main method.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Starts the program.
   *
   * @param primaryStage The primary stage.
   */
  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle(TITLE);
    MainApplicationWindow rc = new MainApplicationWindow();
    primaryStage.setScene(new Scene(rc, 1280, 720));
    primaryStage.show();
  }
}
