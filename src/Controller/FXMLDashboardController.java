/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Appointment;
import Model.Customer;
import Model.DataManager;
import Model.CurrentUser;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.time.Month;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author james
 */
public class FXMLDashboardController implements Initializable {

    @FXML
    private TableView<Customer> customerTbl;
    @FXML
    private TableColumn<Customer, String> customerNameCustTblCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableView<Appointment> appointmentsTbl;
    @FXML
    private TableColumn<Customer, String> customerIDCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private Button newCustomerBtn;
    @FXML
    private Button updateCustomerBtn;
    @FXML
    private Button reportsBtn;
    @FXML
    private RadioButton allRdBtn;
    @FXML
    private RadioButton monthRdBtn;
    @FXML
    private RadioButton weekRdBtn;
    @FXML
    private Button newAppointmentBtn;
    @FXML
    private Button updateAppointmentBtn;
    @FXML
    private TableColumn<?, ?> dateCol;
    @FXML
    private TableColumn<?, ?> timeCol;
    @FXML
    private ToggleGroup filterByToggleGroup;
    @FXML
    private Button filterBtn;
    @FXML
    private Label errorLbl;
    @FXML
    private Button deleteCustomerBtn;
    @FXML
    private Button deleteAppointmentBtn;
    private Customer selectedCustomer;
    private Appointment selectedAppointment;
    private static boolean alertShown = false;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
        //  Set Customer table items
        customerTbl.setItems(DataManager.getAllCustomers());
        
        //  set Cell values
        customerNameCustTblCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
        //  Set Appointment table items
        appointmentsTbl.setItems(CurrentUser.getAllAppointments());
        
        //  set Cell values
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        //  Set default toggle selection to all
        filterByToggleGroup.selectToggle(allRdBtn);
        
