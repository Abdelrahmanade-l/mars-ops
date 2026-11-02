/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colony;

/**
 *
 * @author Sky Hawk
 */
public class Environment {
    public enum Condition{NORMAL,DUST_STORM,COMM_DISRUPTION}
    private Condition currentCondition=Condition.NORMAL;

    public Condition getCurrentCondition() {
        return currentCondition;
    }

    public void setCondition(Condition currentCondition) {
        this.currentCondition = currentCondition;
    }
    
}
