/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messageBeans;

import business.ControllerFactory3Remote;
import business.ControllerFactoryRemote;
import business.controller.person.IAuthentificationController;
import java.rmi.RemoteException;
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
public class authentificationBean {

    /**
     * Creates a new instance of authentificationBean
     */
    @EJB(name = "_cFactory1")
    private ControllerFactoryRemote cFactory;
    private IAuthentificationController controller;

    private boolean loggedIn = false;
    private String username;
    private String password;
    
    public authentificationBean() {
    }
    
    public String login() {
        checkController();
        try {
            Long right = controller.Authenticate(username, password);
            
            if(right > 0) {
                loggedIn = true;
            }
            
        } catch (RemoteException ex) {
            Logger.getLogger(authentificationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "index";
    }
    
    public String logout() {
        loggedIn = false;
        return "index";
    }

    private void checkController() {
        if (controller == null) {
            try {
                controller = cFactory.getAuthentificationController();
            } catch (RemoteException ex) {
                Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
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
    
    
    
    
}
