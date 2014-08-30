/**
 * @author Vijay Patil
 *
 */
public class Location {
    public float lat;
    public float lon;
    public int loc;
    
    public Location(float lat, float lon, int loc) {
        this.lat = lat;
        this.lon = lon;
        this.loc = loc;
    }
    
    public Location(int i) {
        this.lat = 0;
        this.lon = 0;
        this.loc = i;
    }
    
}
