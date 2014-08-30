import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
                
        //location penalty
        float penalty=0.5f/(s1.size()+s2.size());
        
        //time length
        int len=s2.get(s2.size()-1).hour;
        if(s1.get(s1.size()-1).hour > s2.get(s2.size()-1).hour)
            len=s1.get(s1.size()-1).hour;
        
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
          
                    //time penalty
                    float tp = penalty*(Math.abs(s1.get(i-1).hour-s2.get(j-1).hour))/len;
                    
                    //service reward
                    float sr=0f;
                    HashSet<String> distinctItems = new HashSet<String>(s1.get(i-1).appids);
                    distinctItems.addAll(s2.get(j-1).appids);
                    int servicesIntersect = s1.get(i-1).appids.size() + s2.get(j-1).appids.size() - distinctItems.size();
                    int servicesUnion = distinctItems.size();
                    if(servicesUnion!=0)
                        sr = penalty*servicesIntersect/servicesUnion;
                    else if(servicesIntersect==0) //intersect and union both zero means same service (no service in both nodes means similar service)
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
    
    /**
     * @param a: first matrix
     * @param b: second matrix
     * @return Hubert's Gamma Statistics
     * @throws CTMSPException
     */
    public static float calculateHubertsGammaStats(float[][] a, float[][] b) throws CTMSPException{
        //Input Validation: both matrices must be of same size and square matrices
        if(a==null || b==null)
            throw new CTMSPException();
        if(a.length!=a[0].length || a.length!=b.length || a[0].length!=b[0].length)
            throw new CTMSPException();
        
        int n = a.length;
        int m = n*(n-1)/2;
        float sum=0f;
        
        for(int i=0; i<(n-1); i++)
            for(int j=i+1; j<n; j++)
                sum=sum+(a[i][j]*b[i][j]);
        
        return sum/(float)m;
    }
    
    /**
     * @param elements: set of elements which need to be clustered
     * @param simMat: similarity matrix of elements
     * @param threshold: used to decide nearest MTS's
     * @return clustering result
     * @throws CTMSPException
     */
    public static HashMap<Integer,HashSet<Integer>> performCAST(HashSet<Integer> elements, float[][] simMat, float threshold) throws CTMSPException{
        
        //Input Validation
        if(threshold==0 || elements==null || simMat==null)
            throw new CTMSPException();
        if(simMat.length!=simMat[0].length || elements.size()!=simMat.length)
            throw new CTMSPException();
        
        //Create empty clustering result which we are going to return at the end
        HashMap<Integer,HashSet<Integer>> clusters = new HashMap<Integer,HashSet<Integer>>();
        int clusterId = 1;
        
        //keep iterating until we have elements which are not assigned to any cluster
        //TODO check whether this goes into infinite loop
        while(!elements.isEmpty()){
            
            //select pair from elements with highest similarity 
            float max = 0f;
            int mts1=1,mts2=2; //these choices will be replaced by below logic (worst case, if max not found, then these choices are good enough)
            ArrayList<Integer> alElements = new ArrayList<Integer>(elements);
            for(int i=0; i<alElements.size(); i++)
                for(int j=i+1; j<alElements.size(); j++)
                    if(simMat[alElements.get(i)][alElements.get(j)]>max){
                        max=simMat[alElements.get(i)][alElements.get(j)];
                        mts1=alElements.get(i);
                        mts2=alElements.get(j);
                    }
            
            //add selected pair to new cluster
            HashSet<Integer> c = new HashSet<Integer>();
            c.add(mts1);
            c.add(mts2);
            
            //keep iterating until cluster gets converged
            //TODO check whether this goes into infinite loop
            boolean clusterConverged=false;
            while(!clusterConverged){
                boolean addedCloseMTStoCluster=false;
                boolean removedDistantMTSfromCluster=false;
                
                //calculate cluster's self similarity, by taking mean similarity
                float selfSimC = 0;
                ArrayList<Integer> alC = new ArrayList<Integer>(c);
                for(int i=0; i<alC.size(); i++)
                    for(int j=i+1; j<alC.size(); j++)
                        selfSimC = selfSimC + simMat[alC.get(i)][alC.get(j)];
                selfSimC = selfSimC/(alC.size()*(alC.size()-1)/2);
                
                //find nearest MTS which is not in cluster
                ArrayList<Integer> alEle = new ArrayList<Integer>(elements);
                alEle.removeAll(c);
                float minDistance=Float.MAX_VALUE;
                int nearestMTS=0;
                for(int i=0; i<alC.size(); i++)
                    for(int j=0; j<alEle.size(); j++){
                        if(Math.abs(selfSimC - simMat[alC.get(i)][alEle.get(j)]) < minDistance){
                            nearestMTS = alEle.get(j);
                            minDistance = Math.abs(selfSimC - simMat[alC.get(i)][alEle.get(j)]);
                        }
                    }
                
                //add that nearest close MTS into cluster
                if(nearestMTS!=0 && minDistance<=threshold){
                    c.add(nearestMTS);
                    addedCloseMTStoCluster=true;
                }
                
                //re-calculate cluster's self similarity, by taking mean similarity
                selfSimC = 0;
                alC = new ArrayList<Integer>(c);
                for(int i=0; i<alC.size(); i++)
                    for(int j=i+1; j<alC.size(); j++)
                        selfSimC = selfSimC + simMat[alC.get(i)][alC.get(j)];
                selfSimC = selfSimC/(alC.size()*(alC.size()-1)/2);
                
                //find farthest MTC within cluster
                int farthestMTS1=0, farthestMTS2=0;
                float maxDistance=0f;
                for(int i=0; i<alC.size(); i++)
                    for(int j=i+1; j<alC.size(); j++)
                        if(Math.abs(selfSimC - simMat[alC.get(i)][alC.get(j)]) > maxDistance){
                            farthestMTS1 = alC.get(i);
                            farthestMTS2 = alC.get(j);
                            maxDistance = Math.abs(selfSimC - simMat[alC.get(i)][alC.get(j)]);
                        }
                
                //remove that farthest distant pair from cluster
                if(farthestMTS1!=0 && maxDistance>threshold){
                    c.remove(farthestMTS1);
                    c.remove(farthestMTS2);
                    removedDistantMTSfromCluster=true;
                }
                
                if(!addedCloseMTStoCluster && !removedDistantMTSfromCluster)
                    clusterConverged=true;
            }
            
            //add cluster to clustering result
            clusters.put(clusterId, c);
            clusterId++;
            
            //remove clustered elements from set "elements"
            elements.removeAll(c);
        }
        
        return clusters;
    }
}
