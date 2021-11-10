/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
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
@Remote
public interface RoomTypeSessionBeanRemote {
    public RoomType createNewRoomType(RoomType newRoomType) throws RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException;
    public List<RoomType> retrieveAllRoomTypes();
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException;
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException;
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException;
    public RoomType retrieveRoomTypeByRoomTypeName(String typeName) throws RoomTypeNotFoundException;
    public List<RoomType> retrieveAllAvailableRoomTypes() throws NoRoomTypeAvailableException;
    public List<RoomType> retrieveAllAvailableRoomTypesExceptCurrent(Long inRoomTypeId) throws NoRoomTypeAvailableException;
    public List<RoomType> retrieveAllAvailableRoomTypesBasedOnSize(Date startDate, Date endDate, int numOfRooms) throws NoRoomTypeAvailableException;








    
}
