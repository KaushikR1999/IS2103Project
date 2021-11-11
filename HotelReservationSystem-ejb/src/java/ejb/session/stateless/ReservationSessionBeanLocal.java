/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.NoRoomAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author kaushikr
 */
@Local
public interface ReservationSessionBeanLocal {

    public List<Reservation> retrieveReservationsByBookingDate(Date bookingDateTime) throws ReservationNotFoundException;

    public void deleteReservation(Long reservationId) throws ReservationNotFoundException;

    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;

    public void updateReservation(Reservation reservation) throws ReservationNotFoundException, InputDataValidationException;

    public Reservation createNewReservation(Reservation newReservation) throws ReservationNotFoundException, CreateNewReservationException, InputDataValidationException;

    public List<Reservation> retrieveAllReservations();

    public void allocateRoomToCurrentDayReservations(Date bookingDateTime) throws NoRoomAvailableException;
    
    public String retrieveRoomsAllocatedInString(Long reservationId) throws ReservationNotFoundException;

    public void allocateRoomToReservation(Long reservationId) throws NoRoomAvailableException;

    public List<Reservation> retrieveUpgradedReservations(Date bookingDate) throws ReservationNotFoundException;

    public List<Reservation> retrieveRejectedReservations(Date bookingDate) throws ReservationNotFoundException;
    
}
