
package emergencies;


import colony.Location;
public abstract class Emergency {
    protected String id;
    protected Location location;
    protected boolean resolved;
    
    public Emergency(String id,Location location){
        this.id=id;
        this.location=location;
        this.resolved=false;
    }
    public String getId(){return id;}
    public Location getLocation(){return location;}
    public boolean isResolved(){return resolved;}
    public void setResolved(boolean resolved){this.resolved=resolved;}
}
