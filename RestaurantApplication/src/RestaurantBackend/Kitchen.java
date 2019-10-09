package RestaurantBackend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Scanner;

/**
 * A kitchen;
 *
 * <p>Contains the ingredient inventory
 */
public class Kitchen extends Observable implements FileCreator {

  private ArrayList<KitchenIngredient> inventory;
  private LinkedList<OrderedDish> toMake;
  private ArrayList<OrderedDish> inProgress;
  private ArrayList<OrderedDish> toDeliver;

  Kitchen(String inventoryFilePath) {
    initializeInventory(inventoryFilePath);
    toMake = new LinkedList<>();
    inProgress = new ArrayList<>();
    toDeliver = new ArrayList<>();
  }

  /** @return a String representation of all orders in progress */
  String inProgress() {
    StringBuilder sb = new StringBuilder();
    for (OrderedDish d : toMake) {
      sb.append(d.toString());
    }
    for (OrderedDish d : toDeliver) {
      sb.append(d.toString());
    }
    for (OrderedDish d : inProgress) {
      sb.append(d.toString());
    }
    return sb.toString();
  }

  /**
   * Add the given ordered dish to the collection of those in progress
   *
   * @param d the ordered dish to be added
   */
  void addInProgress(OrderedDish d) {
    inProgress.add(d);
  }

  /**
   * Move the given ordered dish from the in-progress collection to those waiting to be delivered
   *
   * @param d the Ordered Dish to be move
   */
  void moveToDeliver(OrderedDish d) {
    inProgress.remove(d);
    toDeliver.add(d);
  }

  /**
   * Initializes the inventory.
   *
   * <p>Each line of the input file should contain information pertaining to a single kitchen
   * ingredient and be formatted as follows: Ingredient name | Number of said KitchenIngredient |
   * Threshold to re-order
   *
   * <p>For example: Pasta | 19.5 | 10
   *
   * @param inventoryFilePath: the path to the file with details on the inventory
   */
  private void initializeInventory(String inventoryFilePath) {
    inventory = new ArrayList<>();
    try {
      Scanner sc = new Scanner(new File(inventoryFilePath));
      while (sc.hasNextLine()) {
        initializeInventoryHelper(sc.nextLine());
      }
    } catch (FileNotFoundException e) {
      createNewFile(inventoryFilePath);
    }
  }

  /**
   * Helper for the initializeInventory method
   *
   * @param line: the line to parse.
   */
  private void initializeInventoryHelper(String line) {
    String[] input = line.split("\\s\\|\\s");
    inventory.add(
        new KitchenIngredient(input[0], Double.valueOf(input[1]), Double.valueOf(input[2]), this));
  }

  /**
   * Receive new ingredients
   *
   * @param ingredient: the new ingredient
   */
  void receiveShipment(Ingredient ingredient) {
    KitchenIngredient toInc = getKitchenIngredient(ingredient);
    if (toInc == null) { // add a new ingredient
      this.inventory.add(
          new KitchenIngredient(ingredient.getName(), ingredient.getAmount(), 20, this));
    } else {
      toInc.increment(ingredient.getAmount());
    }
  }

  /**
   * Check if there are sufficient ingredients to make a certain recipe
   *
   * @param recipe: an ArrayList of ingredients that you need to make a recipe
   * @return true if sufficient ingredients, false otherwise
   */
  boolean sufficientIngredients(ArrayList<Ingredient> recipe) {
    for (Ingredient ing : recipe) {
      KitchenIngredient checkIng = getKitchenIngredient(ing);
      if (checkIng == null || checkIng.getAmount() - ing.getAmount() < 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Search for a KitchenIngredient in the inventory
   *
   * @param ing: the ingredient to search for
   * @return the corresponding KitchenIngredient if it exists, otherwise null
   */
  private KitchenIngredient getKitchenIngredient(Ingredient ing) {
    for (KitchenIngredient kitchenIngredient : this.inventory) {
      if (kitchenIngredient.equals(ing)) {
        return kitchenIngredient;
      }
    }
    return null;
  }

  /**
   * Notify the manager that this kitchen is running low on an ingredient
   *
   * @param ingredient: the ingredient that the kitchen is running low on.
   */
  void notifyManager(KitchenIngredient ingredient) {
    setChanged();
    notifyObservers(ingredient);
  }

  /**
   * Use the given ingredients and deduct them from the inventory
   *
   * @param ingredients: the ingredients to be used
   */
  public void useIngredients(ArrayList<Ingredient> ingredients) {
    for (Ingredient toUse : ingredients) {
      Ingredient ing = getKitchenIngredient(toUse);
      if (ing != null) {
        ing.decrement(toUse.getAmount());
      }
    }
  }

  /**
   * Add an ArrayList of OrderedDishes to the queue of dishes to be made.
   *
   * @param dishesToAdd: the dishes to be added
   */
  void addOrderedDishesToMake(ArrayList<OrderedDish> dishesToAdd) {
    toMake.addAll(dishesToAdd);
    if (toMake.size() > 0) {
      setChanged();
      notifyObservers(toMake.peekFirst().toString());
    }
  }

  /**
   * Remove the next ordered dish to be made
   *
   * @return the next ordered dish in the queue
   */
  OrderedDish nextOrderedDish() {
    OrderedDish next = null;
    if (!toMake.isEmpty()) {
      next = toMake.removeFirst();
    }
    setChanged();
    notifyObservers(!toMake.isEmpty() ? toMake.peekFirst().toString() : "");
    return next;
  }

  /** @return the formatted kitchen inventory */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Current Inventory === \n");
    for (Ingredient ing : inventory) {
      sb.append(ing).append("\n");
    }
    return sb.toString();
  }
}
