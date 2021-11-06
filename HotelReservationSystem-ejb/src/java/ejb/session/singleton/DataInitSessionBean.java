/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AccessRightsEnum;

/**
 *
 * @author kaushikr
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public DataInitSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        if(em.find(Employee.class, 1l) == null)
        {
            initialiseData();
        }
    }
        
        private void initialiseData() {
        Employee employee = new Employee("sysadmin","password",AccessRightsEnum.SYSTEM_ADMIN);
        em.persist(employee);
        em.flush();
    }
        
}
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
