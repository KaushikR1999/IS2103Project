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
import entity.Room;
import entity.RoomType;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRightsEnum;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

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

    public HotelOperationGeneralModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, Employee currentEmployee) {
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
            System.out.println("3: View All Room Types");
            System.out.println("-----------------------");
            System.out.println("4: Create New Room");
            System.out.println("5: Update Room");
            System.out.println("6: Delete Room");
            System.out.println("7: View All Rooms"); 
            System.out.println("-----------------------");
            System.out.println("8: View Room Allocation Exception Report");
            System.out.println("9: Back\n");
            response = 0;
            
            while(response < 1 || response > 9)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewRoomType();
                    System.out.println("not implemented yet");

                }
                else if(response == 2)
                {
                    doViewRoomTypeDetails();
                    System.out.println("not implemented yet");
                }
                else if(response == 3)
                {
                    doViewAllRoomTypes();
                    System.out.println("not implemented yet");
                }
                else if(response == 4)
                {
                    //doCreateNewRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 5)
                {
                    //doUpdateRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 6)
                {
                    //doDeleteRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 7)
                {
                    //doViewAllRooms();
                    System.out.println("not implemented yet");
                }
                else if(response == 8)
                {
                    //doViewAllRoomAllocationReport();
                    System.out.println("not implemented yet");
                }
                else if (response == 9)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 9)
            {
                break;
            }
        }
    }
    
    private void doCreateNewRoomType()
    {
        Scanner scanner = new Scanner(System.in);
        RoomType  newRoomType = new RoomType();
        
        System.out.println("*** HoRS Management Client :: Hotel Operations (General) :: Create New Room Type ***\n");
        System.out.print("Enter Room Type Name> ");
        newRoomType.setTypeName(scanner.nextLine().trim());
        System.out.print("Enter Description> ");
        newRoomType.setDescription(scanner.nextLong());
        System.out.print("Enter Room Size> ");
        newRoomType.setSize(scanner.nextDouble());
        System.out.print("Enter Number of Beds> ");
        newRoomType.setBed(scanner.nextInt());
        System.out.print("Enter Room Capacity> ");
        newRoomType.setCapacity(scanner.nextInt());
        System.out.print("Enter number of amenities>");
        int noOfAmenities = scanner.nextInt();
        for( int i = 0; i < noOfAmenities; i++) {
            int noCounter = i + 1;
            System.out.print("Enter name of amenity #" + noCounter + ">" );
            newRoomType.getAmenities().add(scanner.nextLine().trim());
        }
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);
        
        if(constraintViolations.isEmpty())
        {        
            try
            {
                newRoomType = roomTypeSessionBeanRemote.createNewRoomType(newRoomType);

                System.out.println("New Room Type created successfully!: " + newRoomType.getRoomTypeId()+ "\n");
            }
            catch(RoomTypeNameExistException ex)
            {
                System.out.println("An error has occurred while creating the new Room Type!: The Room Type name already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new Room Type!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }
    
    private void doViewRoomTypeDetails()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoRS Management Client :: Hotel Operations (General) :: View Room Type Details ***\n");
        System.out.print("Enter Room Type Name> ");
        String typeName = scanner.nextLine().trim();
        
        try
        {
            RoomType roomType = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeName(typeName);
            System.out.printf("%10s%20s%20s%20s%13s%20s\n", "Name", "Description", "Room Size", "Number Of Beds", "Room Capacity", "Amenities");
            System.out.printf("%10s%20s%20d%20d%20d%20s\n", roomType.getTypeName(), roomType.getDescription(), roomType.getSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities().toString());
            System.out.println("------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                doUpdateRoomType(roomType);
            }
            else if(response == 2)
            {
                doDeleteRoomType(roomType);
            }
        }
        catch(RoomTypeNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving product: " + ex.getMessage() + "\n");
        }
    }
    
    private void doUpdateRoomType(RoomType roomType)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        Integer integerInput;
        Double doubleInput;
        Long longInput;
        
        System.out.println("*** HoRS Management Client :: Hotel Operations (General) :: Update Room Type ***\n");
        System.out.print("Enter Room Type Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setTypeName(input);
        }
        
        System.out.print("Enter Description (blank if no change)> ");
        longInput = scanner.nextLong();
        if(longInput.intValue()> 0)
        {
            roomType.setDescription(longInput);
        }
        
        System.out.print("Enter Room Size (negative number if no change)> ");
        doubleInput = scanner.nextDouble();
        if(doubleInput >= 0)
        {
            roomType.setSize(doubleInput);
        }
        
        System.out.print("Enter Number of Beds (negative number if no change)> ");
        integerInput = scanner.nextInt();
        if(integerInput >= 0)
        {
            roomType.setBed(integerInput);
        }
        
        System.out.print("Enter Room Capacity (negative number if no change)> ");
        integerInput = scanner.nextInt();
        if(integerInput >= 0)
        {
            roomType.setCapacity(integerInput);
        }
        
        System.out.print("Enter Updated Amenities List (zero or negative amount if no change)> $");
        System.out.print("Confirm void/refund this sale transaction? (Enter 'Y' to update amenities, any button if otherwise)> ");
            String updateAmenities = scanner.nextLine().trim();
            
            if(updateAmenities.equals("Y"))
            {
                    System.out.print("Enter number of amenities>");
                    int noOfAmenities = scanner.nextInt();
                    for( int i = 0; i < noOfAmenities; i++) {
                        int noCounter = i + 1;
                        System.out.print("Enter name of amenity #" + noCounter + ">" );
                        roomType.getAmenities().add(scanner.nextLine().trim());
                        System.out.println("Update Inputs Successfully Obtained\n");

                    } 
            }
            else
            {
                System.out.println("Update Inputs Successfully Obtained\n");
            }
            
        scanner.nextLine();
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                roomTypeSessionBeanRemote.updateRoomType(roomType);
                System.out.println("Room Type updated successfully!\n");
            }
            catch (RoomTypeNotFoundException | UpdateRoomTypeException ex) 
            {
                System.out.println("An error has occurred while updating room type: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }
    
        private void doDeleteRoomType(RoomType roomType)
    {
        Scanner scanner = new Scanner(System.in);     
        String input;
        
        System.out.println("*** HoRS Management Client :: Hotel Operations (General) :: Delete Room Type ***\n");
        System.out.printf("Confirm Delete Room Type %s (Room Type Id: %s) (Enter 'Y' to Delete)> ", roomType.getTypeName(), roomType.getRoomTypeId());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                roomTypeSessionBeanRemote.deleteRoomType(roomType.getRoomTypeId());
                System.out.println("Room Type deleted successfully!\n");
            } 
            catch (RoomTypeNotFoundException | DeleteRoomTypeException ex) 
            {
                System.out.println("An error has occurred while deleting room type: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("Room Type NOT deleted! Room Type Disabled.\n");
        }
    }
    
        private void doViewAllRoomTypes()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: Hotel Operations (General) :: View All Room Types ***\n");
        
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
        System.out.printf("%10s%20s%20s%20s%13s%20s\n", "Name", "Description", "Room Size", "Number Of Beds", "Room Capacity", "Amenities");

        for(RoomType roomType : roomTypes)
        {
            System.out.printf("%10s%20s%20d%20d%20d%20s\n", roomType.getTypeName(), roomType.getDescription(), roomType.getSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities().toString());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    
        private void showInputDataValidationErrorsForRoomType(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
