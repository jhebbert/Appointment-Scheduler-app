/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Appointment;
import Model.DataManager;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author james
 */
public class ReportsController implements Initializable {

    @FXML
    private TextArea reportsTxtArea;
    @FXML
    private Button scheduleBtn;
    @FXML
    private Button appointmentTypesBtn;
    @FXML
    private Button totalBtn;
    @FXML
    private Button dashboardBtn;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    //  
    @FXML
    private void onScheduleClick(ActionEvent event) {
        
        reportsTxtArea.clear();
        
        String newLine = "\n";
        
        reportsTxtArea.appendText("Consultant schedules: " + newLine + newLine);
        
        //  Retrieves all users stored in the database
        ArrayList<User> allUsers = User.retrieveAllUsers();
        // For each user in all users
        allUsers.stream().map((user) -> {
            reportsTxtArea.appendText(user.getUserName() + ":" + newLine);
            return user;
            //  Retrieve each appointment from user
            }).map((user) -> User.retrieveUserAppointments(user)).forEachOrdered((userApts) -> {
            userApts.forEach((apt) -> {
                //  append appointment information to reportTxtArea
                reportsTxtArea.appendText("Start: " + DataManager.convertToLocal(apt.getStart()).toString() 
                        + " End: " + DataManager.convertToLocal(apt.getEnd()).toString() + newLine );
            });
            
            //  Add a new line to keep things clean and readable
            reportsTxtArea.appendText(newLine);
            
            //  Clear userApts, so that on the next call it does not duplicate 
            //  each appointment
            userApts.clear();
            
        });
        
        //  Clear allUsers to avoid duplication on next call
        allUsers.clear();
    }

    //  Shows a repot of how many of each type of appointment in a given month
    @FXML
    private void onTypesClick(ActionEvent event) {
        
        reportsTxtArea.clear();
        
        String newLine = "\n";
        
        ArrayList<Appointment> apts = new ArrayList<>();
        ArrayList<String> filteredTypes = new ArrayList<>();
        
        try {
        
        //  Retrieve appointments from the database
        ResultSet result = DataManager.startQuery("SELECT * FROM appointment;");
        
        while (result.next()) {
            Appointment newAppointment = new Appointment();
            newAppointment.setType(result.getString("type"));
            Timestamp startTStamp = result.getTimestamp("start");
            newAppointment.setStart(startTStamp.toLocalDateTime());
            apts.add(newAppointment);
           
        }
        
        //  Find appointments in the current months and store the type in 
        //  filteredTypes
        for (Appointment apt : apts) {
            int monthValue = DataManager.convertToLocal(apt.getStart()).getMonthValue();
            int monthNow = LocalDateTime.now().getMonthValue();
            int aptYear = DataManager.convertToLocal(apt.getStart()).getYear();
            int yearNow = LocalDateTime.now().getYear();
            
            if (monthValue == monthNow && aptYear == yearNow) {
                filteredTypes.add(apt.getType());
                
            }
        }
        
        //  Use a hash map to count the number of each type of appointment
        Map<String, Integer> hm = new HashMap<>();
            
            for (String i : filteredTypes) { 
            Integer j = hm.get(i.toLowerCase()); 
            hm.put(i.toLowerCase(), (j == null) ? 1 : j + 1); 
            }
            
            //  Append the type of appointment, and number of that type for each
            //  distinct type to the reports text area
            for (Map.Entry<String, Integer> val : hm.entrySet()) { 
            reportsTxtArea.appendText(val.getKey() + ": " + val.getValue() + newLine); 
        } 
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //  Displays the total number scheduled appointments in the database
    @FXML
    private void onTotalClick(ActionEvent event) {
        
        String newLine = "\n";
        int counter = 0;
       
        
        reportsTxtArea.clear();
        reportsTxtArea.appendText("The total number of scheduled appointments is: " + newLine);
        
        try {
        //  Retreieve appointments from database
        ResultSet aptResult = DataManager.startQuery("SELECT * FROM appointment;");
       
        //  While there are more appointments increment counter
        while (aptResult.next()) {
            counter++;
            
        }
        
        //  Append the value of counter to the reports text area
        reportsTxtArea.appendText(String.valueOf(counter));
        
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //  Return to dashboard
    @FXML
    private void onDashboardClick(ActionEvent event) {
        
        try {
            Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/FXMLDashboard.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(addPartScene);
        window.show();
            }
            catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            }
    }
    
}
