package RestaurantBackend;

/**
 * A Cook
 *
 * <p>Cooks can determine whether or not to make a dish, and complete dishes
 */
public class Cook extends Employee {

  private OrderedDish currentDish;
  private boolean dishInProgress;

  /**
   * A new Cook
   *
   * @param name: name of Cook
   * @param r: Cook's restaurant
   */
  Cook(String name, Restaurant r) {
    super(name, r);
    currentDish = null;
    dishInProgress = false;
  }

  /**
   * Start making the next ordered dish if possible, otherwise, have it cancelled.
   *
   * <p>Doesn't consider the next ordered dish unless the current one is complete (null)
   */
  public void markSeenAndParse() {
    if (!dishInProgress) {
      getNextDish();
      if (currentDish != null) {
        if (kitchen.sufficientIngredients(currentDish.ingredients)) {
          currentDish.updateStatus(true);
          kitchen.useIngredients(currentDish.ingredients);
          kitchen.addInProgress(currentDish);
          dishInProgress = true;
          LogWriter.getInstance()
              .write(String.format("%s has seen %s", this.name, currentDish.toString()));
        } else {
          currentDish.updateStatus(false);
          LogWriter.getInstance()
              .write(String.format("%s has seen %s", this.name, currentDish.toString()));
          currentDish = null;
        }
      } else getNextDish();
    }
  }

  /** Complete the the current Dish */
  public void completeDish() {
    if (this.currentDish != null && this.currentDish.canCook()) {
      LogWriter.getInstance()
          .write(String.format("%s has finished cooking %s", this.name, currentDish));
      this.currentDish.updateStatus(true);
      super.kitchen.moveToDeliver(this.currentDish);
      this.currentDish = null;
      dishInProgress = false;
    }
  }

  public OrderedDish getCurrentDish() {
    return currentDish;
  }

  /**
   * Get the next dish and make it the current dish, assuming the current dish is null
   */
  private void getNextDish() {
    this.currentDish = kitchen.nextOrderedDish();
  }
}
