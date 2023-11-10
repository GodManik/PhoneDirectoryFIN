package com.telephone.phonedirectory;

import java.io.Serializable;

/**
 * Class representing a contact in the phone directory.
 */
public class Contact implements Serializable {
    private String name;
    private String phone;
    private String type;

    /**
     * Constructs a new Contact object with the specified name, phone number, and type.
     *
     * @param name  the name of the contact
     * @param phone the phone number of the contact
     * @param type  the type of the phone number (e.g., mobile, home, work)
     */
    public Contact(String name, String phone, String type) {
        this.name = name;
        this.phone = phone;
        this.type = type;
    }

    /**
     * Returns the name of the contact.
     *
     * @return the name of the contact
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the phone number of the contact.
     *
     * @return the phone number of the contact
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the type of the phone number for the contact.
     *
     * @return the type of the phone number
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a string representation of the contact in the format: "name - phone (type)".
     *
     * @return a string representation of the contact
     */
    @Override
    public String toString() {
        return name + " - " + phone + " (" + type + ")";
    }
}
