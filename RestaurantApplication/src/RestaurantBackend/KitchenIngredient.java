package RestaurantBackend;

/** A kitchen ingredient */
class KitchenIngredient extends Ingredient {

    private double threshold;
    private Kitchen kitchen;

    /**
     * Constructor for a new KitchenIngredient
     *
     * @param name Name of the ingredient
     * @param value The initial amount of the ingredient
     * @param threshold The threshold, which when the amount is below it, the ingredient needs to be reordered
     * @param kitchen The kitchen that this ingredient belongs to
     */
    KitchenIngredient(String name, double value, double threshold, Kitchen kitchen) {
        super(name, value);
        this.kitchen = kitchen;
        this.threshold = threshold;
    }

    /**
     * Decrements the current amount of ingredient by the given value.
     * Notifies the Manager when amount after decrementing is below the set threshold.
     *
     * @param value The amount that you want to decrement the ingredient by.
     */
    @Override
    public void decrement(double value) {
        super.decrement(value);
        if (this.getAmount() <= threshold) {
            kitchen.notifyManager(this);
        }
    }
}