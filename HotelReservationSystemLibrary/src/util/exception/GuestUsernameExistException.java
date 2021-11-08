/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author yuenz
 */
public class GuestUsernameExistException extends Exception{

    /**
     * Creates a new instance of <code>GuestUsernameExistException</code>
     * without detail message.
     */
    public GuestUsernameExistException() {
    }

    /**
     * Constructs an instance of <code>GuestUsernameExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestUsernameExistException(String msg) {
        super(msg);
    }
}
