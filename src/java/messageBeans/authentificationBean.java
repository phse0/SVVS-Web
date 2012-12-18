/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messageBeans;

import business.ControllerFactory2Remote;
import business.ControllerFactory3Remote;
import business.ControllerFactoryRemote;
import business.controller.departments.IDepartmentController;
import business.controller.person.IAuthentificationController;
import business.controller.person.IPersonController;
import business.controller.role.IRoleController;
import data.DTOs.IPersonDTO;
import data.DTOs.IRoleDTO;
import data.DTOs.IRoleRightsDTO;
import data.DTOs.ITournamentDTO;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Michael
 */
@ManagedBean
@SessionScoped
public class authentificationBean implements Serializable{

    /**
     * Creates a new instance of authentificationBean
     */
    @EJB(name = "_cFactory1")
    private ControllerFactoryRemote cFactory;
    @EJB(name = "_cFactory3")
    private ControllerFactory3Remote cFactory3;
    private IAuthentificationController authController;
    private IPersonController personController;
    private IRoleController roleController;
    private IDepartmentController deptController;
    private boolean loggedIn = false;
    private IPersonDTO loggedPerson;
    private List<IRoleDTO> roles;
    private String username;
    private String password;
    
    public authentificationBean() {
    }
    
    public String login() {
        checkController();
        try {
            Long right = authController.Authenticate(username, password);
            loggedPerson = personController.loadPersonWithUsername(username);
            roles = roleController.loadRolesOfPerson(loggedPerson);
            
            if (right > 0) {
                loggedIn = true;
                
            }
            
        } catch (RemoteException ex) {
            Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "index";
    }
    
    public String logout() {
        loggedIn = false;
        loggedPerson = null;
        return "index";
    }
    
    private void checkController() {
        if (authController == null) {
            try {
                authController = cFactory.getAuthentificationController();
            } catch (RemoteException ex) {
                Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (personController == null) {
            try {
                personController = cFactory.getPersonController();
            } catch (RemoteException ex) {
                Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (roleController == null) {
            try {
                roleController = cFactory3.getRoleController();
            } catch (RemoteException ex) {
                Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (deptController == null) {
            try {
                deptController = cFactory.getDepartmentController();
            } catch (RemoteException ex) {
                Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isLoggedIn() {
        return loggedIn;
    }
    
    public String getLoggedPersonName() {
        try {
            return loggedPerson.getLastname() + " " + loggedPerson.getFirstname();
        } catch (RemoteException ex) {
            Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public boolean canEditTournament(ITournamentDTO tourn) {
        try {        
            System.out.println(tourn.getName() + ", " + tourn.getSport().getName());
        } catch (RemoteException ex) {
            Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (IRoleDTO r : roles) {
            try {
                System.out.println(r.getRoleRight().getName() + ", " + r.getDepartment().getName() + ", " + r.getSport().getName());
                
                String roleRightName = r.getRoleRight().getName();
                if ("Admin".equals(roleRightName)) {
                    return true;
                } else if ("Manager".equals(roleRightName) && deptController.isSportInDepartmentID(r.getDepartment().getId(), tourn.getSport().getId())) {
                    return true;
                } else if ("Trainer".equals(roleRightName) && r.getSport().equals(tourn.getSport())) {
                    return true;
                }
                
            } catch (RemoteException ex) {
                Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        return false;
    }
}
