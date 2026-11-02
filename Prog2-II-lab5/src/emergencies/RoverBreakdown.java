/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package emergencies;

import agents.ExplorationRover;

/**
 *
 * @author Sky Hawk
 */
public class RoverBreakdown extends Emergency{
    private ExplorationRover rover;
    public ExplorationRover getRover(){return rover;}
    public RoverBreakdown(String id,ExplorationRover rover){
        super(id,rover.getLocation());
        this.rover=rover;
    }
}
