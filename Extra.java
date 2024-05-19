public enum Extra {
    SELECT_EXTRA_PREFERENCE, NONE, ONE_OR_MORE, SKIP;

    /**
     * @return a prettified version of the relevant enum constant
     */
    public String toString() {
        String prettified = "NA";
        switch (this) {
            case SELECT_EXTRA_PREFERENCE -> prettified = "Select extra preference";
            case NONE -> prettified =  "None";
            case ONE_OR_MORE -> prettified = "One or more";
            case SKIP -> prettified = "I don't mind";
        }
        return prettified;
    }
}
