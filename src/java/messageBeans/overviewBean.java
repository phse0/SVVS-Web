/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messageBeans;

import business.ControllerFactory2Remote;
import business.ControllerFactory3Remote;
import business.controller.team.ITeamController;
import business.controller.tournament.ITournamentController;
import business.controller.tournament.edit.ITournamentEdit;
import data.DTOs.IMatchDTO;
import data.DTOs.IPersonDTO;
import data.DTOs.ISportsmanTrainingTeamDTO;
import data.DTOs.ITeamDTO;
import data.DTOs.ITournamentDTO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.LinkedList;
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
public class overviewBean implements Serializable {

    /**
     * Creates a new instance of overviewBean
     */
    @EJB(name = "_cFactory3")
    private ControllerFactory3Remote cFactory;
    @EJB(name = "_cFactory2")
    private ControllerFactory2Remote cFactory2;
    
    private ITournamentController tournController;
    private ITournamentEdit tournEditcontroller;
    private ITeamController teamController;
    private int tournamentID;
    private ITournamentDTO actualTournament;
    
    
    private String team1;
    private String team2;
    private String resultTeam1;
    private String resultTeam2;
    private String location;
    private String date;
    private boolean finished;

    public overviewBean() {
    }

    public List<ITournamentDTO> getTournaments() {
        checkController();
        try {
            return tournController.loadTournaments();
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String viewTournament(ITournamentDTO tournament) {
        checkController();

        try {
            this.tournamentID = tournament.getId();
            this.actualTournament = tournController.loadTournamentDTO(tournamentID);
            this.finished = actualTournament.isFinished();
            return "tournamentOverview";
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "index";
    }

    public List<ITeamDTO> getTeams() {
        checkController();

        try {
            return actualTournament.getTeams();
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public List<String> getTeamNames() {
        List<String> names = new LinkedList<String>();
        
        for(ITeamDTO t : getTeams()) {
            try {
                names.add(t.getName());
            } catch (RemoteException ex) {
                Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return names;
    }

    public List<String> loadPlayersOfTeam(ITeamDTO team) {
        checkController();

        List<String> sportsman = new LinkedList<String>();

        try {
            List<ISportsmanTrainingTeamDTO> sportsmanTemp = teamController.loadAssignedPlayersOfTeamID(actualTournament.getId(), team.getId());
            
            for (ISportsmanTrainingTeamDTO t : sportsmanTemp) {
                IPersonDTO p = t.getSportsman().getPerson();
                String returnString = p.getFirstname() + " " + p.getLastname();

                if (t.getPosition() != null && !"".equals(t.getPosition())) {
                    returnString += ", " + t.getPosition();
                }

                sportsman.add(returnString);

            }

        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sportsman;
    }

    public List<IMatchDTO> getMatches() {
        checkController();

        try {
            return actualTournament.getMatches();
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public String saveMatch() {
        checkController();
        
        try {
            tournEditcontroller.AddMatch(actualTournament.getId(), location, Date.valueOf(date), 
                    team1, team2, Integer.parseInt(resultTeam1), Integer.parseInt(resultTeam2));
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "tournamentOverview";
    }
    
    public String saveFinished() {
        
        try {
            tournEditcontroller.EditTournament(actualTournament.getId(), actualTournament.getName(), 
                    actualTournament.getLocation(), Date.valueOf(actualTournament.getDate()), 
                    BigDecimal.valueOf(actualTournament.getFee()), finished, getTeamNames());        
        } catch (RemoteException ex) {
            Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "tournamentOverview";
    }

    public String returnToOverview() {
        return "index";
    }

    private void checkController() {
        if (tournController == null) {
            try {
                tournController = cFactory.getTournamentController();
            } catch (RemoteException ex) {
                Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (teamController == null) {
            try {
                teamController = cFactory2.getTeamController();
            } catch (RemoteException ex) {
                Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(tournEditcontroller == null) {
            try {
                tournEditcontroller = cFactory.getTournamentEdit();
            } catch (RemoteException ex) {
                Logger.getLogger(overviewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getResultTeam1() {
        return resultTeam1;
    }

    public void setResultTeam1(String resultTeam1) {
        this.resultTeam1 = resultTeam1;
    }

    public String getResultTeam2() {
        return resultTeam2;
    }

    public void setResultTeam2(String resultTeam2) {
        this.resultTeam2 = resultTeam2;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public ITournamentDTO getActualTournament() {
        return actualTournament;
    }

    public void setActualTournament(ITournamentDTO actualTournament) {
        this.actualTournament = actualTournament;
    }
    
    
}
