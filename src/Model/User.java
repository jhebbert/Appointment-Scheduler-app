/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class User {
    
    private String userName;
    private int userId;
    private static ArrayList<User> allUsers = new ArrayList();
    private static ArrayList<Appointment> userAppointments = new ArrayList();
    
    //  Return all appointments for the entered user
    public static ArrayList<Appointment> retrieveUserAppointments(User user) {
        
        try {
            
            //  Get all appointments from the database where the user Id matches
            //  the user parameter
            ResultSet appointmentResult = DataManager.startQuery("SELECT * FROM "
                    + "appointment WHERE userId = " + user.getUserId() + ";");
            
            //  Create appointment objects for each retrieved appointment and
            //  set attribute values
            while (appointmentResult.next()) {
                Appointment newAppointment = new Appointment();
                newAppointment.setType(appointmentResult.getString("type"));
                newAppointment.setAppointmentId(appointmentResult.getInt("appointmentId"));

                Timestamp startTStamp = appointmentResult.getTimestamp("start");
                Timestamp endTStamp = appointmentResult.getTimestamp("end");

                LocalDateTime start = startTStamp.toLocalDateTime();
                LocalDateTime end = endTStamp.toLocalDateTime();

                //  Calcualate length based on start and end dates
                LocalDateTime from = LocalDateTime.from(start);
                long length = from.until(end, ChronoUnit.MINUTES);

                newAppointment.setLength(length);
                newAppointment.setStartDate(DataManager.convertToLocal(startTStamp).toLocalDate());
                newAppointment.setStartTime(DataManager.convertToLocal(startTStamp).toLocalTime());
                newAppointment.setEndDate(DataManager.convertToLocal(endTStamp).toLocalDate());
                newAppointment.setEndTime(DataManager.convertToLocal(endTStamp).toLocalTime());
                newAppointment.setStart(start);
                newAppointment.setEnd(end);

                newAppointment.setCustomerID(appointmentResult.getInt("customerID"));

                userAppointments.add(newAppointment);

        }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return userAppointments;
        
    }
    
    //  Gets all Users from database
    public static ArrayList<User> retrieveAllUsers() {
        
        try {
        ResultSet userResult = DataManager.startQuery("SELECT * FROM user;");
        while (userResult.next()) {
            
            User newUser = new User();
            newUser.setUserId(userResult.getInt("userId"));
            newUser.setUserName(userResult.getString("userName"));
            
            allUsers.add(newUser);
            
        }
        
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return allUsers;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public static void setAllUsers(ArrayList<User> allUsers) {
        User.allUsers = allUsers;
    }

    public ArrayList<Appointment> getUserAppointments() {
        return userAppointments;
    }

    public void setUserAppointments(ArrayList<Appointment> userAppointments) {
        this.userAppointments = userAppointments;
    }
    
}