        //  Show alert if there is an appointmnet withing 15 min of logging in.
        //  Will not show after login.
        if (!alertShown) {
             DataManager.checkUpcoming();
             alertShown = true;
        }        
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
//      
    }    

    //  Go to the add customer screen
    @FXML
    private void onNewCustomerClick(ActionEvent event) {
        
        try {
            Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/AddCustomer.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(addPartScene);
        window.show();
            }
            catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            }
    }

    //  Go to the add modify customer screen
    @FXML
    private void onUpdateCustomerClick(ActionEvent event) {
        
        selectedCustomer = customerTbl.getSelectionModel().getSelectedItem();
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/ModifyCustomer.fxml"));
            loader.load();
            
            ModifyCustomerController MCController = loader.getController();
            
            //  Make sure there is a customer selected
            if (!(selectedCustomer == null)) {
            //  pass selected customer to ModifyCustomerController
            MCController.sendCustomer(selectedCustomer);
        
        //  Go to the modify customer screen    
        Parent addPartScene = loader.getRoot();
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(new Scene(addPartScene));
        window.show();
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    //  Go to the report screen
    @FXML
    private void onReportClick(ActionEvent event) {
        
        try {
            Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(addPartScene);
        window.show();
            }
            catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            }
    }

    //  Go to the add appartment screen
    @FXML
    private void onNewAppointmentClick(ActionEvent event) {
        
        selectedCustomer = customerTbl.getSelectionModel().getSelectedItem();
        
        try {
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/AddAppointment.fxml"));
            loader.load();
            
            //  Send selected customer to ModifyAppiontmentController
            AddAppointmentController AAcontroller = loader.getController();
            if (!(selectedCustomer == null)) {
            AAcontroller.sendCustomer(selectedCustomer);
            
        //  Go to add appointment screen
        Parent addPartScene = loader.getRoot();
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(new Scene(addPartScene));
        window.show();
        }
            else {
                errorLbl.setVisible(true);
            }
            } catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            }
    }

    //  Go to modify appointment screen
    @FXML
    private void onUpdateAppointmentClick(ActionEvent event) {
        
        selectedAppointment = appointmentsTbl.getSelectionModel().getSelectedItem();
        
        try {
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/ModifyAppointment.fxml"));
            loader.load();
            
            //  Send selected appointment to ModifyAppiontmentController
            ModifyAppointmentController MAController = loader.getController();
            if (!(selectedAppointment == null)) {
            MAController.sendAppointment(selectedAppointment);
            
        //  Go to the modify appointment screen
        Parent addPartScene = loader.getRoot();
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(new Scene(addPartScene));
        window.show();
        }
            else {
                errorLbl.setVisible(true);
            }
            } catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            }
    }

    //  Display only appointments for the selected customer
    @FXML
    private void onFilterBtnClick(ActionEvent event) throws Exception {
        
        //  Get selected customer from customer table
        selectedCustomer = customerTbl.getSelectionModel().getSelectedItem();
        try {
            
        //  Create new observable list to hold the selected customer's appointments
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
        
        //  Iterate through all of the user's appointments
        for (Appointment a : CurrentUser.getAllAppointments()) {
           
            //  Add all appointments where the customerId matches the selected 
            //  customer's ID.
            if (selectedCustomer.getCustomerID() == a.getCustomerID())
                filteredAppointments.add(a);
        }
            appointmentsTbl.setItems(filteredAppointments);
         } catch (Exception e) {
                System.out.println("Please select a customer from the table");
            }
        }

    //  Remove Customer
    @FXML
    private void onDeleteCustomerClick(ActionEvent event) {
        
        //  Show alert and confirm removal 
        Alert deleteWarning = new Alert(Alert.AlertType.CONFIRMATION);
        deleteWarning.setTitle("WARNING!");
        deleteWarning.setHeaderText("You are about to remove a Customer!");
        deleteWarning.setContentText("Are you sure you want to procede?");
        
        Optional<ButtonType> selection = deleteWarning.showAndWait();
        
        //  If confirmed call remove customer on the selected customer
        if (selection.get() == ButtonType.OK) {
        selectedCustomer = customerTbl.getSelectionModel().getSelectedItem();
        DataManager.removeCustomer(selectedCustomer);
        }
       
    }

    //  Remove appointment
    @FXML
    private void onDeleteAppointmentClick(ActionEvent event) throws SQLException {
        
        //  Show alert and confirm removal
        Alert deleteWarning = new Alert(Alert.AlertType.CONFIRMATION);
        deleteWarning.setTitle("WARNING!");
        deleteWarning.setHeaderText("You are about to remove an Appointment!");
        deleteWarning.setContentText("Are you sure you want to procede?");
        
        Optional<ButtonType> selection = deleteWarning.showAndWait();
        
        //  If confirmed then remove appointment
        if (selection.get() == ButtonType.OK) {
        selectedAppointment = appointmentsTbl.getSelectionModel().getSelectedItem();
        CurrentUser.deleteAppointment(selectedAppointment);
        CurrentUser.getAllAppointments().remove(selectedAppointment);
        }
        else {
        }
    }

    //  Show only appointments in the current month on toggle month
    @FXML
    @SuppressWarnings("null")
    private void onMonthToggle(ActionEvent event) throws SQLException {
        
        Month currentMonth = LocalDateTime.now().getMonth();
        
        ObservableList<Appointment> allAppointments = CurrentUser.getAllAppointments();
        ObservableList<Appointment> filterdByMonth = FXCollections.observableArrayList();
        
        for (Appointment apt : allAppointments) {
            if (apt.getStart().getMonth().equals(currentMonth) &&
                    apt.getStart().getYear() == (LocalDateTime.now().getYear())) {
                filterdByMonth.add(apt);
            }
        }
        
        //  Set the appointment table to show the filtered appointments
        appointmentsTbl.setItems(filterdByMonth);
            System.out.println("Month");
    }

    //  Display this weeks appointments in appointment table on toggle week
    @FXML
    private void onWeekToggle(ActionEvent event) throws SQLException {
        
        LocalDateTime day = LocalDateTime.now();
        
        while (!day.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            day = day.minusDays(1);
        }
        
        ObservableList<Appointment> allAppointments = CurrentUser.getAllAppointments();
        ObservableList<Appointment> filterdByWeek = FXCollections.observableArrayList();
        
        for (Appointment apt : allAppointments) {
            // Filters for all appointments between last Sunday and the coming Sunday
            if (apt.getStart().isAfter(day) && apt.getStart().isBefore(day.plusDays(7))) {
                filterdByWeek.add(apt);
            }
        }
        //  Set appointment table to filteredByWeek
        appointmentsTbl.setItems(filterdByWeek);
    }

    //  Display all scheduled appointments in the appointments table on toggle all
    @FXML
    private void onAllToggle(ActionEvent event) throws SQLException {
        
        appointmentsTbl.setItems(CurrentUser.getAllAppointments());
        
    }
    
    
}
