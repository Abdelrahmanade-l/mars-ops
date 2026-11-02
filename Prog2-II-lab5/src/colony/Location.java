
package colony;

public class Location {
    private final String name;
    public Location(String name){this.name=name;}
    public String getName(){return name;}
    @Override public String toString(){return name;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location other = (Location) o;
        return name != null && name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.toLowerCase().hashCode();
    }



}
