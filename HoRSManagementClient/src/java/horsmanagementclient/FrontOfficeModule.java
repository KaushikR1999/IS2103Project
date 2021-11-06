/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Employee;
import entity.Guest;
import java.util.Scanner;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRightsEnum;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author yuenz
 */
public class FrontOfficeModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    
    private Employee currentEmployee;
    
    public FrontOfficeModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FrontOfficeModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote, Employee currentEmployee) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuFrontOffice() throws InvalidAccessRightException
    {
        if(currentEmployee.getEmployeeRole()!= AccessRightsEnum.GUEST_RELATION_OFFICER)
        {
            throw new InvalidAccessRightException("You don't have GUEST_RELATION_OFFICER rights to access the front office module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management Client :: Front Office ***\n");
            System.out.println("1: Walk-in Search Room");
            System.out.println("-----------------------");
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("-----------------------");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    //doWalkInSearchRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 2)
                {
                    //doCheckInGuest();
                    System.out.println("not implemented yet");
                }
                else if(response == 3)
                {
                    //doCheckOutGuest();
                    System.out.println("not implemented yet");
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
                break;
            }
        }
    }
    
    
  
}
