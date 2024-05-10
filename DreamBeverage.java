import java.util.*;

public class DreamBeverage {
    private final float minPrice;
    private final float maxPrice;
    private final Map<Criteria,Object> criteria;

    /**
     * This constructor is used to create a user's dream beverage
     * @param minPrice the lowest price a user desires
     * @param maxPrice the highest price a user desires
     * @param criteria a Map containing the criteria used to compare DreamBeverages
     */
    public DreamBeverage(float minPrice, float maxPrice, Map<Criteria, Object> criteria) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        if(criteria==null) this.criteria=new HashMap<>();
        else this.criteria = new HashMap<>(criteria);
    }

    /**
     * This constructor is used to create the 'dream' aspects of real beverages
     * @param criteria a Map containing the criteria used to compare DreamBeverages
     */
    public DreamBeverage(Map<Criteria,Object> criteria){
        if(criteria==null) this.criteria=new LinkedHashMap<>();
        else this.criteria = new LinkedHashMap<>(criteria);
        minPrice=-1;
        maxPrice=-1;
    }

    // getters and setters

    public float getMinPrice() {
        return minPrice;
    }


    public float getMaxPrice() {
        return maxPrice;
    }


    /**
     * @return the entire map of criteria keys, and values
     */
    public Map<Criteria, Object> getAllCriteria() {
        return new HashMap<>(criteria);
    }

    /**
     * @param key a Criteria (enum) constant representing a search criteria
     * @return a value stored in the map at a given key
     */
    public Object getCriteria(Criteria key){return getAllCriteria().get(key);}

    /**
     * @return a String describing all the 'dream' features of a beverage
     */
    public String getDescription(){
        StringBuilder description = new StringBuilder();
        for(Criteria key: criteria.keySet()) {
            String criteriaChoice = getCriteria(key).toString();
            if (key.equals(Criteria.MILK)) {
                criteriaChoice = getCriteria(Criteria.MILK).toString().replace("[", "").replace("]", "");
            } else if (key.equals(Criteria.EXTRAS)) {
                criteriaChoice = getCriteria(Criteria.EXTRAS).toString().replace("[", "").replace("]", "");
            }
            description.append("\n").append(key).append(": ").append(criteriaChoice);
        }
        return description.toString();
    }

    /**
     * compares a DreamBeverage against a real DreamBeverage features
     * @param realBeverage a Beverage object representing a real, registered beverage
     * @return true if the real Beverage's 'dream' features match this DreamBeverage's features
     * this method is sourced and adapted from COSC120 week 7 lecture 7.2 DreamGeek.java matches()
     */
    public boolean matches(DreamBeverage realBeverage) {
        for(Criteria key : realBeverage.getAllCriteria().keySet()) {
            // compare each criteria (key - values) that both the realBeverage and the dreamBeverage have to each other)
            if(this.getAllCriteria().containsKey(key)){
                // if the criteria to compare in both realBeverage and dreamBeverage are a collection (set), check for intersect of the 2 collection
                if(getCriteria(key) instanceof Collection<?> && realBeverage.getCriteria(key) instanceof Collection<?>){
                    Set<Object> intersect = new HashSet<>((Collection<?>) realBeverage.getCriteria(key));
                    intersect.retainAll((Collection<?>) getCriteria(key));
                    if(intersect.isEmpty()) return false;
                }
                // if only the criteria in realBeverage is a collection and the criteria in the dreamBeverage is not, check if the collection contains the singular criteria
                else if(realBeverage.getCriteria(key) instanceof Collection<?> && !(getCriteria(key) instanceof Collection<?>)){
                    if(!((Collection<?>) realBeverage.getCriteria(key)).contains(getCriteria(key))) return false;
                }
                // if only the criteria in dreamBeverage is a collection and the criteria in the realBeverage is not, check if the collection contains the singular criteria
                else if(!(realBeverage.getCriteria(key) instanceof Collection<?>) && getCriteria(key) instanceof Collection<?>){
                    if(!((Collection<?>) getCriteria(key)).contains(realBeverage.getCriteria(key))) return false;
                }
                // finally, if they're both singular, check for equality
                else if(!getCriteria(key).equals(realBeverage.getCriteria(key))) return false;
            }
        }
        return true;
    }
}
