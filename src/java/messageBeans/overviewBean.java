/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messageBeans;

import business.ControllerFactory3Remote;
import business.controller.tournament.ITournamentController;
import data.DTOs.IMatchDTO;
import data.DTOs.ITeamDTO;
import data.DTOs.ITournamentDTO;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Michael
 */
@ManagedBean
@SessionScoped
public class overviewBean {

    /**
     * Creates a new instance of overviewBean
     */
    @EJB(name = "_cFactory3")
    private ControllerFactory3Remote cFactory;
    private ITournamentController controller;
    private int tournamentID;
    private ITournamentDTO actualTournament;

    public overviewBean() {
    }

    public List<ITournamentDTO> getTournaments() {
        checkController();
        try {
            return controller.loadTournaments();
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String viewTournament(ITournamentDTO tournament) {
        checkController();

        try {
            this.tournamentID = tournament.getId();
            this.actualTournament = controller.loadTournamentDTO(tournamentID);
            return "tournamentOverview";
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return "index";
    }

    public List<ITeamDTO> getTeams() {
        try {
            return actualTournament.getTeams();
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public List<IMatchDTO> getMatches() {
        try {
            return actualTournament.getMatches();
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String returnToOverview() {
        return "index";
    }
    
    private void checkController() {
        if (controller == null) {
            try {
                controller = cFactory.getTournamentController();
            } catch (RemoteException ex) {
                Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
