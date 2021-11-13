/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.RoomRateSessionBeanLocal;
import java.util.Date;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.NoRateAvailableException;

/**
 *
 * @author yuenz
 */
@WebService(serviceName = "RoomRateWebService")
@Stateless()
public class RoomRateWebService {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "calculateRoomRateOnlineReservations")
    public int calculateRoomRateOnlineReservations(@WebParam(name = "startDate") Date startDate,
                                                    @WebParam(name = "endDate") Date endDate,
                                                    @WebParam(name = "inRoomTypeId") Long inRoomTypeId) 
                                throws NoRateAvailableException
    {
        return roomRateSessionBeanLocal.calculateRoomRateOnlineReservations(startDate, endDate, inRoomTypeId);
    }
}
