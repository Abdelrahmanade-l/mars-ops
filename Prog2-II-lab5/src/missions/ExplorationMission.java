
package missions;


public class ExplorationMission extends Mission {
    public ExplorationMission(String id,int duration){
        super(id,duration);
    }
    @Override public String getType(){return "Exploration";}
}
