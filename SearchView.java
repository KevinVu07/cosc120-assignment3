/**
 * @author Dr Andreas Shepley (asheple2@une.edu.au | andreashepley01@gmail.com)
 * created for COSC120 (Trimester 1 2022)
 * last revised: Trimester 1 2024
 */

import javax.imageio.ImageIO;
import javax.lang.model.element.TypeElement;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class SearchView {

    //create a field to store the user’s type of beverage selection.
    private TypeOfBeverage typeOfBeverage;

    //create a CardLayout object
    private final CardLayout cardLayout  = new CardLayout();
    //create final String labels (constants) to associate with each JPanel that will act as a card
    private final String COFFEE_PANEL = "Coffee";
    private final String TEA_PANEL = "Tea";
    private final String EXTRA_CHOICE_PANEL = "Extra choice preference";
    private final String IMAGE_PANEL = "beverage images";
    //create a JPanel (container) field. Its layout will be set to CardLayout later,
    //so that it can act as a container of cards.
    private JPanel typeOfDreamBeverageSpecificCriteriaPanel;
    private JPanel extraSelectionPanel;
    private JPanel relevantExtra;


    //add fields to store user choices for sugar, milk
    private Sugar sugar;
    private Milk milk;

    //add two int vars for min and max price (and getters too).
    //Create another int var for default min price and int var for default price range (4).
    //Also add two fields that will act as feedback labels for the user
    private final int minDefaultPrice=0;
    private final int defaultPriceRange=10;
    private int minPrice = minDefaultPrice;
    private int maxPrice = minDefaultPrice+defaultPriceRange;
    private final JLabel feedbackMin = new JLabel(" "); //set to blank to start with
    private final JLabel feedbackMax = new JLabel(" ");

    //these are all the available milk options, extras options, obtained from the file
    private final Set<Milk> availableMilk;
    private final Set<String> availableExtras;
    private JList<String> moreThanOneExtraList;
    private JLabel moreThanOneExtraInstruction;
    private JComboBox<Extra> extraChoiceSelection;
    private Extra extraChoice;

    //create fields to store the user’s choices
    private Set<String> chosenExtras;
    private int numberOfShots;
    private int steepingTime;
    private int temperature;

    //finally, create a constructor that will be used to instantiate this class, initializing the available milk,
    //extras data structures to the values derived from the menu.txt file.
    //These will be used to populate the milk, extras JLists.
    /**
     * constructor used to initialise the search view
     * @param availableMilk all the milk options the user can choose from - derived from menu.txt
     * @param availableExtras all the extra options the user can choose from - derived from menu.txt
     */
    public SearchView(Set<Milk> availableMilk, Set<String> availableExtras){
        this.availableMilk = availableMilk;
        this.availableExtras = availableExtras;
        //also initialise the user-choice data structures to empty
        this.chosenExtras = new HashSet<>();
    }

    // this method will bring together all the search panels in the class, placing the ‘type’ dropdown
    //list at the top of the search view, followed by the generic criteria panel. It then sets the layout manager
    //of the panel that varies based on type to CardLayout, adding all the cards (JPanels) to this ‘container’ JPanel.
    /**
     * a method to generate a JPanel that represents the search view
     * also initialises the variable JPanel layout to CardLayout, enabling 'swapping out' of cards based on relationship type
     * @return the JPanel described.
     */
    public JPanel generateSearchView() throws IOException {
        //create a new JPanel to contain the other JPanels
        JPanel criteria = new JPanel();
        //this will stack the sub-containers vertically
        criteria.setLayout(new BoxLayout(criteria,BoxLayout.Y_AXIS));
        //add the type of beverage, and generic filters to the panel, along with some padding

        JPanel typeOfBeveragePanel = this.userInputTypeOfBeverage();
        typeOfBeveragePanel.setAlignmentX(0);
        criteria.add(typeOfBeveragePanel);
        JPanel generic = this.userInputGenericCriteria();
        generic.setAlignmentX(0);
        criteria.add(generic);

        criteria.add(Box.createRigidArea(new Dimension(0,20)));

        //initialise the JPanel that is to contain the beverage-specific filters
        typeOfDreamBeverageSpecificCriteriaPanel = new JPanel();
        //set the layout to cardLayout, then add all the beverage-specific panels to it (we'll switch between the panels as we need them)
        //the string constants are necessary to keep track of the cards
        typeOfDreamBeverageSpecificCriteriaPanel.setAlignmentX(0);
        typeOfDreamBeverageSpecificCriteriaPanel.setAlignmentY(0);
        typeOfDreamBeverageSpecificCriteriaPanel.setLayout(cardLayout);
        typeOfDreamBeverageSpecificCriteriaPanel.add(this.generateImagePanel(),IMAGE_PANEL);
        typeOfDreamBeverageSpecificCriteriaPanel.add(this.userInputCoffee(),COFFEE_PANEL);
        typeOfDreamBeverageSpecificCriteriaPanel.add(this.userInputTea(),TEA_PANEL);
        //add the beverage-specific panel to the main search panel and return it
        criteria.add(typeOfDreamBeverageSpecificCriteriaPanel);

        extraSelectionPanel = new JPanel();
        extraSelectionPanel.setAlignmentX(0);
        extraSelectionPanel.add(this.userInputExtraChoice());
        criteria.add(extraSelectionPanel);
//        extraSelectionPanel.setVisible(false);
        return criteria;
    }

    //EDIT 3: this method should return a JPanel that enables users to select the type of
    //relationship they’re after from a dropdown list.
    /**
     * a method to populate a dropdown list with types of relationships, prompt user selection, and listen for user selection of a type
     * @return a JPanel containing instruction to user and type of relationship dropdown list
     */
    public JPanel userInputTypeOfBeverage(){
        //create a combo box (drop-down list), populating it with the types of beverages available to the user
        JComboBox<TypeOfBeverage> typeOfDreamBeverageJComboBox = new JComboBox<>(TypeOfBeverage.values());
        //set the program focus on this combo-box - selecting the type of beverage should be the user's first step
        typeOfDreamBeverageJComboBox.requestFocusInWindow();
        //set the 'selected item' to SELECT_TYPE to prompt the user to select a type of relationship
        typeOfDreamBeverageJComboBox.setSelectedItem(TypeOfBeverage.SELECT_TYPE);
        //initialise the data field
        typeOfBeverage = (TypeOfBeverage) typeOfDreamBeverageJComboBox.getSelectedItem();
        //Check for changes in user selection, by adding an ItemListener to the dropdown list.
        //This should point to a method (ifTypeSelected), which handles the program's response when a type is selected.
        typeOfDreamBeverageJComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) ifTypeSelected(typeOfDreamBeverageJComboBox);
        });
        //create and return a new JPanel to contain the padding, instructional label and dropdown list
        JPanel typeOfBeverageSelectionPanel = new JPanel();
        typeOfBeverageSelectionPanel.setLayout(new BoxLayout(typeOfBeverageSelectionPanel,BoxLayout.Y_AXIS));
        typeOfBeverageSelectionPanel.add(Box.createRigidArea(new Dimension(0,20)));
        typeOfBeverageSelectionPanel.add(typeOfDreamBeverageJComboBox);
        typeOfBeverageSelectionPanel.add(Box.createRigidArea(new Dimension(0,20)));
        return typeOfBeverageSelectionPanel;
    }

    //This method should handle the program’s response to a user selecting which type of beverage they want.
    /**
     * method called if user selects type of beverage - used to display the appropriate search criteria panel
     * @param typeOfDreamBeverageJComboBox the dropdown list from which user has selected a type of beverage
     */
    public void ifTypeSelected(JComboBox<TypeOfBeverage> typeOfDreamBeverageJComboBox){
        //It should first update the typeOfBeverage field
        typeOfBeverage = (TypeOfBeverage) typeOfDreamBeverageJComboBox.getSelectedItem();
        assert typeOfBeverage != null; //we know that typeOfDreamBeverage won't be null
        //use the CardLayout object to show the appropriate JPanel 'card' based on the user's dropdown list choice
        //you can then switch between the cards based on the user’s selection, using the show method.
        if (typeOfBeverage.equals(TypeOfBeverage.SELECT_TYPE)) cardLayout.show(typeOfDreamBeverageSpecificCriteriaPanel,IMAGE_PANEL);
        else {
            extraChoiceSelection.setSelectedItem(Extra.SELECT_EXTRA_PREFERENCE);
            extraSelectionPanel.setVisible(true);
            if (typeOfBeverage.equals(TypeOfBeverage.TEA)) {
                cardLayout.show(typeOfDreamBeverageSpecificCriteriaPanel,TEA_PANEL);
            } else if(typeOfBeverage.equals(TypeOfBeverage.COFFEE)) cardLayout.show(typeOfDreamBeverageSpecificCriteriaPanel,COFFEE_PANEL);
        }
        }


    //this method creates a ButtonGroup, adding 3 radio buttons to it, representing yes,
    //no, or I don't mind.  These buttons are added to a JPanel, which is returned.
    /**
     * a method that allows users to select sugar, from options - yes, no, or I don't mind
     * @return a JPanel of radio buttons from which users can select only one option
     */
    public JPanel userInputSugar(){
        //the button group ensures only one button is selected
        ButtonGroup sugarButtonGroup = new ButtonGroup();
        //create one button for each option
        JRadioButton yes = new JRadioButton(Sugar.YES.toString(), true); // sugar selected to yes by default
        JRadioButton no = new JRadioButton(Sugar.NO.toString());
        JRadioButton skip = new JRadioButton(Sugar.I_DONT_MIND.toString());
        yes.requestFocusInWindow();
        sugar = Sugar.I_DONT_MIND;
        //add the buttons to the group
        sugarButtonGroup.add(yes);
        sugarButtonGroup.add(no);
        sugarButtonGroup.add(skip);
        //set the action command - value when clicked
        yes.setActionCommand(Sugar.YES.name());
        no.setActionCommand(Sugar.NO.name());
        skip.setActionCommand(Sugar.I_DONT_MIND.name());
        //add an action listener to each button, where the action listener updates the sugar field if a user changes their selection
        ActionListener actionListener = e-> sugar = Sugar.valueOf(sugarButtonGroup.getSelection().getActionCommand().toUpperCase());
        yes.addActionListener(actionListener);
        no.addActionListener(actionListener);
        skip.addActionListener(actionListener);

        //create and return a new JPanel (add all the buttons to it first)
        JPanel sugarPanel = new JPanel();
        sugarPanel.setAlignmentX(0);
        sugarPanel.setBorder(BorderFactory.createTitledBorder("Would you like sugar with your drink?"));
        sugarPanel.add(yes);
        sugarPanel.add(no);
        sugarPanel.add(skip);

        return sugarPanel;
    }


    //this method creates a dropdown list of milk, allowing the user to select ONE option.
    //It uses an ItemListener to detect changes in user selection, updating the field accordingly.
    //It returns a JPanel containing an instruction, and the dropdown list.
    /**
     * a method that allows the user to select their preferred milk option
     * @return a JPanel containing a dropdown list and instructional JLabel
     */
    public JPanel userInputMilk(){
        //a dropdown list of all milk options
        JComboBox<Milk> milkJComboBox = new JComboBox<>(Milk.values());
        milkJComboBox.setAlignmentX(0);
        //let's assume the user prefers full cream
        milkJComboBox.setSelectedItem(Milk.WHOLE);
        //initialise the field
        milk = (Milk) milkJComboBox.getSelectedItem();
        //update the field if user changes selection
        milkJComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                milk = (Milk) milkJComboBox.getSelectedItem();
            }
        });
        //create and return JPanel containing instruction, and dropdown list and padding
        JPanel milkPanel = new JPanel();
        milkPanel.setLayout(new BoxLayout(milkPanel,BoxLayout.Y_AXIS));
        milkPanel.setAlignmentX(0);
        milkPanel.add(Box.createRigidArea(new Dimension(0,5)));
        JLabel instruction = new JLabel("Which milk would you like with your drink?");
        instruction.setAlignmentX(0);
        milkPanel.add(instruction);
        milkPanel.add(milkJComboBox);
        milkPanel.add(Box.createRigidArea(new Dimension(0,5)));
        return milkPanel;
    }

    /**
     * method to load all the extra options from the dataset for a specific type of beverage
     * @param type type of beverage to load the extra options for (coffee or tea)
     * @return a set of extra options (string) that the Caffeinated Geek offers for that specific type of beverage
     */
    private static Set<String> loadExtraOptions(String type) {
        Path path = Path.of(MenuSearcher.filePath);

        List<String> itemData = null;
        try {
            itemData = Files.readAllLines(path);
        } catch (IOException io) {
            System.out.println("The file could not be loaded. Check file path is correct. Terminating.\nError message: " + io.getMessage());
            System.exit(0);
        }

        Set<String> extraOptions = new LinkedHashSet<>();
        for (int i = 1; i < itemData.size(); i++) {
            String[] elements = itemData.get(i).split("\\[");
            String[] extras = elements[2].replace("],", "").trim().toLowerCase().split(",");
            for (String extra : extras) {
                extra = extra.trim();
                // check if type of beverage is coffee or tea, and then add the extra option to the coffee or tea extras hash sets accordingly
                String[] itemInfo = elements[0].split(",");
                String typeOfBeverage = itemInfo[0].toUpperCase();
                if (TypeOfBeverage.valueOf(typeOfBeverage).toString().equalsIgnoreCase(type) && !extra.isBlank()) {
                    extraOptions.add(extra);
                }
            }
        }
        return extraOptions;
    }

    public JPanel userInputExtraChoice() {
        // Let user have options to select NONE, ONE, MORE THAN ONE and SKIP for extras.
        extraChoiceSelection = new JComboBox<>(Extra.values());
        extraChoiceSelection.setAlignmentX(0);
        extraChoiceSelection.setPreferredSize(new Dimension(150,30)); //sizes the dropdown list
        extraChoiceSelection.requestFocusInWindow();
        //this prevents the dropdown list from automatically selecting extra preference for user
        extraChoiceSelection.setSelectedItem(Extra.SELECT_EXTRA_PREFERENCE);
        extraChoice = Extra.SELECT_EXTRA_PREFERENCE; // initialize the user's extra choice selection to the dummy value
        extraChoiceSelection.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) onExtraChoiceSelected();
        });

        //create and return JPanel containing instruction, and dropdown list and padding
        JPanel extraChoicePanel = new JPanel();
        extraChoicePanel.setLayout(new BoxLayout(extraChoicePanel,BoxLayout.Y_AXIS));
        extraChoicePanel.setAlignmentX(0);
        extraChoicePanel.add(Box.createRigidArea(new Dimension(0,5)));
        JLabel instruction = new JLabel("Would you like to have extra/s with your drink (More than one, None, or Skip / I don't mind)?");
        instruction.setAlignmentX(0);
        extraChoicePanel.add(instruction);
        extraChoicePanel.add(extraChoiceSelection);
        extraChoicePanel.add(Box.createRigidArea(new Dimension(0,5)));
        return extraChoicePanel;
    }

    //this method creates a JList of all the extras options, allowing the user to select one or more values.
    //It restricts the visible extras options to three values but has a scroll bar to enable users to see all available options.
    /**
     * a method that allows the user to select preferred extras for their drink (or SKIP)
     * @return a JPanel containing a dropdown list and instructional JLabel
     */
    public JPanel userInputExtras(TypeOfBeverage typeOfBeverage){
        String type = typeOfBeverage.toString();
        JPanel moreThanOneExtraChoicePanel = new JPanel();

        if (!type.equals(TypeOfBeverage.SELECT_TYPE)) {
            // get the extra option set for the type of beverage
            Set<String> extraOptionSet = loadExtraOptions(type);
            // convert extra set to extra array
            String[] extraOptionArray = extraOptionSet.stream().toArray(String[] ::new);
            System.out.println("Extra options for " + typeOfBeverage + " is" + Arrays.toString(extraOptionArray));

            //Create a JList of all the extras
            moreThanOneExtraList = new JList<>(extraOptionArray);
            //enable multi-selection
            moreThanOneExtraList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            //create a scroll pane to limit the visible size of the JList and enable scrolling
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(moreThanOneExtraList);
            moreThanOneExtraList.setLayoutOrientation(JList.VERTICAL); //vertical scrollbar
            //set the size of the scroll pane
            scrollPane.setPreferredSize(new Dimension(250, 150));
            //always show the scrollbar
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            //set the position of the scroll bar  to the top of the scrollable area
            SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition( new Point(0, 0) ));

            //update the extra field if the user selects a new item
            ListSelectionListener listSelectionListener = l-> chosenExtras = new HashSet<>(moreThanOneExtraList.getSelectedValuesList());
            moreThanOneExtraList.addListSelectionListener(listSelectionListener);



            //add the dropdown list, and an instructional JLabel to a panel and return it
            moreThanOneExtraChoicePanel.setLayout(new BoxLayout(moreThanOneExtraChoicePanel,BoxLayout.Y_AXIS));
            moreThanOneExtraChoicePanel.add(Box.createRigidArea(new Dimension(0,5)));
            moreThanOneExtraInstruction = new JLabel("Which extra/s would you like for your drink?");
            moreThanOneExtraInstruction.setAlignmentX(0);
            moreThanOneExtraChoicePanel.add(moreThanOneExtraInstruction);
            JLabel clarification = new JLabel("(To multi-select, hold Ctrl)");
            clarification.setAlignmentX(0);
            clarification.setFont(new Font("", Font. ITALIC, 12));
            moreThanOneExtraChoicePanel.add(clarification);
            scrollPane.setAlignmentX(0);
            moreThanOneExtraChoicePanel.add(scrollPane); //add the scrollable area to your JPanel as usual
            moreThanOneExtraChoicePanel.add(Box.createRigidArea(new Dimension(0,5)));

//            // disable more than one extra lists as long as the user not choosing one or more extra option
//            if (!extraChoice.equals(Extra.ONE_OR_MORE)) {
//                moreThanOneExtraInstruction.setVisible(false);
//                moreThanOneExtraList.setVisible(false);
//            }
        }
        return moreThanOneExtraChoicePanel;
    }

    /**
     * This method handles the situation where the user selects an option for extra (None, One or more, Skip).
     * It populates the extras list for one or more or hides the list for none and skip.
     */
    private void onExtraChoiceSelected() {
        // set the field selected extra choice to the user's choice
        extraChoice = (Extra) extraChoiceSelection.getSelectedItem();
        System.out.println("Selected Extra: " + extraChoice); // Debug statement
        assert extraChoice != null; // we know it isn't null
        if (relevantExtra != null) {
            extraSelectionPanel.remove(relevantExtra);
        }
//        Set<String> relevantExtraOptions = null;
        if (extraChoice.equals(Extra.ONE_OR_MORE) && !typeOfBeverage.equals(TypeOfBeverage.SELECT_TYPE)) {
            relevantExtra = this.userInputExtras(typeOfBeverage);
            chosenExtras = null;
            extraSelectionPanel.add(relevantExtra);
            extraSelectionPanel.revalidate();
            extraSelectionPanel.repaint();
            MenuSearcher.mainWindow.pack();
//            moreThanOneExtraInstruction.setVisible(true);
//            moreThanOneExtraList.setVisible(true);
        } else {
            if (relevantExtra != null) {
                relevantExtra.setEnabled(false);
            }
            extraSelectionPanel.revalidate();
            extraSelectionPanel.repaint();
//            moreThanOneExtraInstruction.setVisible(false);
//            moreThanOneExtraList.setVisible(false);
            if (extraChoice.equals(Extra.SKIP) && chosenExtras != null) {
                chosenExtras.clear(); // clear selected extras if NONE is selected
            } else if (extraChoice.equals(Extra.NONE)) {
                chosenExtras = null;
            }
        }
//        String[] relevantExtraOptionArray = relevantExtraOptions.stream().toArray(String[] ::new);
//        //Create a JList of all the extras
//        moreThanOneExtraList = new JList<>(relevantExtraOptionArray);
////        enable multi-selection
//        moreThanOneExtraList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    //this method will allow the user to enter a min price >= 0, and a max price >= to the min price.
    //It will provide feedback to the user as they type, using a DocumentListener.
    /**
     * method used to get and validate user input for price range
     * this is a very long method - perhaps the functionality of the action listeners could be delegated to one or two methods?
     * @return a JPanel containing instructions, text fields for price input and feedback for validation
     */
    public JPanel getUserInputPriceRange(){
        //labels for the text boxes
        JLabel minLabel = new JLabel("Min. price");
        JLabel maxLabel = new JLabel("Max. price");
        //create text boxes...
        JTextField min = new JTextField(4);
        JTextField max = new JTextField(4);
        //set default values for the age range text boxes (editable)
        min.setText(String.valueOf(minPrice));
        max.setText(String.valueOf(maxPrice));

        //this is how you change the font and size of text
        feedbackMin.setFont(new Font("", Font. ITALIC, 12));
        feedbackMin.setForeground(Color.RED);
        feedbackMax.setFont(new Font("", Font. ITALIC, 12));
        feedbackMax.setForeground(Color.RED);

        //let’s add a document listener to the min and max price text fields.
        //You will see that the insertUpdate, removeUpdate and changedUpdate method declarations will
        //be automatically added. You must implement these (or leave them blank).
        //We’ll implement the first two, so that whenever the user enters or removes text from the fields,
        //we check whether the contents are valid.
        min.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                //if the check min method returns false, request user addresses invalid input
                if(!checkMin(min)) min.requestFocus();
                checkMax(max); //after min has been updated, check max is still valid
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                //removing and inserting should be subjected to the same checks
                if(!checkMin(min))min.requestFocus();
                checkMax(max);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {} //NA
        });
        max.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!checkMax(max)) max.requestFocusInWindow();
                checkMin(min);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!checkMax(max))max.requestFocusInWindow();
                checkMin(min);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        //add the text fields and labels to a panel
        JPanel priceRangePanel = new JPanel(); //flowlayout by default
        priceRangePanel.add(minLabel);
        priceRangePanel.add(min);
        priceRangePanel.add(maxLabel);
        priceRangePanel.add(max);

        JPanel finalPanel = new JPanel();
        finalPanel.setBorder(BorderFactory.createTitledBorder("Enter desired price range"));
        finalPanel.setLayout(new BoxLayout(finalPanel,BoxLayout.Y_AXIS)); //stack elements vertically
        finalPanel.setAlignmentX(0);
        finalPanel.add(priceRangePanel);
        feedbackMin.setAlignmentX(0);
        feedbackMax.setAlignmentX(0);
        finalPanel.add(feedbackMin); //feedback below age entry text boxes
        finalPanel.add(feedbackMax);

        return finalPanel;
    }

    //EDIT 16: to minimize code repetition, let’s outsource the task of validating the user entry to these two methods.
    //They will return true if input is valid, and false if it isn’t. If input is not valid, we will request that
    //the user changes their input.
    /**
     * validates user input for min price
     * @param minEntry the JTextField used to enter min price
     * @return true if valid price, false if invalid
     */
    private boolean checkMin(JTextField minEntry){
        feedbackMin.setText("");
        try{
            int tempMin = Integer.parseInt(minEntry.getText());
            if(tempMin < minDefaultPrice || tempMin> maxPrice) {
                feedbackMin.setText("Min price must be >= "+minDefaultPrice+" and <= "+ maxPrice +". Defaulting to "+minPrice+" - "+ maxPrice +".");
                minEntry.selectAll();
                return false;
            }else {
                minPrice=tempMin;
                feedbackMin.setText("");
                return true;
            }
        }catch (NumberFormatException n){
            feedbackMin.setText("Please enter a valid number for min price. Defaulting to "+minPrice+" - "+ maxPrice +".");
            minEntry.selectAll();
            return false;
        }
    }

    /**
     * validates user input for max price
     * @param maxEntry the JTextField used to enter max price
     * @return true if valid price, false if invalid
     */
    private boolean checkMax(JTextField maxEntry){
        feedbackMax.setText("");
        try{
            int tempMax = Integer.parseInt(maxEntry.getText());
            if(tempMax < minPrice) {
                feedbackMax.setText("Max price must be >= min price. Defaulting to "+minPrice+" - "+ maxPrice +".");
                maxEntry.selectAll();
                return false;
            }else {
                maxPrice = tempMax;
                feedbackMax.setText("");
                return true;
            }
        }catch (NumberFormatException n){
            feedbackMax.setText("Please enter a valid number for max price. Defaulting to "+minPrice+" - "+ maxPrice +".");
            maxEntry.selectAll();
            return false;
        }
    }


    /*--------------------coffee type specific panels------------------*/

    //Next, let’s add in functionality to filter based on the coffee type. This will involve
    //created one JLists, to allow users to choose one number of shot option

    //EDIT 19: this method will act as a helper method, allowing a JList passed in as a
    //parameter to be added to a scroll pane.
    public JPanel userInputNumberOfShots(){
        //a dropdown list of all number of shots options
        Integer[] numberOfShotsOptions = {1, 2, 3};
        JComboBox<Integer> numberOfShotsJComboBox = new JComboBox<>(numberOfShotsOptions);
        numberOfShotsJComboBox.setAlignmentX(0);
        //let's assume the user prefers 1 shot
        numberOfShotsJComboBox.setSelectedItem(1);
        //initialise the field
        numberOfShots = (Integer) numberOfShotsJComboBox.getSelectedItem();
        //update the field if user changes selection
        numberOfShotsJComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                numberOfShots = (Integer) numberOfShotsJComboBox.getSelectedItem();
            }
        });
        //create and return JPanel containing instruction, and dropdown list and padding
        JPanel numberOfShotsPanel = new JPanel();
        numberOfShotsPanel.setLayout(new BoxLayout(numberOfShotsPanel,BoxLayout.Y_AXIS));
        numberOfShotsPanel.setAlignmentX(0);
        numberOfShotsPanel.add(Box.createRigidArea(new Dimension(0,5)));
        JLabel instruction = new JLabel("How many shots of coffee would you like with your drink?");
        instruction.setAlignmentX(0);
        numberOfShotsPanel.add(instruction);
        numberOfShotsPanel.add(numberOfShotsJComboBox);
        numberOfShotsPanel.add(Box.createRigidArea(new Dimension(0,5)));
        return numberOfShotsPanel;
    }


    public JPanel userInputCoffeeExtras(){
        TypeOfBeverage type = TypeOfBeverage.COFFEE;
        JPanel extrasPanel = new JPanel();
        extrasPanel = userInputExtras(type);
        return extrasPanel;
    }

    /**
     * @return a JPanel containing all search parameters for coffee type
     */
    public JPanel userInputCoffee(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        jPanel.setAlignmentX(0);
        jPanel.add(Box.createRigidArea(new Dimension(0,30)));
        JPanel numberOfShots = userInputNumberOfShots();
        numberOfShots.setAlignmentX(0);
        jPanel.add(numberOfShots);
        jPanel.add(Box.createRigidArea(new Dimension(0,30)));
//        JPanel extras = userInputCoffeeExtras();
//        extras.setAlignmentX(0);
//        jPanel.add(extras);
//        jPanel.add(Box.createRigidArea(new Dimension(0,30)));
        return jPanel;
    }


    //Next, let’s add in functionality to filter based on the tea type.
    //namely temperature and steeping time, also a JComboBox and user can only select one option (or SKIP)

    public JPanel userInputTemperature(){
        //a dropdown list of all temperature options
        String[] tempOptions = {"80 degrees: For a mellow, gentler taste", "85 degrees: For slightly sharper than mellow", "90 degrees: Balanced, strong but not too strong", "95 degrees: Strong, but not acidic", "100 degrees: For a bold, strong flavour", "I don't mind"};
        JComboBox<String> temperatureJComboBox = new JComboBox<>(tempOptions);
        temperatureJComboBox.setAlignmentX(0);
        //let's assume the user prefers 80 degrees
        temperatureJComboBox.setSelectedItem("80 degrees: For a mellow, gentler taste");
        //initialise the field
        String response = (String) temperatureJComboBox.getSelectedItem();
        if (response.equals("I don't mind")) {
            temperature = -1;
        } else {
            temperature = convertTemperatureToInt(response);
        }
        //update the field if user changes selection
        temperatureJComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String newResponse = (String) temperatureJComboBox.getSelectedItem();
                if (newResponse.equals("I don't mind")) {
                    temperature = -1;
                } else {
                    temperature = convertTemperatureToInt(newResponse);
                }
            }
        });
        //create and return JPanel containing instruction, and dropdown list and padding
        JPanel temperaturePanel = new JPanel();
        temperaturePanel.setLayout(new BoxLayout(temperaturePanel,BoxLayout.Y_AXIS));
        temperaturePanel.setAlignmentX(0);
        temperaturePanel.add(Box.createRigidArea(new Dimension(0,5)));
        JLabel instruction = new JLabel("Which temperature would you like your drink to be at?");
        instruction.setAlignmentX(0);
        temperaturePanel.add(instruction);
        temperaturePanel.add(temperatureJComboBox);
        temperaturePanel.add(Box.createRigidArea(new Dimension(0,5)));
        return temperaturePanel;
    }

    public int convertTemperatureToInt(String response) {
        int temperature = -1;
        switch (response) {
            case "80 degrees: For a mellow, gentler taste" -> temperature = 80;
            case "85 degrees: For slightly sharper than mellow" -> temperature = 85;
            case "90 degrees: Balanced, strong but not too strong" -> temperature = 90;
            case "95 degrees: Strong, but not acidic" -> temperature = 95;
            case "100 degrees: For a bold, strong flavour" -> temperature = 100;
        }
        return temperature;
    }

    public JPanel userInputSteepingTime(){
        //a dropdown list of all steeping time options
        String[] steepingTimeOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "I don't mind"};
        JComboBox<String> steepingTimeJComboBox = new JComboBox<>(steepingTimeOptions);
        steepingTimeJComboBox.setAlignmentX(0);
        //let's assume the user prefers 1 minutes
        steepingTimeJComboBox.setSelectedItem("1");
        //initialise the field
        String response = (String) steepingTimeJComboBox.getSelectedItem();
        if (response.equals("I don't mind")) {
            steepingTime = -1;
        } else {
            steepingTime = Integer.parseInt(response);
        }
        //update the field if user changes selection
        steepingTimeJComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String newResponse = (String) steepingTimeJComboBox.getSelectedItem();
                if (newResponse.equals("I don't mind")) {
                    steepingTime = -1;
                } else {
                    steepingTime = Integer.parseInt(newResponse);
                }
            }
        });
        //create and return JPanel containing instruction, and dropdown list and padding
        JPanel steepingTimePanel = new JPanel();
        steepingTimePanel.setLayout(new BoxLayout(steepingTimePanel,BoxLayout.Y_AXIS));
        steepingTimePanel.setAlignmentX(0);
        steepingTimePanel.add(Box.createRigidArea(new Dimension(0,5)));
        JLabel instruction = new JLabel("How long would you like the steeping time to be for your drink (minutes)?");
        instruction.setAlignmentX(0);
        steepingTimePanel.add(instruction);
        steepingTimePanel.add(steepingTimeJComboBox);
        steepingTimePanel.add(Box.createRigidArea(new Dimension(0,5)));
        return steepingTimePanel;
    }

    public JPanel userInputTeaExtras(){
        TypeOfBeverage type = TypeOfBeverage.TEA;
        JPanel extrasPanel = new JPanel();
        extrasPanel = userInputExtras(type);
        return extrasPanel;
    }


    //this method will create a JPanel adding each of the panels returned the above two methods.
    /**
     * @return a JPanel containing all search parameters for tea type
     */
    public JPanel userInputTea(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        jPanel.setAlignmentX(0);
        jPanel.add(Box.createRigidArea(new Dimension(0,30)));
        JPanel temperature = userInputTemperature();
        temperature.setAlignmentX(0);
        jPanel.add(temperature);
        jPanel.add(Box.createRigidArea(new Dimension(0,30)));
        JPanel steepingTime = userInputSteepingTime();
        steepingTime.setAlignmentX(0);
        jPanel.add(steepingTime);
        jPanel.add(Box.createRigidArea(new Dimension(0,30)));
//        JPanel extras = userInputTeaExtras();
//        extras.setAlignmentX(0);
//        jPanel.add(extras);
//        jPanel.add(Box.createRigidArea(new Dimension(0,30)));
        return jPanel;
    }




    /*--------------------overall panels------------------*/

    //add all the general search filter panels (milk, extras, sugar and price range) into one larger
    //panel and return it. This panel will be displayed regardless of the type of beverage selected by the user.
    /**
     * method to bring together the JPanels containing generic search criteria, i.e. milk, extras, sugar
     * @return a JPanel containing sub-JPanels for each generic criteria
     */
    public JPanel userInputGenericCriteria(){
        JPanel genericCriteria = new JPanel();
        genericCriteria.setLayout(new BoxLayout(genericCriteria,BoxLayout.Y_AXIS));
        //use the methods below to generate the sub-panels
        genericCriteria.add(this.getUserInputPriceRange());
        genericCriteria.add(this.userInputSugar());
        genericCriteria.add(this.userInputMilk());
//        genericCriteria.add(this.userInputExtraChoice());
//        genericCriteria.add(this.userInputExtras(typeOfBeverage));
        return genericCriteria;
    }

    //EDIT 18: this method combines two beverages images representing the types of beverage.
    //This is the first card in the CardLayout JPanel (typeOfDreamBeverageSpecificCriteriaPanel).
    /**
     * a method to generate a panel of 2 images representing the 2 types of beverage
     * @return the described JPanel
     */
    public JPanel generateImagePanel() throws IOException {
        //load the 2 images as JLabels
        // Photo by Fahmi Fakhrudin on Unsplash
        String coffeeImagePath = "./assignment3/images/coffee.jpeg";
        BufferedImage coffeeImg = ImageIO.read(new File(coffeeImagePath));
        Image resizeCoffeeImg = coffeeImg.getScaledInstance(128, 128, Image.SCALE_DEFAULT);
        JLabel coffee = new JLabel(new ImageIcon(resizeCoffeeImg));
        // Photo by CHI CHEN on Unsplash
        String teaImagePath = "./assignment3/images/tea.jpeg";
        BufferedImage teaImg = ImageIO.read(new File(teaImagePath));
        Image resizeTeaImg = teaImg.getScaledInstance(128, 128, Image.SCALE_DEFAULT);
        JLabel tea = new JLabel(new ImageIcon(resizeTeaImg));
        //create a new container panel, add the 2 images to it and return the panel
        JPanel imagePanel = new JPanel();
        imagePanel.add(coffee);
        imagePanel.add(tea);
        return imagePanel;
    }



    /*--------------------getters------------------*/
    //Create a getter for typeOfDreamBeverage. This is how the program accesses the user's selection
    /**
     * @return the user's type of beverage selection
     */
    public TypeOfBeverage getTypeOfBeverage() {
        return typeOfBeverage;
    }

    //add getters to access user choices for milk, sugar and extras


    public Sugar getSugar() {
        return sugar;
    }

    public Milk getMilk() {
        return milk;
    }

    public Set<String> getChosenExtras() {
        return chosenExtras;
    }

    public int getNumberOfShots() {
        return numberOfShots;
    }

    public int getSteepingTime() {
        return steepingTime;
    }

    public int getTemperature() {
        return temperature;
    }


//create two getters for min and max price
    /**
     * @return the user's desired min price
     */
    public int getMinPrice() {
        return minPrice;
    }
    /**
     * @return the user's desired max price
     */
    public int getMaxPrice() {
        return maxPrice;
    }
    }





