import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

/**
 * @author Vijay Patil
 *
 */
public class CTMSPUtilTest {

    @Test
    public void testGetLBSAlignmentSimilarityScore(){
        
        //Example from research paper
        ArrayList<String> al0 = new ArrayList<String>();
        
        ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
        s1.add(new MTSNode(new Location(0,0,1), 3, al0));
        ArrayList<String> al12 = new ArrayList<String>();
        al12.add("1");
        s1.add(new MTSNode(new Location(0,0,4), 5, al12));
        s1.add(new MTSNode(new Location(0,0,3), 8, al0));
        s1.add(new MTSNode(new Location(0,0,5), 19, al0));
        ArrayList<String> al15 = new ArrayList<String>();
        al15.add("4");
        al15.add("5");
        s1.add(new MTSNode(new Location(0,0,7), 20, al15));
        MobileTransactionSequence seq1 = new MobileTransactionSequence("1", s1);
        
        ArrayList<MTSNode> s2 = new ArrayList<MTSNode>();
        ArrayList<String> al21 = new ArrayList<String>();
        al21.add("1");
        s2.add(new MTSNode(new Location(0,0,1), 1, al21));
        s2.add(new MTSNode(new Location(0,0,2), 4, al0));
        ArrayList<String> al23 = new ArrayList<String>();
        al23.add("2");
        s2.add(new MTSNode(new Location(0,0,3), 6, al23));
        s2.add(new MTSNode(new Location(0,0,5), 8, al0));
        ArrayList<String> al25 = new ArrayList<String>();
        al25.add("4");
        s2.add(new MTSNode(new Location(0,0,7), 17, al25));
        MobileTransactionSequence seq2 = new MobileTransactionSequence("2", s2);
        
        float score;
        score = CTMSPUtil.getLBSAlignmentSimilarityScore(seq1, seq2);
        System.out.println(score);
        score = CTMSPUtil.getLBSAlignmentSimilarityScore(seq2, seq1);
        System.out.println(score);
        score = CTMSPUtil.getLBSAlignmentSimilarityScore(seq1, seq1);
        System.out.println(score); 
        score = CTMSPUtil.getLBSAlignmentSimilarityScore(seq2, seq2);
        System.out.println(score);
    }
    
