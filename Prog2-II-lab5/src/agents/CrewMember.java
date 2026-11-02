package agents;

import colony.Location;

public abstract class CrewMember implements Agent {

    protected String id, name;
    protected Location location;
    protected boolean available = true;
    private int health = MAX_HEALTH;
    private int fatigue = 0;
    private String skills = "";
    
    public static final int MAX_HEALTH = 100;
    public static final int MAX_FATIGUE = 100;
    public static final int MIN_USABLE_HEALTH = 40;

    public CrewMember(String id, String name, Location location, String... skillsArray) {
        this.id = id;
        this.location = location;

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty");
        }
        this.name = name;

        for (String s : skillsArray) {
            if (s != null && !s.trim().isEmpty()) {
                addSkill(s);
            }
        }
    }

    @Override public String getId() { return id; }

    @Override public Location getLocation() { return location; }
    
    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
     
    @Override public void setAvailable(boolean available) { this.available = available; }
    
    // FIX 1: Added isAvailable()
    @Override public boolean isAvailable() { return available && isUsable(); }

    public String getName() { return name; }

    protected String label() { return getId() + " '" + name + "'"; }

    public int getHealth() { return health; }

    public String getHealthStatus() {
        if (health >= 70) return "healthy";
        if (health >= MIN_USABLE_HEALTH) return "injured";
        if (health > 0) return "inDANGER";
        return "disabled";
    }

    public void injure(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        health = Math.max(0, health - amount);
    }

    public int getFatigue() { return fatigue; }

    public void increaseFatigue(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        fatigue = Math.min(MAX_FATIGUE, fatigue + amount);
    }

    public void rest(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        fatigue = Math.max(0, fatigue - amount);
    }

    public String getSkills() { return skills; }

    public boolean hasSkill(String skill) {
        if (skill == null || skills.isEmpty()) return false;
        String[] skillList = skills.split(",\\s*");
        for (String s : skillList) {
            if (s.equalsIgnoreCase(skill.trim())) return true;
        }
        return false;
    }

    public void addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            String trimmed = skill.trim();
            if (!hasSkill(trimmed)) {
                if (skills.isEmpty()) {
                    skills = trimmed;
                } else {
                    skills += ", " + trimmed;
                }
            }
        }
    }

    public boolean isUsable() {
        return health >= MIN_USABLE_HEALTH && fatigue < MAX_FATIGUE;
    }
    
    @Override
    public boolean tick() {
        if (!isUsable()) return false;
        
       /* if (!available) {
            this.health = Math.max(0, this.health - 5);
            this.fatigue = Math.min(MAX_FATIGUE, this.fatigue + 10);
        } else {
            
            this.fatigue = Math.max(0, this.fatigue - 2);
        }*/
        if (available) {
            this.fatigue = Math.max(0, this.fatigue - 2);
        }
        return true;
    }

    @Override
    public void heal(int amount) { this.health = Math.min(MAX_HEALTH, this.health + amount); }

    @Override
    public void restoreHealth(int amount) { this.health = Math.min(MAX_HEALTH, this.health + amount); }

    // FIX 2: Added requiresCommunication()
    @Override
    public boolean requiresCommunication() { return false; }

    // FIX 3: Added performDuty() (Abstract so scientists/engineers must define it)
    @Override
    public abstract boolean performDuty();
    
    @Override
    public void takeDamageFromMission() {
        this.health = Math.max(0, this.health - 5); // Humans lose 5 HP per tick on a mission
        this.fatigue += 10;
    }
    @Override
    public void restAndHeal(int amount) {
        if (this.location.getName().contains("Habitat")) {
            this.health = Math.min(100, this.health + amount);
            this.fatigue = Math.max(0, this.fatigue - 5);
        }
    }
    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ID=" + id + ", Name=" + name
                + ", Health=" + getHealth() + " (" + getHealthStatus() + "), Fatigue=" + getFatigue()
                + ", Available=" + isAvailable() + ", Location=" + location.getName() + "]";
    }
}