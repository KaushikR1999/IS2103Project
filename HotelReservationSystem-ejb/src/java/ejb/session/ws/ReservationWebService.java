/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author yuenz
 */
@WebService(serviceName = "ReservationWebService")
@Stateless()
public class ReservationWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "createNewReservation")
    public Reservation createNewReservation(@WebParam(name = "newReservation") Reservation newReservation)
            throws ReservationNotFoundException, CreateNewReservationException, InputDataValidationException
    {     
        
        Reservation reservation = reservationSessionBeanLocal.createNewReservation(newReservation);
        em.detach(reservation);
        
        for(Room r : reservation.getRooms()){
            em.detach(r);
            r.getReservations().clear();
            //em.detach(r.getRoomType());
            //r.getRoomType().getRoomRates().clear();
            }
        reservation.getRooms().clear();
        
        /*em.detach(reservation.getRoomType());
        for(RoomRate rr : reservation.getRoomType().getRoomRates()) {
            em.detach(rr);
           rr.setRoomType(null);
        }
        reservation.getRoomType().getRoomRates().clear();
        reservation.setRoomType(null);(*/

        if(reservation.getGuest() != null) {
        reservation.setGuest(null);
        em.detach(reservation.getGuest());
        }
        return reservation;
    }
    
    @WebMethod(operationName = "allocateRoomToReservation")
    public void allocateRoomToReservation(@WebParam(name = "reservationId") Long reservationId)
    {     
        reservationSessionBeanLocal.allocateRoomToReservation(reservationId);
    }
    
    @WebMethod(operationName = "retrieveReservationByReservationId")
    public Reservation retrieveReservationByReservationId(@WebParam(name = "reservationId") Long reservationId)
            throws ReservationNotFoundException
    {     
        Reservation reservation = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);
         em.detach(reservation);
        
        for(Room r : reservation.getRooms()){
            em.detach(r);
            r.getReservations().clear();
            //em.detach(r.getRoomType());
            //r.getRoomType().getRoomRates().clear();
            }
            reservation.getRooms().clear();
            

         //em.detach(reservation.getRoomType());
       //for(RoomRate rr : reservation.getRoomType().getRoomRates()) {
           //em.detach(rr);
            //rr.setRoomType(null);
        //}
        //reservation.getRoomType().getRoomRates().clear();
        //reservation.setRoomType(null);   
        //}

        if(reservation.getGuest() != null) {
        reservation.setGuest(null);
        em.detach(reservation.getGuest());
        }
        return reservation;
    }
    
    @WebMethod(operationName = "retrieveRoomsAllocatedInString")
    public String retrieveRoomsAllocatedInString(@WebParam(name = "reservationId") Long reservationId)
            throws ReservationNotFoundException
    {     
        return reservationSessionBeanLocal.retrieveRoomsAllocatedInString(reservationId);
    }
    
    @WebMethod(operationName = "addPartnerToReservation")
    public void addPartnerToReservation(@WebParam(name = "reservation") Reservation reservation, @WebParam(name = "partner") Partner partner)
    {     
        reservationSessionBeanLocal.addPartnerToReservation(reservation, partner);
    }
    
    @WebMethod(operationName = "retrieveReservationsByPartner")
    public List<Reservation> retrieveReservationsByPartner(@WebParam(name = "partner") Partner partner) throws ReservationNotFoundException
    {     
        List<Reservation> list = reservationSessionBeanLocal.retrieveReservationsByPartner(partner);
        for(Reservation r : list){
            em.detach(r);
            for(Room ro : r.getRooms()){
            em.detach(ro);
            ro.getReservations().clear();
            //em.detach(r.getRoomType());
            //r.getRoomType().getRoomRates().clear();
            }
            r.getRooms().clear();
            
            if(r.getGuest() != null) {
        r.setGuest(null);
        em.detach(r.getGuest());
        }
        }
        return list;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
