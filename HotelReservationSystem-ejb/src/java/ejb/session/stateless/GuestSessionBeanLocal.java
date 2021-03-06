/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Local;
import util.exception.GuestNotFoundException;
import util.exception.GuestUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yuenz
 */
@Local
public interface GuestSessionBeanLocal {

    public Guest retrieveGuestByUsername(String username) throws GuestNotFoundException;

    public Guest guestLogin(String username, String password) throws InvalidLoginCredentialException;

    public Long createNewGuest(Guest newGuest) throws GuestUsernameExistException, UnknownPersistenceException, InputDataValidationException;
    
}
