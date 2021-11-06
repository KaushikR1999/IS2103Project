/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import util.enumeration.AccessRightsEnum;
import util.exception.InvalidAccessRightException;
/**
 *
 * @author yuenz
 */
public class SystemAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    
    private Employee currentEmployee;
    
    public SystemAdministrationModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, Employee currentEmployee){
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuSystemAdministration() throws InvalidAccessRightException
    {
        if(currentEmployee.getEmployeeRole()!= AccessRightsEnum.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMIN rights to access the system administration module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management Client :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-----------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    //doCreateNewEmployee();
                    System.out.println("not implemented yet");
                }
                else if(response == 2)
                {
                    //doViewAllEmployees();
                    System.out.println("not implemented yet");
                }
                else if(response == 3)
                {
                    //doCreateNewPartner();
                    System.out.println("not implemented yet");
                }
                else if(response == 4)
                {
                    //doViewAllPartners();
                    System.out.println("not implemented yet");
                }
                else if(response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 5)
            {
                break;
            }
        }
    }
    
}
