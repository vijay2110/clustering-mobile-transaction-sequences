import java.util.ArrayList;

/**
 * @author Vijay Patil
 *
 */
public class MTSNode implements Comparable<MTSNode> {
    
    public Location location;
    public int hour;
    public ArrayList<String> appids = null;
    
    public MTSNode(Location location, int hour, ArrayList<String> appids) {
        this.location = location;
        this.hour = hour;
        this.appids = appids; 
    }
    
    public MTSNode(int hour, int loc, String appid) {
        this.appids = new ArrayList<String>();
        appids.add(appid);
        this.location = new Location(loc);
        this.hour = hour;
    }

    @Override
    public int compareTo(MTSNode o) {
        return this.hour-o.hour;
    }
}
