/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
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
public class HotelOperationGeneralModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    
    
    private Employee currentEmployee;
    
    public HotelOperationGeneralModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationGeneralModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, RoomSessionBeanRemote roomRateSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, Employee currentEmployee) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuHotelOperationGeneral() throws InvalidAccessRightException
    {
        if(currentEmployee.getEmployeeRole()!= AccessRightsEnum.OPS_MANAGER)
        {
            throw new InvalidAccessRightException("You don't have OPS_MANAGER rights to access the general operation manager module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management Client :: Hotel Operation (General) ***\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: Update Room Type");
            System.out.println("4: Delete Room Type");
            System.out.println("5: View All Room Types");
            System.out.println("-----------------------");
            System.out.println("6: Create New Room");
            System.out.println("7: Update Room");
            System.out.println("8: Delete Room");
            System.out.println("9: View All Rooms");
            System.out.println("-----------------------");
            System.out.println("10: View Room Allocation Exception Report");
            System.out.println("11: Back\n");
            response = 0;
            
            while(response < 1 || response > 11)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    //doCreateNewRoomType();
                    System.out.println("not implemented yet");

                }
                else if(response == 2)
                {
                    //doViewRoomTypeDetails();
                    System.out.println("not implemented yet");
                }
                else if(response == 3)
                {
                    //doUpdateRoomType();
                    System.out.println("not implemented yet");
                }
                else if(response == 4)
                {
                    //doDeleteRoomType();
                    System.out.println("not implemented yet");
                }
                else if(response == 5)
                {
                    //doViewAllRoomTypes();
                    System.out.println("not implemented yet");
                }
                else if(response == 6)
                {
                    //doCreateNewRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 7)
                {
                    //doUpdateRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 8)
                {
                    //doDeleteRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 9)
                {
                    //doViewAllRooms();
                    System.out.println("not implemented yet");
                }
                else if(response == 10)
                {
                    //doViewAllRoomAllocationReport();
                    System.out.println("not implemented yet");
                }
                else if (response == 11)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 11)
            {
                break;
            }
        }
    }
    
}
