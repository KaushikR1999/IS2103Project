/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewRoomRateException;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author kaushikr
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @Resource
    private EJBContext eJBContext;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomRateSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    /**
     *
     * @param roomTypeId
     * @param newRoomRate
     * @return
     * @throws RoomTypeNotFoundException
     * @throws CreateNewRoomRateException
     * @throws InputDataValidationException
     */
    @Override
    public Long createNewRoomRate(Long roomTypeId, RoomRate newRoomRate) throws RoomTypeNotFoundException, CreateNewRoomRateException, InputDataValidationException
    {
        
        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(newRoomRate);
        
        if (constraintViolations.isEmpty()) {
            if (newRoomRate != null) {
                RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(roomTypeId);
                newRoomRate.setRoomType(roomType);
                roomType.getRoomRates().add(newRoomRate);

                em.persist(newRoomRate);

                em.flush();

                return newRoomRate.getRoomRateId();
            } else {
                throw new CreateNewRoomRateException("Room Rate information not provided");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        

    }
    
    public List<RoomRate> retrieveAllRoomRates()
    {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr ORDER BY rr.roomRateId ASC");
        
        return query.getResultList();
    }
    
    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException
    {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        
        if(roomRate != null)
        {
            
            return roomRate;
        }
        else
        {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " does not exist!");
        }                
    }
    
   
    
    @Override
    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, InputDataValidationException {
        if (roomRate != null && roomRate.getRoomRateId() != null) {
            Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(roomRate);

            if (constraintViolations.isEmpty()) {
                
                RoomRate roomRateToUpdate = retrieveRoomRateByRoomRateId(roomRate.getRoomRateId());
                
                roomRateToUpdate.setName(roomRate.getName());
                roomRateToUpdate.setRoomRateType(roomRate.getRoomRateType());
                roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                roomRateToUpdate.setStartDate(roomRate.getStartDate());
                roomRateToUpdate.setEndDate(roomRate.getEndDate());
                
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomRateNotFoundException("Staff ID not provided for staff to be updated");
        }
    }
    
    public void deleteRoomRate(Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException, DeleteRoomRateException
    {
        RoomRate roomRateToRemove = retrieveRoomRateByRoomRateId(roomRateId);
        
        Long roomTypeId = roomRateToRemove.getRoomType().getRoomTypeId();
        
        List<Room> rooms = roomSessionBeanLocal.retrieveRoomsByRoomTypeId(roomTypeId);
        
        roomRateToRemove.getRoomType().getRoomRates().remove(roomRateToRemove);
        
        if (rooms.isEmpty()) {
            em.remove(roomRateToRemove);
        } 
        else {
            roomRateToRemove.setAssignable(false);
            throw new DeleteRoomRateException("Room Rate ID " + roomRateId + " is associated with existing room(s) and cannot be deleted! It will be disabled.");
        }
    }
    
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRate>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
