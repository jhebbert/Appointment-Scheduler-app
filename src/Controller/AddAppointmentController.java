/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Appointment;
import Model.Customer;
import Model.DataManager;
import static Model.DataManager.startQuery;
import Model.CurrentUser;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author james
 */
public class AddAppointmentController implements Initializable {

    @FXML
    private TextField customerIDFld;
    @FXML
    private TextField timeFld;
    @FXML
    private TextField dateFld;
    @FXML
    private TextField lengthFld;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField typeFld;
    @FXML
    private Label errorLbl;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void onSaveBtnClick(ActionEvent event) {
        
        Appointment newAppointment = new Appointment();
        
        try {
            
        //  Set attributes form newAppointment from text fields
        newAppointment.setCustomerID(Integer.parseInt(customerIDFld.getText()));
        LocalDate date = LocalDate.parse(dateFld.getText());
        newAppointment.setStartDate(date);
        LocalTime time = LocalTime.parse(timeFld.getText());
        newAppointment.setStartTime(time);
        newAppointment.setStart(LocalDateTime.of(date, time));
        newAppointment.setType(typeFld.getText());
        
        //  Appointment length in min
        long length = Long.parseLong(lengthFld.getText());
        
        //  Check appointment length against max possible lenght to prevent
        //  accidentaly scheduling an appointment over multiple days.
        if (length > 0 && length < 720) {
            
            //  Calculate and set the end time for the appointment
            newAppointment.setEnd(newAppointment.getStart().plusMinutes(length));
            
            newAppointment.setLength(length);

            LocalDateTime startDateTime = LocalDateTime.of(date, time);
            LocalDateTime endDateTime = startDateTime.plusMinutes(length);

            newAppointment.setStart(startDateTime);
            newAppointment.setEnd(endDateTime);

            //  Check that the appointment is within business hours
            if (DataManager.checkWithinHours(newAppointment.getStartTime(), endDateTime.toLocalTime())) {

                //  Check for conflict with existing appointments
                if (CurrentUser.checkOverlap(startDateTime, endDateTime)) {
                    errorLbl.setText("Schedule conflict");
                    errorLbl.setVisible(true);
                } else {
                    
                    //  If within business hours and no overlap conflict then 
                    //  insert the appointment into the database (insertAppointment
                    //  returns true if successful.
                    if (DataManager.insertAppointment(newAppointment.getCustomerID(), 
                            newAppointment.getType(), DataManager.convertToUTC(startDateTime), 
                            DataManager.convertToUTC(endDateTime))) {

                        //  Convert the entered start time to UTC time to compare 
                        //  to stored time in the database
                        LocalDateTime convertedStart = DataManager.convertToUTC(startDateTime);

                        /*
                        Appointment IDs are dynamically set in the database. 
                        aptResult retrieves the appointmentId for the newly inserted
                        appointment so that it can be added to the appointment 
                        object in memory
                        */
                        ResultSet aptResult = startQuery("SELECT appointmentId From appointment "
                            + "WHERE start = '" + convertedStart + "';");
                        if (aptResult.next()) {
                            newAppointment.setAppointmentId(aptResult.getInt("appointmentId"));
                        }

                        //  Add new appointment to allAppointments
                        CurrentUser.addAppointment(newAppointment);

                        //  Return to dashboard
                        Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/FXMLDashboard.fxml"));
                        Scene addPartScene = new Scene(addPartParent);
                        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

                        window.setScene(addPartScene);
                        window.show();

                        }
                    }
                } else {
                    errorLbl.setText("Business hours: 09:00-17:00");
                    errorLbl.setVisible(true);
                }
        } else {
            errorLbl.setText("Invalid Length");
            errorLbl.setVisible(true);
        }
        } 
        catch (NumberFormatException e) {
            errorLbl.setVisible(true);
            errorLbl.setText("Check data types");
        }
        catch (IOException e) {
           System.out.println("Error:  " + e.getMessage());
        }
        catch (Exception e) {
            errorLbl.setText("Check data types and formatting");
            errorLbl.setVisible(true);
            
        }
       
    }

    //  Return to dashboard
    @FXML
    private void onCancelBtnClick(ActionEvent event) {
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

    //  Used to send the customerId from the selected customer on the dashboard
    //  to the AddAppointmentController, so the new appointment can be tied to 
    //  the selected customer
    public void sendCustomer(Customer selected) {
        
        customerIDFld.setText(String.valueOf(selected.getCustomerID()));
        
    }
    
}
