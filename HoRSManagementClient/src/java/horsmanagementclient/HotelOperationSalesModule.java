/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import entity.Employee;
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
public class HotelOperationSalesModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    
    private Employee currentEmployee;
    
    public HotelOperationSalesModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationSalesModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, Employee currentEmployee) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuHotelOperationSales() throws InvalidAccessRightException
    {
        if(currentEmployee.getEmployeeRole()!= AccessRightsEnum.SALES_MANAGER)
        {
            throw new InvalidAccessRightException("You don't have SALES_MANAGER rights to access the sales manager module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) ***\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details");
            System.out.println("3: Update Room Rate");
            System.out.println("4: Delete Room Rate");
            System.out.println("5: View All Room Rates");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    //doCreateNewRoomRate();
                    System.out.println("not implemented yet");

                }
                else if(response == 2)
                {
                    //doViewRoomRateDetails();
                    System.out.println("not implemented yet");
                }
                else if(response == 3)
                {
                    //doUpdateRoomRate();
                    System.out.println("not implemented yet");
                }
                else if(response == 4)
                {
                    //doDeleteRoomRate();
                    System.out.println("not implemented yet");
                }
                else if(response == 5)
                {
                    //doViewAllRoomRates();
                    System.out.println("not implemented yet");
                }
                else if (response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
    
    
    
    
}
