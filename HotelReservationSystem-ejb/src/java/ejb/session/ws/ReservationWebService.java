/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Reservation;
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
        return reservationSessionBeanLocal.createNewReservation(newReservation);
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
