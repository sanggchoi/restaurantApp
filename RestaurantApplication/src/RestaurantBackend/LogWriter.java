package RestaurantBackend;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Observable;

public class LogWriter extends Observable{

  private static final LogWriter INSTANCE = new LogWriter();
  private static final String LOG_PATH = "log.txt";

  /**
   * Returns the current instance of the LogWriter.
   *
   * @return The current instance of the LogWriter.
   */
  public static LogWriter getInstance(){
    return INSTANCE;
  }

  /**
   *  Writes the given line of text into the log text file.
   *
   * @param line The line of text that you wish to write into
   */
  void write(String line) {
    try {
      this.setChanged();
      notifyObservers(line);
      FileWriter fw = new FileWriter(new File(LOG_PATH), true);
      fw.write(line + "\r\n");
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   *  Creates a new text file, its name using the current time.
   *  The given payment record will be written into the created file.
   *
   * @param line The payment records.
   */
  void writeToPaymentRecords(String line) {
    try {
      FileWriter fw = new FileWriter(new File(String.format("PaymentRecords\\%s.txt",LocalDate.now().toString())), true);
      fw.write(line + "\r\n");
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
