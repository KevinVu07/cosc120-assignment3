import java.text.NumberFormat;

public class Beverage {
    // fields
    private final long itemId;
    private final String itemName;
    private final float price;
    private final String description;
    private final DreamBeverage genericFeatures;

    /**
     * This constructor is used to create a real beverage
     * @param itemId the unique item ID of a beverage
     * @param itemName: the name of the beverage
     * @param price: the price of the beverage
     * @param description: the description of the beverage
     * @param dreamBeverage: the "dream" features of the beverage
     */
    //constructor
    public Beverage(long itemId, String itemName, float price, String description, DreamBeverage dreamBeverage) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.description = description;
        this.genericFeatures = dreamBeverage;
    }

    //getters and setters

    public long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public float getPrice() {
        return price;
    }

    /**
     * format the price to be the correct format $x.xx
     * @param price the price of the beverage
     * @return a formatted string of the price
     */
    public String formatPrice(float price) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(price);
    }


    public String getDescription() {
        return description;
    }

    /**
     * this method returns the generic ("dream") features of the beverage
     * @return a DreamBeverage object that contains the criteria map of the beverage
     */
    public DreamBeverage getGenericFeatures() {
        return genericFeatures;
    }

    /**
     * this method display the menu of the beverage in a form that is presentable to the user with info about the details of the beverage
     * @return a StringBuilder object that represents the details of the beverage
     */
    public StringBuilder displayMenu() {
        StringBuilder displayMenu = new StringBuilder();
        String priceString = formatPrice(this.getPrice());
        displayMenu.append(this.getItemName()).append(" (").append(this.getItemId()).append(")\n").append(this.getDescription()).append("\n").append("Ingredients:").append(this.genericFeatures.getDescription()).append("\nPrice: ").append(priceString).append("\n\n");
        return displayMenu;
    }

}
