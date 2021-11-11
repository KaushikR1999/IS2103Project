/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.RoomSessionBeanLocal;
import java.util.Date;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author yuenz
 */
@WebService(serviceName = "RoomWebService")
@Stateless()
public class RoomWebService {

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "retrieveRoomsAvailableForBookingByRoomType")
    public int retrieveRoomsAvailableForBookingByRoomType(@WebParam(name = "inStartDate") Date inStartDate,
                                                    @WebParam(name = "inEndDate") Date inEndDate,
                                                    @WebParam(name = "inRoomTypeId") Long inRoomTypeId) 
    {
        
        return roomSessionBeanLocal.retrieveRoomsAvailableForBookingByRoomType(inStartDate, inEndDate, inRoomTypeId);
    }
}
