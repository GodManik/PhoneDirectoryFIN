package com.telephone.phonedirectory;

import javafx.collections.FXCollections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;


import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneDirectoryTest {
    private PhoneDirectory phoneDirectory;

    @BeforeEach
    public void setUp() {
        phoneDirectory = new PhoneDirectory();
        // Инициализируем contacts до запуска тестов
        phoneDirectory.setContacts(FXCollections.observableArrayList());
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        String testName = testInfo.getTestMethod().orElseThrow().getName();
        File dataFile = new File(testName + "_phone_directory.dat");
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }

    @Test
    public void testAddContact() {
        Contact contact = new Contact("John Doe", "1234567890", "Mobile");

        phoneDirectory.addContact(contact);

        assertEquals(1, phoneDirectory.getContacts().size());
        assertEquals(contact, phoneDirectory.getContacts().get(0));
    }

    @Test
    public void testDeleteContact() {
        Contact contact = new Contact("John Smith", "9876543210", "Home");

        phoneDirectory.addContact(contact);
        phoneDirectory.deleteContact(contact);

        assertEquals(0, phoneDirectory.getContacts().size());
    }

}
