import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuSearcher {
    // fields
    public static final String filePath = "./assignment3/menu.txt";
    private final static String appName = "The Caffeinated Geek";
    private static Menu menu;
    private final static String iconPath = "./assignment3/icon.png";
    static ImageIcon icon = new ImageIcon(iconPath);

    // create two sets to contain all available milk, extras
    private static final Set<Milk> availableMilk = new HashSet<>();
    private static final Set<String> availableExtras = new HashSet<>();

    //these Sets of Criteria control what beverage details are based - generates descriptions based on beverage type sought by user
    private static final Set<Criteria> coffeeFeatures = new LinkedHashSet<>(Arrays.asList(Criteria.NUMBER_OF_SHOTS, Criteria.MILK, Criteria.SUGAR, Criteria.EXTRAS));
    private static final Set<Criteria> teaFeatures = new LinkedHashSet<>(Arrays.asList(Criteria.TEMPERATURE, Criteria.STEEPING_TIME, Criteria.MILK, Criteria.SUGAR, Criteria.EXTRAS));

    private static TypeOfBeverage type;

    //a map containing the criteria:value pairs based on user input
    private static Map<Criteria,Object> criteria = new HashMap<>();

    //JFrame main window fields
    private static JFrame mainWindow=null; //main container
    private static JPanel searchView = null; //view 1
    //results view field/s
    private static JComboBox<String> optionsCombo = null;



    //EDIT 37 - the main method creates the main JFrame. It initialises it with the search view, sets
    //its minimum size and ensures that it exits on close.

    /**
     * main method initialises and creates main JFrame
     * @param args none required
     */
    public static void main(String[] args) throws IOException {
        // load all items from menu.txt to a Menu object
        menu = loadItems();
        mainWindow = new JFrame(appName);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setIconImage(icon.getImage());
        mainWindow.setMinimumSize(new Dimension(300,300));
        searchView=generateSearchView();
        mainWindow.setContentPane(searchView);
        mainWindow.pack();
        mainWindow.setVisible(true);


    }

    //this method creates a new instance of SearchView – it can be used when first
    //creating the search view, and when ‘refreshing’ it.
    /**
     * method used to 'refresh' or set to empty the fields in the search view
     * @return a searchCriteria object (with default user-input)
     */
    public static SearchView refreshSearchView(){
        return new SearchView(availableMilk,availableExtras);
    }

    //this method is used to create the final search JPanel, with N-E-S-W layout,
    //adding the SearchView object to the center, a ‘submit’ button on the bottom,
    //and padding on the left and right.
    /**
     * method to generate the search view of the app, by using the searchCriteria object
     * @return a JPanel representing the search view (with submit button) in N-E-S-W format
     */
    public static JPanel generateSearchView() throws IOException {
        //JPanel to contain search fields and button
        JPanel searchWindow = new JPanel();
        searchWindow.setLayout(new BorderLayout());
        //initialise the searchCriteria object with a fresh search view
        SearchView searchCriteria = refreshSearchView();
        JPanel searchCriteriaPanel = searchCriteria.generateSearchView();
        //add this panel to the main panel
        searchWindow.add(searchCriteriaPanel,BorderLayout.CENTER);
        //add a search button (which when clicked by the user leads to the database search)
        JButton search = new JButton("Search for matching beverages");
        ActionListener actionListener = e -> {
            try {
                conductSearch(searchCriteria);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }; //this will create a DreamBeverage object from user input and search the database with it
        search.addActionListener(actionListener);
        searchWindow.add(search,BorderLayout.SOUTH);
        //add padding and return the search window (view 1 in main frame)
        searchWindow.add(Box.createRigidArea(new Dimension(20,0)),BorderLayout.WEST);
        searchWindow.add(Box.createRigidArea(new Dimension(20,0)),BorderLayout.EAST);
        return searchWindow;
    }

    //EDIT 32: this method uses the SearchView getters to access the user’s choices. It populates a Map object
    //with these choices then creates a DreamBeverage object (as previously done). It concludes by searching
    //the menu database and calling the showResults method.
    /**
     * method used to extract user-entered data to create a DreamBeverage object which will be used to search the database of real beverages for a match
     * @param searchCriteria an instance of the SearchCriteria class (used to generate JPanels for user to enter/select filters)
     */
    public static void conductSearch(SearchView searchCriteria) throws IOException {
        type = searchCriteria.getTypeOfBeverage();
        if(type==TypeOfBeverage.SELECT_TYPE) {
            JOptionPane.showMessageDialog(mainWindow,"You MUST select a type of beverage.\n","Invalid search",JOptionPane.INFORMATION_MESSAGE,null);
            return;
        }
        criteria.put(Criteria.TYPE_OF_BEVERAGE,type);
        //add the user's selections to the map (if not NA)
        Milk milk = searchCriteria.getMilk();
        if(!milk.equals(Milk.I_DONT_MIND)) criteria.put(Criteria.MILK,milk);
        Sugar sugar = searchCriteria.getSugar();
        if(!sugar.equals(Sugar.I_DONT_MIND))criteria.put(Criteria.SUGAR,sugar);
        Set<String> chosenExtras = searchCriteria.getChosenExtras();
        if(!chosenExtras.isEmpty() && chosenExtras != null) {
            criteria.put(Criteria.EXTRAS,searchCriteria.getChosenExtras());
        } else if (chosenExtras == null) {
            criteria.put(Criteria.EXTRAS, null);
        }
        //get the user's price range selection
        int minPrice = searchCriteria.getMinPrice();
        int maxPrice = searchCriteria.getMaxPrice();

        //depending on the type of beverage chosen by the user, get the values of their filter entries
        if (type.equals(TypeOfBeverage.COFFEE)) {
            int numberOfShots = searchCriteria.getNumberOfShots();
            criteria.put(Criteria.NUMBER_OF_SHOTS, numberOfShots);
        }

        if(type.equals(TypeOfBeverage.TEA)){
            int temperature = searchCriteria.getTemperature();
            if (temperature > 0) criteria.put(Criteria.TEMPERATURE, temperature);
            int steepingTime = searchCriteria.getSteepingTime();
            System.out.println("steeping time of dream beverage is "+ steepingTime);
            if (steepingTime > 0) criteria.put(Criteria.STEEPING_TIME, steepingTime);
        }

        //create a DreamBeverage object, and use it to search the real beverage database
        DreamBeverage dreamBeverage = new DreamBeverage(minPrice,maxPrice,criteria);
        List<Beverage> potentialMatches = menu.findDreamBeverage(dreamBeverage);
        //pass the result into the showResultsView method
        showResults(potentialMatches);
    }

    //EDIT 33: this method displays all potential matches (if there are none, it informs the user via a dialog
    //box – see noResults). It calls generateBeverageDescriptions to display a non-editable text area containing
    //the beverage descriptions. It also uses selectFromResultsPanel to generate a dropdown list, allowing the user
    //to select a beverage of their choice, or alternatively, search again.
    /**
     * a method that brings together in a JPanel (view 2) the results, and user-selection options
     * @param potentialMatches an arraylist of beverage objects that match the user's selection criteria
     */
    public static void showResults(List<Beverage> potentialMatches) throws IOException {
        if(potentialMatches.size()==0){
            noResults();
            return;  //terminate the method if there are no results
        }
        //this is the overall panel (view 2) used to display the matching beverages and the dropdown list
        JPanel results = new JPanel();
        results.setLayout(new BorderLayout()); //N-E-S-W
        results.add(Box.createRigidArea(new Dimension(0,10)),BorderLayout.NORTH); //add padding to the top of the panel
        results.add(generateBeverageDescriptions(potentialMatches),BorderLayout.CENTER); //add the scroll pane - containing geek descriptions
        results.add(selectFromResultsPanel(potentialMatches),BorderLayout.SOUTH); //add the dropdown list and search again button to the bottom
        results.add(Box.createRigidArea(new Dimension(20,0)),BorderLayout.WEST); //add padding on the left/right sides of the panel
        results.add(Box.createRigidArea(new Dimension(20,0)),BorderLayout.EAST);
        mainWindow.setContentPane(results); //set main window (JFrame) to the results panel (view 2)
        mainWindow.revalidate();
    }


    //EDIT 34: rather than using JLabel to display the beverage info, this method uses
    //uneditable text areas, allowing for nicer formatting, and use of a scroll bar.
    /**
     * method to generate JScrollPane containing descriptions of matching beverages
     * @param potentialMatches an arraylist of beverage objects that match the user's selection criteria
     * @return JScrollPane a scroll pane containing a collection of non-editable JTextAreas each representing
     * a description of 1 matching beverage
     */
    public static JScrollPane generateBeverageDescriptions(List<Beverage> potentialMatches){
        //this array will contain all the user's options - a collection of beverage names they can choose from
        Map<String, Beverage> options = new HashMap<>();
//        Map<String,JLabel> optionsImages = loadImages(optionsImages(potentialMatches),imageSizeResultsView);

        //panel to contain one text area per beverage (each text area describes 1 beverage)
        JPanel beverageDescriptions = new JPanel();
        beverageDescriptions.setBorder(BorderFactory.createTitledBorder("Matches found!! The following beverages meet your criteria: "));
        beverageDescriptions.setLayout(new BoxLayout(beverageDescriptions,BoxLayout.Y_AXIS)); //stack vertically
        beverageDescriptions.add(Box.createRigidArea(new Dimension(0,10))); //padding

        //loop through the matches, generating a description of each
        for (Beverage beverage: potentialMatches) {
            JTextArea beverageDescription = new JTextArea("\n"+beverage.displayMenu());
            beverageDescription.setEditable(false); //ensure the description can't be edited!
            //this will ensure that if the description is long, it 'overflows'
            beverageDescription.setLineWrap(true);
            beverageDescription.setWrapStyleWord(true); //ensure words aren't broken across new lines

            //add the text panel to the main results panel
            beverageDescriptions.add(beverageDescription);
            beverageDescriptions.add(Box.createRigidArea(new Dimension(0,5)));

            options.put(beverage.getItemName(), beverage); //populate the array used for the dropdown list
        }
        //next, initialise the combo box with the beverage names (key set)
        optionsCombo = new JComboBox<>(options.keySet().toArray(new String[0]));


        //add a scroll pane to the results window, so that if there are many results, users can scroll as needed
        JScrollPane verticalScrollBar = new JScrollPane(beverageDescriptions);
        verticalScrollBar.setPreferredSize(new Dimension(300, 450));
        verticalScrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //this positions the scrollbar at the top (without it, the scrollbar loads part way through
        //adding of the text areas to the JPanel, resulting in the scrollbar being halfway down the results
        SwingUtilities.invokeLater(() -> verticalScrollBar.getViewport().setViewPosition( new Point(0, 0) ));
        return verticalScrollBar;
    }


    //EDIT 36: – this method generates a dropdown list of matching beverages, and a button that allows the user
    //to search again if they don’t like their results. It contains 2 action listeners – the first regenerates
    //the search view if the user chooses to search again, and the second displays a dialog box confirming their order
    //was taken if they select an option from the dropdown list.
    /**
     * @param potentialMatches an arraylist of beverage objects that match the user's selection criteria
     * method to generate dropdown list (of beverages) and search again button panel
     */
    public static JPanel selectFromResultsPanel(List<Beverage> potentialMatches){
        //give the user the option to search again if they don't like their results
        JLabel noneMessage = new JLabel("If none meet your criteria, close to exit, or search again with different criteria");
        JButton editSearchCriteriaButton = new JButton("Search again");
        ActionListener actionListenerEditCriteria = e -> {
            try {
                reGenerateSearchView();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
        editSearchCriteriaButton.addActionListener(actionListenerEditCriteria);

        //the user must choose from one of the real beverage - set the default string to instruct them
        String defaultOption = "Select beverage";
        optionsCombo.addItem(defaultOption);
        optionsCombo.setSelectedItem(defaultOption);
        //if the user selects an option, e.g. a beverage from the dropdown list, this action listener will pick up on it
        ActionListener actionListener = e -> checkUserSelection(potentialMatches);
        optionsCombo.addActionListener(actionListener);

        //create a panel for the button and the dropdown list - this has a flowlayout - left to right placement
        JPanel buttonOptionPanel = new JPanel();
        buttonOptionPanel.add(optionsCombo);
        buttonOptionPanel.add(editSearchCriteriaButton);

        //create and return a panel containing the panel with button/dropdown list, as well as a border/title and padding
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel,BoxLayout.Y_AXIS)); //stack vertically
        selectionPanel.add(Box.createRigidArea(new Dimension(0,10)));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Please select a beverage that you would like to order from the matching beverages list:"));
        selectionPanel.add(noneMessage);
        selectionPanel.add(buttonOptionPanel);
        selectionPanel.add(Box.createRigidArea(new Dimension(10,0)));
        return selectionPanel;
    }

    /**
     * method to reset the search view to blank, and reset the main frame to the search view
     */
    public static void reGenerateSearchView() throws IOException {
        searchView = generateSearchView();
        mainWindow.setContentPane(searchView);
        mainWindow.revalidate();
    }

    /**
     * a method used to generate a popup box informing the user that their search returned no results
     * resets the search view to blank
     */
    public static void noResults() throws IOException {
        JOptionPane.showMessageDialog(mainWindow,"Unfortunately your search returned no matching beverage.\n","No Matching Beverage",JOptionPane.INFORMATION_MESSAGE,icon);
        reGenerateSearchView();
    }

    /**
     * method used in action listener to write the order details of the chosen beverage, displaying a message confirming the order was taken
     * @param potentialMatches an ArrayList of compatible beverages (user can only select one of these beverages)
     */
    public static void checkUserSelection(List<Beverage> potentialMatches){
        String decision = (String) optionsCombo.getSelectedItem();
        assert decision != null; //we know it can't be null
        //if the user has selected a real beverage, see which one it is and return their details
        for (Beverage beverage : potentialMatches) {
            if (decision.equals(beverage.getItemName())) {
                Geek customer = getUserDetails();
                SearchView dreamBeverage = new SearchView(availableMilk, availableExtras);
                writeOrderToFile(customer, beverage, dreamBeverage);
                JOptionPane.showMessageDialog(mainWindow, "Thanks for placing an order. We will bring out your drink shortly.");
                break; //once the matching beverage is found, no need to keep looping
            }
        }
    }

    /**
     * a method that get the geek details from user input and store it in a Geek object
     * @return a Geek object that has all the details of the geek who wants to order a drink
     * this method was sourced and adapted from COSC120 Tutorial 7 pt3 FindAPet.java getUserDetails()
     */
    private static Geek getUserDetails(){

        String name;
        do{
            name = JOptionPane.showInputDialog("Please enter your full name (in format Firstname Surname): ");
            if(name==null) {
                JOptionPane.showMessageDialog(null, "Order process is cancelled.");
                System.exit(0);
            }
        } while(!isValidFullName(name));

        String phoneNumber;
        do{
            phoneNumber = JOptionPane.showInputDialog("Please enter your phone number (10-digit number in the format 0412345678): ");
            if(phoneNumber==null) {
                JOptionPane.showMessageDialog(null, "Order process is cancelled.");
                System.exit(0);
            }}
        while(!isValidPhoneNumber(phoneNumber));

        return new Geek(name, phoneNumber);
    }





    /**
     * a method to write the order details of a geek to a file, including the geek details (name, phone number) and the geek chosen coffee drink details
     * @param geek the Geek object that has the details of the geek who placed the order
     * @param chosenBeverage the Coffee object that has the details of the chosen coffee that the geek wants
     */
    private static void writeOrderToFile(Geek geek, Beverage chosenBeverage, SearchView dreamBeverage) {
        String filePath = "./assignment3/order_" + geek.phoneNumber() + ".txt";
        Path path = Path.of(filePath);
        /* Order details:
        Name: Dr. Walter Shepman
        Order number: 0486756465
        Item: Mocha (30213)
        Milk: Full-cream */

        String milkChoice = criteria.get(Criteria.MILK).toString();
        System.out.println(milkChoice);
        if (milkChoice == null) {
            milkChoice = "Whichever is ok.";
        } else {
            milkChoice = criteria.get(Criteria.MILK).toString();
        }
        String lineToWrite = "Order details:\n" +
                "Name: " + geek.name() + "\n" +
                "Order number: " + geek.phoneNumber() + "\n" +
                "Item: " + chosenBeverage.getItemName() + " (" + chosenBeverage.getItemId() + ")\n" +
                "Milk: " + milkChoice;

        try {
            Files.writeString(path, lineToWrite);
        }catch (IOException io){
            System.out.println("File could not be written. \nError message: "+io.getMessage());
            System.exit(0);
        }
    }

    //-------------------------------load data-----------------------------------------------------------

    /**
     * method to load all menu items from dataset menu.txt
     * @return a Menu object that has a list allBeverages containing all the beverages from the menu.txt dataset
     * this method was sourced and adapted from COSC120 Lecture 4 SeekAGeek.java loadGeeks
     */
    private static Menu loadItems() {
        Menu menu = new Menu();
        Path path = Path.of(filePath);

        List<String> itemData = null;
        try{
            itemData = Files.readAllLines(path);
        }catch (IOException io){
            System.out.println("The file could not be loaded. Check file path is correct. Terminating.\nError message: "+io.getMessage());
            System.exit(0);
        }

        for (int i=1;i<itemData.size();i++) {
            String[] elements = itemData.get(i).split("\\[");
            String[] itemInfo = elements[0].split(","); // [type, menu item ID, menu item name, price, numberOfShots, temperature, steeping time, sugar]
            TypeOfBeverage typeOfBeverage = null;
            try {
                String type = itemInfo[0].trim().toUpperCase();
                typeOfBeverage = TypeOfBeverage.valueOf(type);
            } catch (IllegalArgumentException e) {
                System.out.println("There is an error with the argument passed in the TypeOfBeverage.valueOf() method");
            }
            long itemId = -1;
            try {
                itemId = Long.parseLong(itemInfo[1]);
            } catch (NumberFormatException e) {
                System.out.println("There is an error trying to import a beverage item ID. Terminating.\nError message: "+e.getMessage());
                System.exit(0);
            }
            String itemName = itemInfo[2].trim();
            float price = -1;
            try {
                price = Float.parseFloat(itemInfo[3]);
            } catch (NumberFormatException e) {
                System.out.println("There is an error trying to import an item price. Terminating.\nError message: "+e.getMessage());
                System.exit(0);
            }
            int numberOfShots = -1;
            if (typeOfBeverage == TypeOfBeverage.COFFEE) {
                try {
                    numberOfShots = Integer.parseInt(itemInfo[4]);
                } catch (NumberFormatException e) {
                    System.out.println("There is an error trying to import an item number of shots. Terminating.\nError message: "+e.getMessage());
                    System.exit(0);
                }
            }
            int temperature = -1;
            int steepingTime = -1;
            if (typeOfBeverage == TypeOfBeverage.TEA) {
                try {
                    temperature = Integer.parseInt(itemInfo[5]);
                } catch (NumberFormatException e) {
                    System.out.println("There is an error trying to import a tea temperature. Terminating.\nError message: "+e.getMessage());
                    System.exit(0);
                }
                try {
                    steepingTime = Integer.parseInt(itemInfo[6]);
                } catch (NumberFormatException e) {
                    System.out.println("There is an error trying to import a tea steeping time. Terminating.\nError message: "+e.getMessage());
                    System.exit(0);
                }
            }
            Sugar sugar = null;
            try {
                sugar = Sugar.valueOf(itemInfo[7].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("There is an error with the argument passed in the Sugar.valueOf() method");
            }

            String[] milksString = elements[1].replace("],","").split(",");
            Set<Milk> milks = new HashSet<>();
            for (String milkString:milksString) {
                Milk milk = Milk.OAT;
                if (milkString.equals("Full-cream")) {
                    milk = Milk.valueOf("WHOLE");
                } else if (milkString.isEmpty()) {
                    milk = Milk.valueOf("NONE");
                } else {
                    milkString = milkString.trim().toUpperCase();
                    try {
                        milk = Milk.valueOf(milkString);
                    } catch (IllegalArgumentException e) {
                        System.out.println("There is an error with the argument milkString passed in the Milk.valueOf() method");
                    }

                }
                milks.add(milk);
            }

            String[] extraString = elements[2].replace("],","").trim().toLowerCase().split(",");
            Set<String> extras = new HashSet<>();
            for (String extra: extraString) {
                extra = extra.trim();
                extras.add(extra);
            }

            String description = elements[3].replace("]", "");

            Map<Criteria,Object> criteriaMap = new LinkedHashMap<>();
            criteriaMap.put(Criteria.TYPE_OF_BEVERAGE, typeOfBeverage);
            if (numberOfShots >= 0) criteriaMap.put(Criteria.NUMBER_OF_SHOTS, numberOfShots);
            if (temperature >= 0) criteriaMap.put(Criteria.TEMPERATURE, temperature);
            if (steepingTime >= 0) criteriaMap.put(Criteria.STEEPING_TIME, steepingTime);
            criteriaMap.put(Criteria.SUGAR, sugar);
            criteriaMap.put(Criteria.MILK, milks);
            criteriaMap.put(Criteria.EXTRAS, extras);

            DreamBeverage dreamBeverage = new DreamBeverage(criteriaMap);
            Beverage beverage = new Beverage(itemId, itemName, price, description, dreamBeverage);

            menu.addItem(beverage);
        }
        return menu;
    }

    /**
     * a regex matcher that ensures that the user's entry starts with a 0 and is followed by 9 digits
     * @param phoneNumber the candidate phone number entered by the user
     * @return true if phone number matches regex/false if not
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^0\\d{9}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * regex for full name in Firstname Surname format
     * @param fullName the geek full name entered by the geek
     * @return true if name matches regex/false if not
     * this method was sourced and adapted from COSC120 Lecture 4 SeekAGeek.java isValidFullName
     */
    public static boolean isValidFullName(String fullName) {
        String regex = "^[A-Z][a-z]+\\s[A-Z][a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fullName);
        return matcher.matches();
    }

}
