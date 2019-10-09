package RestaurantBackend;

/**
 * An Ingredient
 *
 * <p>Ingredients have a name, and an amount
 *
 * <p>They can be incremented and decremented
 */
public class Ingredient {

  private String name;
  private double amount;

  /**
   * A new ingredient
   *
   * @param name: name of ingredient
   * @param amount: amount of ingredient
   */
  public Ingredient(String name, double amount) {
    this.name = name;
    this.amount = amount;
  }

  /**
   * Add ingredient
   *
   * @param amount: given amount of ingredient
   */
  public void increment(double amount) {
    this.amount += amount;
  }

  /**
   * Remove ingredient
   *
   * @param amount: given amount of ingredient
   */
  public void decrement(double amount) {
    this.amount = Math.max(this.amount - amount, 0);
  }

  double getAmount() {
    return this.amount;
  }

  String getName() {
    return this.name;
  }

  /**
   * A String representation of this Ingredient, formatted as follows: amount value
   *
   * <p>return: a string representation of this Ingredient
   */
  @Override
  public String toString() {
    return String.format("%s %s", this.amount, this.name);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Ingredient && this.getName().equals(((Ingredient) obj).getName());
  }
}
