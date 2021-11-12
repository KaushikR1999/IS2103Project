/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Reservation;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.NoRoomTypeAvailableException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author yuenz
 */
@WebService(serviceName = "RoomTypeWebService")
@Stateless()
public class RoomTypeWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    
    

    /**
     * This is a sample web service operation
     */
        @WebMethod(operationName = "retrieveAllAvailableRoomTypesBasedOnSize")
    public List<RoomType> retrieveAllAvailableRoomTypesBasedOnSize(@WebParam(name = "startDate") Date startDate,
                                                    @WebParam(name = "endDate") Date endDate,
                                                    @WebParam(name = "numberOfRooms") int numberOfRooms) 
                                throws NoRoomTypeAvailableException
    {    
        
         List<RoomType> roomTypes = roomTypeSessionBeanLocal.retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms);
        
        for(RoomType rt : roomTypes)
        {
            em.detach(rt);
            
            for(RoomRate rr : rt.getRoomRates())
            {
                em.detach(rr);
                rr.setRoomType(null);
            }
        }
        
        return roomTypes;
    }
    
    @WebMethod(operationName = "retrieveRoomTypeByReservation")
    public RoomType retrieveRoomTypeByReservation(@WebParam(name = "reservation") Reservation reservation) throws RoomTypeNotFoundException
    {     
        
       RoomType ansRoomType = roomTypeSessionBeanLocal.retrieveRoomTypeByReservation(reservation);
       em.detach(ansRoomType);
       
        for(RoomRate rr : ansRoomType.getRoomRates()) {
            em.detach(rr);
            rr.setRoomType(null);
        }
        ansRoomType.getRoomRates().clear();
        return ansRoomType;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
