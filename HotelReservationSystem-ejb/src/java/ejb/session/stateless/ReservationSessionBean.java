/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
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
import util.enumeration.ReservationStatusEnum;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.NoRoomAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author kaushikr
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Reservation createNewReservation(Reservation newReservation) throws ReservationNotFoundException, CreateNewReservationException, InputDataValidationException {

        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(newReservation);

        if (constraintViolations.isEmpty()) {
            if (newReservation != null) {

                em.persist(newReservation);

                em.flush();
                newReservation.getRooms();
                newReservation.getNumberOfUpgradedRooms();

                return newReservation;
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
    public void deleteReservation(Long reservationId) throws ReservationNotFoundException {
        Reservation reservationToRemove = retrieveReservationByReservationId(reservationId);

        List<Room> rooms = reservationToRemove.getRooms();

        for (Room room : rooms) {
            room.getReservations().remove(reservationToRemove);
        }

        em.remove(reservationToRemove);
    }

    @Override
    public List<Reservation> retrieveAllReservations() {
        Query query = em.createQuery("SELECT r FROM Reservation r ORDER BY r.reservationId ASC");

        return query.getResultList();
    }

    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.reservationId = :inReservationId");
        query.setParameter("inReservationId", reservationId);

        try {
            return (Reservation) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ReservationNotFoundException("Reservation Id " + reservationId + " does not exist!");
        }
    }

    @Override
    public List<Reservation> retrieveReservationsByBookingDate(Date bookingDateTime) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.bookingDateTime = :inBookingDateTime");
        query.setParameter("inBookingDateTime", bookingDateTime);

        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("No reservations exist for " + bookingDateTime);
        }
    }

    public List<Reservation> retrievePendingReservationsByStartDate(Date startDate) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :inStartDate AND r.status = :inStatus");
        query.setParameter("inStartDate", startDate);
        query.setParameter("inStatus", ReservationStatusEnum.PENDING);

        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("No pending reservations exist for " + startDate);
        }
    }

    @Override
    public void allocateRoomToCurrentDayReservations(Date startDate) {

        List<Reservation> reservations = new ArrayList<>();

        try {
            reservations = retrievePendingReservationsByStartDate(startDate);
        } catch (ReservationNotFoundException ex) {
            System.out.println("Unable to allocate rooms as " + ex.getMessage());
        }

        for (Reservation reservation : reservations) {
            RoomType currentRoomType = reservation.getRoomType();

            int numberOfRooms = reservation.getNumberOfRooms();

            List<Room> rooms = new ArrayList<>();

            List<Room> retrievedRooms = roomSessionBeanLocal.retrieveListOfRoomsAvailableForBookingByRoomType(reservation.getStartDate(), reservation.getEndDate(), currentRoomType.getRoomTypeId());
            
            for (Room r : retrievedRooms){
                System.out.println(r.getReservations().size());

            }

            boolean upgraded = false;

            if (retrievedRooms.size() >= numberOfRooms) {
                for (int i = 0; i < numberOfRooms; i++) {
                    rooms.add(retrievedRooms.get(i));
                }
            } else if (retrievedRooms.size() >= 0) {
                for (Room room : retrievedRooms) {
                    rooms.add(room);
                }
                int roomsNeeded = numberOfRooms - retrievedRooms.size();
                currentRoomType = currentRoomType.getNextHighestRoomType();
                System.out.println(currentRoomType.getNextHighestRoomType());
                if (currentRoomType == null) {
                    reservation.setStatus(ReservationStatusEnum.REJECTED);
                    break;
                } else {
                    retrievedRooms = roomSessionBeanLocal.retrieveListOfRoomsAvailableForBookingByRoomType(reservation.getStartDate(), reservation.getEndDate(), currentRoomType.getRoomTypeId());
                    if (retrievedRooms.size() >= roomsNeeded) {
                        for (int i = 0; i < roomsNeeded; i++) {
                            rooms.add(retrievedRooms.get(i));
                        }
                    } else {
                        reservation.setStatus(ReservationStatusEnum.REJECTED);
                        break;
                    }
                }
            }

            if (numberOfRooms == rooms.size()) {
                for (Room room : rooms) {
                    reservation.getRooms().add(room);
                    room.getReservations().add(reservation);
                    if (room.getRoomType().equals(reservation.getRoomType().getNextHighestRoomType())) {
                        upgraded = true;
                    }
                }
            }

            if (upgraded) {
                reservation.setStatus(ReservationStatusEnum.UPGRADED);
            } else {
                reservation.setStatus(ReservationStatusEnum.ALLOCATED);
            }

        }
    }

    @Override
    public void allocateRoomToReservation(Long reservationId) {

        try {
            Reservation reservation = retrieveReservationByReservationId(reservationId);

            RoomType currentRoomType = reservation.getRoomType();

            int numberOfRooms = reservation.getNumberOfRooms();

            List<Room> rooms = new ArrayList<>();

            List<Room> retrievedRooms = roomSessionBeanLocal.retrieveListOfRoomsAvailableForBookingByRoomType(reservation.getStartDate(), reservation.getEndDate(), currentRoomType.getRoomTypeId());

            System.out.println(retrievedRooms.size());

            boolean upgraded = false;

            if (retrievedRooms.size() >= numberOfRooms) {
                for (int i = 0; i < numberOfRooms; i++) {
                    rooms.add(retrievedRooms.get(i));
                }
            } else if (retrievedRooms.size() >= 0) {
                for (Room room : retrievedRooms) {
                    rooms.add(room);
                }
                int roomsNeeded = numberOfRooms - retrievedRooms.size();
                currentRoomType = currentRoomType.getNextHighestRoomType();
                if (currentRoomType == null) {
                    reservation.setStatus(ReservationStatusEnum.REJECTED);
                } else {
                    retrievedRooms = roomSessionBeanLocal.retrieveListOfRoomsAvailableForBookingByRoomType(reservation.getStartDate(), reservation.getEndDate(), currentRoomType.getRoomTypeId());
                    if (retrievedRooms.size() >= roomsNeeded) {
                        for (int i = 0; i < roomsNeeded; i++) {
                            rooms.add(retrievedRooms.get(i));
                        }
                    } else {
                        reservation.setStatus(ReservationStatusEnum.REJECTED);
                    }
                }
            }

            if (numberOfRooms == rooms.size()) {
                for (Room room : rooms) {
                    reservation.getRooms().add(room);
                    room.getReservations().add(reservation);
                    if (room.getRoomType().equals(reservation.getRoomType().getNextHighestRoomType())) {
                        upgraded = true;
                    }
                }
            }

            if (upgraded) {
                reservation.setStatus(ReservationStatusEnum.UPGRADED);
            } else {
                reservation.setStatus(ReservationStatusEnum.ALLOCATED);
            }

        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public String retrieveRoomsAllocatedInString(Long reservationId) throws ReservationNotFoundException {
        List<Room> reservedRooms = retrieveReservationByReservationId(reservationId).getRooms();
        String noRoomsAvailableString = "No Rooms Allocated Yet!";
        List<String> roomsAssignedArray = new ArrayList<>();

        if (reservedRooms.isEmpty()) {
            return noRoomsAvailableString;
        } else {
            for (Room r : reservedRooms) {
                roomsAssignedArray.add(r.getRoomNumber());
            }
        }
        return roomsAssignedArray.toString();
    }

    @Override
    public List<Reservation> retrieveUpgradedReservations(Date startDate) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :inStartDate AND r.status = :StatusUpgraded");
        query.setParameter("inStartDate", startDate);
        query.setParameter("StatusUpgraded", ReservationStatusEnum.UPGRADED);

        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("No upgraded reservations exist for " + startDate);
        }
    }

    @Override
    public List<Reservation> retrieveRejectedReservations(Date startDate) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :inStartDate AND r.status = :StatusRejected");
        query.setParameter("inStartDate", startDate);
        query.setParameter("StatusRejected", ReservationStatusEnum.REJECTED);

        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("No rejected reservations exist for " + startDate);
        }
    }

    @Override
    public int getNumberOfUpgradedRooms(Reservation reservation) {
        int upgradedRooms = 0;
        
        List<Room> rooms = reservation.getRooms();
        
        RoomType roomType = reservation.getRoomType();

        for (Room room : rooms) {
            if (!room.getRoomType().equals(roomType)) {
                upgradedRooms++;
            }
        }

        return upgradedRooms;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
