/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package missions;

/**
 *
 * @author Sky Hawk
 */
public class ResearchMission extends Mission {
    public ResearchMission(String id,int duration){
        super(id,duration);
    }
    @Override public String getType(){return "Research";}
}
