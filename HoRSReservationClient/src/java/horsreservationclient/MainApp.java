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
import entity.Reservation;
import entity.RoomType;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import static java.time.Instant.now;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.ReservationStatusEnum;
import util.enumeration.ReservationTypeEnum;
import util.exception.CreateNewReservationException;
import util.exception.GuestUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.NoRateAvailableException;
import util.exception.NoRoomAvailableException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.SameDayReservationException;
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
    private ReservationSessionBeanRemote reservationSessionBeanRemote;

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

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS Reservation Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Register");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: View My Reservation Details");
            System.out.println("5: View All My Reservations");
            System.out.println("6: Exit\n");
            response = 0;

            while (response < 1 || response > 6) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    if (currentGuest == null) {
                        try {
                            doLogin();
                            System.out.println("Login successful as " + currentGuest.getUsername() + "!\n");
                        } catch (InvalidLoginCredentialException ex) {
                            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                        }
                    }
                } else if (response == 2) {
                    doRegisterAsGuest();
                } else if (response == 3) {
                    doSearchHotelRoom();
                } else if (response == 4) {
                    doViewMyReservationDetails();
                } else if (response == 5) {
                    doViewAllMyReservations();
                } else if (response == 6) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 6) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** HoRS Reservation Client :: Guest Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            currentGuest = guestSessionBeanRemote.guestLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doRegisterAsGuest() {
        Scanner scanner = new Scanner(System.in);
        Guest newGuest = new Guest();

        System.out.println("*** HoRS Reservation Client :: Register As Guest ***\n");
        System.out.print("Enter username> ");
        newGuest.setUsername(scanner.nextLine().trim());
        System.out.print("Enter password> ");
        newGuest.setPassword(scanner.nextLine().trim());
        ;

        Set<ConstraintViolation<Guest>> constraintViolations = validator.validate(newGuest);

        if (constraintViolations.isEmpty()) {
            try {
                Long newGuestId = guestSessionBeanRemote.createNewGuest(newGuest);
                System.out.println("New guest created successfully!: " + newGuestId + "\n");
            } catch (GuestUsernameExistException ex) {
                System.out.println("An error has occurred while creating the new guest!: The user name already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new guest!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForGuest(constraintViolations);
        }
    }

    private void doSearchHotelRoom() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date startDate;
            Date endDate;
            Date bookingDateTime = new Date();
            Integer numberOfRooms;

            System.out.println("*** Hotel Reservation Client :: Search Hotel Room ***\n");
            System.out.print("Enter CheckIn Date (dd/mm/yyyy)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter CheckOut Date (dd/mm/yyyy)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            if (endDate.equals(startDate)) {
                throw new SameDayReservationException("Start and end date cannot be on the same day!");
            }
//            System.out.print("Enter Booking Date (dd/mm/yyyy)> ");
//            bookingDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Number of Rooms> ");
            numberOfRooms = scanner.nextInt();

            System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Room Type ID", "Room Type", "Price Per Room", "NumOfRooms", "Room Capacity", "Room Beds");
            try {

                for (RoomType rt : roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms)) {
                    System.out.printf("%8s%20s%20d%15d%20d%20d\n", rt.getRoomTypeId().toString(), rt.getTypeName(), roomRateSessionBeanRemote.calculateRoomRateOnlineReservations(startDate, endDate, rt.getRoomTypeId()), roomSessionBeanRemote.retrieveRoomsAvailableForBookingByRoomType(startDate, endDate, rt.getRoomTypeId()), rt.getCapacity(), rt.getBed());
                }
            } catch (NoRateAvailableException | NoRoomTypeAvailableException ex) {
                System.out.println("An unknown error has occurred while retrieving available hotel rooms!: " + ex.getMessage() + "\n");
            }

            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                if (currentGuest != null) {
                    Reservation newReservation = new Reservation();

                    while (true) {
                        try {
                            List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypesBasedOnSize(startDate, endDate, numberOfRooms);
                            System.out.println("*** HoRS Reservation Client :: Make Reservation ***\n");
                            String output = "Select Desired Room Type to Reserve (";
                            int i = 1;
                            for (RoomType roomType : roomTypes) {
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
                                newReservation.setRoomType(roomTypes.get(roomTypeInt - 1));
                                newReservation.setTotalReservationFee(roomRateSessionBeanRemote.calculateRoomRateOnlineReservations(startDate, endDate, roomTypes.get(roomTypeInt - 1).getRoomTypeId()));
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
                    newReservation.setReservationType(ReservationTypeEnum.ONLINE);
                    newReservation.setBookingDateTime(new Date());
                    newReservation.setGuest(currentGuest);

                    Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(newReservation);

                    if (constraintViolations.isEmpty()) {
                        try {
                            newReservation = reservationSessionBeanRemote.createNewReservation(newReservation);

                            System.out.println("Reservation is a success! Do remember your Reservation ID: " + newReservation.getReservationId() + "\n");
                            currentGuest.getReservations().add(newReservation);

                        } catch (CreateNewReservationException ex) {
                            System.out.println("An error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
                        } catch (ReservationNotFoundException ex) {
                            System.out.println("An unknown error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
                        } catch (InputDataValidationException ex) {
                            System.out.println(ex.getMessage() + "\n");
                        }
                    } else {
                        showInputDataValidationErrorsForReservation(constraintViolations);
                    }

                    Date compareStartDate = setTimeToMidnight(startDate);
                    Date compareBookingDate = setTimeToMidnight(bookingDateTime);
                    Date currentDayTwoAm = setTimeToTwoAm(bookingDateTime);
                    if (compareStartDate.equals(compareBookingDate) && bookingDateTime.after(currentDayTwoAm)) {
                        reservationSessionBeanRemote.allocateRoomToReservation(newReservation.getReservationId());
                    }
                } else {
                    System.out.println("Please login first before making a reservation!\n");
                }
            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (SameDayReservationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Date setTimeToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date setTimeToTwoAm(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private void doViewMyReservationDetails() {
        Long reservationId;
        Reservation r;

        if (currentGuest != null) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Reservation Client :: View My Reservation ***\n");
            System.out.print("Enter reservationId > ");
            reservationId = scanner.nextLong();
            scanner.nextLine();

            try {

                r = reservationSessionBeanRemote.retrieveReservationByReservationId(reservationId);
                System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", "Reservation ID", "Booking Date Time", "Start Date", "End Date", "Room Type", "Price", "Booking Status", "Rooms Allocared");
                System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", r.getReservationId(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(r.getBookingDateTime()), new SimpleDateFormat("yyyy-MM-dd").format(r.getStartDate()), new SimpleDateFormat("yyyy-MM-dd").format(r.getEndDate()), r.getRoomType().getTypeName(), r.getTotalReservationFee(), r.getStatus(), reservationSessionBeanRemote.retrieveRoomsAllocatedInString(r.getReservationId()));

            } catch (ReservationNotFoundException ex) {

                System.out.println("Unable to get reservation details! :" + ex.getMessage());

            }

            System.out.print("Press any key to continue...> ");

            scanner.nextLine();

        } else {

            System.out.println("Please login first before viewing a reservation!\n");
        }
    }

    private void doViewAllMyReservations() {

        if (currentGuest != null) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Reservation Client :: View My Reservation Details ***\n");

            try {
                List<Reservation> reservations = reservationSessionBeanRemote.retrieveGuestReservations(currentGuest);
                //            reservations.size();
                System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", "Reservation ID", "Booking Date Time", "Start Date", "End Date", "Room Type", "Price", "Booking Status", "Rooms Allocated");
                try {
                    for (Reservation r : reservations) {
                        System.out.printf("%19s%18s%17s%20s%20s%20s%20s%31s\n", r.getReservationId(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(r.getBookingDateTime()), new SimpleDateFormat("yyyy-MM-dd").format(r.getStartDate()), new SimpleDateFormat("yyyy-MM-dd").format(r.getEndDate()), r.getRoomType().getTypeName(), r.getTotalReservationFee(), r.getStatus(), reservationSessionBeanRemote.retrieveRoomsAllocatedInString(r.getReservationId()));
//                System.out.print(r.getStatus());
                    }

                } catch (ReservationNotFoundException ex) {

                    System.out.println("Unable to get reservation details! :" + ex.getMessage());

                }

                System.out.print("Press any key to continue...> ");

                scanner.nextLine();
            } catch (ReservationNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

        } else {
            System.out.println("Please login first before viewing your reservations!\n");
        }
    }

    private void showInputDataValidationErrorsForGuest(Set<ConstraintViolation<Guest>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    private void showInputDataValidationErrorsForReservation(Set<ConstraintViolation<Reservation>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

}
