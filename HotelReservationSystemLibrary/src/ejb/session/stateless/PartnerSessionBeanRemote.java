/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.PartnerUsernameOrOrganisationExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaushikr
 */
@Remote
public interface PartnerSessionBeanRemote {
    
    public List<Partner> retrieveAllPartners();
    public Long createNewPartner(Partner newPartner) throws PartnerUsernameOrOrganisationExistException, UnknownPersistenceException, InputDataValidationException;
    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialException;
    public Partner retrievePartnerByUsername(String username) throws PartnerNotFoundException;
    public Partner retrievePartnerById(Long partnerId) throws PartnerNotFoundException;



}
