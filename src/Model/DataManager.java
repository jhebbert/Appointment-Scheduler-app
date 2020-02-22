package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

/**
 *
 * @author james
 */
public class DataManager {
    
    // Store customer objects
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    
    //  Store appointments in the curent week
    private ObservableList<Appointment> appointmentsWeek = FXCollections.observableArrayList();
    
    //  Store appointments in the curent week
    private ObservableList<Appointment> applointmentsMonth = FXCollections.observableArrayList();
    
    
    //  Insert a customer into the database
    public static boolean insertCustomer(String name, String address, String phone) {
        int addressId = 0;
        
        try {
        Statement stmt = DBConnection.startConnection().createStatement();
        
        //  Foreign key constraint requires the address to be added first before 
        //  a customers address can be added to the database
        stmt.executeUpdate("insert into address (address, address2, cityId, postalCode, phone,"
                + "createDate, createdBy, lastUpdate, lastUpdateBy)" +
                "values ('" + address +"', '', 1, \"11114\", '" + phone + "', '" + LocalDateTime.now() +
                "', \"\", '" + LocalDateTime.now() + "', \"\");");
        
        //  Pull the addressId from the database so it can be added to the customer
        ResultSet addressIdResult = stmt.executeQuery("SELECT addressId FROM address where address = '" +
                address + "' AND phone = '" + phone + "';");
        
        //  If the addressId query returned anything, then store that value in addressId
        if (addressIdResult.next())
            addressId = addressIdResult.getInt("addressId");
        
        //  Customer insert
        stmt.executeUpdate("INSERT INTO customer (customerName, addressId, active, createDate, createdBy,"
                + " lastUpdate, lastUpdateBy)"
                + "values ('" + name +"'," + addressId + ", 1, '" + LocalDateTime.now() + "', '', '" + LocalDateTime.now() + "', '');");
        
        //  Retrieve customerId from customer table to add it to the customer object in memory
        ResultSet customerIdResult = DataManager.startQuery("SELECT customerId FROM"
                + " customer WHERE customerName = '" + name + "';");
        
        //  Create new Customer object and assign values to the attributes
        Customer newCustomer = new Customer();
        if (customerIdResult.next()) {
            newCustomer.setCustomerID(customerIdResult.getInt("customerId"));
        }
        newCustomer.setAddress(address);
        newCustomer.setAddressId(addressId);
        newCustomer.setName(name);
        newCustomer.setPhoneNumber(phone);
        allCustomers.add(newCustomer);
        
        return true;
        
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
    
    //  Insert a new appointment into the database
    public static boolean insertAppointment(int id, String type, LocalDateTime start, LocalDateTime end) {
         
        try {
        Statement stmt = DBConnection.startConnection().createStatement();
        stmt.executeUpdate("INSERT INTO appointment (customerId, userId, title, "
                + "description, location, contact, type, url, start, end, "
                + "createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "VALUES (" + id + ", " + CurrentUser.getId() + ", '', '', '', '', '" 
                + type + "', '', '" + start + "', '" + end + "', '" + LocalDateTime.now() + "', '', '" 
                + LocalDateTime.now() + "', '');");
        
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }
    
    //  Update an existing appointment in the database
    public static boolean updateAppointment(int appointmentId, String type, LocalDateTime start, LocalDateTime end) {
        try {
        Statement stmt = DBConnection.startConnection().createStatement();
        stmt.executeUpdate("UPDATE appointment "
                + "SET type = '" + type +"', start = '" + start + "', end = '" + end + "' "
                + "WHERE appointmentId = " + appointmentId + ";");
        return true;
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
    
    //  Starts a new database query using the query parameter
    public static ResultSet startQuery(String query) throws SQLException {
        Statement stmt = DBConnection.startConnection().createStatement();
        ResultSet result = stmt.executeQuery(query);
        return result;
    }
    
    // Convert from the users local time to UTC time
    public static LocalDateTime convertToUTC(LocalDateTime localTime) {
        
        ZonedDateTime zonedLocalTime = localTime.atZone(TimeZone.getDefault().toZoneId());
        ZonedDateTime zonedUTCTime = zonedLocalTime.toInstant().atZone(TimeZone.getTimeZone("UTC").toZoneId());
        
        LocalDateTime utcTime = zonedUTCTime.toLocalDateTime();

        return utcTime;
    }
    
    //  Convert from UTC to the users local time 
    public static ZonedDateTime convertToLocal(Timestamp tStamp) {
        
        ZonedDateTime utcTime = tStamp.toLocalDateTime().atZone(TimeZone.getTimeZone("UTC").toZoneId());

        ZonedDateTime convertedDateTime = utcTime.toInstant().atZone(TimeZone.getDefault().toZoneId());
        
        return convertedDateTime;
    }

    
    public static LocalDateTime convertToLocal(LocalDateTime utcTime) {
        
        ZonedDateTime zonedUTCTime = utcTime.atZone(TimeZone.getTimeZone("UTC").toZoneId());
        ZonedDateTime zonedLocalTime = zonedUTCTime.toInstant().atZone(TimeZone.getDefault().toZoneId());
        
        LocalDateTime convertedDateTime = zonedLocalTime.toLocalDateTime();
        
        return convertedDateTime;
    }
    
    //Returns an observable list of all customers stored in the database.
    //Returned list will always match database.
    //Called after log in is aproved. Should only need to be called once each 
    //time the program is run.
    public static ObservableList<Customer> retrieveAllCustomers() throws SQLException {
        
        // Clear list, so any changes the database since the last call will not lead to discrepancies or duplications
        allCustomers.clear();
        ResultSet customerTblResult = startQuery("SELECT * FROM customer;");
        
        while (customerTblResult.next()) {
            Customer customer = new Customer();
            int addressId = customerTblResult.getInt("addressId");
            customer.setCustomerID(customerTblResult.getInt("customerID"));
            customer.setName(customerTblResult.getString("customerName")); 
            ResultSet addressTblResult = startQuery("SELECT address, phone FROM address WHERE addressId = " + addressId + ";");
            if (addressTblResult.next()) {
                customer.setAddress(addressTblResult.getString("address"));
                customer.setPhoneNumber(addressTblResult.getString("phone"));
            }
            
            allCustomers.add(customer);
        }

        return allCustomers;
    }
    
    //  Update customer name in the database
    public static boolean updateCustomerName(Customer selected) {
        
        try {
        Statement stmt = DBConnection.startConnection().createStatement();
        stmt.executeUpdate("UPDATE customer "
                + "SET customerName = '" + selected.getName() + "' "
                + "WHERE customerId = " + selected.getCustomerID() + ";");
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        return false;
    }
    
    
    //Update addressId in the database
    public static void updateCustomerAddressId(int addressId, int customerId) throws SQLException {
        
        Statement stmt = DBConnection.startConnection().createStatement();
        stmt.executeUpdate("UPDATE customer "
                + "SET addressId = " + addressId + " "
                + "WHERE customerId = " + customerId + ";");
        
    }
    
    //  Insert address into the database
    public static void insertAddress(String address, String phone) throws SQLException {
        
        Statement stmt = DBConnection.startConnection().createStatement();
        stmt.executeUpdate("insert into address (address, address2, cityId, postalCode, phone,"
                + "createDate, createdBy, lastUpdate, lastUpdateBy)" +
                "values ('" + address +"', '', 1, \"11114\", '" + phone + "', '" + LocalDateTime.now() + "', \"\", '" + LocalDateTime.now() + "', \"\");");
    }

    //  Get the observable list 'allCustomers' from memory
    public static ObservableList<Customer> getAllCustomers() throws SQLException {
        if (allCustomers.isEmpty())
            retrieveAllCustomers();
            
        return allCustomers;
    }
    
    //  Remove customer from memory and from the database
    public static void removeCustomer(Customer selected) {

        try {
        Iterator<Appointment> itr = CurrentUser.getAllAppointments().iterator();
            
        while (itr.hasNext()) {
            Appointment apt = itr.next();
            if (apt.getCustomerID() == selected.getCustomerID()) {
                CurrentUser.deleteAppointment(apt);
                itr.remove();
            }
        }
        Statement stmt = DBConnection.startConnection().createStatement();
        stmt.executeUpdate("DELETE FROM customer WHERE customerId = " + selected.getCustomerID() + ";");
        allCustomers.remove(selected);
        
                
        
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    public static void setAllCustomers(ObservableList<Customer> allCustomers) {
        DataManager.allCustomers = allCustomers;
    }

    public ObservableList<Appointment> getCustomerWeek() {
        return appointmentsWeek;
    }

    public void setCustomerWeek(ObservableList<Appointment> customerWeek) {
        this.appointmentsWeek = customerWeek;
    }

    public ObservableList<Appointment> getCustomerMonth() {
        return applointmentsMonth;
    }

    public void setCustomerMonth(ObservableList<Appointment> customerMonth) {
        this.applointmentsMonth = customerMonth;
    }
    
    //  Make sure the appointment time falls within normal business hours
    public static boolean checkWithinHours(LocalTime start, LocalTime end) {
        LocalTime startHours = LocalTime.of(9, 0);
        LocalTime endHours = LocalTime.of(17, 0);
        
        boolean inHours = false;
        if ((start.isAfter(startHours) && start.isBefore(endHours) && end.isBefore(endHours)) ||
                ((start.equals(startHours) || start.isAfter(startHours) ) && (end.isBefore(endHours) || end.equals(endHours))))
            inHours = true;
        
        return inHours;
    }
    
    //  Gives an alert if there is an appointment within 15 min of logging in
    public static void checkUpcoming() throws Exception {
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime allertWindow = now.plusMinutes(15);
        String name = "";
        
        for (Appointment apt : CurrentUser.getAllAppointments()) {
            if (apt.getStart().isAfter(now) && apt.getStart().isBefore(allertWindow)) {
                
                int customerId = apt.getCustomerID();
                
                for (Customer cust : allCustomers) {
                    if (cust.getCustomerID() == customerId)
                        name = cust.getName();
                }
                
                 Alert upcomingAppointment = new Alert(Alert.AlertType.INFORMATION);
        upcomingAppointment.setTitle("Alert");
        upcomingAppointment.setHeaderText("Upcoming Appointment");
        upcomingAppointment.setContentText("You have an appointment with " + name + " at " + apt.getStartTime() + "!");
        
        upcomingAppointment.show();
            }
        }
        
    }
    
    
}
