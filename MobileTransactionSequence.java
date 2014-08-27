import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Vijay Patil
 *
 */
public class MobileTransactionSequence {
    public int userid;
    ArrayList<MTSNode> seq = null;
    
    public MobileTransactionSequence(int userid, ArrayList<MTSNode> seq) {
        this.userid = userid;
        this.seq = seq;
        //sort Mobile Transaction Sequences based on hour key
        Collections.sort(this.seq);
    }
}
