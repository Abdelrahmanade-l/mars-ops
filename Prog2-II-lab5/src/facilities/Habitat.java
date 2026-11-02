/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package facilities;

import colony.Location;

/**
 *
 * @author Sky Hawk
 */
public class Habitat extends Facility{
    private final LifeSupportSystem lifeSupport;
    public Habitat(String id, Location location,int capacity) {
        super(id, location,capacity);
        this.lifeSupport = new LifeSupportSystem();
    }
    public LifeSupportSystem getLifeSupport(){return lifeSupport;}
}
