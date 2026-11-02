package agents;

import colony.Location;
import missions.Mission;
import emergencies.Emergency;

public abstract class Robot implements Agent {

    protected String id;
    protected Location location;
    protected boolean available = true;
    
    public static final int MAX_BATTERY = 100;
    public static final int MAX_HEALTH = 100;
    private static final int DRAIN_PER_TICK_ON_MISSION = 5;
    private static final int DRAIN_PER_TICK_IDLE = 1;
    
    private int battery = MAX_BATTERY;
    private int health = MAX_HEALTH;

    protected Robot(String id, Location location) {
        this.id = id;
        this.location = location;
    }
    
    @Override
    public String getId() { return id; }

    @Override
    public Location getLocation() { return location; }
    
    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean isAvailable() { 
        return available && isUsable(); 
    }

    @Override
    public void setAvailable(boolean available) { 
        this.available = available; 
    }


    public int getBatteryLevel() { return battery; }

    public boolean hasEnoughBattery(int required) { return battery >= required; }

    public void drainBattery(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        battery = Math.max(0, battery - amount);
    }

    public void recharge(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        battery = Math.min(MAX_BATTERY, battery + amount);
    }
    
    public boolean performDuty() {
        return true;
    }

    public int getHealth() { return health; }
    public int getDamage() { return 100 - health; }

    public boolean isBrokenDown() { return health == 0; }
    public boolean isDamaged() { return health < MAX_HEALTH; }

    public void addDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        health = Math.max(0, health - amount);
    }

    public void restoreHealth(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        health = Math.min(MAX_HEALTH, health + amount);
    }

    public void repairFully() { health = MAX_HEALTH; }

    public String getOperationalStatus() {
        if (isBrokenDown()) return "brokenDown";
        if (battery == 0) return "batteryEmpty";
        if (isDamaged()) return "damaged";
        return "active";
    }

    public boolean isUsable() {
        return health > 0 && battery > 0;
    }

    @Override
    public boolean tick() {
        if (!isUsable()) {
            return false;
        }
        //drainBattery(!available ? DRAIN_PER_TICK_ON_MISSION : DRAIN_PER_TICK_IDLE);
        if (available) {
            drainBattery(DRAIN_PER_TICK_IDLE);
        }
        return true; 
    }

    public String details() {
        return "battery=" + battery + "%, health=" + health + "% (" + getOperationalStatus() + ")";
    }

    @Override
    public abstract boolean canPerform(Mission mission);

    @Override
    public abstract boolean canHandle(Emergency emergency);

     @Override
    public boolean requiresCommunication() {
        return false; 
    }
    
    @Override
    public void heal(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        restoreHealth(amount); 
    }
    
    @Override
    public void takeDamageFromMission() {
        this.battery = Math.max(0, this.battery - 15);
    }

    @Override
    public void restAndHeal(int amount) {
        if (this.location.getName().contains("Habitat")) {
            recharge(amount * 2);
            restoreHealth(amount);
        }

    }
    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ID=" + id + ", " + details()
                + ", Available=" + isAvailable() + ", Location=" + location.getName() + "]";
    }
}