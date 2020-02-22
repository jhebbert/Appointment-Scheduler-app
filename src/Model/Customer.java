/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import static Model.CurrentUser.getAllAppointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author james
 */

public class Customer {
    
    private String name;
    private String address;
    private String phoneNumber;
    private int customerID;
    private int addressId;
    ObservableList<Appointment> customerAppointments = FXCollections.observableArrayList();
   
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String Address) {
        this.address = Address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
    
    //  Gets and returns all appointments for the selected customer
    public ObservableList<Appointment> getCustomerAppointments(Customer selected) throws Exception {
        
        for (Appointment apt: getAllAppointments()) {
            if (apt.getCustomerID() == selected.getCustomerID()) {
                customerAppointments.add(apt);
            }
        }
        
        return customerAppointments;
    }

    public void setCustomerAppointments(ObservableList<Appointment> customerAppointments) {
        this.customerAppointments = customerAppointments;
    }
    
}
