/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.CreateNewReservationException_Exception;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidLoginCredentialException;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.NoRateAvailableException_Exception;
import ws.client.NoRoomTypeAvailableException_Exception;
import ws.client.Partner;
import ws.client.PartnerNotFoundException_Exception;
import ws.client.Reservation;
import ws.client.ReservationNotFoundException_Exception;
import ws.client.ReservationStatusEnum;
import ws.client.ReservationTypeEnum;
import ws.client.RoomType;
import ws.client.RoomTypeNotFoundException_Exception;

/**
 *
 * @author yuenz
 */
public class MainApp {
    
        private Partner currentPartner;

    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        
        
        while(true)
        {
            System.out.println("*** Holiday Reservation System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Search Hotel Room");
            System.out.println("3: View My Reservation Details");
            System.out.println("4: View All My Reservations");
            System.out.println("5: Exit\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    if(currentPartner == null)
                    {
                        try
                        {
                            doLogin();
                            System.out.println("Login successful as " + currentPartner.getUsername()+ " from " + currentPartner.getOrganisation() +" !\n");
                        }
                        catch(InvalidLoginCredentialException_Exception ex) 
                        {
                            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                        }
                    }
                }
                else if (response == 2)
                {
                    doSearchHotelRoom();
                }
                else if (response == 3)
                {
                    doViewMyReservationDetails();
                }
                else if (response == 4)
                {
                   doViewAllMyReservations();
                }
                else if (response == 5)
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
    
    private void doLogin() throws InvalidLoginCredentialException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** HoRS Reservation System :: Partner Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentPartner = partnerLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException_Exception("Missing login credential!", new InvalidLoginCredentialException());
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
            Date bookingDateTime = new Date();
            Integer numberOfRooms;

            System.out.println("*** Hotel Reservation System :: Search Hotel Room ***\n");
            System.out.print("Enter CheckIn Date (dd/mm/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter CheckOut Date (dd/mm/yyyy)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            GregorianCalendar startingDate = new GregorianCalendar();
            startingDate.setTime(startDate);
            XMLGregorianCalendar startingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(startingDate);
            GregorianCalendar endingDate = new GregorianCalendar();
            endingDate.setTime(endDate);
            XMLGregorianCalendar endingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(endingDate);
            /*if (endDate.equals(startDate)){
                throw new SameDayReservationException("Start and end date cannot be on the same day!");
            }*/
//            System.out.print("Enter Booking Date (dd/mm/yyyy)> ");
//            bookingDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Number of Rooms> ");
            numberOfRooms = scanner.nextInt();
            
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Room Type ID", "Room Type", "Price Per Room", "NumOfRooms", "Room Capacity", "Room Beds");
            try{
                
                for (RoomType rt : retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms)) {
                    System.out.printf("%8s%20s%20d%15d%20d%20d\n", rt.getRoomTypeId().toString(), rt.getTypeName(), calculateRoomRateOnlineReservations(startDate, endDate, rt.getRoomTypeId()), retrieveRoomsAvailableForBookingByRoomType(startDate, endDate, rt.getRoomTypeId()), rt.getCapacity(), rt.getBed());
                }
            } catch (NoRateAvailableException_Exception | NoRoomTypeAvailableException_Exception | DatatypeConfigurationException ex){
                System.out.println("An unknown error has occurred while retrieving available hotel rooms!: " + ex.getMessage() + "\n");
            }
            
            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                if(currentPartner != null)
                { 
                    Reservation newReservation = new Reservation();
                    
                    while(true) {
                     try {
                        List<RoomType> roomTypes = retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms);
                        System.out.println("*** HoRS Reservation Client :: Make Reservation ***\n");                
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
                            newReservation.setTotalReservationFee(calculateRoomRateOnlineReservations(startDate, endDate, roomTypes.get(roomTypeInt-1).getRoomTypeId()));
                            break;
                        } else {
                            System.out.println("Invalid option, please try again!\n");
                        }
                         } catch (NoRateAvailableException_Exception | NoRoomTypeAvailableException_Exception | DatatypeConfigurationException ex) {
                        System.out.println(ex.getMessage() + "\n");
                        break;
                    }

                    }
                    newReservation.setStartDate(startingDateAgain);
                    newReservation.setEndDate(endingDateAgain);
                    newReservation.setNumberOfRooms(numberOfRooms);
                    newReservation.setStatus(ReservationStatusEnum.PENDING);
                    newReservation.setReservationType(ReservationTypeEnum.ONLINE);
                    GregorianCalendar bookingDateTiming = new GregorianCalendar();
                    bookingDateTiming.setTime(new Date());
                    XMLGregorianCalendar bookingDateTimingAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(bookingDateTiming);
                    newReservation.setBookingDateTime(bookingDateTimingAgain);

                           
                        try
                        {
                            newReservation = createNewReservation(newReservation);
                            addPartnerToReservation(newReservation, currentPartner.getPartnerId());

                            System.out.println("Reservation is a success! Do remember your Reservation ID: " + newReservation.getReservationId()+ "\n");
                            
                        }
                        
