/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Guest;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRightsEnum;
import util.exception.InvalidAccessRightException;
import util.exception.NoRateAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.SameDayReservationException;

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
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    
    private Employee currentEmployee;
    
    public FrontOfficeModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FrontOfficeModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, Employee currentEmployee) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
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
    
    private void doWalkInSearchRoom() 
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date startDate;
            Date endDate;
            Date bookingDateTime;
            Integer numberOfRooms;

            System.out.println("*** Hotel Reservation Client :: Search Hotel Room ***\n");
            System.out.print("Enter CheckIn Date (dd/mm/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter CheckOut Date (dd/mm/yyyy)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            if (endDate.equals(startDate)){
                throw new SameDayReservationException("Start and end date cannot be on the same day!");
            }
//            System.out.print("Enter Booking Date (dd/mm/yyyy)> ");
//            bookingDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Number of Rooms> ");
            numberOfRooms = scanner.nextInt();
            
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Option", "Room Type", "Price Per Room", "NumOfRooms", "Room Capacity", "Room Beds");
            try{
                
                for (RoomType rt : roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms)) {
                    System.out.printf("%8s%20s%20d%15d%20d%20d\n", rt.getRoomTypeId().toString(), rt.getTypeName(), roomRateSessionBeanRemote.calculateRoomRateOnlineReservations(startDate, endDate, rt.getRoomTypeId()), roomSessionBeanRemote.retrieveRoomsAvailableForBookingByRoomType(startDate, endDate, rt.getRoomTypeId()), rt.getCapacity(), rt.getBed());
                }
            } catch (NoRateAvailableException | NoRoomTypeAvailableException ex){
                System.out.println("An unknown error has occurred while retrieving available hotel rooms!: " + ex.getMessage() + "\n");
            }
            
            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
              
                }
                else
                {
                    System.out.println("Please login first before making a reservation!\n");
                }
        }
        catch(ParseException ex)
        {
            System.out.println("Invalid date input!\n");
        } 
        catch(SameDayReservationException ex)
        {
            System.out.println(ex.getMessage());
        } 
    }
    
    
  
}
