/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.NoRoomTypeAvailableException;

/**
 *
 * @author yuenz
 */
@WebService(serviceName = "RoomTypeWebService")
@Stateless()
public class RoomTypeWebService {

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
        return roomTypeSessionBeanLocal.retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms);
    }
}
