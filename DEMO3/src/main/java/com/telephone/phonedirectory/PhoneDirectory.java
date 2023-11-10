package com.telephone.phonedirectory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing a telephone directory application.
 */
public class PhoneDirectory extends Application {

    private static final Logger logger = LogManager.getLogger(PhoneDirectory.class);

    private static final String DATA_FILE = "phone_directory.dat";
    private ObservableList<Contact> contacts;
    private ListView<Contact> contactsListView;

    /**
     * Method that launches the application and displays the graphical user interface.
     *
     * @param primaryStage the primary stage of the application
     */
    @Override
    public void start(Stage primaryStage) {
        contacts = FXCollections.observableArrayList();
        loadContacts();
        logger.info("Application started");

        primaryStage.setTitle("Phone Directory");

        contactsListView = new ListView<>();
        contactsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setText(null);
                } else {
                    setText(contact.toString());
                }
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Search");

        ComboBox<String> sortByComboBox = new ComboBox<>();
        sortByComboBox.getItems().addAll("Name", "Phone");
        sortByComboBox.setValue("Name");

        Button sortButton = new Button("Sort");
        sortButton.setOnAction(e -> {
            String sortBy = sortByComboBox.getValue();
            if (sortBy.equals("Name")) {
                contacts.sort(Comparator.comparing(Contact::getName));
            } else if (sortBy.equals("Phone")) {
                contacts.sort(Comparator.comparing(Contact::getPhone));
            }
        });

        VBox topPane = new VBox(5, searchField, sortByComboBox, sortButton);
        topPane.setAlignment(Pos.CENTER);
        topPane.setPadding(new Insets(10));

        Button addButton = new Button("Add Contact");
        addButton.setOnAction(e -> showAddContactDialog());

        Button deleteButton = new Button("Delete Contact");
        deleteButton.setOnAction(e -> deleteSelectedContact());

        HBox buttonBar = new HBox(10, addButton, deleteButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(10));

        VBox contentPane = new VBox(10, contactsListView);
        contentPane.setPadding(new Insets(10));
        VBox.setVgrow(contactsListView, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(contentPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.getDeltaY() > 0) {
                scrollPane.setVvalue(scrollPane.getVvalue() - 0.005);
            } else if (event.getDeltaY() < 0) {
                scrollPane.setVvalue(scrollPane.getVvalue() + 0.005);
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topPane);
        borderPane.setCenter(scrollPane);
        borderPane.setBottom(buttonBar);

        Scene scene = new Scene(borderPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> saveContacts());

        FilteredList<Contact> filteredList = new FilteredList<>(contacts);
        contactsListView.setItems(filteredList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(getSearchPredicate(newValue));
        });
    }

    /**
     * Method that displays a dialog for adding a new contact.
     */
    private void showAddContactDialog() {
        Dialog<Contact> dialog = new Dialog<>();
        dialog.setTitle("Add Contact");
        dialog.setHeaderText("Enter contact information");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Mobile", "Home", "Work", "Other");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, new Label("Name:"), nameField);
        gridPane.addRow(1, new Label("Phone:"), phoneField);
        gridPane.addRow(2, new Label("Type:"), typeComboBox);

        dialog.getDialogPane().setContent(gridPane);

        Button addButtonControl = (Button) dialog.getDialogPane().lookupButton(addButton);
        addButtonControl.addEventFilter(ActionEvent.ACTION, event -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String type = typeComboBox.getValue();

            if (name.isEmpty() || phone.isEmpty() || type == null) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please fill in all the fields before adding the contact.");
                alert.initOwner(dialog.getDialogPane().getScene().getWindow());
                alert.showAndWait();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String type = typeComboBox.getValue();

                return new Contact(name, phone, type);
            }
            return null;
        });

        Optional<Contact> result = dialog.showAndWait();

        result.ifPresent(this::addContact);
    }

    /**
     * Method that adds a new contact to the directory.
     *
     * @param contact the new contact to be added
     */
    public void addContact(Contact contact) {
        if (contact != null) {
            contacts.add(contact);

            String name = contact.getName();
            String phone = contact.getPhone();
            String type = contact.getType();
            logger.info("New contact has been added: [" + "Name: " + name + ", Phone: " + phone + ", Type: " + type + "]");
        }
    }

    /**
     * Method that deletes the selected contact from the directory.
     */
    private void deleteSelectedContact() {
        Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            deleteContact(selectedContact);
        }
    }

    /**
     * Method that deletes a contact from the directory.
     *
     * @param contact the contact to be deleted
     */
    public void deleteContact(Contact contact) {
        contacts.remove(contact);
        String name = contact.getName();
        String phone = contact.getPhone();
        String type = contact.getType();
        logger.info("Contact has been deleted: [" + "Name: " + name + ", Phone: " + phone + ", Type: " + type + "]");
    }

    /**
     * Method that saves the contacts to the data file.
     */
    public void saveContacts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(new ArrayList<>(contacts));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that loads the contacts from the data file.
     */
    @SuppressWarnings("unchecked")
    public void loadContacts() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                List<Contact> savedContacts = (List<Contact>) ois.readObject();
                contacts.addAll(savedContacts);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method that returns the predicate for filtering contacts based on a search query.
     *
     * @param searchText the search query
     * @return the predicate for filtering contacts
     */
    public Predicate<Contact> getSearchPredicate(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return contact -> true;
        }
        String lowercaseSearchText = searchText.toLowerCase();
        return contact -> contact.getName().toLowerCase().contains(lowercaseSearchText)
                || contact.getPhone().toLowerCase().contains(lowercaseSearchText);
    }

    /**
     * Method that returns the list of contacts.
     *
     * @return the list of contacts
     */
    public ObservableList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ObservableList<Contact> contacts) {
        this.contacts = contacts;
    }
}