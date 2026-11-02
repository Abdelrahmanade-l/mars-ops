package colony;

import agents.Agent;
import facilities.Facility;
import facilities.Habitat;
import missions.Mission;
import missions.MissionState;
import resources.ResourceType;
import emergencies.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Colony {

    private List<Agent> agents;
    private List<Facility> facilities;
    private Mission activeMission;
    private Environment environment;

    private static final int LEAK_SPARE_PARTS = 2;

    public Colony() {
        this.agents = new ArrayList<>();
        this.facilities = new ArrayList<>();
        this.activeMission = null;
        this.environment = new Environment();
    }

    public Environment getEnvironment() { return environment; }
    public Mission getActiveMission() { return activeMission; }
    public List<Agent> getAgents() { return agents; }
    public List<Facility> getFacilities() { return facilities; }

    public void addAgent(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null");
        }
        for (Agent existing : agents) {
            if (existing.getId().equals(agent.getId())) {
                throw new IllegalArgumentException("Agent already exists");
            }
        }
        agents.add(agent);
    }

    public void addFacility(Facility facility) {
        if (facility == null) {
            throw new IllegalArgumentException("Facility cannot be null");
        }
        facilities.add(facility);
    }

    public int getTotalResource(ResourceType type) {
        int total = 0;
        for (Facility f : facilities) {
            if (f.isOperational()) {
                total += f.GetResource(type);
            }
        }
        return total;
    }

    public boolean hasEnoughResources(Map<ResourceType, Integer> required) {
        if (required == null) return false;

        for (Map.Entry<ResourceType, Integer> entry : required.entrySet()) {
            ResourceType type = entry.getKey();
            Integer amount = entry.getValue();

            if (type == null || amount == null || amount < 0) return false;
            if (getTotalResource(type) < amount) return false;
        }
        return true;
    }

    private boolean consumeResource(Map<ResourceType, Integer> required) {
        if (!hasEnoughResources(required)) return false;

        for (Map.Entry<ResourceType, Integer> entry : required.entrySet()) {
            int remaining = entry.getValue();

            for (Facility facility : facilities) {
                if (!facility.isOperational()) continue;
                if (remaining == 0) break;

                int available = facility.GetResource(entry.getKey());
                int amountToTake = Math.min(available, remaining);

                if (amountToTake > 0) {
                    int taken = facility.ConsumeResource(entry.getKey(), amountToTake);
                    remaining -= taken;
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    public boolean submitMission(Mission mission, List<Agent> missionAgents) {
        if (mission == null) {
            System.out.println("Mission cannot be null.");
            return false;
        }

        if (missionAgents == null || missionAgents.isEmpty()) {
            return reject(mission, "At least one agent is required.");
        }

        if (activeMission != null && activeMission.getState() != MissionState.COMPLETED
                && activeMission.getState() != MissionState.FAILED) {
            return reject(mission, "Another mission is already active.");
        }

        Environment.Condition condition = environment.getCurrentCondition();
        if (condition == Environment.Condition.DUST_STORM && mission.getType().equalsIgnoreCase("Exploration")) {
            return reject(mission, "Surface operations are not allowed during a dust storm.");
        }

        for (Agent a : missionAgents) {
            if (!a.isAvailable()) {
                return reject(mission, "Agent " + a.getId() + " is not available.");
            }
            if (!a.canPerform(mission)) {
                return reject(mission, "Agent does not possess the required capability.");
            }
            if (condition == Environment.Condition.COMM_DISRUPTION && a.requiresCommunication()) {
                return reject(mission, "Communication is disrupted.");
            }
        }

        if (!hasEnoughResources(mission.getRequiredResources())) {
            return reject(mission, "Insufficient resources.");
        }

        if (!consumeResource(mission.getRequiredResources())) {
            return reject(mission, "Unable to consume the required resources.");
        }

        mission.assign(missionAgents);
        activeMission = mission;
        System.out.println("Mission " + mission.getId() + " ASSIGNED.");
        return true;
    }

    private boolean reject(Mission mission, String reason) {
        if (mission == null) {
            System.out.println("Mission cannot be assigned. " + reason);
            return false;
        }
        mission.fail(reason);
        System.out.println("Mission " + mission.getId() + " cannot be assigned. Reason: " + reason);
        return false;
    }

    public void advanceTime() {
        // 1. Progress active mission and check environmental/agent failures
        if (activeMission != null) {
            Environment.Condition cond = environment.getCurrentCondition();

            if (cond == Environment.Condition.DUST_STORM
                    && activeMission.getType().equalsIgnoreCase("Exploration")) {
                activeMission.fail("Dust storm halted surface operations.");
            } else if (cond == Environment.Condition.COMM_DISRUPTION
                    && hasCommDependentAgent(activeMission)) {
                activeMission.fail("Communication disruption lost control of remote unit.");
            } else {
                // Check if any assigned agent became unusable (e.g. battery 0 or injured)
                for (Agent a : activeMission.getAssignedAgents()) {
                    if (!a.tick()) { // tick() returns false if agent is unusable
                        activeMission.fail("Assigned agent " + a.getId() + " became unusable.");
                        break;
                    }
                }
            }

            // If still valid, start/advance the mission
            if (activeMission.getState() != MissionState.FAILED) {
                if (activeMission.getState() == MissionState.ASSIGNED) {
                    activeMission.start();
                }
                activeMission.tick();
            }

            if (activeMission.getState() == MissionState.COMPLETED
                    || activeMission.getState() == MissionState.FAILED) {
                System.out.println("Mission " + activeMission.getId() + " " + activeMission.getState());
                activeMission = null;
            }
        }

        // 2. Tick idle agents and heal/recharge those resting in an operational Habitat
        for (Agent agent : agents) {
            if (agent.isAvailable()) {
                agent.tick();
                for (Facility f : facilities) {
                    if (f.isOperational()
                            && f.getLocation().equals(agent.getLocation())
                            && f instanceof Habitat) {
                        int healRate = ((Habitat) f).getLifeSupport().getHealingAmount();
                        agent.restAndHeal(healRate);
                    }
                }
            }
        }
    }

    private boolean hasCommDependentAgent(Mission mission) {
        for (Agent a : mission.getAssignedAgents()) {
            if (a.requiresCommunication()) return true;
        }
        return false;
    }

    public void handleEmergency(Emergency emergency) {
        Agent humanResponder = null;
        Agent robotResponder = null;

        for (Agent agent : agents) {
            if (agent.isAvailable() && agent.canHandle(emergency)) {
                if (agent instanceof agents.CrewMember && humanResponder == null) {
                    humanResponder = agent;
                } else if (agent instanceof agents.Robot && robotResponder == null) {
                    robotResponder = agent;
                }
            }
        }

        if (emergency instanceof OxygenLeak) {
            OxygenLeak leak = (OxygenLeak) emergency;
            System.out.println("EMERGENCY: Oxygen Leak at " + leak.getLocation().getName());

            leak.getHabitat().ConsumeResource(ResourceType.OXYGEN, leak.getOxygenLoss());

            if (humanResponder == null || robotResponder == null) {
                leak.getHabitat().ConsumeResource(ResourceType.OXYGEN, leak.getOxygenLoss());
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: Missing responders. Oxygen lost twice.");
                return;
            }
            if (getTotalResource(ResourceType.SPARE_PARTS) < LEAK_SPARE_PARTS) {
                leak.getHabitat().ConsumeResource(ResourceType.OXYGEN, leak.getOxygenLoss());
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: Insufficient repair resources. Oxygen lost twice.");
                return;
            }


            humanResponder.setLocation(leak.getLocation());
            robotResponder.setLocation(leak.getLocation());

            consumeResource(Map.of(ResourceType.SPARE_PARTS, LEAK_SPARE_PARTS));
            emergency.setResolved(true);
            System.out.println("Emergency RESOLVED: Oxygen leak fixed by "
                    + humanResponder.getId() + " and " + robotResponder.getId() + ".");
        }

        else if (emergency instanceof Fire) {
            System.out.println("EMERGENCY: Fire at " + emergency.getLocation().getName());
            if (humanResponder == null) {
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: No Engineer or Medical Officer available.");
                return;
            }
            if (getTotalResource(ResourceType.MEDICAL) < 5 || getTotalResource(ResourceType.SPARE_PARTS) < 2) {
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: Insufficient medical supplies or spare parts.");
                return;
            }

            humanResponder.setLocation(emergency.getLocation());
            if (robotResponder != null) {
                robotResponder.setLocation(emergency.getLocation());
            }

            consumeResource(Map.of(ResourceType.MEDICAL, 5, ResourceType.SPARE_PARTS, 2));
            for (Facility f : facilities) {
                if (f.getLocation().equals(emergency.getLocation())) {
                    f.setOperational(false);
                    System.out.println("Warning: Facility at " + f.getLocation() + " is now OFFLINE due to fire damage.");
                }
            }
            emergency.setResolved(true);
            System.out.println("Emergency RESOLVED: Fire extinguished.");
        }

        else if (emergency instanceof RoverBreakdown) {
            RoverBreakdown breakdown = (RoverBreakdown) emergency;
            System.out.println("EMERGENCY: Rover Breakdown at " + emergency.getLocation().getName());

            // If the broken rover is on the active mission, fail the mission
            if (activeMission != null && activeMission.getAssignedAgents().contains(breakdown.getRover())) {
                activeMission.fail("Rover " + breakdown.getRover().getId() + " broke down during mission.");
                activeMission = null;
            }

            if (robotResponder == null) {
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: No Repair Robot available.");
                return;
            }
            if (getTotalResource(ResourceType.SPARE_PARTS) < 3) {
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: Insufficient spare parts.");
                return;
            }

            robotResponder.setLocation(breakdown.getLocation());
            consumeResource(Map.of(ResourceType.SPARE_PARTS, 3));
            breakdown.getRover().restoreHealth(50);
            emergency.setResolved(true);
            System.out.println("Emergency RESOLVED: Rover repaired.");
        }

        else if (emergency instanceof MedicalEmergency) {
            MedicalEmergency med = (MedicalEmergency) emergency;
            System.out.println("EMERGENCY: Medical Emergency for " + med.getPatient().getName());


            boolean facilityOperational = false;
            for (Facility f : facilities) {
                if (f.getLocation().equals(med.getLocation()) && f.isOperational()) {
                    facilityOperational = true;
                    break;
                }
            }
            if (!facilityOperational) {
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: Relevant medical/habitat facility is not operational.");
                return;
            }

            if (humanResponder == null) {
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: No Medical Officer available.");
                return;
            }
            if (getTotalResource(ResourceType.MEDICAL) < 2) {
                emergency.setResolved(false);
                System.out.println("Emergency UNRESOLVED: Insufficient medical supplies.");
                return;
            }

            humanResponder.setLocation(med.getLocation());
            consumeResource(Map.of(ResourceType.MEDICAL, 2));
            if (humanResponder instanceof agents.MedicalOfficer) {
                ((agents.MedicalOfficer) humanResponder).treat(med.getPatient());
            } else {
                med.getPatient().restoreHealth(30); // Fallback just in case
            }
            emergency.setResolved(true);
            System.out.println("Emergency RESOLVED: Patient healed to " + med.getPatient().getHealth() + " HP.");
        }
    }

    public void printStatusReport() {
        System.out.println("===== COLONY STATUS =====");
        System.out.println("Environment: " + environment.getCurrentCondition());
        System.out.println("Active mission: " + (activeMission == null ? "none" : activeMission.getId()));
        System.out.println("\n--- Colony-Wide Resources ---");
        for (ResourceType type : ResourceType.values()) {
            System.out.println(type + ": " + getTotalResource(type));
        }
        System.out.println("\n--- Agents ---");
        for (Agent a : agents) {
            System.out.println(a);
        }
        System.out.println("\n--- Facilities ---");
        for (Facility f : facilities) {
            System.out.println(
                    "Facility: " + f.getClass().getSimpleName()
                            + " | ID: " + f.getId()
                            + " | Location: " + f.getLocation().getName()
                            + " | Operational: " + f.isOperational());
        }
        System.out.println("=========================");
    }
}