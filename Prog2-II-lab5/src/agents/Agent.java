package agents;
import colony.Location;
import missions.Mission;
import emergencies.Emergency;


public interface Agent {
    String getId();
    boolean isAvailable();
    Location getLocation();
    void setLocation(Location location);
    void setAvailable(boolean available);
    boolean canPerform(Mission mission);
    boolean canHandle(Emergency emergency);
    boolean tick();
    void heal(int amount);
    void restoreHealth(int amount);
    boolean requiresCommunication();
    boolean performDuty();
    
    void takeDamageFromMission();
    void restAndHeal(int amount);
}