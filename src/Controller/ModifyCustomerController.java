/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Customer;
import Model.DataManager;
import static Model.DataManager.insertAddress;
import static Model.DataManager.updateCustomerAddressId;
import Model.EmptyFieldException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class ModifyCustomerController implements Initializable {

    @FXML
    private TextField nameFld;
    @FXML
    private TextField addressFld;
    @FXML
    private TextField phoneFld;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    private int customerId;
    Customer selectedCustomer;
    @FXML
    private Label errorLbl;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    //  FIXME: break this into multiple simpler methods
    @FXML
    private void onSaveBtnClick(ActionEvent event)  {
        
        try {
        String address = addressFld.getText();
        String name = nameFld.getText();
        String phone = phoneFld.getText();
        
        //  Verify all text fields have been completed
        if (address.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            throw new EmptyFieldException("Please complete all text fields");
        }
        
        //  Update the selected Customer's address in memory
        selectedCustomer.setAddress(address);
        
        //  If the Customer's name in memory does not match what is in the nameFld
        //  then update the name in memory
        if (!selectedCustomer.getName().equals(name)) {
        selectedCustomer.setName(name);
        DataManager.updateCustomerName(selectedCustomer);
        }
        
        selectedCustomer.setPhoneNumber(phone);
        
        ResultSet result = DataManager.startQuery("SELECT addressId FROM address WHERE address = '" + address + "';");
        
        //  If the address in the addressFld matches an address in the database
        //  use the associated addressId for the customer
        if (result.next()) {
            
            updateCustomerAddressId(result.getInt("addressId"), customerId);
            
        //  If not, then add the new address to the database
        } else {
            
            insertAddress(selectedCustomer.getAddress(), selectedCustomer.getPhoneNumber());
            result = DataManager.startQuery("SELECT addressId FROM address WHERE address = '" + address + "';");
            if (result.next()) {
            updateCustomerAddressId(result.getInt("addressId"), customerId);
            } else {
                System.out.println("HERE");
            }
        }
        
        //  Go back to the dashboard
        Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/FXMLDashboard.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(addPartScene);
        window.show();
        }
        catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (EmptyFieldException e) {
            errorLbl.setText(e.getMessage());
            errorLbl.setVisible(true);
        }

    }

    
    //  Goes back to the dashboard
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

    //  Allows communication between the dashboard controller and this controller
    //  file.
    public void sendCustomer(Customer selected) {
        
        selectedCustomer = selected;
        
        nameFld.setText(selected.getName());
        phoneFld.setText(selected.getPhoneNumber());
        addressFld.setText(selected.getAddress());
        customerId = selected.getCustomerID();
        
    }
    
   
}
