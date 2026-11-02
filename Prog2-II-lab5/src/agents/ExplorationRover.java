package agents;

import colony.Location;
import missions.Mission;
import missions.ExplorationMission;
import emergencies.Emergency;

public class ExplorationRover extends Robot {
    
    public ExplorationRover(String id, Location location) { 
        super(id, location); 
    }

    @Override 
    public boolean canPerform(Mission mission) {
        // Rover needs at least 20% battery to explore
        return mission instanceof ExplorationMission && getBatteryLevel() > 20;
    }

    @Override 
    public boolean canHandle(Emergency emergency) { 
        return false; 
    }
}