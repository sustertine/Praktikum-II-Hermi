package org.coolsoft;

import java.util.HashMap;

public class Product {
    private String idNumber;
    private HashMap<String, String> attributes;

    public Product() {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
    }

    public void addAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }


    //Testne metode
    public void printAtt() {
        for (Object name : attributes.keySet()) {
            String key = name.toString();
            String val = attributes.get(key);

            System.out.println(key + " : " + val);
        }
    }

}
