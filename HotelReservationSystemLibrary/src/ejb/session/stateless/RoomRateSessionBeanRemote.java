/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CreateNewRoomRateException;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.NoRateAvailableException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author kaushikr
 */
@Remote
public interface RoomRateSessionBeanRemote {
    
    public Long createNewRoomRate(String roomTypeName, RoomRate newRoomRate) throws RoomTypeNotFoundException, CreateNewRoomRateException, InputDataValidationException;

    public List<RoomRate> retrieveAllRoomRates();

    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException;

    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, InputDataValidationException;

    public void deleteRoomRate(Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException, DeleteRoomRateException;
    
    public int calculateRoomRateOnlineReservations(Date startDate, Date endDate, Long inRoomTypeId) throws NoRateAvailableException;
    
    public int calculateWalkInReservations(Date startDate, Date endDate, Long inRoomTypeId) throws NoRateAvailableException;


}
