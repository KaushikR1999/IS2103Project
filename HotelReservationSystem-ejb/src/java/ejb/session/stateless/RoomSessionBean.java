/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RoomStatusEnum;

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
    public List<Room> retrieveRoomsByRoomTypeId(Long roomTypeId)
    {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :inRoomTypeId");
        query.setParameter("inRoomTypeId", roomTypeId);
        
        return query.getResultList();
    }
    
    public int retrieveRoomsAvailableForBooking(Date inStartDate, Date inEndDate, Long inRoomTypeId){
        
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :inRoomTypeId AND r.assignable = true AND r.roomStatus = RoomStatusAvailable");
        query.setParameter("inRoomTypeId", inRoomTypeId);
        query.setParameter("true", true);
        query.setParameter("RoomStatusAvailable", RoomStatusEnum.AVAILABLE);
        List<Room> finalRoomsAvailable = new ArrayList<>();
        
        List<Room> roomsAvailable = query.getResultList();
        
        for ( Room r : roomsAvailable) {
        
        List<Reservation> roomReservations = r.getReservations();
        
            for (Reservation res : roomReservations){
            
                Date resStartDate = res.getStartDate();
                Date resEndDate = res.getEndDate();
            
                if(inStartDate.before(resStartDate) && inEndDate.after(resStartDate) ||
                inStartDate.before(resEndDate) && inEndDate.after(resEndDate) ||
                inStartDate.before(resStartDate) && inEndDate.after(resEndDate) ||
                inStartDate.after(resStartDate) && inEndDate.before(resEndDate)  )
                {
                    continue;
                } else {
                    finalRoomsAvailable.add(r);
                }
            }   
        }
        return finalRoomsAvailable.size();
    }
}
