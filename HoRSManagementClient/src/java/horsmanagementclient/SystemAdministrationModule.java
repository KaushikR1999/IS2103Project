/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import util.enumeration.AccessRightsEnum;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.PartnerUsernameOrOrganisationExistException;
import util.exception.UnknownPersistenceException;
/**
 *
 * @author yuenz
 */
public class SystemAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    
    private Employee currentEmployee;
    
    public SystemAdministrationModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, Employee currentEmployee){
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuSystemAdministration() throws InvalidAccessRightException
    {
        if(currentEmployee.getEmployeeRole()!= AccessRightsEnum.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMIN rights to access the system administration module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management Client :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-----------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewEmployee();
//                    System.out.println("not implemented yet");
                }
                else if(response == 2)
                {
                    doViewAllEmployees();
//                    System.out.println("not implemented yet");
                }
                else if(response == 3)
                {
                    doCreateNewPartner();
//                    System.out.println("not implemented yet");
                }
                else if(response == 4)
                {
                    doViewAllPartners();
//                    System.out.println("not implemented yet");
                }
                else if(response == 5)
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
    
        private void doCreateNewEmployee()
    {
        Scanner scanner = new Scanner(System.in);
        Employee newEmployee = new Employee();
        
        System.out.println("*** HoRS Management Client :: System Administration :: Create New Employee ***\n");
        System.out.print("Enter Username> ");
        newEmployee.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newEmployee.setPassword(scanner.nextLine().trim());
        
        while(true)
        {
            System.out.print("Select Access Right (1: System Administrator, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
            Integer accessRightInt = scanner.nextInt();
            
            if(accessRightInt >= 1 && accessRightInt <= 4)
            {
                newEmployee.setEmployeeRole(AccessRightsEnum.values()[accessRightInt-1]);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        scanner.nextLine();
        
        Set<ConstraintViolation<Employee>>constraintViolations = validator.validate(newEmployee);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long newEmployeeId = employeeSessionBeanRemote.createNewEmployee(newEmployee);
                System.out.println("New employee created successfully!: " + newEmployeeId + "\n");
            }
            catch(EmployeeUsernameExistException ex)
            {
                System.out.println("An error has occurred while creating the new staff!: The user name already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForEmployee(constraintViolations);
        }
    }
    
    private void doViewAllEmployees()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: System Administration :: View All Employees ***\n");
        
        List<Employee> employees = employeeSessionBeanRemote.retrieveAllEmployees();
        System.out.printf("%10s%20s\n", "Username", "Employee Role");

        for(Employee employee:employees)
        {
            System.out.printf("%10s%20s\n\n", employee.getUsername(), employee.getEmployeeRole());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForEmployee(Set<ConstraintViolation<Employee>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void doCreateNewPartner()
    {
        Scanner scanner = new Scanner(System.in);
        Partner newPartner = new Partner();
        
        System.out.println("*** HoRS Management Client :: System Administration :: Create New Partner ***\n");
        System.out.print("Enter Username> ");
        newPartner.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newPartner.setPassword(scanner.nextLine().trim());
        System.out.print("Enter Organisation> ");
        newPartner.setOrganisation(scanner.nextLine().trim());
        
        Set<ConstraintViolation<Partner>>constraintViolations = validator.validate(newPartner);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long newPartnerId = partnerSessionBeanRemote.createNewPartner(newPartner);
                System.out.println("New partner created successfully!: " + newPartnerId + "\n");
            }
            catch(PartnerUsernameOrOrganisationExistException ex)
            {
                System.out.println("An error has occurred while creating the new partner!: The username or organisation already exists\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new partner!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForPartner(constraintViolations);
        }
    }
    
    private void doViewAllPartners()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: System Administration :: View All Patners ***\n");
        
        List<Partner> partners = partnerSessionBeanRemote.retrieveAllPartners();
        System.out.printf("%10s%20s\n", "Username", "Organisation");

        for(Partner partner:partners)
        {
            System.out.printf("%10s%20s\n\n", partner.getUsername(), partner.getOrganisation());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForPartner(Set<ConstraintViolation<Partner>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
