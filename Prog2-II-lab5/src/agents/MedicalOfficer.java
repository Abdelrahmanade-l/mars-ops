package agents;

import colony.Location;
import emergencies.Emergency;
import emergencies.Fire;
import emergencies.MedicalEmergency;
import missions.Mission;
import missions.RescueMission;

public class MedicalOfficer extends CrewMember {

    public static final int TREATMENT_HEALING = 50;

    public MedicalOfficer(String id, String name, Location location, String... skills) {
        super(id, name, location, skills);
    }
    @Override
    public boolean canPerform(Mission mission) {
        return mission instanceof RescueMission;
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return emergency instanceof MedicalEmergency || emergency instanceof Fire;
    }

    public boolean treat(CrewMember patient) {
        if (patient == null || !isUsable()) 
            return false;
        patient.heal(TREATMENT_HEALING);
        System.out.println("Medical officer " + getName() + " treated " + patient.getName() + " (health now " + patient.getHealth() + ").");
        return true;
    }

    @Override
    public boolean performDuty() {
    if (!isUsable()) {
        System.out.println(getName() + "Can not provide medical care.");
        return false;
    }
    System.out.println("Providing medical care.");
    return true;
}
    
}
