package RestaurantBackend;

import java.util.*;

/**
 * A Dish
 *
 * <p>Contains the name of the dish, the cost of ordering this dish, and the ingredients required to
 * make this dish
 */
public class Dish extends Observable {
  /** name of the dish */
  private String name;

  /** cost of the dish */
  private double cost;

  /**
   * ArrayList of the ingredients required to make this dish
   */
  protected ArrayList<Ingredient> ingredients;

  /**
   * A new dish
   *
   * @param name: the name of the dish
   * @param cost: the cost of the dish
   * @param ingredients: The ingredients required to make this dish
   */
  Dish(String name, double cost, ArrayList<Ingredient> ingredients) {
    this.name = name;
    this.cost = cost;
    this.ingredients = ingredients;
  }

  String getName() {
    return this.name;
  }

  /**
   * @return cost of dish
   */
  double getCost() {
    return cost;
  }

  /**
   * @return a deep copy of this dish
   */
  Dish deepDishCopy() {
    ArrayList<Ingredient> ingredientsCopy = new ArrayList<>();
    for (Ingredient i : this.ingredients) {
      ingredientsCopy.add(new Ingredient(i.getName(), i.getAmount()));
    }
    return new Dish(this.name, this.cost, ingredientsCopy);
  }

  /**
   * A string representation, formatted as follows:
   * <<name of dish>>, with amount1 ingredient1, amount2 ingredient2 etc.
   *
   * @return A String representation of this dish
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(Ingredient i : ingredients) {
      sb.append(i.toString());
      sb.append(", ");
    }
    sb.delete(sb.length()-2,sb.length());
    return String.format("%s, with %s", this.name, sb.toString());
  }
}
