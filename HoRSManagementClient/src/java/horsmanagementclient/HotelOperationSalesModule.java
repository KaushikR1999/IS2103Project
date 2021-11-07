/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import entity.Employee;
import entity.RoomRate;
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
    
    private Employee currentEmployee;
    
    private final SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    
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
            System.out.println("3: View All Room Rates");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewRoomRate();
//                    System.out.println("not implemented yet");

                }
                else if(response == 2)
                {
                    doViewRoomRateDetails();
//                    System.out.println("not implemented yet");
                }
                else if(response == 3)
                {
                    doViewAllRoomRates();
//                    doUpdateRoomRate();
//                    System.out.println("not implemented yet");
                }
//                else if(response == 4)
//                {
////                    doDeleteRoomRate();
////                    System.out.println("not implemented yet");
//                }
//                else if(response == 5)
//                {
//                    
////                    System.out.println("not implemented yet");
//                }
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
    
    
    
    
    private void doViewRoomRateDetails()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: View Room Rate Details ***\n");
        System.out.print("Enter Room Rate ID> ");
        Long roomRateId = scanner.nextLong();
        
        try
        {
            RoomRate roomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomRateId(roomRateId);
            System.out.printf("%8s%20s%20s%15s%20s%20s%20s\n", "Room Rate ID", "Name", "Room Type", "Rate Type", "Rate Per Night", "Start Date", "End Date");
            System.out.printf("%8s%20s%20s%15s%20s%20s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), roomRate.getRoomType(), roomRate.getRoomRateType(), roomRate.getRatePerNight(), roomRate.getStartDate(), roomRate.getEndDate());         
            System.out.println("------------------------");
            System.out.println("1: Update Staff");
            System.out.println("2: Delete Staff");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                doUpdateRoomRate(roomRate);
            }
            else if(response == 2)
            {
                doDeleteRoomRate(roomRate);
            }
        }
        catch(RoomRateNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving room rate: " + ex.getMessage() + "\n");
        }
    }
    
    private void doCreateNewRoomRate()
    {
        Scanner scanner = new Scanner(System.in);
        RoomRate newRoomRate = new RoomRate();
        Double doubleInput;
        
        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: Create New Room Rate ***\n");
        System.out.print("Enter Name> ");
        newRoomRate.setName(scanner.nextLine().trim());
        
        while(true)
        {
            System.out.print("Select Room Rate Type (1: Published, 2: Normal, 3: Peak, 4: Promotion)> ");
            Integer roomRateTypeInt = scanner.nextInt();
            
            if(roomRateTypeInt >= 1 && roomRateTypeInt <= 4)
            {
                newRoomRate.setRoomRateType(RoomRateTypeEnum.values()[roomRateTypeInt-1]);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        System.out.print("Enter Rate Per Night > ");
        doubleInput = scanner.nextDouble();
        if(doubleInput >= 0)
        {
            newRoomRate.setRatePerNight(doubleInput);
        } 
        else {
            System.out.println("Invalid option, please try again!\n");
        }
        
        Date startDate = new Date();
        
        while (true) {
            System.out.print("Enter Start Date> ");
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
            System.out.print("Enter End Date> ");
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
        
        System.out.print("Enter Room Type Id > ");
        Long roomTypeId = scanner.nextLong();
        
        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(newRoomRate);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long newRoomRateId = roomRateSessionBeanRemote.createNewRoomRate(roomTypeId, newRoomRate);
                System.out.println("New room rate created successfully!: " + newRoomRateId + "\n");
            }
            catch(CreateNewRoomRateException | RoomTypeNotFoundException ex)
            {
                System.out.println("An unknown error has occurred while creating the new room rate!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }
    }
    
    private void doUpdateRoomRate(RoomRate roomRate)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        Integer integerInput;
        BigDecimal bigDecimalInput;
        Double doubleInput;
        
        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: View Room Rate Details :: Update Room Rate ***\n");
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomRate.setName(input);
        }
        
        while(true)
        {
            System.out.print("Select Room Rate Type (0: No Change, 1: Published, 2: Normal, 3: Peak, 4: Promotion)> ");
            Integer roomRateTypeInt = scanner.nextInt();
            
            if(roomRateTypeInt >= 1 && roomRateTypeInt <= 4)
            {
                roomRate.setRoomRateType(RoomRateTypeEnum.values()[roomRateTypeInt-1]);
                break;
            }
            else if (roomRateTypeInt == 0)
            {
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        System.out.print("Enter Rate Per Night (negative number if no change)> ");
        doubleInput = scanner.nextDouble();
        if(doubleInput >= 0)
        {
            roomRate.setRatePerNight(doubleInput);
        }
        
        Date startDate = new Date();
        
        while (true) {
            System.out.print("Enter Start Date> ");
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
            System.out.print("Enter End Date> ");
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
        
        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRate);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                roomRateSessionBeanRemote.updateRoomRate(roomRate);
                System.out.println("Room Rate updated successfully!\n");
            }
            catch (RoomRateNotFoundException ex) 
            {
                System.out.println("An error has occurred while updating product: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }
    }
    
    private void doDeleteRoomRate(RoomRate roomRate) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** HoRS Management Client :: Hotel Operation (Sales) :: View Room Rate Details :: Delete Staff ***\n");
        System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                roomRateSessionBeanRemote.deleteRoomRate(roomRate.getRoomRateId());
                System.out.println("Room Rate deleted successfully!\n");
            } catch (RoomTypeNotFoundException | RoomRateNotFoundException | DeleteRoomRateException ex) {
                System.out.println("An error has occurred while deleting the staff: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Staff NOT deleted!\n");
        }
    }
    
    private void doViewAllRoomRates()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: System Administration :: View All Staffs ***\n");
        
        List<RoomRate> roomRates = roomRateSessionBeanRemote.retrieveAllRoomRates();
        System.out.printf("%8s%20s%20s%15s%20s%20s%20s\n", "Room Rate ID", "Name", "Room Type", "Rate Type", "Rate Per Night", "Start Date", "End Date");

        for(RoomRate roomRate:roomRates)
        {
            System.out.printf("%8s%20s%20s%15s%20s%20s%20s\n", roomRate.getRoomRateId().toString(), roomRate.getName(), roomRate.getRoomType(), roomRate.getRoomRateType(), roomRate.getRatePerNight(), roomRate.getStartDate(), roomRate.getEndDate());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForRoomRate(Set<ConstraintViolation<RoomRate>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
