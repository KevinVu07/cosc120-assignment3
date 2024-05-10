public enum Sugar {
    YES, NO, I_DONT_MIND;

    /**
     * @return a prettified version of the relevant enum constant
     */
    public String toString() {
        String prettified = "NA";
        switch (this) {
            case YES -> prettified =  "yes";
            case NO -> prettified = "no";
            case I_DONT_MIND -> prettified = "I don't mind";
        }
        return prettified;
    }
}
