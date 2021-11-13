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
public class NoRateAvailableException extends Exception{

    /**
     * Creates a new instance of <code>NoRateAvailableException</code> without
     * detail message.
     */
    public NoRateAvailableException() {
    }

    /**
     * Constructs an instance of <code>NoRateAvailableException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoRateAvailableException(String msg) {
        super(msg);
    }
}
