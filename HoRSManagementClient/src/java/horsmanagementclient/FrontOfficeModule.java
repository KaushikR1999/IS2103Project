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
import entity.Reservation;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRightsEnum;
import util.enumeration.ReservationStatusEnum;
import util.enumeration.ReservationTypeEnum;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.NoRateAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.ReservationNotFoundException;
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
                    doWalkInSearchRoom();
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
            Date bookingDateTime = new Date();
            Integer numberOfRooms;

            System.out.println("*** HoRS Management Client :: Front Office :: Walk-In Search Room ***\n");
            System.out.print("Enter CheckIn Date (dd/mm/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter CheckOut Date (dd/mm/yyyy)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            if (endDate.equals(startDate)){
                throw new SameDayReservationException("Start and end date cannot be on the same day!");
            }
            System.out.print("Enter Number of Rooms> ");
            numberOfRooms = scanner.nextInt();
            
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Room Type ID", "Room Type", "Price Per Room", "NumOfRooms", "Room Capacity", "Room Beds");
            try{
                
                for (RoomType rt : roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms)) {
                    System.out.printf("%8s%20s%20d%15d%20d%20d\n", rt.getRoomTypeId().toString(), rt.getTypeName(), roomRateSessionBeanRemote.calculateWalkInReservations(startDate, endDate, rt.getRoomTypeId()), roomSessionBeanRemote.retrieveRoomsAvailableForBookingByRoomType(startDate, endDate, rt.getRoomTypeId()), rt.getCapacity(), rt.getBed());
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
                Reservation newReservation = new Reservation();

                while(true) {
                 try {
                    List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms);
                    System.out.println("*** HoRS Management Client :: Front Office :: Walk-In Reserve Room ***\n");                
                    String output = "Select Desired Room Type to Reserve (";
                    int i = 1;
                    for (RoomType roomType: roomTypes) {
                        output += i + ": " + roomType.getTypeName();
                        i++;
                        if (i <= roomTypes.size()) {
                            output += ", ";
                        }
                    }
                    output += ")> ";

                    System.out.print(output);
                    Integer roomTypeInt = scanner.nextInt();

                    if (roomTypeInt >= 1 && roomTypeInt <= roomTypes.size()) {
                        newReservation.setRoomType(roomTypes.get(roomTypeInt-1));
                        newReservation.setTotalReservationFee(roomRateSessionBeanRemote.calculateWalkInReservations(startDate, endDate, roomTypes.get(roomTypeInt-1).getRoomTypeId()));
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                     } catch (NoRateAvailableException | NoRoomTypeAvailableException ex) {
                    System.out.println(ex.getMessage() + "\n");
                    break;
                }

                }
                newReservation.setStartDate(startDate);
                newReservation.setEndDate(endDate);
                newReservation.setNumberOfRooms(numberOfRooms);
                newReservation.setStatus(ReservationStatusEnum.PENDING);
                newReservation.setReservationType(ReservationTypeEnum.WALK_IN);
                newReservation.setBookingDateTime(new Date());

                Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(newReservation);

                if(constraintViolations.isEmpty())
                {        
                    try
                    {
                        newReservation = reservationSessionBeanRemote.createNewReservation(newReservation);

                        System.out.println("Reservation is a success!: " + newReservation.getReservationId()+ "\n");
                    }

                    catch(CreateNewReservationException ex)
                    {
                        System.out.println("An error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
                    }
                    catch(ReservationNotFoundException ex)
                    {
                        System.out.println("An unknown error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
                    }
                    catch(InputDataValidationException ex)
                    {
                        System.out.println(ex.getMessage() + "\n");
                    }
                }
                else
                {
                    showInputDataValidationErrorsForReservation(constraintViolations);
                }
                
                Date compareStartDate = setTimeToMidnight(startDate);
                Date compareBookingDate = setTimeToMidnight(bookingDateTime);
                Date currentDayTwoAm = setTimeToTwoAm(bookingDateTime);
                if(compareStartDate.equals(compareBookingDate) && bookingDateTime.after(currentDayTwoAm)) {
                    reservationSessionBeanRemote.allocateRoomToReservation(newReservation.getReservationId());
                }

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
    
    public static Date setTimeToMidnight(Date date) {
    Calendar calendar = Calendar.getInstance();

    calendar.setTime( date );
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
    }
    
    public static Date setTimeToTwoAm(Date date) {
    Calendar calendar = Calendar.getInstance();

    calendar.setTime( date );
    calendar.set(Calendar.HOUR_OF_DAY, 2);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
}
    
    private void showInputDataValidationErrorsForReservation(Set<ConstraintViolation<Reservation>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
