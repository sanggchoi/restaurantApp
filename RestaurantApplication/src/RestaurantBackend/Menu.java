package RestaurantBackend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * The restaurant menu
 *
 * <p>Contains the names of all menu items, and the ingredients required to make said item.
 */
public class Menu implements FileCreator {

  private ArrayList<Dish> items;

  Menu(String menuPath) {
    initializeMenu(menuPath);
  }

  /**
   * Initializes the menu
   *
   * <p>Each Line of the input file should be formatted as follows, where each ingredient is unique:
   * ItemName | Cost | Ingredient1, # | Ingredient2, # | ...
   *
   * <p>For example: Aglio E Olio | 10 | Olive Oil, 0.3 | Parsley, 0.5 | Salt, 0.1 | Pepper, 0.1 |
   * Lemon, 1 |
   *
   * @param menuPath: the file path containing the menu items
   */
  private void initializeMenu(String menuPath) {
    items = new ArrayList<>();
    try {
      Scanner sc = new Scanner(new File(menuPath));
      while (sc.hasNextLine()) {
        items.add(formatDishHelper(sc.nextLine()));
      }
    } catch (FileNotFoundException e) {
      createNewFile(menuPath);
    }
    items.sort(Comparator.comparing(Dish::getName));
  }

  /** @return a deep copy of all the menu items */
  public ArrayList<Dish> getMenuItems() {
    ArrayList<Dish> ret = new ArrayList<>();
    for (Dish d : this.items) {
      ret.add(d.deepDishCopy());
    }
    return ret;
  }

  /**
   * Formats the given String to return a Dish with the correct properties
   *
   * @param line: the String to be formatted
   * @return a Dish with the correct properties
   */
  private Dish formatDishHelper(String line) {
    String[] input = line.split("\\s\\|\\s");
    String name = input[0];
    double cost = Double.valueOf(input[1]);
    ArrayList<Ingredient> ingredients = new ArrayList<>();
    for (int i = 2; i < input.length; i++) {
      String[] ingredientString = input[i].split(",\\s");
      ingredients.add(new Ingredient(ingredientString[0], Double.valueOf(ingredientString[1])));
    }
    return new Dish(name, cost, ingredients);
  }
}
