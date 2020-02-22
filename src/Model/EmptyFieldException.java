/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author james
 */

//  Customer Exception for alerting the user that a field has been left empty
public class EmptyFieldException extends Exception {
    
     public EmptyFieldException(String errorMessage) {
        super(errorMessage);
    }
    
}
