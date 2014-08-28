import java.util.ArrayList;

/**
 * @author Vijay Patil
 *
 */
public class CTMSPUtil {
    /**
     * @param seq1: Mobile Transactions Sequence 1
     * @param seq2: Mobile Transactions Sequence 2
     * @return similarity score
     */
    public static float getLBSAlignmentSimilarityScore(MobileTransactionSequence seq1, MobileTransactionSequence seq2){
        
        //get sequences
        ArrayList<MTSNode> s1=seq1.seq;
        ArrayList<MTSNode> s2=seq2.seq;
                
        //time length
        //int len=24;
        
        //location penalty
        float penalty=0.5f/(s1.size()+s2.size());
        
        //score matrix of LBS alignment
        float[][] m = new float[s1.size()+1][s2.size()+1];
        
        //initialize 0th row and 0th column of score matrix
        m[0][0] = 0.5f;
        for(int i=1; i<=s1.size(); i++)
            m[i][0]=m[i-1][0]-penalty;
        for(int j=1; j<=s2.size(); j++)
            m[0][j]=m[0][j-1]-penalty;
        
        //fills rest of the matrix
        for(int i=1; i<=s1.size(); i++){
            for(int j=1; j<=s2.size(); j++){
                if(s1.get(i-1).location.loc==s2.get(j-1).location.loc){
          
                    //time length
                    int len=s2.get(j-1).hour;
                    if(s1.get(i-1).hour > s2.get(j-1).hour)
                        len=s1.get(i-1).hour;
                    
                    //time penalty
                    float tp = penalty*(Math.abs(s1.get(i-1).hour-s2.get(j-1).hour))/len;
                    
                    //service reward
                    float sr=0f;
                    ArrayList<String> temps1 = new ArrayList<String>(s1.get(i-1).appids); 
                    temps1.retainAll(s2.get(j-1).appids);
                    int servicesIntersect = temps1.size();
                    temps1 = new ArrayList<String>(s1.get(i-1).appids);
                    temps1.removeAll(s2.get(j-1).appids);
                    temps1.addAll(s2.get(j-1).appids);
                    int servicesUnion = temps1.size();
                    if(servicesUnion!=0)
                        sr = penalty*servicesIntersect/servicesUnion;
                    else if(servicesIntersect==0) //intersect and union both zero means same service
                        sr = penalty;
                    
                    //calculate score
                    m[i][j]=Math.max(m[i-1][j-1]-tp+sr, Math.max(m[i-1][j]-penalty, m[i][j-1]-penalty)); 
                }else{
                    m[i][j]=Math.max(m[i-1][j]-penalty, m[i][j-1]-penalty);
                }
            }
        }
        
        //displays score matrix for debug purpose
        //for(int i=0; i<=s1.size(); i++){
        //  System.out.println(" ");
        //  for(int j=0; j<=s2.size(); j++)
        //      System.out.print(m[i][j]+" ");
        //  }
        //System.out.println(" ");
        
        //return final score
        return m[s1.size()][s2.size()];
    }
    
    /**
     * @param seqs: Multiple Mobile Transaction Sequences
     * @return similarity matrix for provided seqs
     */
    public static float[][] getSimilarityMatrix(ArrayList<MobileTransactionSequence> seqs){
        float sim[][] = new float[seqs.size()][seqs.size()];
        
        for(int i=0; i<seqs.size(); i++)
            for(int j=0; j<seqs.size(); j++)
                sim[i][j] = getLBSAlignmentSimilarityScore(seqs.get(i), seqs.get(j));
        
        return sim;
    }
}