    @Test
    public void testGetSimilarityMatrix(){
        
        ArrayList<MobileTransactionSequence> seqs = new ArrayList<MobileTransactionSequence>();
        
        ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
        ArrayList<String> al11 = new ArrayList<String>(); al11.add("a1");s1.add(new MTSNode(new Location(100,10,11), 9, al11));
        ArrayList<String> al12 = new ArrayList<String>(); al12.add("a2");s1.add(new MTSNode(new Location(100,10,11), 10, al12));
        ArrayList<String> al13 = new ArrayList<String>(); al13.add("a1");s1.add(new MTSNode(new Location(100,10,11), 15, al13));
        ArrayList<String> al14 = new ArrayList<String>(); al14.add("a3");s1.add(new MTSNode(new Location(100,10,11), 15, al14));
        ArrayList<String> al15 = new ArrayList<String>(); al15.add("a2");s1.add(new MTSNode(new Location(101,11,12), 13, al15));
        ArrayList<String> al16 = new ArrayList<String>(); al16.add("a3");s1.add(new MTSNode(new Location(101,11,12), 13, al16));
        ArrayList<String> al17 = new ArrayList<String>(); al17.add("a4");s1.add(new MTSNode(new Location(102,10,13), 18, al17));
        ArrayList<String> al18 = new ArrayList<String>(); al18.add("a4");s1.add(new MTSNode(new Location(102,10,13), 19, al18));
        MobileTransactionSequence seq1 = new MobileTransactionSequence("1", s1);
        seqs.add(seq1);
        
        ArrayList<MTSNode> s2 = new ArrayList<MTSNode>();
        ArrayList<String> al21 = new ArrayList<String>(); al21.add("a1");s2.add(new MTSNode(new Location(100,10,11), 9, al21));
        ArrayList<String> al22 = new ArrayList<String>(); al22.add("a2");s2.add(new MTSNode(new Location(100,10,11), 10, al22));
        ArrayList<String> al23 = new ArrayList<String>(); al23.add("a3");s2.add(new MTSNode(new Location(101,11,12), 13, al23));
        MobileTransactionSequence seq2 = new MobileTransactionSequence("2", s2);
        seqs.add(seq2);
        
        ArrayList<MTSNode> s3 = new ArrayList<MTSNode>();
        ArrayList<String> al31 = new ArrayList<String>(); al31.add("a4");s3.add(new MTSNode(new Location(102,12,13), 17, al31));
        ArrayList<String> al32 = new ArrayList<String>(); al32.add("a2");s3.add(new MTSNode(new Location(102,12,13), 18, al32));
        ArrayList<String> al33 = new ArrayList<String>(); al33.add("a3");s3.add(new MTSNode(new Location(102,12,13), 19, al33));
        MobileTransactionSequence seq3 = new MobileTransactionSequence("3", s3);
        seqs.add(seq3);
        
        ArrayList<MTSNode> s4 = new ArrayList<MTSNode>();
        ArrayList<String> al41 = new ArrayList<String>(); al41.add("a5");s4.add(new MTSNode(new Location(103,13,14), 10, al41));
        ArrayList<String> al42 = new ArrayList<String>(); al42.add("a2");s4.add(new MTSNode(new Location(104,14,15), 11, al42));
        ArrayList<String> al43 = new ArrayList<String>(); al43.add("a3");s4.add(new MTSNode(new Location(101,11,12), 14, al43));
        ArrayList<String> al44 = new ArrayList<String>(); al44.add("a5");s4.add(new MTSNode(new Location(103,13,14), 18, al44));
        MobileTransactionSequence seq4 = new MobileTransactionSequence("4", s4);
        seqs.add(seq4);
        
        ArrayList<MTSNode> s5 = new ArrayList<MTSNode>();
        ArrayList<String> al51 = new ArrayList<String>(); al51.add("a1");s5.add(new MTSNode(new Location(103,13,14), 9, al51));
        ArrayList<String> al52 = new ArrayList<String>(); al52.add("a2");s5.add(new MTSNode(new Location(104,14,15), 10, al52));
        ArrayList<String> al53 = new ArrayList<String>(); al53.add("a3");s5.add(new MTSNode(new Location(101,11,12), 14, al53));
        ArrayList<String> al54 = new ArrayList<String>(); al54.add("a4");s5.add(new MTSNode(new Location(102,12,13), 18, al54));
        ArrayList<String> al55 = new ArrayList<String>(); al55.add("a5");s5.add(new MTSNode(new Location(103,13,14), 20, al55));
        MobileTransactionSequence seq5 = new MobileTransactionSequence("5", s5);
        seqs.add(seq5);
        
        float[][] simMat = CTMSPUtil.getSimilarityMatrix(seqs);
        for(int i=0; i<seqs.size(); i++){
            System.out.println(" ");
            for(int j=0; j<seqs.size(); j++)
                System.out.print(simMat[i][j]+" ");
            }
        System.out.println(" ");
    }
    
    @Test
    public void testSameMTS(){
        
        ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
        ArrayList<String> al11 = new ArrayList<String>(); al11.add("a1");s1.add(new MTSNode(new Location(100,10,11), 9, al11));
        ArrayList<String> al12 = new ArrayList<String>(); al12.add("a2");s1.add(new MTSNode(new Location(100,10,11), 9, al12));
        ArrayList<String> al13 = new ArrayList<String>(); al13.add("a3");s1.add(new MTSNode(new Location(101,11,12), 9, al13));
        MobileTransactionSequence seq1 = new MobileTransactionSequence("1", s1);
        
        ArrayList<MTSNode> s2 = new ArrayList<MTSNode>();
        ArrayList<String> al21 = new ArrayList<String>(); al21.add("a1");s2.add(new MTSNode(new Location(100,10,11), 9, al21));
        ArrayList<String> al22 = new ArrayList<String>(); al22.add("a2");s2.add(new MTSNode(new Location(100,10,11), 9, al22));
        ArrayList<String> al23 = new ArrayList<String>(); al23.add("a3");s2.add(new MTSNode(new Location(101,11,12), 9, al23));
        MobileTransactionSequence seq2 = new MobileTransactionSequence("2", s2);
        
        float score = CTMSPUtil.getLBSAlignmentSimilarityScore(seq1, seq2);
        System.out.println(score);
        
        ArrayList<MTSNode> s3 = new ArrayList<MTSNode>();
        ArrayList<String> al31 = new ArrayList<String>(); al31.add("a1"); al31.add("a2"); al31.add("a3");
        s3.add(new MTSNode(new Location(100,10,11), 9, al31));
        MobileTransactionSequence seq3 = new MobileTransactionSequence("1", s3);
        
        ArrayList<MTSNode> s4 = new ArrayList<MTSNode>();
        ArrayList<String> al41 = new ArrayList<String>(); al41.add("a2"); al41.add("a3"); al41.add("a1");
        s4.add(new MTSNode(new Location(100,10,11), 9, al41));
        MobileTransactionSequence seq4 = new MobileTransactionSequence("1", s4);
        
        score = CTMSPUtil.getLBSAlignmentSimilarityScore(seq3, seq4);
        System.out.println(score);
    }
    
