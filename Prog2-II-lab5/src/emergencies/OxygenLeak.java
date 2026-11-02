/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package emergencies;

/**
 *
 * @author Sky Hawk
 */
import facilities.Habitat;
import resources.ResourceType;

public class OxygenLeak extends Emergency{
    private Habitat habitat;
    private int oxygenLoss;

    public OxygenLeak(String id, Habitat habitat, int oxygenLoss){
        super(id,habitat.getLocation());
        this.habitat =habitat;
        this.oxygenLoss =oxygenLoss;
    }
    public Habitat getHabitat(){return habitat;}
    public int getOxygenLoss(){return oxygenLoss;}
}
