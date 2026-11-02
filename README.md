MARS-OPS: Mars Colony Simulator (Lab 5)
=======================================

1. SYSTEM OVERVIEW
------------------
This program simulates a Mars colony with human crew, robots, facilities, resources, missions, and emergencies.
It is divided into 6 packages:
- agents: Agent interface, CrewMember (Scientist, Engineer, MedicalOfficer), and Robot (ExplorationRover, RepairRobot, Drone).
- facilities: Facility base class (Habitat, Laboratory, Storage, PowerStation, CommunicationStation) and LifeSupportSystem.
- missions: Mission base class, MissionState enum, and concrete missions (Exploration, Repair, Research, Rescue).
- emergencies: Emergency base class (OxygenLeak, Fire, RoverBreakdown, MedicalEmergency).
- resources: ResourceType enum (OXYGEN, WATER, FOOD, FUEL, BATTERY, MEDICAL, SPARE_PARTS).
- colony: Colony (main manager), Environment, and Location.


2. OOP RELATIONSHIPS & DESIGN DECISIONS
---------------------------------------
- Inheritance & Polymorphism: CrewMember and Robot implement the Agent interface. Each subclass overrides canPerform(Mission) and canHandle(Emergency) so the Colony checks capabilities polymorphically without checking concrete types.
- Composition: Habitat creates and owns a final LifeSupportSystem in its constructor.
- Aggregation: Colony holds lists of Agent and Facility objects created outside the Colony.
- Association: Agents interact with Mission and CrewMember objects (e.g., MedicalOfficer.treat(patient)).


3. RESOURCE ALLOCATION POLICY
-----------------------------
- Colony-wide totals are calculated by summing resources only from operational facilities (isOperational() == true).
- All-or-Nothing Rule: An operation only starts if the colony has enough total resources. Otherwise, it fails and consumes nothing.
- Allocation Policy: Resources are consumed sequentially from operational facilities in the order they were added to the colony's facility list.


4. MISSION STATES & TIME SIMULATION
-----------------------------------
Only ONE mission can be active at a time.
- ASSIGNED: Mission is accepted, resources are consumed, and agents are marked unavailable.
- IN_PROGRESS: Mission starts on the next advanceTime() tick. Each tick increases elapsedTime by 1. On a mission, humans lose 5 HP and gain 10 fatigue per tick; robots lose 15% battery per tick.
- COMPLETED: Reached when elapsedTime >= duration. Agents are released (available = true).
- FAILED: Happens immediately if submission fails, or mid-mission if weather changes, an agent becomes unusable, or a rover breaks down. Agents are released.

Idle & Healing Rules (per tick):
- Idle humans lose 2 fatigue; idle robots lose 1% battery.
- Available agents located in an operational Habitat heal/recharge based on the Habitat's LifeSupportSystem (efficiency / 10).
- Usable limits: Humans need health >= 40 and fatigue < 100. Robots need health > 0 and battery > 0.


5. ENVIRONMENTAL EFFECTS
------------------------
- NORMAL: All operations run normally.
- DUST_STORM: Exploration missions cannot be submitted. If an Exploration mission is already active during advanceTime(), it immediately fails.
- COMM_DISRUPTION: Missions using communication-dependent agents (like Drone) cannot start. If an active mission uses a Drone when communication disruption occurs, it fails on the next tick.


6. EMERGENCY RULES
------------------
Responders must be available and able to handle the emergency. Dispatched responders are moved to the emergency location. If responders or resources are missing, the emergency is marked UNRESOLVED and no repair/medical resources are consumed.

- Oxygen Leak (Habitat):
  * Responders: 1 Human (Engineer) + 1 Robot (RepairRobot).
  * Resources: 2 SPARE_PARTS.
  * Effect: Immediately deducts oxygenLoss from the Habitat. If resolved, the leak stops. If unresolved, oxygenLoss is deducted a second time (2x total loss).

- Fire (Facility):
  * Responders: 1 Human (Engineer or MedicalOfficer; RepairRobot optional).
  * Resources: 5 MEDICAL and 2 SPARE_PARTS.
  * Effect: Fire is extinguished, and the affected facility becomes offline (operational = false).

- Rover Breakdown:
  * Responders: 1 Robot (RepairRobot).
  * Resources: 3 SPARE_PARTS.
  * Effect: If the broken rover is on the active mission, that mission fails. Repairing restores +50% health to the rover.

- Medical Emergency:
  * Responders: 1 Human (MedicalOfficer) + an operational facility at the patient's location.
  * Resources: 2 MEDICAL.
  * Effect: MedicalOfficer calls treat(patient), healing the patient by +50 HP (up to 100).
