package agents;

import colony.Location;
import emergencies.Emergency;
import emergencies.Fire;
import emergencies.OxygenLeak;
import emergencies.RoverBreakdown;
import missions.Mission;
import missions.RepairMission;

public class Engineer extends CrewMember {

    public Engineer(String id, String name, Location location, String... skills) {
        super(id, name, location, skills);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission instanceof RepairMission;
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return emergency instanceof OxygenLeak || emergency instanceof Fire || emergency instanceof RoverBreakdown;
    }

    public void repair(Mission mission) {
        System.out.println("Engineer " + getName() + " is repairing" + (mission == null ? "." : " for mission " + mission.getId() + "."));
    }

    @Override
    public boolean performDuty() {
    if (!isUsable()) {
        System.out.println(getName() + "Can not perform duty.");
        return false;
    }
    System.out.println("Repairing equipment.");
    return true;
    }
    
}
