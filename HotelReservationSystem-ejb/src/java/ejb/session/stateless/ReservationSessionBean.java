/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewReservationException;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author kaushikr
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewReservation(Reservation newReservation) throws ReservationNotFoundException, CreateNewReservationException, InputDataValidationException
    {
        
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(newReservation);
        
        if (constraintViolations.isEmpty()) {
            if (newReservation != null) {
                
                em.persist(newReservation);

                em.flush();

                return newReservation.getReservationId();
            } else {
                throw new CreateNewReservationException("Reservation information not provided");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        

    }
    
    @Override
    public void updateReservation(Reservation reservation) throws ReservationNotFoundException, InputDataValidationException {
        if (reservation != null && reservation.getReservationId() != null) {
            Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);

            if (constraintViolations.isEmpty()) {
                
                Reservation reservationToUpdate = retrieveReservationByReservationId(reservation.getReservationId());
                
                reservationToUpdate.setBookingDateTime(reservation.getBookingDateTime());
                reservationToUpdate.setStartDate(reservation.getStartDate());
                reservationToUpdate.setEndDate(reservation.getEndDate());
                reservationToUpdate.setGuest(reservation.getGuest());
                reservationToUpdate.setStatus(reservation.getStatus());
                reservationToUpdate.setTotalReservationFee(reservation.getTotalReservationFee());
                reservationToUpdate.setReservationType(reservation.getReservationType());
                reservationToUpdate.setNumberOfRooms(reservation.getNumberOfRooms());
                
                for (Room room : reservation.getRooms()) {
                    reservationToUpdate.getRooms().add(room);
                }
 
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new ReservationNotFoundException("Reservation ID not provided for reservation to be updated");
        }
    }
    
    @Override
    public void deleteReservation(Long reservationId) throws ReservationNotFoundException
    {
        Reservation reservationToRemove = retrieveReservationByReservationId(reservationId);
        
        List<Room> rooms = reservationToRemove.getRooms();
                
        for (Room room : rooms) {
            room.getReservations().remove(reservationToRemove);
        }
        
        em.remove(reservationToRemove);
    }
    
    @Override
    public List<Reservation> retrieveAllReservations()
    {
        Query query = em.createQuery("SELECT r FROM Reservation r ORDER BY r.reservationId ASC");
        
        return query.getResultList();
    }
    
    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.reservationId = :inReservationId");
        query.setParameter("inReservationId", reservationId);
        
        try
        {
            return (Reservation)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new ReservationNotFoundException("Reservation Id " + reservationId + " does not exist!");
        }
    }
    
    @Override
    public List<Reservation> retrieveReservationsByBookingDate (Date bookingDateTime) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.bookingDateTime = :inBookingDateTime");
        query.setParameter("inBookingDateTime", bookingDateTime);
        
        try
        {
            return query.getResultList();
        }
        catch(NoResultException ex)
        {
            throw new ReservationNotFoundException("No reservations exist for " + bookingDateTime);
        }
    }
    
    public void allocateRoomToCurrentDayReservations (Date bookingDateTime) {
        
        List <Reservation> reservations = new ArrayList <Reservation> ();
        
        try {
            reservations = retrieveReservationsByBookingDate(bookingDateTime);
        } catch (ReservationNotFoundException ex) {
            System.out.println ("Unable to allocate rooms as " + ex.getMessage());
        }
        
        for (Reservation reservation : reservations) {
            RoomType roomType = reservation.getRoomType();
            
            int NumberOfRooms = reservation.getNumberOfRooms();
            
            for (int i = 0; i < NumberOfRooms; i++) {
                RoomType currentRoomType = reservation.getRoomType();
                boolean allocated = false;
                while (allocated != true && currentRoomType != null) {
                    allocated = allocateRoom (roomType, reservation);
                    currentRoomType = currentRoomType.getNextHighestRoomType();
                }
            }
            
            if (reservation.getRooms().size() != NumberOfRooms) {
                // raise exception in exception Report
                List<Room> rooms = reservation.getRooms();
                for (Room room : rooms) {
                    room.getReservations().remove(reservation);
                }
                reservation.getRooms().clear();
            } 
        }
    }
    
    public boolean allocateRoom (RoomType roomType, Reservation reservation) {
        List<Room> rooms = roomSessionBeanLocal.retrieveListOfRoomsAvailableForBookingByRoomType(reservation.getStartDate(), reservation.getEndDate(), roomType.getRoomTypeId());
        if (rooms.isEmpty()) {
            // check
            return false;
        } else {
            Room room = rooms.get(0);
            room.getReservations().add(reservation);
            reservation.getRooms().add(room);
            return true;
        }
    }
    
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