                        catch(CreateNewReservationException_Exception | PartnerNotFoundException_Exception ex)
                        {
                            System.out.println("An error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
                        }
                        catch(ReservationNotFoundException_Exception ex)
                        {
                            System.out.println("An unknown error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
                        }
                        catch(InputDataValidationException_Exception ex)
                        {
                            System.out.println(ex.getMessage() + "\n");
                        }

                    Date compareStartDate = setTimeToMidnight(startDate);
                    Date compareBookingDate = setTimeToMidnight(bookingDateTime);
                    Date currentDayTwoAm = setTimeToTwoAm(bookingDateTime);
                    if(compareStartDate.equals(compareBookingDate) && bookingDateTime.after(currentDayTwoAm)) {
                        allocateRoomToReservation(newReservation.getReservationId());
                    }
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
        catch(DatatypeConfigurationException ex)
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
    
    private void doViewMyReservationDetails() {
        Long reservationId;
        Reservation r;
        
        if(currentPartner != null)
        {
            Scanner scanner = new Scanner(System.in);
        
            System.out.println("*** Holiday Reservation System :: View My Reservation ***\n");
            System.out.print("Enter reservationId > ");
            reservationId = scanner.nextLong();
            scanner.nextLine();

            try{
            
                r = retrieveReservationByReservationId(reservationId);  
                System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", "Reservation ID", "Booking Date Time", "Start Date", "End Date", "Room Type", "Price", "Booking Status", "Rooms Allocared");
                System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", r.getReservationId(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(r.getBookingDateTime().toGregorianCalendar().getTime()), new SimpleDateFormat("yyyy-MM-dd").format(r.getStartDate().toGregorianCalendar().getTime()), new SimpleDateFormat("yyyy-MM-dd").format(r.getEndDate().toGregorianCalendar().getTime()), retrieveRoomTypeByReservation(r).getTypeName(), r.getTotalReservationFee(), r.getStatus(), retrieveRoomsAllocatedInString(r.getReservationId()));
            
                } catch (ReservationNotFoundException_Exception | RoomTypeNotFoundException_Exception ex ) {
                
                System.out.println("Unable to get reservation details! :" +ex.getMessage());
            
                }
        
                System.out.print("Press any key to continue...> ");
        
                scanner.nextLine();
        
        } else {
            
            System.out.println("Please login first before viewing a reservation!\n");
        }
    }
    
    private void doViewAllMyReservations()
    {
        
        if(currentPartner != null)
        {
            Scanner scanner = new Scanner(System.in);
        
            System.out.println("*** HoRS Reservation Client :: View My Reservation Details ***\n");
        try{
               
            List<Reservation> reservations = retrieveReservationsByPartner(currentPartner.getPartnerId(), true);
            System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", "Reservation ID", "Booking Date Time", "Start Date", "End Date", "Room Type", "Price", "Booking Status", "Rooms Allocared");
        

            for(Reservation r : reservations){
                System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", r.getReservationId(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(r.getBookingDateTime().toGregorianCalendar().getTime()), new SimpleDateFormat("yyyy-MM-dd").format(r.getStartDate().toGregorianCalendar().getTime()), new SimpleDateFormat("yyyy-MM-dd").format(r.getEndDate().toGregorianCalendar().getTime()), retrieveRoomTypeByReservation(r).getTypeName(), r.getTotalReservationFee(), r.getStatus(), retrieveRoomsAllocatedInString(r.getReservationId()));
                }
            
            } catch (ReservationNotFoundException_Exception | RoomTypeNotFoundException_Exception | PartnerNotFoundException_Exception ex) {
                
            System.out.println("Unable to get reservation details! :" +ex.getMessage());
            
            }
        
            System.out.print("Press any key to continue...> ");
        
            scanner.nextLine();
        
        } else {
            System.out.println("Please login first before viewing your reservations!\n");
        }
    }

    
    private static Partner partnerLogin(java.lang.String username, java.lang.String password) throws InvalidLoginCredentialException_Exception {
        ws.client.PartnerWebService_Service service = new ws.client.PartnerWebService_Service();
        ws.client.PartnerWebService port = service.getPartnerWebServicePort();
        return port.partnerLogin(username, password);
    }
    
    private static Reservation retrieveReservationByReservationId(java.lang.Long reservationId) throws ReservationNotFoundException_Exception {
        ws.client.ReservationWebService_Service service = new ws.client.ReservationWebService_Service();
        ws.client.ReservationWebService port = service.getReservationWebServicePort();
        return port.retrieveReservationByReservationId(reservationId);
    }
    
    private static String retrieveRoomsAllocatedInString(java.lang.Long reservationId) throws ReservationNotFoundException_Exception {
        ws.client.ReservationWebService_Service service = new ws.client.ReservationWebService_Service();
        ws.client.ReservationWebService port = service.getReservationWebServicePort();
        return port.retrieveRoomsAllocatedInString(reservationId);
    }
    
    private static java.util.List<ws.client.RoomType> retrieveAllAvailableRoomTypesBasedOnSize(java.util.Date startDate, java.util.Date endDate, java.lang.Integer numOfRooms) throws NoRoomTypeAvailableException_Exception, DatatypeConfigurationException {
        ws.client.RoomTypeWebService_Service service = new ws.client.RoomTypeWebService_Service();
        ws.client.RoomTypeWebService port = service.getRoomTypeWebServicePort();
        GregorianCalendar startingDate = new GregorianCalendar();
        startingDate.setTime(startDate);
        XMLGregorianCalendar startingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(startingDate);
        GregorianCalendar endingDate = new GregorianCalendar();
        endingDate.setTime(endDate);
        XMLGregorianCalendar endingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(endingDate);
        return port.retrieveAllAvailableRoomTypesBasedOnSize(startingDateAgain, endingDateAgain, numOfRooms);
    }
    
    private static java.lang.Integer calculateRoomRateOnlineReservations(java.util.Date startDate, java.util.Date endDate, java.lang.Long inRoomTypeId) throws NoRateAvailableException_Exception, DatatypeConfigurationException {
        ws.client.RoomRateWebService_Service service = new ws.client.RoomRateWebService_Service();
        ws.client.RoomRateWebService port = service.getRoomRateWebServicePort();
        GregorianCalendar startingDate = new GregorianCalendar();
        startingDate.setTime(startDate);
        XMLGregorianCalendar startingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(startingDate);
        GregorianCalendar endingDate = new GregorianCalendar();
        endingDate.setTime(endDate);
        XMLGregorianCalendar endingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(endingDate);
        return port.calculateRoomRateOnlineReservations(startingDateAgain, endingDateAgain, inRoomTypeId);
    }
    
    private static Reservation createNewReservation(ws.client.Reservation newReservation) throws CreateNewReservationException_Exception, InputDataValidationException_Exception, ReservationNotFoundException_Exception {
        ws.client.ReservationWebService_Service service = new ws.client.ReservationWebService_Service();
        ws.client.ReservationWebService port = service.getReservationWebServicePort();
        return port.createNewReservation(newReservation);
    }
    
    private static void allocateRoomToReservation(java.lang.Long reservationId) {
        ws.client.ReservationWebService_Service service = new ws.client.ReservationWebService_Service();
        ws.client.ReservationWebService port = service.getReservationWebServicePort();
        port.allocateRoomToReservation(reservationId);
    }
    
    private static java.lang.Integer retrieveRoomsAvailableForBookingByRoomType(java.util.Date inStartDate, java.util.Date inEndDate, java.lang.Long inRoomTypeId) throws DatatypeConfigurationException {
        ws.client.RoomWebService_Service service = new ws.client.RoomWebService_Service();
        ws.client.RoomWebService port = service.getRoomWebServicePort();
        GregorianCalendar startingDate = new GregorianCalendar();
        startingDate.setTime(inStartDate);
        XMLGregorianCalendar startingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(startingDate);
        GregorianCalendar endingDate = new GregorianCalendar();
        endingDate.setTime(inEndDate);
        XMLGregorianCalendar endingDateAgain = DatatypeFactory.newInstance().newXMLGregorianCalendar(endingDate);
        return port.retrieveRoomsAvailableForBookingByRoomType(startingDateAgain, endingDateAgain, inRoomTypeId);
    }
    
    private static void addPartnerToReservation(ws.client.Reservation reservation, java.lang.Long partnerId) throws PartnerNotFoundException_Exception {
        ws.client.ReservationWebService_Service service = new ws.client.ReservationWebService_Service();
        ws.client.ReservationWebService port = service.getReservationWebServicePort();
        port.addPartnerToReservation(reservation, partnerId);
    }
    
    private static RoomType retrieveRoomTypeByReservation(ws.client.Reservation reservation) throws RoomTypeNotFoundException_Exception {
        ws.client.RoomTypeWebService_Service service = new ws.client.RoomTypeWebService_Service();
        ws.client.RoomTypeWebService port = service.getRoomTypeWebServicePort();
        return port.retrieveRoomTypeByReservation(reservation);
    }
    
    private static List<Reservation> retrieveReservationsByPartner(java.lang.Long partnerId, java.lang.Boolean loadReservation) throws ReservationNotFoundException_Exception, PartnerNotFoundException_Exception {
        ws.client.ReservationWebService_Service service = new ws.client.ReservationWebService_Service();
        ws.client.ReservationWebService port = service.getReservationWebServicePort();
        return port.retrieveReservationsByPartner(partnerId, loadReservation);
    }
    
}