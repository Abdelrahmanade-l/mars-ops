import colony.*;
import facilities.*;
import agents.*;
import missions.*;
import resources.ResourceType;
import emergencies.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // 1. Setup Colony & Locations
        Colony colony = new Colony();
        Location habLoc = new Location("Habitat A");
        Location storageLoc = new Location("Storage Facility");
        Location labLoc = new Location("Laboratory 1");
        
        // 2. Setup Facilities
        Habitat habitat = new Habitat("H1", habLoc, 500); 
        habitat.AddResource(ResourceType.OXYGEN, 300);
        habitat.AddResource(ResourceType.SPARE_PARTS, 2); 
        habitat.AddEquipment("Oxygen Scrubber", 2);
        
        Storage storage = new Storage("S1", storageLoc, 1000);
        storage.AddResource(ResourceType.OXYGEN, 200);
        storage.AddResource(ResourceType.SPARE_PARTS, 10);
        storage.AddResource(ResourceType.BATTERY, 500);
        storage.AddResource(ResourceType.MEDICAL, 10);

        Laboratory lab = new Laboratory("L1", labLoc, 100);
        
        colony.addFacility(habitat);
        colony.addFacility(storage);
        colony.addFacility(lab);

        // 3. Setup Agents (Using your new varargs for skills)
        Scientist scientist = new Scientist("S1", "Ada", habLoc, "Botany", "Geology");
        Engineer engineer = new Engineer("E1", "Ben", habLoc, "Mechanical Repair");
        MedicalOfficer medic = new MedicalOfficer("M1", "Cleo", habLoc, "Trauma Surgery");
        
        ExplorationRover rover = new ExplorationRover("R1", habLoc);
        RepairRobot repairBot = new RepairRobot("R2", habLoc);
        Drone drone = new Drone("D1", habLoc);

        colony.addAgent(scientist);
        colony.addAgent(engineer);
        colony.addAgent(medic);
        colony.addAgent(rover);
        colony.addAgent(repairBot);
        colony.addAgent(drone);

        // =========================================================================
        System.out.println("\n===== Scenario 1: Successful Exploration =====");
        Mission m1 = new ExplorationMission("M1", 3);
        m1.addRequiredResource(ResourceType.BATTERY, 20);
        colony.submitMission(m1, Arrays.asList(scientist, rover));
        for (int i = 0; i < 3; i++) colony.advanceTime();
        System.out.println("Mission State: " + m1.getState());

        // =========================================================================
        System.out.println("\n===== Scenario 2: Mission Rejection (Wrong Agent) =====");
        Mission m2 = new RepairMission("M2", 2);
        m2.addRequiredResource(ResourceType.SPARE_PARTS, 2);
        colony.submitMission(m2, Arrays.asList(scientist)); 
        System.out.println("Reason: " + m2.getFailureReason());

        // =========================================================================
        System.out.println("\n===== Scenario 3: Resource Shortage =====");
        int oxyBefore = colony.getTotalResource(ResourceType.OXYGEN);
        Mission m3 = new ExplorationMission("M3", 1);
        m3.addRequiredResource(ResourceType.OXYGEN, 600);
        colony.submitMission(m3, Arrays.asList(scientist, rover));
        System.out.println("Reason: " + m3.getFailureReason());
        System.out.println("Oxygen Before: " + oxyBefore + " | Oxygen After: " + colony.getTotalResource(ResourceType.OXYGEN));

        // =========================================================================
        System.out.println("\n===== Scenario 4: Oxygen Leak (Resolved) =====");
        System.out.println("Habitat Oxygen Before: " + habitat.GetResource(ResourceType.OXYGEN));
        OxygenLeak leak = new OxygenLeak("LEAK1", habitat, 50);
        colony.handleEmergency(leak);
        System.out.println("Habitat Oxygen After: " + habitat.GetResource(ResourceType.OXYGEN));

        // =========================================================================
        System.out.println("\n===== Scenario 5: Dust Storm (Environment) =====");
        colony.getEnvironment().setCondition(Environment.Condition.DUST_STORM);
        Mission m5 = new ExplorationMission("M5", 2);
        m5.addRequiredResource(ResourceType.BATTERY, 10);
        colony.submitMission(m5, Arrays.asList(scientist, rover));
        System.out.println("Reason: " + m5.getFailureReason());

        // Reset Weather
        colony.getEnvironment().setCondition(Environment.Condition.NORMAL);

        // =========================================================================
        System.out.println("\n===== Scenario 6: Fire Emergency =====");
        System.out.println("Lab Operational Before: " + lab.isOperational());
        Fire fire = new Fire("F1", labLoc);
        colony.handleEmergency(fire);
        System.out.println("Lab Operational After: " + lab.isOperational());

        // =========================================================================
        System.out.println("\n===== Scenario 7: Medical Emergency =====");
        scientist.injure(40); // Scientist takes 40 damage (100 -> 60)
        System.out.println("Scientist Health Before: " + scientist.getHealth());
        MedicalEmergency medEmergency = new MedicalEmergency("MED1", scientist);
        colony.handleEmergency(medEmergency);
        System.out.println("Scientist Health After: " + scientist.getHealth());

        // =========================================================================
        System.out.println("\n===== Scenario 8: Rover Breakdown (Unresolved) =====");
        // Drain all spare parts to force a failure
        storage.ConsumeResource(ResourceType.SPARE_PARTS, 100); 
        
        rover.addDamage(60); // Rover is broken
        System.out.println("Rover Health Before: " + rover.getHealth() + "%");
        RoverBreakdown breakdown = new RoverBreakdown("RB1", rover);
        colony.handleEmergency(breakdown);
        System.out.println("Rover Health After: " + rover.getHealth() + "%");

        // =========================================================================
        System.out.println("\n===== Scenario 9: Oxygen Leak (Unresolved / 2x Loss) =====");
        int oxyBeforeLeak2 = habitat.GetResource(ResourceType.OXYGEN);
        System.out.println("Habitat Oxygen Before Leak 2: " + oxyBeforeLeak2);
        OxygenLeak leak2 = new OxygenLeak("LEAK2", habitat, 30);
        colony.handleEmergency(leak2);
        System.out.println("Habitat Oxygen After Leak 2: " + habitat.GetResource(ResourceType.OXYGEN));

        // =========================================================================
        System.out.println("\n===== Scenario 10: Communication Disruption =====");
        colony.getEnvironment().setCondition(Environment.Condition.COMM_DISRUPTION);
        Mission m10 = new RescueMission("M10", 1);
        m10.addRequiredResource(ResourceType.BATTERY, 10);
        // Drone requires communication, so this should fail
        colony.submitMission(m10, Arrays.asList(drone)); 
        System.out.println("Reason: " + m10.getFailureReason());
        
        colony.getEnvironment().setCondition(Environment.Condition.NORMAL);

        // =========================================================================
        System.out.println("\n===== Scenario 11: Life Support Healing =====");
        // Degrade Life Support to 50% (Heals 5 HP per tick)
        habitat.getLifeSupport().degrade(50);
        engineer.injure(40);
        System.out.println("Engineer Health Before Resting: " + engineer.getHealth());
        // Engineer is in Habitat A and available. Advance time by 1.
        colony.advanceTime(); 
        System.out.println("Engineer Health After 1 Tick (Heals 5): " + engineer.getHealth());

        // =========================================================================
                // =========================================================================
        System.out.println("\n===== Scenario 12: Health/Battery Decrease on Mission =====");
        // Let's reset the environment and check the Rover and Scientist
        colony.getEnvironment().setCondition(Environment.Condition.NORMAL);
        
        System.out.println("Scientist Health Before Mission: " + scientist.getHealth());
        System.out.println("Rover Battery Before Mission: " + rover.getBatteryLevel() + "%");
        
        Mission m12 = new ExplorationMission("M12", 2);
        m12.addRequiredResource(ResourceType.BATTERY, 10);
        colony.submitMission(m12, Arrays.asList(scientist, rover));
        
        // Advance time by 1 unit. During this time, they are ON the mission.
        colony.advanceTime(); 
        
        System.out.println("Scientist Health After 1 Tick on Mission: " + scientist.getHealth());
        System.out.println("Rover Battery After 1 Tick on Mission: " + rover.getBatteryLevel() + "%");
        
        // Finish the mission so agents become available again
        colony.advanceTime();
        
        // =========================================================================
        System.out.println("\n===== Scenario 13: No Healing Outside Habitat =====");
        // The Scientist is now in the Habitat (Habitat A), so they should heal next tick.
        // The Drone is ALSO in Habitat A, so it should recharge.
        // BUT, let's move the Engineer to the Storage Facility to prove they DON'T heal there!
        
        System.out.println("Engineer Health Before (in Storage): " + engineer.getHealth());
        System.out.println("Scientist Health Before (in Habitat): " + scientist.getHealth());
        engineer.setLocation(storageLoc);
        // Advance time by 1. Engineer is in Storage (No heal). Scientist is in Habitat (Heals).
        colony.advanceTime();
        
        System.out.println("Engineer Health After 1 Tick in Storage (No change expected): " + engineer.getHealth());
        System.out.println("Scientist Health After 1 Tick in Habitat (Should heal): " + scientist.getHealth());
        System.out.println();
        colony.printStatusReport();
        
    }
}