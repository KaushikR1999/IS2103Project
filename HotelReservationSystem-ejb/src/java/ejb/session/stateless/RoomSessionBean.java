/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewRoomException;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomNotFoundException;
import util.enumeration.RoomStatusEnum;
import util.exception.RoomNumberExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaushikr
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewRoom(Room newRoom) throws RoomNotFoundException, CreateNewRoomException, InputDataValidationException, RoomNumberExistException, UnknownPersistenceException 
    {
        
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(newRoom);
        
        if (constraintViolations.isEmpty()) {
            try {
                if (newRoom != null & newRoom.getRoomType() != null) {
                    em.persist(newRoom);

                    em.flush();

                    return newRoom.getRoomId();
                } else {
                    throw new CreateNewRoomException("Room Type information not provided");
                }
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RoomNumberExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        

    }
    
    @Override
    public List<Room> retrieveAllRooms()
    {
        Query query = em.createQuery("SELECT r FROM Room r ORDER BY r.roomId ASC");
        
        return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveRoomsByRoomTypeId(Long roomTypeId)
    {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :inRoomTypeId");
        query.setParameter("inRoomTypeId", roomTypeId);
        
        return query.getResultList();
    }
    
    @Override
    public int retrieveRoomsAvailableForBookingByRoomType(Date inStartDate, Date inEndDate, Long inRoomTypeId){
        
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :inRoomTypeId AND r.assignable = :true AND r.roomStatus = :RoomStatusAvailable");
        query.setParameter("inRoomTypeId", inRoomTypeId);
        query.setParameter("true", TRUE);
        query.setParameter("RoomStatusAvailable", RoomStatusEnum.AVAILABLE);
        List<Room> finalRoomsAvailable = new ArrayList<>();
        
        List<Room> roomsAvailable = query.getResultList();
        
        for ( Room r : roomsAvailable) {
        
        List<Reservation> roomReservations = r.getReservations();
        
        boolean available = TRUE;
        
            for (Reservation res : roomReservations){
            
                Date resStartDate = res.getStartDate();
                Date resEndDate = res.getEndDate();
            
                if(!(inStartDate.before(resStartDate) && inEndDate.after(resStartDate) ||
                inStartDate.before(resEndDate) && inEndDate.after(resEndDate) ||
                inStartDate.before(resStartDate) && inEndDate.after(resEndDate) ||
                inStartDate.after(resStartDate) && inEndDate.before(resEndDate))  )
                {
//                    finalRoomsAvailable.add(r);
                    break;
                } else {
                    available = FALSE;
                    continue;
                }
            }
            if (available == TRUE) {
                finalRoomsAvailable.add(r);
            }
        }
        return finalRoomsAvailable.size();
    }
        
    @Override
    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException
    {
        Room room = em.find(Room.class, roomId);
        
        if(room != null)
        {
            
            return room;
        }
        else
        {
            throw new RoomNotFoundException("Room ID " + roomId + " does not exist!");
        }                
    }
    
    @Override
    public void updateRoom(Room room) throws RoomNotFoundException, InputDataValidationException {
        if (room != null && room.getRoomId() != null) {
            Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);

            if (constraintViolations.isEmpty()) {

                Room roomToUpdate = retrieveRoomByRoomId(room.getRoomId());

                roomToUpdate.setRoomNumber(room.getRoomNumber());
                roomToUpdate.setRoomStatus(room.getRoomStatus());
                roomToUpdate.setRoomType(room.getRoomType());

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomNotFoundException("Room ID not provided for room to be updated");
        }
    }
    
    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException
    {
        Room roomToRemove = retrieveRoomByRoomId(roomId);
        
        List<Reservation> reservations = roomToRemove.getReservations();
        
        if(reservations.isEmpty())
        {
            em.remove(roomToRemove);
        }
        else
        {
            roomToRemove.setAssignable(false);
            throw new DeleteRoomException("Room ID " + roomId + " is currently being used and cannot be deleted! It will be disabled.");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
