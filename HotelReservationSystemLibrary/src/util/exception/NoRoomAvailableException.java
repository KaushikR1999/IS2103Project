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
public class NoRoomAvailableException extends Exception{

    /**
     * Creates a new instance of <code>NoRoomAvailableException</code> without
     * detail message.
     */
    public NoRoomAvailableException() {
    }

    /**
     * Constructs an instance of <code>NoRoomAvailableException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoRoomAvailableException(String msg) {
        super(msg);
    }
}
