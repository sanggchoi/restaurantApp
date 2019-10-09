package RestaurantBackend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/** A Restaurant */
public class Restaurant implements FileCreator {

  // lists of employees
  private ArrayList<Server> servers;
  private ArrayList<Cook> cooks;
  private ArrayList<Manager> managers;
  // the kitchen
  private Kitchen kitchen;
  // the menu
  private Menu menu;

  /**
   * Create a new instance of Restaurant
   *
   * @param employeePath: path to employee information file
   * @param menuPath: path to menu information file
   * @param inventoryPath: path to inventory information file
   */
  public Restaurant(String employeePath, String menuPath, String inventoryPath) {
    menu = new Menu(menuPath);
    kitchen = new Kitchen(inventoryPath);
    initializeEmployees(employeePath);
  }

  /**
   * Returns the Kitchen of this Restaurant
   *
   * @return the Kitchen of this Restaurant
   */
  public Kitchen getKitchen() {
    return kitchen;
  }

  /**
   * Initializes all employees using the given employee.txt file path
   *
   * <p>Each line of the input file should contain an employee and be formatted as follows:
   * Occupation | Name
   *
   * <p>For example: Server | John
   */
  private void initializeEmployees(String employeePath) {
    cooks = new ArrayList<>();
    servers = new ArrayList<>();
    managers = new ArrayList<>();
    try {
      Scanner sc = new Scanner(new File(employeePath));
      while (sc.hasNextLine()) {
        String[] input = sc.nextLine().split("\\s\\|\\s");
        switch (input[0]) {
          case "Server":
            servers.add(new Server(input[1], this));
            break;
          case "Cook":
            cooks.add(new Cook(input[1], this));
            break;
          case "Manager":
            Manager toAdd = new Manager(input[1], this);
            getKitchen().addObserver(toAdd);
            managers.add(toAdd);
            break;
          default:
            break;
        }
      }
    } catch (FileNotFoundException e) {
      createNewFile(employeePath);
    }
  }

  /**
   * @return a deep copy of this restaurant's servers
   */
  public ArrayList<Server> getServers() {
    return new ArrayList<>(servers);
  }

  /**
   * @return a deep copy of this restaurant's managers
   */
  public ArrayList<Manager> getManagers() {
    return new ArrayList<>(managers);
  }

  /**
   * @return a deep copy of this restaurant's cooks
   */
  public ArrayList<Cook> getCooks() {
    return new ArrayList<>(cooks);
  }

  /**
   * @return this restaurant's menu
   */
  public Menu getMenu() {
    return this.menu;
  }
}
