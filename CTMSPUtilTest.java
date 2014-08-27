import java.util.ArrayList;

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
        MobileTransactionSequence seq1 = new MobileTransactionSequence(1, s1);
        
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
        MobileTransactionSequence seq2 = new MobileTransactionSequence(2, s2);
        
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
        MobileTransactionSequence seq1 = new MobileTransactionSequence(1, s1);
        seqs.add(seq1);
        
        ArrayList<MTSNode> s2 = new ArrayList<MTSNode>();
        ArrayList<String> al21 = new ArrayList<String>(); al21.add("a1");s2.add(new MTSNode(new Location(100,10,11), 9, al21));
        ArrayList<String> al22 = new ArrayList<String>(); al22.add("a2");s2.add(new MTSNode(new Location(100,10,11), 10, al22));
        ArrayList<String> al23 = new ArrayList<String>(); al23.add("a3");s2.add(new MTSNode(new Location(101,11,12), 13, al23));
        MobileTransactionSequence seq2 = new MobileTransactionSequence(2, s2);
        seqs.add(seq2);
        
        ArrayList<MTSNode> s3 = new ArrayList<MTSNode>();
        ArrayList<String> al31 = new ArrayList<String>(); al31.add("a4");s3.add(new MTSNode(new Location(102,12,13), 17, al31));
        ArrayList<String> al32 = new ArrayList<String>(); al32.add("a2");s3.add(new MTSNode(new Location(102,12,13), 18, al32));
        ArrayList<String> al33 = new ArrayList<String>(); al33.add("a3");s3.add(new MTSNode(new Location(102,12,13), 19, al33));
        MobileTransactionSequence seq3 = new MobileTransactionSequence(3, s3);
        seqs.add(seq3);
        
        ArrayList<MTSNode> s4 = new ArrayList<MTSNode>();
        ArrayList<String> al41 = new ArrayList<String>(); al41.add("a5");s4.add(new MTSNode(new Location(103,13,14), 10, al41));
        ArrayList<String> al42 = new ArrayList<String>(); al42.add("a2");s4.add(new MTSNode(new Location(104,14,15), 11, al42));
        ArrayList<String> al43 = new ArrayList<String>(); al43.add("a3");s4.add(new MTSNode(new Location(101,11,12), 14, al43));
        ArrayList<String> al44 = new ArrayList<String>(); al44.add("a5");s4.add(new MTSNode(new Location(103,13,14), 18, al44));
        MobileTransactionSequence seq4 = new MobileTransactionSequence(4, s4);
        seqs.add(seq4);
        
        ArrayList<MTSNode> s5 = new ArrayList<MTSNode>();
        ArrayList<String> al51 = new ArrayList<String>(); al51.add("a1");s5.add(new MTSNode(new Location(103,13,14), 9, al51));
        ArrayList<String> al52 = new ArrayList<String>(); al52.add("a2");s5.add(new MTSNode(new Location(104,14,15), 10, al52));
        ArrayList<String> al53 = new ArrayList<String>(); al53.add("a3");s5.add(new MTSNode(new Location(101,11,12), 14, al53));
        ArrayList<String> al54 = new ArrayList<String>(); al54.add("a4");s5.add(new MTSNode(new Location(102,12,13), 18, al54));
        ArrayList<String> al55 = new ArrayList<String>(); al55.add("a5");s5.add(new MTSNode(new Location(103,13,14), 20, al55));
        MobileTransactionSequence seq5 = new MobileTransactionSequence(5, s5);
        seqs.add(seq5);
        
        float[][] simMat = CTMSPUtil.getSimilarityMatrix(seqs);
        for(int i=0; i<seqs.size(); i++){
            System.out.println(" ");
            for(int j=0; j<seqs.size(); j++)
                System.out.print(simMat[i][j]+" ");
            }
        System.out.println(" ");
    }
}
