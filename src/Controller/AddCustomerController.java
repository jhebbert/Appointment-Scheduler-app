/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.DataManager;
import Model.EmptyFieldException;
import java.io.IOException;
import java.net.URL;
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
public class AddCustomerController implements Initializable {

    @FXML
    private TextField nameFld;
    @FXML
    private TextField phoneFld;
    @FXML
    private TextField addressFld;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label errorLbl;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    

    
    @FXML
    private void onSaveBtnClick(ActionEvent event) {
        
        String name = nameFld.getText();
        String phone = phoneFld.getText();
        String address = addressFld.getText();
        
        //  Verify all text fields are completed
        //  FIXME: create a separate method that can handle field verification to
        //  avoid duplicate code between this and the modifyCustomerController
        try {
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            throw new EmptyFieldException("please complete all text fields");
        }
        
        //  Verify customer has been added to database
        if (DataManager.insertCustomer(name, address, phone)) {
            System.out.println("Customer added to database");
        }
        
        //  Return to dashboard
        Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/FXMLDashboard.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(addPartScene);
        window.show();
            }
            catch (IOException e) {
               System.out.println("Error: " + e.getMessage());
            }
            catch (EmptyFieldException e) {
                errorLbl.setText(e.getMessage());
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

    
}
