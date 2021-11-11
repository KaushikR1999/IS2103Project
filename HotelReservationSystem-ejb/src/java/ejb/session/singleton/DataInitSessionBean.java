/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AccessRightsEnum;
import util.enumeration.ReservationStatusEnum;
import util.enumeration.ReservationTypeEnum;
import util.enumeration.RoomRateTypeEnum;
import util.enumeration.RoomStatusEnum;

/**
 *
 * @author kaushikr
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public DataInitSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        if(em.find(Employee.class, 1l) == null)
        {
            initialiseData();
        }
    }
        
    private void initialiseData() {
        
        // employees
        
        Employee sysadmin = new Employee("sysadmin", "password", AccessRightsEnum.SYSTEM_ADMIN);
        Employee opmanager = new Employee("opmanager", "password", AccessRightsEnum.OPS_MANAGER);
        Employee salesmanager = new Employee("salesmanager", "password", AccessRightsEnum.SALES_MANAGER);
        Employee guestrelo = new Employee("guestrelo", "password", AccessRightsEnum.GUEST_RELATION_OFFICER);
        
        em.persist(sysadmin);
        em.flush();
        em.persist(opmanager);
        em.flush();
        em.persist(salesmanager);
        em.flush();
        em.persist(guestrelo);
        em.flush();
        
        // roomTypes String typeName, String description, double size, int bed, int capacity
        
        RoomType grandSuite = new RoomType("Grand Suite", "grand", 6, 6, 6);
        
        RoomType juniorSuite = new RoomType("Junior Suite", "junior", 5, 5, 5);
        juniorSuite.setNextHighestRoomType(grandSuite);
        
        RoomType familyRoom = new RoomType("Family Room", "family", 4, 4, 4);
        familyRoom.setNextHighestRoomType(juniorSuite);
        
        RoomType premierRoom = new RoomType("Premier Room", "premier", 3, 3, 3);
        premierRoom.setNextHighestRoomType(familyRoom);
        
        RoomType deluxeRoom = new RoomType("Deluxe Room", "deluxe", 2, 2, 2);
        deluxeRoom.setNextHighestRoomType(premierRoom);
        
        em.persist(grandSuite);
        em.flush();
        em.persist(juniorSuite);
        em.flush();
        em.persist(familyRoom);
        em.flush();
        em.persist(premierRoom);
        em.flush();
        em.persist(deluxeRoom);
        em.flush();
        
        // rooms String roomNumber, RoomStatusEnum roomStatus, RoomType roomType
        
        Room roomA = new Room("0101", RoomStatusEnum.AVAILABLE, deluxeRoom);
        Room roomB = new Room("0201", RoomStatusEnum.AVAILABLE, deluxeRoom);
        Room roomC = new Room("0301", RoomStatusEnum.AVAILABLE, deluxeRoom);
        Room roomD = new Room("0401", RoomStatusEnum.AVAILABLE, deluxeRoom);
        Room roomE = new Room("0501", RoomStatusEnum.AVAILABLE, deluxeRoom);
        
        Room roomF = new Room("0102", RoomStatusEnum.AVAILABLE, premierRoom);
        Room roomG = new Room("0202", RoomStatusEnum.AVAILABLE, premierRoom);
        Room roomH = new Room("0302", RoomStatusEnum.AVAILABLE, premierRoom);
        Room roomI = new Room("0402", RoomStatusEnum.AVAILABLE, premierRoom);
        Room roomJ = new Room("0502", RoomStatusEnum.AVAILABLE, premierRoom);
        
        Room roomK = new Room("0103", RoomStatusEnum.AVAILABLE, familyRoom);
        Room roomL = new Room("0203", RoomStatusEnum.AVAILABLE, familyRoom);
        Room roomM = new Room("0303", RoomStatusEnum.AVAILABLE, familyRoom);
        Room roomN = new Room("0403", RoomStatusEnum.AVAILABLE, familyRoom);
        Room roomO = new Room("0503", RoomStatusEnum.AVAILABLE, familyRoom);
        
        Room roomP = new Room("0104", RoomStatusEnum.AVAILABLE, juniorSuite);
        Room roomQ = new Room("0204", RoomStatusEnum.AVAILABLE, juniorSuite);
        Room roomR = new Room("0304", RoomStatusEnum.AVAILABLE, juniorSuite);
        Room roomS = new Room("0404", RoomStatusEnum.AVAILABLE, juniorSuite);
        Room roomT = new Room("0504", RoomStatusEnum.AVAILABLE, juniorSuite);
        
        Room roomU = new Room("0105", RoomStatusEnum.AVAILABLE, grandSuite);
        Room roomV = new Room("0205", RoomStatusEnum.AVAILABLE, grandSuite);
        Room roomW = new Room("0305", RoomStatusEnum.AVAILABLE, grandSuite);
        Room roomX = new Room("0405", RoomStatusEnum.AVAILABLE, grandSuite);
        Room roomY = new Room("0505", RoomStatusEnum.AVAILABLE, grandSuite);
        
        em.persist(roomA);
        em.flush();
        em.persist(roomB);
        em.flush();
        em.persist(roomC);
        em.flush();
        em.persist(roomD);
        em.flush();
        em.persist(roomE);
        em.flush();
        em.persist(roomF);
        em.flush();
        em.persist(roomG);
        em.flush();
        em.persist(roomH);
        em.flush();
        em.persist(roomI);
        em.flush();
        em.persist(roomJ);
        em.flush();
        em.persist(roomK);
        em.flush();
        em.persist(roomL);
        em.flush();
        em.persist(roomM);
        em.flush();
        em.persist(roomN);
        em.flush();
        em.persist(roomO);
        em.flush();
        em.persist(roomP);
        em.flush();
        em.persist(roomQ);
        em.flush();
        em.persist(roomR);
        em.flush();
        em.persist(roomS);
        em.flush();
        em.persist(roomT);
        em.flush();
        em.persist(roomU);
        em.flush();
        em.persist(roomV);
        em.flush();
        em.persist(roomW);
        em.flush();
        em.persist(roomX);
        em.flush();
        em.persist(roomY);
        em.flush();
        
        
        // roomRates RoomRateTypeEnum roomRateType, Date startDate, Date endDate, String name, double ratePerNight
        
//        private final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

        Date startDateA = new Date(121,11,11);
        Date endDateA = new Date(121, 11, 15);
        
        Date startDateC = new Date(121,11,11);
        Date endDateC = new Date(121, 11, 13);

        Date startDateE = new Date(121,10,11);
        Date endDateE = new Date(121, 10, 14);

        Date startDateG = new Date(121,7,11);
        Date endDateG = new Date(121, 7, 12);

        Date startDateI = new Date(121,11,20);
        Date endDateI = new Date(121, 11, 22);

        
        RoomRate rateA = new RoomRate(RoomRateTypeEnum.PUBLISHED, startDateA, endDateA, "Deluxe Room", 100);
        rateA.setRoomType(deluxeRoom);
        em.persist(rateA);
        em.flush();
        deluxeRoom.getRoomRates().add(rateA);
        
        RoomRate rateB = new RoomRate(RoomRateTypeEnum.NORMAL, null, null, "Deluxe Room", 50);
        rateB.setRoomType(deluxeRoom);
        em.persist(rateB);
        em.flush();
        deluxeRoom.getRoomRates().add(rateB);
        
        RoomRate rateC = new RoomRate(RoomRateTypeEnum.PUBLISHED, startDateC, endDateC, "Premier Room", 200);
        rateC.setRoomType(premierRoom);
        em.persist(rateC);
        em.flush();
        premierRoom.getRoomRates().add(rateC);
        
        RoomRate rateD = new RoomRate(RoomRateTypeEnum.NORMAL, null, null, "Premier Room", 100);
        rateD.setRoomType(premierRoom);
        em.persist(rateD);
        em.flush();
        premierRoom.getRoomRates().add(rateD);
        
        RoomRate rateE = new RoomRate(RoomRateTypeEnum.PUBLISHED, startDateE, endDateE, "Family Room", 300);
        rateE.setRoomType(familyRoom);
        em.persist(rateE);
        em.flush();
        familyRoom.getRoomRates().add(rateE);
        
        RoomRate rateF = new RoomRate(RoomRateTypeEnum.NORMAL, null, null, "Family Room", 150);
        rateF.setRoomType(familyRoom);
        em.persist(rateF);
        em.flush();
        familyRoom.getRoomRates().add(rateF);
        
        RoomRate rateG = new RoomRate(RoomRateTypeEnum.PUBLISHED, startDateG, endDateG, "Junior Suite", 400);
        rateG.setRoomType(juniorSuite);
        em.persist(rateG);
        em.flush();
        juniorSuite.getRoomRates().add(rateG);
        
        RoomRate rateH = new RoomRate(RoomRateTypeEnum.NORMAL, null, null, "Junior Suite", 200);
        rateH.setRoomType(juniorSuite);
        em.persist(rateH);
        em.flush();
        juniorSuite.getRoomRates().add(rateH);
        
        RoomRate rateI = new RoomRate(RoomRateTypeEnum.PUBLISHED, startDateI, endDateI, "Grand Suite", 500);
        rateI.setRoomType(grandSuite);
        em.persist(rateI);
        em.flush();
        grandSuite.getRoomRates().add(rateI);
        
        RoomRate rateJ = new RoomRate(RoomRateTypeEnum.NORMAL, null, null, "Grand Suite", 250);
        rateJ.setRoomType(grandSuite);
        em.persist(rateJ);
        em.flush();
        grandSuite.getRoomRates().add(rateJ);
        
        Date startDateX = new Date(121,11, 11);
        Date endDateX = new Date(121, 11, 15);
        Date bookingDateX = new Date(121, 10, 10);
        
        Date startDateY = new Date(121,11, 12);
        Date endDateY = new Date(121, 11, 14);
        Date bookingDateY = new Date(121, 10, 15);
        
        Date startDateZ = new Date(121,11, 16);
        Date endDateZ = new Date(121, 11, 19);
        Date bookingDateZ = new Date(121, 10, 17);
        
        Reservation reservationX = new Reservation (startDateX, endDateX, bookingDateX, ReservationStatusEnum.CONFIRMED, 2.0, ReservationTypeEnum.ONLINE, deluxeRoom, 2);
        em.persist(reservationX);
        em.flush();
        
        Reservation reservationY = new Reservation (startDateY, endDateY, bookingDateX, ReservationStatusEnum.CONFIRMED, 2.0, ReservationTypeEnum.ONLINE, deluxeRoom, 2);
        em.persist(reservationY);
        em.flush();
        
        Reservation reservationZ = new Reservation (startDateZ, endDateZ, bookingDateZ, ReservationStatusEnum.CONFIRMED, 2.0, ReservationTypeEnum.ONLINE, deluxeRoom, 2);
        em.persist(reservationZ);
        em.flush();
    }
        
}
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
