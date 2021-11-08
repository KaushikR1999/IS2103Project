/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import static java.lang.Boolean.TRUE;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author kaushikr
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomTypeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public RoomType createNewRoomType(RoomType newRoomType) throws RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newRoomType);
                em.flush();

                return newRoomType;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new RoomTypeNameExistException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public RoomType retrieveRoomTypeByRoomTypeName(String typeName) throws RoomTypeNotFoundException
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.typeName = :inTypeName");
        query.setParameter("inTypeName", typeName);
        
        try
        {
            return (RoomType)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomTypeNotFoundException("Room Type Name " + typeName + " does not exist!");
        }
    }
    
    @Override
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException
    {
        if(roomType != null && roomType.getRoomTypeId()!= null)
        {
            Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
            if(constraintViolations.isEmpty())
            {
                RoomType roomTypeToUpdate = retrieveRoomTypeByRoomTypeId(roomType.getRoomTypeId());

                if(roomTypeToUpdate.getRoomTypeId().equals(roomType.getRoomTypeId()))
                {
                    roomTypeToUpdate.setTypeName(roomType.getTypeName());
                    roomTypeToUpdate.setDescription(roomType.getDescription());
                    roomTypeToUpdate.setSize(roomType.getSize());
                    roomTypeToUpdate.setBed(roomType.getBed());
                    roomTypeToUpdate.setCapacity(roomType.getCapacity());
                    roomTypeToUpdate.setAmenities(roomType.getAmenities());
                    roomTypeToUpdate.setNextHighestRoomType(roomType.getNextHighestRoomType());
                }
                else
                {
                    throw new UpdateRoomTypeException("ID of room type record to be updated does not match the existing record");
                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new RoomTypeNotFoundException("Product ID not provided for product to be updated");
        }
    }
    
    @Override
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException
    {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        
        if(roomType != null)
        {
            return roomType;
        }
        else
        {
            throw new RoomTypeNotFoundException("Room Type ID " + roomTypeId + " does not exist!");
        }               
    }
    
    @Override
    public List<RoomType> retrieveAllAvailableRoomTypes() throws NoRoomTypeAvailableException
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.assignable = true");
//        query.setParameter("true", TRUE);
        List<RoomType> availableRoomType = query.getResultList();
        
        if(availableRoomType.isEmpty())
        {
            throw new NoRoomTypeAvailableException("There are no available Room types!");
        }
        else
        {
            return availableRoomType;
        }               
    }
    
    @Override
    public List<RoomType> retrieveAllAvailableRoomTypesExceptCurrent(Long inRoomTypeId) throws NoRoomTypeAvailableException
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.assignable = true AND rt.roomTypeId <> :inRoomTypeId");
        query.setParameter("true", TRUE);
        query.setParameter("inRoomTypeId", inRoomTypeId);
        List<RoomType> availableRoomType = query.getResultList();
        
        if(availableRoomType.isEmpty())
        {
            throw new NoRoomTypeAvailableException("There are no available Room types!");
        }
        else
        {
            return availableRoomType;
        }               
    }
    
    
    
    @Override
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException
    {
        RoomType roomTypeToRemove = retrieveRoomTypeByRoomTypeId(roomTypeId);
        
        List<Room> rooms = roomSessionBeanLocal.retrieveRoomsByRoomTypeId(roomTypeId);
        
        for(RoomRate rr : roomTypeToRemove.getRoomRates()) {
            em.remove(rr);
        }
        
        if(rooms.isEmpty())
        {
            em.remove(roomTypeToRemove);
        }
        else
        {
            roomTypeToRemove.setAssignable(false);
            throw new DeleteRoomTypeException("Product ID " + roomTypeId + " is associated with existing room(s) and cannot be deleted! It will be disabled.");
        }
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypes()
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt ORDER BY rt.typeName ASC");
        
        return query.getResultList();
    }
    
    /*public List<RoomType> retrieveAllRoomTypesAvailableForBooking(int numOfRooms)
    {
        
    }*/
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
