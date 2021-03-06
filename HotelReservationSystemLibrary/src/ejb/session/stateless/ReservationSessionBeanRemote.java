/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.NoRoomAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author kaushikr
 */
@Remote
public interface ReservationSessionBeanRemote {
    
    public List<Reservation> retrieveReservationsByBookingDate(Date bookingDateTime) throws ReservationNotFoundException;
    
    public void deleteReservation(Long reservationId) throws ReservationNotFoundException;
    
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;
    
    public void updateReservation(Reservation reservation) throws ReservationNotFoundException, InputDataValidationException;
    
    public Reservation createNewReservation(Reservation newReservation) throws ReservationNotFoundException, CreateNewReservationException, InputDataValidationException;
    
    public List<Reservation> retrieveAllReservations();
    
    public void allocateRoomToCurrentDayReservations(Date bookingDateTime);
    
    public String retrieveRoomsAllocatedInString(Long reservationId) throws ReservationNotFoundException;
    
    public void allocateRoomToReservation(Long reservationId);

    public List<Reservation> retrieveUpgradedReservations(Date bookingDate) throws ReservationNotFoundException;

    public List<Reservation> retrieveRejectedReservations(Date bookingDate) throws ReservationNotFoundException;
    
    public int getNumberOfUpgradedRooms(Reservation reservation);
    
    public List<Reservation> retrieveReservationsByPartner(Long partnerId, boolean loadReservation) throws ReservationNotFoundException, PartnerNotFoundException;
    
    public void addPartnerToReservation(Reservation reservation, Long partnerId) throws PartnerNotFoundException;

    public List<Reservation> retrieveGuestReservations(Guest guest) throws ReservationNotFoundException;
}
