/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package facilities;

/**
 *
 * @author Sky Hawk
 */
import colony.Location;
import resources.ResourceType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public abstract class Facility {
    protected String id;
    protected Location location;
    protected boolean operational;
    protected Map<ResourceType, Integer> resources;
    protected Map<String, Integer> equipment;
    protected int personnel_capacity;
    
    public Facility(String id,Location location,int personnel_capacity)
    {
        this.id = id;
        this.location = location;
        this.operational = true;
        this.resources = new EnumMap<>(ResourceType.class);
        this.equipment = new HashMap<>();
        this.personnel_capacity = personnel_capacity;
    }
    
    public String getId() { return id; }
    public Location getLocation() { return location; }
    public boolean isOperational() { return operational; }
    public void setOperational(boolean operational){this.operational=operational;}
    public int getCapacity() { return personnel_capacity; }
    public int getEquipmentCount(String item) { return equipment.getOrDefault(item, 0); }
    public Map<String, Integer> getEquipment() { return equipment; }
    
    public int getCurrentTotalResources() {
        int total = 0;
        for (int amount : resources.values()) { total += amount; }
        return total;
    }
    
    public int GetResource(ResourceType resource)
    {
        if (resource == null) {
            throw new IllegalArgumentException("Resource type cannot be null.");
        }
        return resources.getOrDefault(resource, 0);
    }
    
    public void AddResource(ResourceType resource, int amount) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource type cannot be null.");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Resource amount cannot be negative.");
        }

        if (getCurrentTotalResources() + amount > personnel_capacity) {
            System.out.println(
                "Cannot add " + amount + " " + resource
                + " to " + id + ". Exceeds capacity."
            );
            return;
        }

        resources.put(
            resource,
            resources.getOrDefault(resource, 0) + amount
        );
    }
    public int ConsumeResource(ResourceType type, int amount)
    {
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null.");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        int current = GetResource(type);
        int taken = Math.min(current, amount);
        resources.put(type, current - taken);
        return taken;
    }
    
    public void AddEquipment(String equipment_type, int amount)
    {
        if (equipment_type == null || equipment_type.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type cannot be empty.");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Equipment amount cannot be negative.");
        }
        int newAmount = equipment.getOrDefault(equipment_type, 0) + amount;
        equipment.put(equipment_type, newAmount);
    }
    
    public void RemoveEquipment(String equipment_type, int amount) {
        if (equipment_type == null || equipment_type.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type cannot be empty.");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Equipment amount cannot be negative.");
        }

        int current = equipment.getOrDefault(equipment_type, 0);
        int remaining = Math.max(0, current - amount);

        equipment.put(equipment_type, remaining);
    }
    public void SetPersonnelCapacity(int capacity)
    {
        this.personnel_capacity = capacity;
        System.out.println("All Facilities' maximum personnel capacity is: "+ capacity);
    }
    public int GetPersonnelCapacity()
    {
        int capacity = this.personnel_capacity;
        System.out.println("All Facilities' maximum personnel capacity is: "+ capacity);
        return capacity;
    }
}
