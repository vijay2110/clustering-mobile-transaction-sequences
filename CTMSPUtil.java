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
        sum=sum/m;
        
        return sum;
    }
    
    /**
     * @param elements: set of elements which need to be clustered
     * @param simMat: similarity matrix of elements
     * @param threshold: used to decide nearest MTS's
     * @return clustering result
     * @throws CTMSPException
     */
    public static HashMap<Integer,HashSet<Integer>> performCAST(HashMap<Integer,HashSet<Integer>> initialClusters, float[][] simMat, float threshold) throws CTMSPException{
        
        //Input Validation
        if(initialClusters==null || simMat==null)
            throw new CTMSPException();
        if(simMat.length!=simMat[0].length || initialClusters.size()!=simMat.length)
            throw new CTMSPException();
        if(initialClusters.size()==0)
            throw new CTMSPException();
        
        //if there is only one member in initialClusters then just return same result, as no more clustering required
        //if threshold is 0 then just return same result, as no more clustering required
        if(initialClusters.size()==1) // || threshold==0f)
            return initialClusters;
        
        //get all elements
        HashSet<Integer> elements = new HashSet<Integer>(initialClusters.keySet());
        
        //Create empty clustering result which we are going to return at the end
        HashMap<Integer,HashSet<Integer>> clusters = new HashMap<Integer,HashSet<Integer>>();
        int clusterId = 0;
        
        //keep iterating until we have elements which are not assigned to any cluster
        //TODO check whether this goes into infinite loop
        while(!elements.isEmpty()){
            
            //select pair from elements with highest similarity 
            float max = 0f;
            int mts1=0,mts2=1; //these choices will be replaced by below logic (worst case, if max not found, then these choices are good enough)
            ArrayList<Integer> alElements = new ArrayList<Integer>(elements);
            HashSet<Integer> c = new HashSet<Integer>();
            if(alElements.size()==1){
                c.add(alElements.get(0));
                clusters.put(clusterId, c);
                break;
            }else{
            
                for(int i=0; i<alElements.size(); i++)
                    for(int j=i+1; j<alElements.size(); j++)
                        if(simMat[alElements.get(i)][alElements.get(j)]>max){
                            max=simMat[alElements.get(i)][alElements.get(j)];
                            mts1=alElements.get(i);
                            mts2=alElements.get(j);
                        }
                
                //add selected pair to new cluster
                
                c.add(mts1);
                c.add(mts2);
            }
                
            
            boolean cChanged=false;
            
            //keep iterating until cluster gets converged
            //TODO check whether this goes into infinite loop
            boolean clusterConverged=false;
            while(!clusterConverged){
                boolean addedCloseMTStoCluster=false;
                boolean removedDistantMTSfromCluster=false;
                
                //calculate cluster's self similarity, by taking mean similarity
                float selfSimC = 0f;
                ArrayList<Integer> alC = new ArrayList<Integer>(c);
                //if(!cChanged)
                //    selfSimC=max;
                //else{
                    cChanged=true;
                    for(int i=0; i<alC.size(); i++)
                        for(int j=i+1; j<alC.size(); j++)
                            selfSimC = selfSimC + simMat[alC.get(i)][alC.get(j)];
                    selfSimC = selfSimC/(float)(alC.size()*(alC.size()-1)/2);
                //}
                
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
                if(nearestMTS!=0 && minDistance<threshold){
                    c.add(nearestMTS);
                    addedCloseMTStoCluster=true;
                }
                
                if(addedCloseMTStoCluster){
                    //re-calculate cluster's self similarity, by taking mean similarity
                    selfSimC = 0;
                    alC = new ArrayList<Integer>(c);
                    for(int i=0; i<alC.size(); i++)
                        for(int j=i+1; j<alC.size(); j++)
                            selfSimC = selfSimC + simMat[alC.get(i)][alC.get(j)];
                    selfSimC = selfSimC/(float)(alC.size()*(alC.size()-1)/2);
                }
                
                if(c.size()>2){
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
                    if(farthestMTS1!=0 && maxDistance>=threshold){
                        c.remove(farthestMTS1);
                        c.remove(farthestMTS2);
                        removedDistantMTSfromCluster=true;
                    }
                }
                
                //check whether cluster is converged
                if(!addedCloseMTStoCluster && !removedDistantMTSfromCluster)
                    clusterConverged=true;
            }
            
            //add cluster to clustering result
            clusters.put(clusterId, c);
            clusterId++;
            
            //remove clustered elements from set "elements"
            ArrayList<Integer> tempC = new ArrayList<Integer>(c);
            for(int k=0; k<tempC.size(); k++)
                elements.remove(tempC.get(k));
        }
        
        return clusters;
    }
    
    public static HashMap<Integer,HashSet<Integer>> performCoSmartCAST(float[][] simMat) throws CTMSPException{
        //TODO Input Validation
        
        //TODO update this - for simplicity (while calculating Hubert's Gamma Stats) populate diagonal elements of similarity matrix as 1
        //for(int i=0; i<simMat.length; i++)
        //    for(int j=0; j<simMat.length; j++)
        //        if(i==j) simMat[i][j]=1f;
        
        HashMap<Integer,HashSet<Integer>> hierarClusteringResult=new HashMap<Integer,HashSet<Integer>>();
        HashMap<Integer,HashSet<Integer>> initialClusters = new HashMap<Integer,HashSet<Integer>>();
        for(int k=0; k<simMat.length; k++){
            HashSet<Integer> members = new HashSet<Integer>();
            members.add(k);
            initialClusters.put(k, members);
            
            HashSet<Integer> hMembers = new HashSet<Integer>();
            hMembers.add(k);
            hierarClusteringResult.put(k, hMembers);
        }
        
        float gammaCObest = -1f;
        HashMap<Integer,HashSet<Integer>> bestClusteringResult = initialClusters;
        float epsilon = 0.0001f;
        float[][] lastCSimMat = simMat;
        float[][] lastTransformedCSimMat = simMat;
        
        boolean noBetterGammaCO = false;
        while(!noBetterGammaCO){
            
            float rUpper = 0f;
            float rLower = 1f;
            ArrayList<HashMap<Integer,HashSet<Integer>>> clusteringResult=new ArrayList<HashMap<Integer,HashSet<Integer>>>();
            ArrayList<Float> thresholds=new ArrayList<Float>();
            ArrayList<Float> gammaCOs=new ArrayList<Float>();
            ArrayList<float[][]> cSimMats=new ArrayList<float[][]>();
            ArrayList<float[][]> transformedCSimMats=new ArrayList<float[][]>();
            int bestI = 0;
            
            do{
                float gammaCOmax = Float.MIN_VALUE;
                bestI = 0;
                for(int i=0; i<5; i++){
                    
                    float tempThreshold = ((float)i*(rUpper-rLower)/4f)+rLower; 
                    thresholds.add(tempThreshold);
                    
                    clusteringResult.add(performCAST(bestClusteringResult, lastCSimMat, tempThreshold));
                    
                    //calculate cluster similarity matrix
                    ArrayList<Integer> alC = new ArrayList<Integer>(clusteringResult.get(i).keySet());
                    int noOfClusters = alC.size();
                    float[][] cSimMat = new float[noOfClusters][noOfClusters];
                    for(int r=0; r<noOfClusters; r++)
                        for(int c=0; c<noOfClusters; c++){
                            if(r==c){
                                //TODO update this populate diagonal elements (self similarity) as 1
                                float selfSimC = 0f;
                                ArrayList<Integer> members = new ArrayList<Integer>(clusteringResult.get(i).get(alC.get(r)));
                                if(members.size()==1)
                                    cSimMat[r][c]=simMat[members.get(0)][members.get(0)];
                                else{
                                    for(int x=0; x<members.size(); x++)
                                        for(int y=x+1; y<members.size(); y++)
                                            selfSimC = selfSimC + simMat[members.get(x)][members.get(y)];
                                    selfSimC = selfSimC/(float)(members.size()*(members.size()-1)/2);
                                    cSimMat[r][c]=selfSimC;
                                }
                            }else{
                                //populate non-diagonal elements (member's similarity of cluster x with member's similarity of cluster y)
                                float tempSum=0f;
                                ArrayList<Integer> xMembers = new ArrayList<Integer>(clusteringResult.get(i).get(alC.get(r)));
                                ArrayList<Integer> yMembers = new ArrayList<Integer>(clusteringResult.get(i).get(alC.get(c)));
                                for(int a=0; a<xMembers.size(); a++)
                                    for(int b=0; b<yMembers.size(); b++)
                                        tempSum = tempSum + simMat[xMembers.get(a)][yMembers.get(b)];
                                cSimMat[r][c]=tempSum/(float)(xMembers.size()*yMembers.size());
                            }
                        }
                    cSimMats.add(cSimMat);
                    
                    //transform cluster similarity matrix so that it has similar dimention of original similarity matrix (simMat)
                    float[][] transformedCSimMat = new float[simMat.length][simMat.length];
                    int[] mapMtsToCluster = new int[simMat.length];
                    for(int a=0; a<alC.size(); a++){
                        ArrayList<Integer> members = new ArrayList<Integer>(clusteringResult.get(i).get(alC.get(a)));
                        for(int b=0; b<members.size(); b++)
                            mapMtsToCluster[members.get(b)]=a;
                    }
                    for(int a=0; a<simMat.length; a++)
                        for(int b=0; b<simMat.length; b++){
                            if(noOfClusters>1) //non diagonal elements should remain 0 if it's just 1 cluster
                                transformedCSimMat[a][b]=cSimMat[mapMtsToCluster[a]][mapMtsToCluster[b]];
                        }
                    transformedCSimMats.add(transformedCSimMat);
                    
                    float gammaObj = calculateHubertsGammaStats(transformedCSimMat,simMat);
                    float gammaClu = calculateHubertsGammaStats(transformedCSimMat, lastTransformedCSimMat);
                    
                    float gammaCO = 2*gammaClu*gammaObj/(gammaClu+gammaObj);  
                    gammaCOs.add(gammaCO);
                    if(gammaCO>gammaCOmax){
                        gammaCOmax=gammaCO;
                        bestI = i;
                    }
                }
                if(bestI<4)
                    rLower=thresholds.get(bestI+1);
                else
                    rLower=thresholds.get(bestI);
                if(bestI>0)
                    rUpper=thresholds.get(bestI-1);
                else
                    rUpper=thresholds.get(bestI);
            }while((rUpper-rLower)<epsilon);
            
            //displaying intermediate clustering results, TODO remove this later
            System.out.println("Diaplying intermediate clustering results");
            ArrayList<Integer> hal = new ArrayList<Integer>(hierarClusteringResult.keySet()); 
            for(int i=0; i<hal.size(); i++){
                System.out.println("Cluster "+hal.get(i));
                ArrayList<Integer> members = new ArrayList<Integer>(hierarClusteringResult.get(hal.get(i)));
                for(int j=0; j<members.size(); j++)
                    System.out.println(members.get(j));
            }
            
            if(gammaCOs.get(bestI)>gammaCObest){
                gammaCObest=gammaCOs.get(bestI);
                bestClusteringResult = clusteringResult.get(bestI);
                lastCSimMat = cSimMats.get(bestI);
                lastTransformedCSimMat = transformedCSimMats.get(bestI);
                
                //merge cluster members because we found new level of hierarchy
                HashMap<Integer,HashSet<Integer>> newHierarClusteringResult = new HashMap<Integer,HashSet<Integer>>();
                ArrayList<Integer> alC = new ArrayList<Integer>(bestClusteringResult.keySet());
                for(int z=0; z<alC.size(); z++){
                    HashSet<Integer> newMembers = new HashSet<Integer>();
                    ArrayList<Integer> members = new ArrayList<Integer>(bestClusteringResult.get(alC.get(z)));
                    for(int memIdx=0; memIdx<members.size(); memIdx++){
                        newMembers.addAll(hierarClusteringResult.get(members.get(memIdx)));
                    }
                    newHierarClusteringResult.put(z,newMembers);
                }
                hierarClusteringResult = newHierarClusteringResult;
                
            }else{
                noBetterGammaCO=true;
            }
            
        }
        return hierarClusteringResult;
    }
}
