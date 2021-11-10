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
import javax.ejb.Stateless;
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
    
    public void allocateRoomToCurrentDayReservations () {
        
        Date bookingDateTime = new java.util.Date();
        
        try {
            List <Reservation> reservations = reservationSessionBeanLocal.retrieveReservationsByBookingDate(bookingDateTime);
        } catch (ReservationNotFoundException ex) {
            System.out.println ("Unable to allocate rooms as " + ex.getMessage());
        }
        
    }
}