    @Test
    public void testPerformCAST() throws CTMSPException{
        int userId=0;
        ArrayList<MobileTransactionSequence> seqs = new ArrayList<MobileTransactionSequence>();
        
        //sequence 1
        ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
        s1.add(new MTSNode(5,1,"S1"));
        s1.add(new MTSNode(6,2,""));
        s1.add(new MTSNode(7,3,"S3"));
        s1.add(new MTSNode(10,4,"S2"));
        s1.add(new MTSNode(11,5,""));
        ArrayList<String> al1 = new ArrayList<String>(); al1.add("S3"); al1.add("S4"); s1.add(new MTSNode(new Location(6),13,al1));
        s1.add(new MTSNode(18,9,""));
        s1.add(new MTSNode(20,11,"S5"));
        s1.add(new MTSNode(21,1,"S1"));
        s1.add(new MTSNode(25,5,""));
        s1.add(new MTSNode(28,6,""));
        s1.add(new MTSNode(30,11,"S2"));
        MobileTransactionSequence seq1 = new MobileTransactionSequence(Integer.toString(userId), s1);
        userId++;
        seqs.add(seq1);
        
        //sequence 2
        ArrayList<MTSNode> s2 = new ArrayList<MTSNode>();
        s2.add(new MTSNode(5,1,"S1"));
        s2.add(new MTSNode(10,2,""));
        s2.add(new MTSNode(19,3,""));
        s2.add(new MTSNode(20,4,"S2"));
        s2.add(new MTSNode(22,2,""));
        s2.add(new MTSNode(27,1,"S1"));
        s2.add(new MTSNode(28,5,""));
        s2.add(new MTSNode(29,6,""));
        s2.add(new MTSNode(30,11,"S2"));
        MobileTransactionSequence seq2 = new MobileTransactionSequence(Integer.toString(userId), s2);
        userId++;
        seqs.add(seq2);
        
        //sequence 3
        ArrayList<MTSNode> s3 = new ArrayList<MTSNode>();
        s3.add(new MTSNode(5,2,"S1"));
        s3.add(new MTSNode(7,1,""));
        s3.add(new MTSNode(9,5,""));
        s3.add(new MTSNode(10,8,"S3"));
        s3.add(new MTSNode(17,4,""));
        s3.add(new MTSNode(19,5,""));
        s3.add(new MTSNode(28,4,"S4"));
        s3.add(new MTSNode(29,2,""));
        s3.add(new MTSNode(30,1,"S3"));
        MobileTransactionSequence seq3 = new MobileTransactionSequence(Integer.toString(userId), s3);
        userId++;
        seqs.add(seq3);
        
        //sequence 4
        ArrayList<MTSNode> s4 = new ArrayList<MTSNode>();
        s4.add(new MTSNode(5,1,"S1"));
        s4.add(new MTSNode(6,2,""));
        s4.add(new MTSNode(7,3,""));
        s4.add(new MTSNode(10,4,"S2"));
        s4.add(new MTSNode(12,5,""));
        ArrayList<String> al2 = new ArrayList<String>(); al2.add("S3"); al2.add("S4"); s4.add(new MTSNode(new Location(6),13,al2));
        s4.add(new MTSNode(17,9,""));
        s4.add(new MTSNode(19,11,"S5"));
        s4.add(new MTSNode(25,1,"S1"));
        s4.add(new MTSNode(27,5,""));
        s4.add(new MTSNode(29,6,""));
        s4.add(new MTSNode(30,11,"S2"));
        MobileTransactionSequence seq4 = new MobileTransactionSequence(Integer.toString(userId), s4);
        userId++;
        seqs.add(seq4);
        
        //sequence 5
        ArrayList<MTSNode> s5 = new ArrayList<MTSNode>();
        s5.add(new MTSNode(1,2,""));
        s5.add(new MTSNode(2,1,""));
        s5.add(new MTSNode(3,5,""));
        s5.add(new MTSNode(6,8,""));
        s5.add(new MTSNode(7,4,""));
        s5.add(new MTSNode(8,2,"S1"));
        s5.add(new MTSNode(15,4,""));
        s5.add(new MTSNode(22,5,""));
        s5.add(new MTSNode(28,4,"S4"));
        MobileTransactionSequence seq5 = new MobileTransactionSequence(Integer.toString(userId), s5);
        userId++;
        seqs.add(seq5);
        
        //sequence 6
        ArrayList<MTSNode> s6 = new ArrayList<MTSNode>();
        s6.add(new MTSNode(5,2,"S1"));
        s6.add(new MTSNode(6,1,""));
        s6.add(new MTSNode(8,5,""));
        s6.add(new MTSNode(10,8,"S3"));
        s6.add(new MTSNode(18,4,""));
        s6.add(new MTSNode(25,7,"S1"));
        s6.add(new MTSNode(28,4,"S4"));
        MobileTransactionSequence seq6 = new MobileTransactionSequence(Integer.toString(userId), s6);
        userId++;
        seqs.add(seq6);
        
        //sequence 7
        ArrayList<MTSNode> s7 = new ArrayList<MTSNode>();
        s7.add(new MTSNode(5,1,"S1"));
        s7.add(new MTSNode(13,4,""));
        s7.add(new MTSNode(23,1,"S1"));
        s7.add(new MTSNode(25,5,""));
        s7.add(new MTSNode(28,6,"S3"));
        s7.add(new MTSNode(30,11,"S2"));
        MobileTransactionSequence seq7 = new MobileTransactionSequence(Integer.toString(userId), s7);
        userId++;
        seqs.add(seq7);
        
        float[][] simMat = CTMSPUtil.getSimilarityMatrix(seqs);
        System.out.println(" ");
        for(int i=0; i<seqs.size(); i++){
            System.out.println(" ");
            for(int j=0; j<seqs.size(); j++)
                System.out.print(simMat[i][j]+" ");
            }
        System.out.println(" ");
        
        HashMap<Integer,HashSet<Integer>> initialClusters = new HashMap<Integer,HashSet<Integer>>();
        for(int k=0; k<simMat.length; k++){
            HashSet<Integer> members = new HashSet<Integer>();
            members.add(k);
            initialClusters.put(k, members);
        }
        HashMap<Integer,HashSet<Integer>> result = CTMSPUtil.performCAST(initialClusters, simMat, 0.2f);
        ArrayList<Integer> ral = new ArrayList<Integer>(result.keySet()); 
        for(int i=0; i<ral.size(); i++){
            System.out.println("Cluster "+ral.get(i));
            ArrayList<Integer> members = new ArrayList<Integer>(result.get(ral.get(i)));
            for(int j=0; j<members.size(); j++)
                System.out.println(members.get(j));
        }
        
        System.out.println("Now testing co-Smart-CAST");
        result = CTMSPUtil.performCoSmartCAST(simMat);
        System.out.println("Final Clustering result");
        ral = new ArrayList<Integer>(result.keySet()); 
        for(int i=0; i<ral.size(); i++){
            System.out.println("Cluster "+ral.get(i));
            ArrayList<Integer> members = new ArrayList<Integer>(result.get(ral.get(i)));
            for(int j=0; j<members.size(); j++)
                System.out.println(members.get(j));
        }
    }
    
