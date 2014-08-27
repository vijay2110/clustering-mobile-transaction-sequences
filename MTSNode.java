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

    @Override
    public int compareTo(MTSNode o) {
        return this.hour-o.hour;
    }
}
