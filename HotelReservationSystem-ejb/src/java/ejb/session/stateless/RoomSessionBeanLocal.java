/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;
import util.exception.CreateNewRoomException;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaushikr
 */
@Local
public interface RoomSessionBeanLocal {

    public List<Room> retrieveRoomsByRoomTypeId(Long roomTypeId);

    public Long createNewRoom(Room newRoom) throws RoomNotFoundException, CreateNewRoomException, InputDataValidationException, RoomNumberExistException, UnknownPersistenceException ;

    public List<Room> retrieveAllRooms();

    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;

    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException;

    public void updateRoom(Room room) throws RoomNotFoundException, InputDataValidationException;

    public int retrieveRoomsAvailableForBookingByRoomType(Date inStartDate, Date inEndDate, Long inRoomTypeId);

    public Room retrieveRoomByRoomNumber(String roomNumber) throws NoResultException;

    public List<Room> retrieveListOfRoomsAvailableForBookingByRoomType(Date inStartDate, Date inEndDate, Long inRoomTypeId);
    
}
