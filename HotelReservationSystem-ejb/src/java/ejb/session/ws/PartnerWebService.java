/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.RoomRate;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author yuenz
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password)
            throws InvalidLoginCredentialException {
        
        Partner ansPartner = partnerSessionBeanLocal.partnerLogin(username, password);
        
        /*for(Reservation r : ansPartner.getPartnerReservations()){
        em.detach(r.getRoomType());
        for(RoomRate rr : r.getRoomType().getRoomRates()) {
            em.detach(rr);
            rr.setRoomType(null);
    }
        }*/
        return ansPartner;
    }
}
