/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author kaushikr
 */
public class CreateNewReservationException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewReservationException</code>
     * without detail message.
     */
    public CreateNewReservationException() {
    }

    /**
     * Constructs an instance of <code>CreateNewReservationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewReservationException(String msg) {
        super(msg);
    }
}
