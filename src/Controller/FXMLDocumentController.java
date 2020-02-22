/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.CurrentUser;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Locale;
import static java.util.Locale.*;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField usernameTxtFld;
    @FXML
    private PasswordField passwordFld;
    @FXML
    private Label errorLbl;
    @FXML
    private Button loginBtn;
    @FXML
    private Label usernameLbl;
    @FXML
    private Label passwordLbl;
    @FXML
    private Label titleLbl;

    private String username;
    private String password;
    
    ResourceBundle loginRB = ResourceBundle.getBundle("Model/login", getDefault());
    
   

   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
       //  Checks system language. If the system language is Danish
       //  then the login screen is displayed in danish.
       //  Properties files in model package hold the string values for
       //  languages
       if (Locale.getDefault().getLanguage().equals("da")) {
           titleLbl.setText(loginRB.getString("title"));
           usernameLbl.setText(loginRB.getString("username"));
           passwordLbl.setText(loginRB.getString("password"));  
       } else {
           titleLbl.setText(loginRB.getString("title"));
           usernameLbl.setText(loginRB.getString("username"));
           passwordLbl.setText(loginRB.getString("password"));
       }
           
       errorLbl.setText("");
        
    }    

    //  Verify login credentials and go to dashboard screen
    @FXML
    private void onLoginClick(ActionEvent event) {
        
        //  Get entered credentials from text fields
        username = usernameTxtFld.getText();
        password = passwordFld.getText();
        CurrentUser user = new CurrentUser();
        user.setUsername(username);
        user.setPassword(password);
        
        //  If user credentials do not match, show error message
        if (!user.checkCredentials()) {
            showLoginError();
        } else {
            try {

                //  Log user and time of login to a text file for record keeping
                logLogin();

                //  Go to dashboard
                Parent addPartParent = FXMLLoader.load(getClass().getResource("/View/FXMLDashboard.fxml"));
                Scene addPartScene = new Scene(addPartParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

                window.setScene(addPartScene);
                window.show();
            }
            catch (IOException e) {
               
            }
        }
        
    }
    
    //  Show the log in error message
    public void showLoginError() {
        
        //  Check the language to use for the log in error message
        //  and get the coresponding string values from the properties files
        if (Locale.getDefault().getLanguage().equals("da")){
        errorLbl.setText(loginRB.getString("denied"));
        }
        else {
            errorLbl.setText(loginRB.getString("denied"));
        }
    }
    
    //  Store login information in a text file
    public static void logLogin() {
        Logger log = Logger.getLogger("log.txt");
        
       try {
            FileHandler fh = new FileHandler("log.txt", true);
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
            log.addHandler(fh);
       } catch (IOException e) {
           System.out.println("Error" + e.getMessage());
       }

        log.setLevel(Level.INFO); 
        
        //  Store userId and current time in log.txt
        log.info("User ID: " +CurrentUser.getId() + ", logged in at:" + LocalTime.now() + ".\n");
    }
    
}
