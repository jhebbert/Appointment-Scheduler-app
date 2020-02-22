/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author james
 */
public class DBConnection {
    
    private static String jdbcURL = "jdbc:mysql://3.227.166.251/U06KZ4";
    
    private static final String MYSQLJDBCDriver = "com.mysql.jdbc.Driver";
    
    private static Connection conn = null;
    
    private static String username = "U06KZ4";
    private static String passwoerd = "53688790788";
    
    //  Returns connection to database that can then be used for any database 
    //  interaction
    public static Connection startConnection() {
        
        try {
        Class.forName(MYSQLJDBCDriver);    
        conn = (Connection) DriverManager.getConnection(jdbcURL, username, passwoerd);
        //System.out.println("Connection succesful");
        
        } catch(ClassNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        
        return conn;
        
    }
    
    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection closed");
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
}
