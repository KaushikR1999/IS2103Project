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
public class SameDayReservationException extends Exception{

    /**
     * Creates a new instance of <code>SameDayReservationException</code>
     * without detail message.
     */
    public SameDayReservationException() {
    }

    /**
     * Constructs an instance of <code>SameDayReservationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SameDayReservationException(String msg) {
        super(msg);
    }
}