    @Test
    public void testPerformCoSmartCAST() throws IOException, CTMSPException{
        File f1=new File("user_sample.txt");
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(f1)));
        int locCounter =1;
        ArrayList<MobileTransactionSequence> seqs = new ArrayList<MobileTransactionSequence>(); 
        HashMap<String,Integer> locations = new HashMap<String,Integer>();
        boolean newUser=false;
        String user=null;
        ArrayList<MTSNode> s1 = new ArrayList<MTSNode>(); 
        int records=0;
        for (String line; (line = reader.readLine()) != null;) {
            line=line.trim().substring(2, line.length()-2);
            String[] tokens = line.split("\",\"");
            
            String[] tToken = tokens[5].trim().split("\":\"");
            tToken[1]=tToken[1].trim();
            long epoch = Long.parseLong(tToken[1]);
            String dateStr = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (epoch*1000));
            Date date = new java.util.Date (epoch*1000);
            
            Integer loc =null;
            String[] latToken = tokens[3].trim().split("\":\"");
            String lat = latToken[1].trim();
            String[] lonToken = tokens[4].trim().split("\":\"");
            String lon = lonToken[1].trim();
            if((Math.abs(Float.parseFloat(lat))>90) || (Math.abs(Float.parseFloat(lon))>180))
                System.out.println("Invalid lat lon: " + line);
            else{
                StringBuffer key = new StringBuffer();
                key.append(lat).append("X").append(lon);
                loc = locations.get(key.toString());
                if(loc==null){
                    loc=locCounter;
                    locations.put(key.toString(), locCounter);
                    locCounter++;
                }
            }
            
            String[] appToken = tokens[1].trim().split("\":\"");
            String appId = appToken[1].trim();
            
            String[] userToken = tokens[0].trim().split("\":\"");
            String tempUser = userToken[1].trim();
            //System.out.println(user+" "+tempUser);
            if(user!=null && !user.equals(tempUser))
                newUser=true;
            
            user=tempUser;
            
            //System.out.println(newUser);
            
            MTSNode node = new MTSNode(date.getHours()+1,loc,appId);
            s1.add(node);
            if(newUser){
                newUser=false;
                MobileTransactionSequence seq = new MobileTransactionSequence(user,s1); 
                seqs.add(seq);
                //System.out.println(s1.size());
                s1=new ArrayList<MTSNode>();
            }
            
            //System.out.println(tokens.length + " " + tToken[1] + " " + date + " " + line);
            records++;
        }
        
        MobileTransactionSequence seq = new MobileTransactionSequence(user,s1); 
        seqs.add(seq);
        //System.out.println(s1.size());
        
        //System.out.println(records + " " + locations.size());
        //System.out.println(locations);
        reader.close();
        
        //System.out.println(seqs.size());
        
        float[][] simMat = CTMSPUtil.getSimilarityMatrix(seqs);
        System.out.println(" ");
        for(int i=0; i<seqs.size(); i++){
            System.out.println(" ");
            for(int j=0; j<seqs.size(); j++)
                System.out.print(simMat[i][j]+" ");
            }
        System.out.println(" ");
        
        
        HashMap<Integer,HashSet<Integer>> initialClusters = new HashMap<Integer,HashSet<Integer>>();
        for(int k=0; k<simMat.length; k++){
            HashSet<Integer> members = new HashSet<Integer>();
            members.add(k);
            initialClusters.put(k, members);
        }
        HashMap<Integer,HashSet<Integer>> result = CTMSPUtil.performCAST(initialClusters, simMat, 0.15f);
        ArrayList<Integer> ral = new ArrayList<Integer>(result.keySet()); 
        for(int i=0; i<ral.size(); i++){
            System.out.println("Cluster "+ral.get(i));
            ArrayList<Integer> members = new ArrayList<Integer>(result.get(ral.get(i)));
            for(int j=0; j<members.size(); j++)
                System.out.println(members.get(j));
        }
        
        
        System.out.println("Testing co-Smart-CAST");
        //HashMap<Integer,HashSet<Integer>> 
        result = CTMSPUtil.performCoSmartCAST(simMat);
        System.out.println("Final Clustering result");
        //ArrayList<Integer> 
        ral = new ArrayList<Integer>(result.keySet()); 
        for(int i=0; i<ral.size(); i++){
            System.out.println("Cluster "+ral.get(i));
            ArrayList<Integer> members = new ArrayList<Integer>(result.get(ral.get(i)));
            for(int j=0; j<members.size(); j++)
                System.out.println(seqs.get(members.get(j)).userid);
        }
        
    }
    
    @Test
    public void testClusteringWithSimilarMTS() throws CTMSPException{
        int userId=0;
        ArrayList<MobileTransactionSequence> seqs = new ArrayList<MobileTransactionSequence>();
        
        //sequence 1
        for(int i=0; i<5; i++){
            ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
            s1.add(new MTSNode(1,1,"S1"));s1.add(new MTSNode(2,2,"S2"));s1.add(new MTSNode(3,3,"S3"));s1.add(new MTSNode(4,4,"S4"));s1.add(new MTSNode(5,5,"S5"));
            MobileTransactionSequence seq1 = new MobileTransactionSequence(Integer.toString(userId), s1); userId++; seqs.add(seq1);
        }
        
        for(int i=0; i<5; i++){
            ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
            s1.add(new MTSNode(10+i,1,"S11"));s1.add(new MTSNode(11+i,2,"S12"));s1.add(new MTSNode(12+i,3,"S13"));s1.add(new MTSNode(13+i,4,"S14"));s1.add(new MTSNode(14+i,5,"S15"));
            MobileTransactionSequence seq1 = new MobileTransactionSequence(Integer.toString(userId), s1); userId++; seqs.add(seq1);
        }
        
        for(int i=0; i<5; i++){
            ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
            s1.add(new MTSNode(20+i,1,"S21"));s1.add(new MTSNode(20+i,2,"S22"));s1.add(new MTSNode(20+i,3,"S23"));s1.add(new MTSNode(20+i,4,"S24"));s1.add(new MTSNode(20+i,5,"S25"));
            MobileTransactionSequence seq1 = new MobileTransactionSequence(Integer.toString(userId), s1); userId++; seqs.add(seq1);
        }
        
        for(int i=0; i<5; i++){
            ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
            s1.add(new MTSNode(1,11,"S31"));s1.add(new MTSNode(2,12,"S32"));s1.add(new MTSNode(3,13,"S33"));s1.add(new MTSNode(4,14,"S34"));s1.add(new MTSNode(5,15,"S35"));
            MobileTransactionSequence seq1 = new MobileTransactionSequence(Integer.toString(userId), s1); userId++; seqs.add(seq1);
        }
        
        for(int i=0; i<5; i++){
            ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
            s1.add(new MTSNode(10+i,21,"S41"));s1.add(new MTSNode(11+i,22,"S42"));s1.add(new MTSNode(12+i,23,"S43"));s1.add(new MTSNode(13+i,24,"S44"));s1.add(new MTSNode(14+i,25,"S45"));
            MobileTransactionSequence seq1 = new MobileTransactionSequence(Integer.toString(userId), s1); userId++; seqs.add(seq1);
        }
        
        for(int i=0; i<5; i++){
            ArrayList<MTSNode> s1 = new ArrayList<MTSNode>();
            s1.add(new MTSNode(24,31,"S51"));s1.add(new MTSNode(24,32,"S52"));s1.add(new MTSNode(24,33,"S53"));s1.add(new MTSNode(24,34,"S54"));s1.add(new MTSNode(24,35,"S55"));
            MobileTransactionSequence seq1 = new MobileTransactionSequence(Integer.toString(userId), s1); userId++; seqs.add(seq1);
        }
        
        System.out.println("Testing co-Smart-CAST");
        HashMap<Integer,HashSet<Integer>> result = CTMSPUtil.performCoSmartCAST(CTMSPUtil.getSimilarityMatrix(seqs));
        System.out.println("Final Clustering result");
        ArrayList<Integer> ral = new ArrayList<Integer>(result.keySet()); 
        for(int i=0; i<ral.size(); i++){
            System.out.println("Cluster "+ral.get(i));
            ArrayList<Integer> members = new ArrayList<Integer>(result.get(ral.get(i)));
            for(int j=0; j<members.size(); j++)
                System.out.println(members.get(j));
        }
        
    }
}
