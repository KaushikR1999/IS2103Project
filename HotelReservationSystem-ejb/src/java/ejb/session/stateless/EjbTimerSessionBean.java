/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import util.exception.NoRoomAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author kaushikr
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {
    
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    @Schedule(hour = "2", minute = "0", second = "0", info = "CurrentDayReservationCheckTimer")
    public void CurrentDayReservationCheckTimer () {
        
        Date bookingDateTime = new java.util.Date();
        
        //try {
            reservationSessionBeanLocal.allocateRoomToCurrentDayReservations(bookingDateTime);
       /* } catch (NoRoomAvailableException ex) {
            System.out.println(ex.getMessage());
        }  */

    }
}