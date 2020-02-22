/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author james
 */


public class CurrentUser {
    
    private String username = null;
    private String password = null;
    
    private ResultSet usernameCheck = null;
    private ResultSet passwordCheck = null;
    private static int id = 0;
    private String passwordFromDatabase;
    
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    
    //Check username and password against database
    public boolean checkCredentials() {
        try {
        Statement stmt = DBConnection.startConnection().createStatement();
        usernameCheck = stmt.executeQuery("SELECT userID from user where userName = '" + username +"';");
        
        if (usernameCheck.next()) {
            id = usernameCheck.getInt(1);
            passwordCheck = stmt.executeQuery("SELECT password FROM user where userName = '" + username +"';");
            if (passwordCheck.next())
            passwordFromDatabase = passwordCheck.getString("password");
            
        if (passwordCheck.getString(1).equals(password)) {
            System.out.println("Access granted!");
            return true;
        } else {
        System.out.println("Access Denied");

        }
           
        }
        
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
    
    //  Remove appointment from database
    public static void deleteAppointment(Appointment selected) {
        
        try {
        
        Statement stmt = DBConnection.startConnection().createStatement();
        stmt.executeUpdate("DELETE FROM appointment WHERE appointmentId = " + selected.getAppointmentId() + ";");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        CurrentUser.id = id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //  Create and populate allAppointments observable list from appointments table
    //  in the data base.
    //  Runs right after log in to retrieve data. Should only need to be called once
    public static ObservableList<Appointment> retrieveAllAppointments() throws SQLException {
        
        // Ensure there is no residual data
        allAppointments.clear();
        
        Statement stmt = DBConnection.startConnection().createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM appointment WHERE "
                + "userId = " + CurrentUser.getId() + ";");
        
        //  While there is still another appointment in the database
        //  create a new appointment object and set values for relevant attributes
        while (result.next()) {
            Appointment newAppointment = new Appointment();
            newAppointment.setType(result.getString("type"));
            newAppointment.setAppointmentId(result.getInt("appointmentId"));
            
            
            Timestamp startTStamp = result.getTimestamp("start");
            Timestamp endTStamp = result.getTimestamp("end");
            
            LocalDateTime start = DataManager.convertToLocal(startTStamp.toLocalDateTime());
            LocalDateTime end = DataManager.convertToLocal(endTStamp.toLocalDateTime());
            
            //  Calculate the lenght of the retrieved appointments from the start
            //  and end times
            LocalDateTime from = LocalDateTime.from(start);
            long length = from.until(end, ChronoUnit.MINUTES);

            newAppointment.setLength(length);
            newAppointment.setStartDate(DataManager.convertToLocal(startTStamp).toLocalDate());
            newAppointment.setStartTime(DataManager.convertToLocal(startTStamp).toLocalTime());
            newAppointment.setEndDate(DataManager.convertToLocal(endTStamp).toLocalDate());
            newAppointment.setEndTime(DataManager.convertToLocal(endTStamp).toLocalTime());
            newAppointment.setStart(start);
            newAppointment.setEnd(end);
            
            newAppointment.setCustomerID(result.getInt("customerID"));
          
            
            allAppointments.add(newAppointment);

         }
        return allAppointments;
    }
    
    
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        
        //  allAppointments will be empty if retrieveAllAppointments has not run yet.
        //  If it has already run then it will just return the appointments stored in 
        //  memory rather than accessing the database.
        if (allAppointments.isEmpty())
            retrieveAllAppointments();
        
        return allAppointments;
    }

    public void setAllAppointments(ObservableList<Appointment> allAppointments) {
        this.allAppointments = allAppointments;
    
    }
    
    public static void addAppointment(Appointment newAppointment) {
        
        allAppointments.add(newAppointment);
        
    }
    
    //  Checks for overlap between scheduled appointments and a new appointment
    public static boolean checkOverlap(LocalDateTime newStart, LocalDateTime newEnd) {
        
        for(Appointment apt : allAppointments) {
            
            if ((newStart.isBefore(apt.getStart()) && newEnd.isAfter(apt.getEnd()))
                    || (newEnd.isAfter(apt.getStart()) && newEnd.isBefore(apt.getEnd()))
                    || (newStart.isAfter(apt.getStart()) && newStart.isBefore(apt.getEnd()))
                    || (newStart.equals(apt.getStart())))
            {
                
            return true;
        }
        }
        return false;
    }
    
    public static void removeAppointment(Appointment apt) {
        allAppointments.remove(apt);
    }
    
}
