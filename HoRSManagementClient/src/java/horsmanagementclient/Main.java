/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author kaushikr
 */
public class Main {

    @EJB
    private static GuestSessionBeanRemote guestSessionBean;
    
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;
    @EJB
    private static RoomSessionBeanRemote roomSessionBean;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBean;
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;
    @EJB
    private static PartnerSessionBeanRemote partnerSessionBean;
    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;
    
    


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(employeeSessionBean, partnerSessionBean, reservationSessionBean, roomRateSessionBean, roomSessionBean, roomTypeSessionBean, guestSessionBean);
        mainApp.runApp();
        // TODO code application logic here
    }
    
}
