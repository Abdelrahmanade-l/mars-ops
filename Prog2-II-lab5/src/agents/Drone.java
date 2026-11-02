package agents;

import colony.Location;
import emergencies.Emergency;
import missions.Mission;
import missions.ExplorationMission;
import missions.RescueMission;

public class Drone extends Robot {

    public Drone(String id, Location location) {
        super(id, location);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission instanceof ExplorationMission || mission instanceof RescueMission;
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return false;
    }

    @Override
    public boolean requiresCommunication() { 
        return true; 
    }

    @Override
    public boolean performDuty() {
    if (!isUsable()) {
        System.out.println("cannot perform aerial reconnaissance.");
        return false;
    }  
    System.out.println("Performing aerial reconnaissance.");
    return true;
}
    

}
