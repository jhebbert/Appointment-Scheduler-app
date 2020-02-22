/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Appointment;
import Model.CurrentUser;
import Model.DataManager;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
public class ModifyAppointmentController implements Initializable {

    @FXML
    private TextField customerIDFld;
    @FXML
    private TextField timeFld;
    @FXML
    private TextField dateFld;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField lengthFld;
    @FXML
    private TextField typeFld;
    @FXML
    private Label errorLbl;
    
    Appointment selectedAppointment;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    // FIXME: Break this up into multiple simpler methods
    @FXML
    private void onSaveBtnClick(ActionEvent event) {
        
        try {
        
        //  Appointment lenght in min.
        int length = Integer.parseInt(lengthFld.getText());
        
        //  Check appointment length against maximum possible length
        //  Will not allow appointments to last into the next day
        if (length > 0 && length < 720) {
        
        //  Parse date and time values from text fields
        LocalDateTime start = LocalDateTime.of(LocalDate.parse(dateFld.getText()), LocalTime.parse(timeFld.getText()));
        LocalDateTime end = start.plusMinutes(Long.parseLong(lengthFld.getText()));
        
        //  Create copy of selected appointment
        Appointment updatedAppointment = selectedAppointment;
        //  Remove selected appointment
        CurrentUser.removeAppointment(selectedAppointment);
        
        //  Check for appointment overlap conflicts
        if (CurrentUser.checkOverlap(start, end)) {
            errorLbl.setText("Schedule conflict");
            errorLbl.setVisible(true);
            //  If there is a conflict the appointments remains unchanged 
            //  (updatedAppointment is a copy).
            CurrentUser.addAppointment(updatedAppointment);
        } else {
        updatedAppointment.setType(typeFld.getText());    
        updatedAppointment.setLength(length);
        updatedAppointment.setStartDate(LocalDate.parse(dateFld.getText()));
        updatedAppointment.setStartTime(LocalTime.parse(timeFld.getText()));
        updatedAppointment.setStart(start);
        
        CurrentUser.addAppointment(updatedAppointment);
        
        /*
        I do not remember exactly why I created a copy of selected appointment
        and then removed selected appointment from allAppointments. I think it
        had something to do with checking for appointment overlap. 
        FIXME: Simplify!
        */
        
        if (DataManager.checkWithinHours(start.toLocalTime(), end.toLocalTime())) {
        
            if(DataManager.updateAppointment(updatedAppointment.getAppointmentId(),
                updatedAppointment.getType(), DataManager.convertToUTC(start), DataManager.convertToUTC(end))) {

                Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/FXMLDashboard.fxml"));
                Scene addPartScene = new Scene(addPartParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

                window.setScene(addPartScene);
                window.show();
        
        } else {
            System.out.println("Error in onSaveBtnClick");
        }
         } else {
             errorLbl.setText("Business hours: 09:00-17:00");
             errorLbl.setVisible(true);
         }
         }
        } else {
            errorLbl.setText("Appointment length invalid");
            errorLbl.setVisible(true);
        }
            }
        
            catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                errorLbl.setText("Check data types and formats");
                errorLbl.setVisible(true);
            }
            catch (DateTimeParseException e) {
                errorLbl.setText("Check data types and formats");
                errorLbl.setVisible(true);
            } catch (Exception e) {
                errorLbl.setText("Check data types and formats");
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

    //  Used to send data from dashboard controller to ModifyAppointmentController
    public void sendAppointment(Appointment selected) {
        
        selectedAppointment = selected;
        
        timeFld.setText(String.valueOf(selected.getStartTime()));
        lengthFld.setText(String.valueOf(selected.getLength()));
        typeFld.setText(selected.getType());
        dateFld.setText(String.valueOf(selected.getStartDate()));
        customerIDFld.setText(String.valueOf(selected.getCustomerID()));
    }
    
}
