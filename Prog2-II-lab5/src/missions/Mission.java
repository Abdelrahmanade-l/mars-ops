/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package missions;

/**
 *
 * @author Sky Hawk
 */
import agents.Agent;
import resources.ResourceType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class Mission {
    protected String id;
    protected int duration;
    protected int elapsedTime;
    protected MissionState state;
    protected List<Agent> assignedAgents;
    protected Map<ResourceType, Integer> requiredResources;
    protected String failureReason;
    
    public Mission(String id, int duration){
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mission ID cannot be empty.");
        }

        if (duration <= 0) {
            throw new IllegalArgumentException("Mission duration must be positive.");
        }
        
        this.id=id;
        this.duration=duration;
        this.assignedAgents=new ArrayList<>();
        this.requiredResources=new EnumMap<>(ResourceType.class);
    }
    
    public abstract String getType();
    
    public String getId(){return id;}
    public MissionState getState(){return state;}
    public String getFailureReason(){return failureReason;}
    public List<Agent>getAssignedAgents(){return assignedAgents;}
    public Map<ResourceType,Integer>getRequiredResources(){return requiredResources;}
    
    public void addRequiredResource(ResourceType type,int amount){
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null.");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Required resource amount cannot be negative.");
        }
        requiredResources.put(type,amount);
    }
    
    public void assign(List<Agent>agents){
        if (agents == null || agents.isEmpty()) {
            throw new IllegalArgumentException("At least one agent is required.");
        }

        for (Agent agent : agents) {
            if (agent == null) {
                throw new IllegalArgumentException("Agent cannot be null.");
            }

            if (!agent.isAvailable()) {
                throw new IllegalArgumentException(
                    "Agent " + agent.getId() + " is not available."
                );
            }
        }
        this.assignedAgents = new ArrayList<>(agents);
        for(Agent a: agents){
            a.setAvailable(false);
        }
        this.state=MissionState.ASSIGNED;
    }
    public void start(){this.state=MissionState.IN_PROGRESS;}
    
    public void tick(){
        if(state!=MissionState.IN_PROGRESS)return;
        elapsedTime++;
        for (Agent a : assignedAgents) {
            a.takeDamageFromMission();
        }
        if(elapsedTime>=duration)complete();
    }
    
    public void complete(){
        this.state=MissionState.COMPLETED;
        releaseAgents();
    }
    
    public void fail(String reason){
        this.state=MissionState.FAILED;
        this.failureReason=reason;
        releaseAgents();
    }
    
    private void releaseAgents(){
        for(Agent a :assignedAgents){a.setAvailable(true);}
        assignedAgents.clear();
    }
}
