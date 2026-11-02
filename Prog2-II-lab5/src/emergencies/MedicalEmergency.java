/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package emergencies;

/**
 *
 * @author Sky Hawk
 */

import agents.CrewMember;
public class MedicalEmergency extends Emergency{
    private CrewMember patient;
    public CrewMember getPatient(){return patient;}
    
    public MedicalEmergency(String id,CrewMember patient){
        super(id,patient.getLocation());
        this.patient=patient;
    }
}
