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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RoomRateTypeEnum;
import util.enumeration.RoomStatusEnum;
import util.exception.CreateNewRoomRateException;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.NoRateAvailableException;
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
     * @param roomTypeName
     * @param newRoomRate
     * @return
     * @throws RoomTypeNotFoundException
     * @throws CreateNewRoomRateException
     * @throws InputDataValidationException
     */
    @Override
    public Long createNewRoomRate(String roomTypeName, RoomRate newRoomRate) throws RoomTypeNotFoundException, CreateNewRoomRateException, InputDataValidationException
    {
        
        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(newRoomRate);
        
        if (constraintViolations.isEmpty()) {
            if (newRoomRate != null) {
                RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
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
    
    @Override
    public List<RoomRate> retrieveAllRoomRates()
    {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr ORDER BY rr.roomRateId ASC");
        
        return query.getResultList();
    }
    
    @Override
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
    @Override
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
    
    @Override
    public int calculateRoomRateOnlineReservations(Date startDate, Date endDate, Long inRoomTypeId) throws NoRateAvailableException{
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType.roomTypeId = :inRoomTypeId AND rr.assignable = :true ORDER BY rr.roomRateType ASC");
        query.setParameter("inRoomTypeId", inRoomTypeId);
        query.setParameter("true", TRUE);
        List<RoomRate> roomRates = query.getResultList();
        
        for(RoomRate r : roomRates ) {
            System.out.println(r.getRoomRateType());
        }

        
        if(roomRates.isEmpty()){
            throw new NoRateAvailableException("No rate can be calculated 1!");
        }
        
        //set the calendar dates
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int price = 0;
        
        
        
       for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime())
       {
           System.out.println("Counting date is" + date);
           boolean checker = false;
           
           for(RoomRate rr : roomRates){
               
               System.out.println(rr.getRatePerNight());
               

               if(rr.getRoomRateType().equals(RoomRateTypeEnum.PROMOTION) && (date.after(rr.getStartDate()) && date.before(rr.getEndDate()))  ){
                   price+=rr.getRatePerNight();
                   checker = true;
                   break;
               } else if (rr.getRoomRateType().equals(RoomRateTypeEnum.PEAK) && (date.after(rr.getStartDate()) && date.before(rr.getEndDate())) ){
                   price+=rr.getRatePerNight();
                   checker = true;
                   break;
               } else if (rr.getRoomRateType().equals(RoomRateTypeEnum.NORMAL)){
                   price+=rr.getRatePerNight();
                   checker = true;
                   break;
               } else {
               }
               
           }
           
           if (checker = false){
                   throw new NoRateAvailableException("No rate can be calculated for one of the nights!");
           }
           
       }
       System.out.println(price);
       return price;

    }
    
       /* public void calculateWalkInReservations(Date startDate, Date endDate, Long inRoomTypeId) throws NoRateAvailableException{
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType.roomTypeId = :inRoomTypeId AND rr.assignable = :true AND rr.roomRateType =: DesiredEnum");
        query.setParameter("inRoomTypeId", inRoomTypeId);
        query.setParameter("true", TRUE);
        query.setParameter("DesiredEnum", RoomRateTypeEnum.PUBLISHED);
        
        //No Result Exception
        RoomRate roomRate = (RoomRate) query.getSingleResult();
       
        
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.add(Calendar.DATE, -1);
        int price = 0;
        
       for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime())
       {
           System.out.println("Counting date is" + date);
           
           for(RoomRate rr : roomRates){

               if(rr.getRoomRateType().equals(RoomRateTypeEnum.PROMOTION) && (date.after(rr.getStartDate()) && date.before(rr.getEndDate()))){
                   price+=rr.getRatePerNight();
               } else if (rr.getRoomRateType().equals(RoomRateTypeEnum.PEAK) && (date.isAfter(date.after(rr.getStartDate()) && d.before(rr.getEndDate())))){
                   price+=rr.getRatePerNight();
               } else if (rr.getRoomRateType().equals(RoomRateTypeEnum.NORMAL)){
                   price+=rr.getRatePerNight();
               } else {
                   throw new NoRateAvailableException("No rate can be calculated!");
               }
               
           }
           
       }
    
    }*/
    
    
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
