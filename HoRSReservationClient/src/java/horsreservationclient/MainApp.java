/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.GuestUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.NoRateAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yuenz
 */
public class MainApp {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private ReservationSessionBeanRemote  reservationSessionBeanRemote;
    
    
    private Guest currentGuest;

    public MainApp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote) {
        this();
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
    }

    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Reservation Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Register");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: View My Reservation Details");
            System.out.println("5: View All My Reservations");
            System.out.println("6: Exit\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    if(currentGuest == null)
                    {
                        try
                        {
                            doLogin();
                            System.out.println("Login successful as " + currentGuest.getUsername()+ "!\n");
                        }
                        catch(InvalidLoginCredentialException ex) 
                        {
                            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                        }
                    }
                }
                else if (response == 2)
                {
                    doRegisterAsGuest();
                }
                else if (response == 3)
                {
                    doSearchHotelRoom();
                }
                else if (response == 4)
                {
                    //doViewReservationDetails();
                }
                else if (response == 5)
                {
                   // doViewAllReservations();
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
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** HoRS Reservation Client :: Guest Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentGuest = guestSessionBeanRemote.guestLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
        
    private void doRegisterAsGuest()
    {
        Scanner scanner = new Scanner(System.in);
        Guest newGuest = new Guest();
        
        System.out.println("*** HoRS Reservation Client :: Register As Guest ***\n");
        System.out.print("Enter username> ");
        newGuest.setUsername(scanner.nextLine().trim());
        System.out.print("Enter password> ");
        newGuest.setPassword(scanner.nextLine().trim());
;
        
        Set<ConstraintViolation<Guest>>constraintViolations = validator.validate(newGuest);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long newStaffId = guestSessionBeanRemote.createNewGuest(newGuest);
                System.out.println("New staff created successfully!: " + newStaffId + "\n");
            }
            catch(GuestUsernameExistException ex)
            {
                System.out.println("An error has occurred while creating the new staff!: The user name already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new staff!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForGuest(constraintViolations);
        }
    }
    
    private void doSearchHotelRoom()
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
//            System.out.print("Enter Booking Date (dd/mm/yyyy)> ");
//            bookingDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Number of Rooms> ");
            numberOfRooms = scanner.nextInt();
            
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Option", "Room Type", "Price", "NumOfRooms", "Room Capacity", "Room Beds");
            try{
                
                for (RoomType rt : roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypes()) {
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
                if(currentGuest != null)
                { 
                    System.out.println("Let's hope it works...");
                   /* try 
                    {
                        Long newTransactionId = holidayReservationSessionBeanRemote.reserveHoliday(currentCustomer.getCustomerId(), paymentMode, creditCardNumber);
                        
                        System.out.println("Reservation of holiday completed successfully!: " + newTransactionId + "\n");
                    } 
                    catch (CheckoutException ex) 
                    {
                        System.out.println("An error has occurred while making the reservation: " + ex.getMessage() + "\n");
                    }*/
                }
                else
                {
                    System.out.println("Please login first before making a reservation!\n");
                }
            }
        }
        catch(ParseException ex)
        {
            System.out.println("Invalid date input!\n");
        }
    }
    
    
        
    private void showInputDataValidationErrorsForGuest(Set<ConstraintViolation<Guest>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
