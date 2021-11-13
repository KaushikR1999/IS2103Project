/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Date;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRightsEnum;
import util.enumeration.RoomRateTypeEnum;
import util.exception.CreateNewRoomRateException;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author yuenz
 */
public class HotelOperationSalesModule {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    private Employee currentEmployee;

    private final SimpleDateFormat formatDate = new SimpleDateFormat("d/M/y");

    public HotelOperationSalesModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationSalesModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, Employee currentEmployee, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuHotelOperationSales() throws InvalidAccessRightException {
        if (currentEmployee.getEmployeeRole() != AccessRightsEnum.SALES_MANAGER) {
            throw new InvalidAccessRightException("You don't have SALES_MANAGER rights to access the sales manager module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) ***\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details");
            System.out.println("3: View All Room Rates");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewRoomRate();
//                    System.out.println("not implemented yet");

                } else if (response == 2) {
                    doViewRoomRateDetails();
//                    System.out.println("not implemented yet");
                } else if (response == 3) {
                    doViewAllRoomRates();
//                    doUpdateRoomRate();
//                    System.out.println("not implemented yet");
                } //                else if(response == 4)
                //                {
                ////                    doDeleteRoomRate();
                ////                    System.out.println("not implemented yet");
                //                }
                //                else if(response == 5)
                //                {
                //                    
                ////                    System.out.println("not implemented yet");
                //                }
                else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    private void doViewRoomRateDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: View Room Rate Details ***\n");
        System.out.print("Enter Room Rate ID> ");
        Long roomRateId = scanner.nextLong();
        
        while (true) {
            try {
                RoomRate roomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomRateId(roomRateId);
                System.out.printf("%8s%13s%20s%20s%20s%20s%20s\n", "Room Rate ID", "Name", "Room Type", "Rate Type", "Rate Per Night", "Start Date", "End Date");
                System.out.printf("%8s%15s%20s%20s%20s%20s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), roomRate.getRoomType().getTypeName(), roomRate.getRoomRateType(), roomRate.getRatePerNight(), formatDate.format(roomRate.getStartDate()), formatDate.format(roomRate.getEndDate()));
                System.out.println("------------------------");
                System.out.println("1: Update Room Rate");
                System.out.println("2: Delete Room Rate");
                System.out.println("3: Back\n");
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {
                    doUpdateRoomRate(roomRate);
                } else if (response == 2) {
                    doDeleteRoomRate(roomRate);
                } else if (response == 3) {
                    break;
                }
            } catch (RoomRateNotFoundException ex) {
                System.out.println("An error has occurred while retrieving room rate: " + ex.getMessage() + "\n");
            }
        }

    }

    private void doCreateNewRoomRate() {
        Scanner scanner = new Scanner(System.in);
        RoomRate newRoomRate = new RoomRate();
        Double doubleInput;

        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: Create New Room Rate ***\n");
        System.out.print("Enter Name> ");
        newRoomRate.setName(scanner.nextLine().trim());

        while (true) {
            System.out.print("Select Room Rate Type (1: Promotion, 2: Peak, 3: Normal, 4: Published)> ");
            Integer roomRateTypeInt = scanner.nextInt();

            if (roomRateTypeInt >= 1 && roomRateTypeInt <= 4) {
                newRoomRate.setRoomRateType(RoomRateTypeEnum.values()[roomRateTypeInt - 1]);
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        System.out.print("Enter Rate Per Night > ");
        doubleInput = scanner.nextDouble();
        if (doubleInput >= 0) {
            newRoomRate.setRatePerNight(doubleInput);
        } else {
            System.out.println("Invalid option, please try again!\n");
        }

        scanner.nextLine();

        if (!(newRoomRate.getRoomRateType().equals(RoomRateTypeEnum.PUBLISHED) || newRoomRate.getRoomRateType().equals(RoomRateTypeEnum.NORMAL))) {
            Date startDate = new Date();

            while (true) {
                System.out.print("Enter Start Date (dd/mm/yyyy)> ");
                try {
                    startDate = formatDate.parse(scanner.nextLine().trim());
                    if (startDate.after(new Date()) || formatDate.format(startDate).equals(formatDate.format(new Date()))) {
                        newRoomRate.setStartDate(startDate);
                        break;
                    } else {
                        throw new DateTimeException("Chosen date is in the past!");
                    }
                } catch (ParseException ex) {
                    System.out.println("An error has occurred while parsing date: " + ex.getMessage() + "\n");
                } catch (DateTimeException ex) {
                    System.out.println("An error has occurred in selecting the date: " + ex.getMessage() + "\n");
                }
            }

            Date endDate = new Date();

            while (true) {
                System.out.print("Enter End Date (dd/mm/yyyy)> ");
                try {
                    endDate = formatDate.parse(scanner.nextLine().trim());
                    if (endDate.after(new Date()) || formatDate.format(endDate).equals(formatDate.format(new Date()))) {
                        newRoomRate.setEndDate(endDate);
                        break;
                    } else {
                        throw new DateTimeException("Chosen date is in the past!");
                    }
                } catch (ParseException ex) {
                    System.out.println("An error has occurred while parsing date: " + ex.getMessage() + "\n");
                } catch (DateTimeException ex) {
                    System.out.println("An error has occurred in selecting the date: " + ex.getMessage() + "\n");
                }
            }
        }

//        System.out.print("Enter Room Type Name > ");
//        String roomTypeName = scanner.nextLine().trim();
        
        while (true) {
            try {
                List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypes();

                String output = "Select Next Highest Room Type";
                System.out.println(output);
                int i = 1;
                for (RoomType otherRoomType : roomTypes) {
                    output = i + ": " + otherRoomType.getTypeName();
                    i++;
                    if (i <= roomTypes.size()) {
                        System.out.println(output);
                    }
                }
                System.out.println(output);
                output = "> ";
                System.out.print(output);
                
                Integer roomTypeInt = scanner.nextInt();

                if (roomTypeInt >= 1 && roomTypeInt <= roomTypes.size()) {
//                    roomType.setNextHighestRoomType(roomTypes.get(roomTypeInt - 1));
                    newRoomRate.setRoomType(roomTypes.get(roomTypeInt-1));
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            } catch (NoRoomTypeAvailableException ex) {
                System.out.println(ex.getMessage() + "\n");
                break;
            }

        }

        Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(newRoomRate);

        if (constraintViolations.isEmpty()) {
            try {
                Long newRoomRateId = roomRateSessionBeanRemote.createNewRoomRate(newRoomRate);
                System.out.println("New room rate created successfully!: " + newRoomRateId + "\n");
            } catch (CreateNewRoomRateException | RoomTypeNotFoundException ex) {
                System.out.println("An unknown error has occurred while creating the new room rate!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }
    }

    private void doUpdateRoomRate(RoomRate roomRate) {
        Scanner scanner = new Scanner(System.in);
        String input;
        Double doubleInput;

        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: View Room Rate Details :: Update Room Rate ***\n");
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomRate.setName(input);
        }

        while (true) {
            System.out.print("Select Room Rate Type (0: No Change, 1: Published, 2: Normal, 3: Peak, 4: Promotion)> ");
            Integer roomRateTypeInt = scanner.nextInt();

            if (roomRateTypeInt >= 1 && roomRateTypeInt <= 4) {
                roomRate.setRoomRateType(RoomRateTypeEnum.values()[roomRateTypeInt - 1]);
                break;
            } else if (roomRateTypeInt == 0) {
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        System.out.print("Enter Rate Per Night (negative number if no change)> ");
        doubleInput = scanner.nextDouble();
        if (doubleInput >= 0) {
            roomRate.setRatePerNight(doubleInput);
        }

        scanner.nextLine();

        if (!(roomRate.getRoomRateType().equals(RoomRateTypeEnum.PUBLISHED) || roomRate.getRoomRateType().equals(RoomRateTypeEnum.NORMAL))) {
            Date startDate = new Date();

            while (true) {
                System.out.print("Enter Start Date (dd/mm/yyyy)> ");
                try {
                    startDate = formatDate.parse(scanner.nextLine().trim());
                    if (startDate.after(new Date()) || formatDate.format(startDate).equals(formatDate.format(new Date()))) {
                        roomRate.setStartDate(startDate);
                        break;
                    } else {
                        throw new DateTimeException("Chosen date is in the past!");
                    }
                } catch (ParseException ex) {
                    System.out.println("An error has occurred while parsing date: " + ex.getMessage() + "\n");
                } catch (DateTimeException ex) {
                    System.out.println("An error has occurred in selecting the date: " + ex.getMessage() + "\n");
                }
            }

            Date endDate = new Date();

            while (true) {
                System.out.print("Enter End Date (dd/mm/yyyy)> ");
                try {
                    endDate = formatDate.parse(scanner.nextLine().trim());
                    if (endDate.after(new Date()) || formatDate.format(endDate).equals(formatDate.format(new Date()))) {
                        roomRate.setEndDate(endDate);
                        break;
                    } else {
                        throw new DateTimeException("Chosen date is in the past!");
                    }
                } catch (ParseException ex) {
                    System.out.println("An error has occurred while parsing date: " + ex.getMessage() + "\n");
                } catch (DateTimeException ex) {
                    System.out.println("An error has occurred in selecting the date: " + ex.getMessage() + "\n");
                }
            }
        }
        
        Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(roomRate);

        if (constraintViolations.isEmpty()) {
            try {
                roomRateSessionBeanRemote.updateRoomRate(roomRate);
                System.out.println("Room Rate updated successfully!\n");
            } catch (RoomRateNotFoundException ex) {
                System.out.println("An error has occurred while updating product: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }
    }

    private void doDeleteRoomRate(RoomRate roomRate) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: View Room Rate Details :: Delete Room Rate ***\n");
        System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                roomRateSessionBeanRemote.deleteRoomRate(roomRate.getRoomRateId());
                System.out.println("Room Rate deleted successfully!\n");
            } catch (RoomTypeNotFoundException | RoomRateNotFoundException | DeleteRoomRateException ex) {
                System.out.println("An error has occurred while deleting the room rate: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Room Rate NOT deleted!\n");
        }
    }

    private void doViewAllRoomRates() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: View All Room Rates ***\n");

        List<RoomRate> roomRates = roomRateSessionBeanRemote.retrieveAllRoomRates();
        System.out.printf("%8s%13s%20s%20s%20s%20s%20s\n", "Room Rate ID", "Name", "Room Type", "Rate Type", "Rate Per Night", "Start Date", "End Date");

        for (RoomRate roomRate : roomRates) {
            if (roomRate.getRoomRateType().equals(RoomRateTypeEnum.PUBLISHED) || roomRate.getRoomRateType().equals(RoomRateTypeEnum.NORMAL)) {
                System.out.printf("%8s%15s%20s%20s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), roomRate.getRoomType().getTypeName(), roomRate.getRoomRateType(), roomRate.getRatePerNight());
            } else {
                System.out.printf("%8s%15s%20s%20s%20s%20s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), roomRate.getRoomType().getTypeName(), roomRate.getRoomRateType(), roomRate.getRatePerNight(), formatDate.format(roomRate.getStartDate()), formatDate.format(roomRate.getEndDate()));
            }
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void showInputDataValidationErrorsForRoomRate(Set<ConstraintViolation<RoomRate>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
