 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author kaushikr
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {
    
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    @Schedule(hour = "2", minute = "0", second = "0", info = "CurrentDayReservationCheckTimer")
    public void CurrentDayReservationCheckTimer () {
        
        Date startDate = new java.util.Date();
        
        reservationSessionBeanLocal.allocateRoomToCurrentDayReservations(startDate);
    }
}