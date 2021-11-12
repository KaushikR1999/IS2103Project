/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.enumeration.AccessRightsEnum;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author yuenz
 */
public class HotelOperationModule {
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RoomSessionBeanRemote  roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    
    private HotelOperationSalesModule hotelOperationSalesModule;
    private HotelOperationGeneralModule hotelOperationGeneralModule;
    
    private Employee currentEmployee;

    public HotelOperationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, Employee currentEmployee) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuHotelOperation() throws InvalidAccessRightException
    {
        if(currentEmployee.getEmployeeRole()!= AccessRightsEnum.OPS_MANAGER && currentEmployee.getEmployeeRole()!= AccessRightsEnum.SALES_MANAGER )
        {
            throw new InvalidAccessRightException("You don't have OPS_MANAGER or SALES_MANAGER rights to access the hotel operations module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management Client :: Hotel Operations ***\n");
            System.out.println("1: General Hotel Operations");
            System.out.println("2: Sales Hotel Operations");
            System.out.println("3: Back\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                 hotelOperationSalesModule = new HotelOperationSalesModule(employeeSessionBeanRemote, roomRateSessionBeanRemote, currentEmployee, roomTypeSessionBeanRemote);
                 hotelOperationGeneralModule = new HotelOperationGeneralModule(employeeSessionBeanRemote, roomSessionBeanRemote, roomTypeSessionBeanRemote, reservationSessionBeanRemote, currentEmployee);

                if(response == 1)
                {
                   try
                    {
                        hotelOperationGeneralModule.menuHotelOperationGeneral();
                    }
                    catch (InvalidAccessRightException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 2)
                {
                   try
                    {
                        hotelOperationSalesModule.menuHotelOperationSales();
                    }
                    catch (InvalidAccessRightException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }
    
}
