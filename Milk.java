public enum Milk {
    WHOLE, SKIM, SOY, ALMOND, OAT, COCONUT, NONE, I_DONT_MIND;

    /**
     * @return a prettified version of the relevant enum constant
     */
    public String toString() {
        String prettified = "NA";
        switch (this) {
            case WHOLE -> prettified =  "Full-cream";
            case SKIM -> prettified = "Skim";
            case SOY -> prettified = "Soy";
            case ALMOND -> prettified = "Almond";
            case OAT -> prettified = "Oat";
            case COCONUT -> prettified = "Coconut";
            case NONE -> prettified = "None";
            case I_DONT_MIND -> prettified = "I don't mind";
        }
        return prettified;
    }
}
