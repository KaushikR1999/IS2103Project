/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Partner;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author yuenz
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password)
            throws InvalidLoginCredentialException {
        return partnerSessionBeanLocal.partnerLogin(username, password);
    }
}
