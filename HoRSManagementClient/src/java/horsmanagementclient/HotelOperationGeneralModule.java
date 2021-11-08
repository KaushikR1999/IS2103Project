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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRightsEnum;
import util.enumeration.RoomStatusEnum;
import util.exception.CreateNewRoomException;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.NoRoomTypeAvailableException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomRateNotFoundException;
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
                    doCreateNewRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 5)
                {
                    doUpdateRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 6)
                {
                    doDeleteRoom();
                    System.out.println("not implemented yet");
                }
                else if(response == 7)
                {
                    doViewAllRooms();
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
        newRoomType.setDescription(scanner.nextLine().trim());
        System.out.print("Enter Room Size> ");
        newRoomType.setSize(scanner.nextDouble());
        System.out.print("Enter Number of Beds> ");
        newRoomType.setBed(scanner.nextInt());
        System.out.print("Enter Room Capacity> ");
        newRoomType.setCapacity(scanner.nextInt());
        System.out.print("Enter number of amenities> ");
        int noOfAmenities = scanner.nextInt();
        scanner.nextLine();
        for( int i = 0; i < noOfAmenities; i++) {
            int noCounter = i + 1;
            System.out.print("Enter name of amenity #" + noCounter + "> " );
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
            System.out.printf("%10s%20s%20f%20d%20d%20s\n", roomType.getTypeName(), roomType.getDescription(), roomType.getSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities().toString());
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
        
        System.out.println("*** HoRS Management Client :: Hotel Operations (General) :: Update Room Type ***\n");
        System.out.print("Enter Room Type Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setTypeName(input);
        }
        
        System.out.print("Enter Description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setDescription(input);
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
        scanner.nextLine();
        System.out.print("Update Amenities? (Enter 'Y' to update amenities, any button if otherwise)> ");
            String updateAmenities = scanner.nextLine().trim();
            
            if(updateAmenities.equals("Y"))
            {
                    System.out.print("Enter number of amenities> ");
                    int noOfAmenities = scanner.nextInt();
                    scanner.nextLine();
                    List<String> newAmenities = new ArrayList<>();
                    for( int i = 0; i < noOfAmenities; i++) {
                        int noCounter = i + 1;
                        System.out.print("Enter name of amenity #" + noCounter + "> " );
                        newAmenities.add(scanner.nextLine().trim());
                    }
                    roomType.setAmenities(newAmenities);
            }
            try {
                
        //if(roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypesExceptCurrent(roomType.getRoomTypeId()).isEmpty()){
       // } else {
        System.out.print("Enter Name of Next Highest Room Type > ");
        input = scanner.nextLine().trim();
            RoomType nextHighestRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeName(input);
            roomType.setNextHighestRoomType(nextHighestRoomType);
               // }
            
        } catch (RoomTypeNotFoundException ex)
        {
            System.out.println("An error has occurred while updating room type: " + ex.getMessage() + "\n");

        }
            //}
        
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
            System.out.printf("%10s%20s%20f%20d%20d%20s\n", roomType.getTypeName(), roomType.getDescription(), roomType.getSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities().toString());
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
        
    private void doCreateNewRoom()
    {
        Scanner scanner = new Scanner(System.in);
        Room newRoom = new Room();
        
        System.out.println("*** HoRS Management Client :: Hotel Operation (General) :: Create New Room ***\n");
        System.out.print("Enter Room Number> ");
        newRoom.setRoomNumber(scanner.nextLine().trim());
        
        while(true)
        {
            System.out.print("Select Room Status (1: Available, 2: Not Available)> ");
            Integer roomStatusInt = scanner.nextInt();
            
            if(roomStatusInt >= 1 && roomStatusInt <= 2)
            {
                if (roomStatusInt == 1) {
                    newRoom.setRoomStatus(RoomStatusEnum.AVAILABLE);
                } else {
                    newRoom.setRoomStatus(RoomStatusEnum.NOT_AVAILABLE);
                }
//                newRoom.setRoomStatus(RoomStatusEnum.values()[roomStatusInt-1]);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        
        while(true)
        {
            try {
                List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypes();
                
                String output = "Select Room Type (";
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
//                    System.out.println(roomTypes.get(roomTypeInt-1));
//                    System.out.println(roomTypes.get(roomTypeInt-1).getClass());
                    newRoom.setRoomType(roomTypes.get(roomTypeInt-1));
//                    System.out.println(newRoom.getRoomType().getClass());
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            catch (NoRoomTypeAvailableException ex) {
                System.out.println(ex.getMessage() + "\n");
                break;
            }
            
        }
        
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(newRoom);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long newRoomId = roomSessionBeanRemote.createNewRoom(newRoom);
                System.out.println("New room created successfully!: " + newRoomId + "\n");
            }
            catch(CreateNewRoomException | RoomNotFoundException | RoomNumberExistException | UnknownPersistenceException ex)
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
            showInputDataValidationErrorsForRoom(constraintViolations);
        }
    }
    
    private void doUpdateRoom()
    {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter Room ID> ");
        Long roomId = scanner.nextLong();
        
        try {
            Room room = roomSessionBeanRemote.retrieveRoomByRoomId(roomId);

            String input;
            Double doubleInput;

            System.out.println("*** HoRS Management Client :: Hotel Operation (General) :: View Room :: Update Room Status ***\n");

            while (true) {
                System.out.print("Select Room Status (0: No Change, 1: Available, 2: Not Available)> ");
                Integer roomStatusInt = scanner.nextInt();

                if (roomStatusInt >= 1 && roomStatusInt <= 2) {
                    room.setRoomStatus(RoomStatusEnum.values()[roomStatusInt - 1]);
                    break;
                } else if (roomStatusInt == 0) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            while (true) {
                try {
                    List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllAvailableRoomTypes();

                    String output = "Select Room Type (0: No Change, ";
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
                        room.setRoomType(roomTypes.get(roomTypeInt-1));
                        break;
                    } else if (roomTypeInt == 0) {
                        break;    
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                } catch (NoRoomTypeAvailableException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }

            }

            Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);

            if (constraintViolations.isEmpty()) {
                try {
                    roomSessionBeanRemote.updateRoom(room);
                    System.out.println("Room updated successfully!\n");
                } catch (RoomNotFoundException ex) {
                    System.out.println("An error has occurred while updating room: " + ex.getMessage() + "\n");
                } catch (InputDataValidationException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            } else {
                showInputDataValidationErrorsForRoom(constraintViolations);
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("An unknown error has occurred while updating the room!: " + ex.getMessage() + "\n");
        }


    }
    
    private void doDeleteRoom() {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter Room ID> ");
        Long roomId = scanner.nextLong();
        
        try {
            Room room = roomSessionBeanRemote.retrieveRoomByRoomId(roomId);

            String input;

            System.out.println("*** HoRS Management Client :: Hotel Operation (General) :: View Room Details :: Delete Room ***\n");
            System.out.printf("Confirm Delete Room %s (Room ID: %s) (Enter 'Y' to Delete)> ", room.getRoomNumber(), room.getRoomId());
            scanner.nextLine();
            input = scanner.nextLine().trim();

            if (input.equals("Y")) {
                try {
                    roomSessionBeanRemote.deleteRoom(room.getRoomId());
                    System.out.println("Room deleted successfully!\n");
                } catch (RoomNotFoundException | DeleteRoomException ex) {
                    System.out.println("An error has occurred while deleting the room: " + ex.getMessage() + "\n");
                }
            } else {
                System.out.println("Room NOT deleted!\n");
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("An unknown error has occurred while deleting the room!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doViewAllRooms()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: Hotel Operation (General) :: View All Rooms ***\n");
        
        List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();
        System.out.printf("%8s%13s%20s%20s\n", "Room ID", "Room Number", "Room Type", "Room Status");
        
        try {
            System.out.println(roomSessionBeanRemote.retrieveRoomByRoomId(new Long(1)));
            System.out.println(roomSessionBeanRemote.retrieveRoomByRoomId(new Long(1)).getRoomType());
            System.out.println(roomSessionBeanRemote.retrieveRoomByRoomId(new Long(1)).getRoomType().getClass());
        } catch (RoomNotFoundException ex) {
            System.out.println(ex);
        }

        for(Room room:rooms)
        {
            
            System.out.println(room.getRoomType().getClass());
//            System.out.printf("%8s%15s%20s%20s\n", room.getRoomId(), room.getRoomNumber(), room.getRoomType().getTypeName(), room.getRoomStatus());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void showInputDataValidationErrorsForRoom(Set<ConstraintViolation<Room>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
