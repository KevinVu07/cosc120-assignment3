public enum TypeOfBeverage {
    COFFEE, TEA, SELECT_TYPE;
    /**
     * @return a prettified version of the relevant enum constant
     */
    public String toString() {
        String prettified = "NA";
        switch (this) {
            case COFFEE -> prettified =  "Coffee";
            case TEA -> prettified = "Tea";
            case SELECT_TYPE -> prettified = "Select beverage type";
        }
        return prettified;
    }
}

