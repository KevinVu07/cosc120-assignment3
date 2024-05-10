import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Menu {
    //fields
    private final Set<Beverage> allBeverages = new HashSet<>();

    //default constructor used, therefore no need to declare it

    //methods
    /**
     * method to add a menu Beverage object to the database (allBeverages)
     * @param beverage a Beverage object
     */
    public void addItem(Beverage beverage){
        allBeverages.add(beverage);
    }


    /**
     * returns a collection of Beverage objects that meet all the customer's criteria
     * @param dreamBeverage a Beverage object representing a user's preferred Beverage
     * @return a collection list of matching Beverage objects
     * this method was sourced and adapted from COSC120 Lecture 4 AllGeeks.java findDreamGeek
     */
    public List<Beverage> findDreamBeverage(DreamBeverage dreamBeverage){
        List<Beverage> matches = new ArrayList<>();
        for(Beverage beverage : allBeverages){
            DreamBeverage genericFeatures = beverage.getGenericFeatures();
            // check if the menu beverage price is in the same range with the user price range
            if(beverage.getPrice()< dreamBeverage.getMinPrice() || beverage.getPrice()> dreamBeverage.getMaxPrice()) continue;
            if(dreamBeverage.matches(genericFeatures)) matches.add(beverage);

        }
        // return the collection of matching menu beverage
        return matches;
    }
}
