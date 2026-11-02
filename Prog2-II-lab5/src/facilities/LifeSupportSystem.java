/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package facilities;

/**
 *
 * @author Sky Hawk
 */
public class LifeSupportSystem {
    private int efficiency = 100;
    public int getEfficiency() { return efficiency; }
    public void degrade(int amount) { 
        efficiency = Math.max(0, efficiency - amount); 
        System.out.println("Life Support Efficiency degraded to " + efficiency + "%");
    }
    public int getHealingAmount() {
        return efficiency / 10; 
    }
}
