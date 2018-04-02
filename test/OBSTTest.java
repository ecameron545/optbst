package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Random;

import impl.NaiveOptimalBSTMapFactory;
import impl.OptimalBSTData;
import impl.OptimalBSTMap;
import impl.OptimalBSTMapFactory;
import optbstutil.OptBSTUtil;

import org.junit.Test;

public class OBSTTest {
	Random rand = new Random(42);
    static OptimalBSTData rawTiny, rawSimple, rawBarrie, rawBaum;
    
    static {
    	 try {
             rawTiny = OptBSTUtil.fromFile("tiny");
         } catch (FileNotFoundException e) {
             System.out.println("File tiny not found");
         }
        try {
            rawSimple = OptBSTUtil.fromFile("simple");
        } catch (FileNotFoundException e) {
            System.out.println("File simple not found");
        }
        try {
            rawBarrie = OptBSTUtil.fromFile("peterpan.prob");
        } catch (FileNotFoundException e) {
            System.out.println("File peterpan.prob not found");
        }
        try {
            rawBaum = OptBSTUtil.fromFile("baum-wwoo.prob.med");
        } catch (FileNotFoundException e) {
            System.out.println("File baum-wwoo.prob.med not found");
        }
    }
    @Test
    public void tinyTreeCost() {
        OptimalBSTMap map = NaiveOptimalBSTMapFactory.buildOptimalBST(rawTiny);
        double expectedSearchCost = OptBSTUtil.expectedSearchCost(map, rawTiny);
        double mine = 2.0;
        System.out.println("Expected cost: Yours: " + expectedSearchCost + " Mine: "+mine);
        System.out.println("Off by " + ((Math.abs(mine-expectedSearchCost)/mine)*100) + "%");
        assertEquals(mine, expectedSearchCost, .001);
    }
    
    @Test
    public void simpleTreeCost() {
        OptimalBSTMap map = NaiveOptimalBSTMapFactory.buildOptimalBST(rawSimple);
        double expectedSearchCost = OptBSTUtil.expectedSearchCost(map, rawSimple);
        double mine = 3.193;
        System.out.println("Expected cost: Yours: " + expectedSearchCost + " Mine: "+mine);
        System.out.println("Off by " + ((Math.abs(mine-expectedSearchCost)/mine)*100) + "%");
        assertEquals(mine, expectedSearchCost, .001);
    }
    
    
    @Test
    public void simpleTreeCorrectness() {
        OptimalBSTMap map = NaiveOptimalBSTMapFactory.buildOptimalBST(rawSimple);
        for (char x = 'a'; x < 'l'; x++) {
            String key = x + "";
            // evens are in the map
            if (x % 2 == 0) {
                assertTrue(map.containsKey(key));
                assertEquals(key, map.get(key));
            }
            // odds aren't
            else {
                assertFalse(map.containsKey(key));
                assertEquals(null, map.get(key));
            }
        }
        
      
    }

    @Test
    public void barriePPtreeCost() {
        OptimalBSTMap map = OptimalBSTMapFactory.buildOptimalBST(rawBarrie);
        double expectedSearchCost = OptBSTUtil.expectedSearchCost(map, rawBarrie);
        double mine = 4.702354342058536;
        System.out.println("Expected cost: Yours: " + expectedSearchCost + " Mine: " + mine );
        System.out.println("Off by " + ((Math.abs(mine-expectedSearchCost)/mine)*100) + "%");
        assertEquals(mine, expectedSearchCost, .001);
    }
    
    @Test
    public void barriePPtreeCorrectPutGet() {
        OptimalBSTMap map = OptimalBSTMapFactory.buildOptimalBST(rawBarrie);

        String[] actual = { "Darling", "Smee", "Wendy", "children", "the" };
        String[] spurious = { "pancake", "popcorn", "walrus", "syzygy" };

        for (String s : actual) {
            assertTrue(map.containsKey(s));
            assertEquals(reverse(s), map.get(s));
        }

        for (String s : spurious) {
            assertFalse(map.containsKey(s));
            assertEquals(null, map.get(s));
        }
    }

    @Test
    public void barriePPtreeCorrectBST() {
        OptimalBSTMap map = OptimalBSTMapFactory.buildOptimalBST(rawBarrie);
        Iterator<String> it = map.iterator();
        String previous = it.next();
        int count = 1;
        while (it.hasNext()) {
            String current = it.next();
            count++;
            assertTrue(previous.compareTo(current) < 0);
            previous = current;
        }
        assertEquals(count, rawBarrie.keys.length);
    }
    
    @Test
    public void baumWWOOtreeCost() {
        OptimalBSTMap map = OptimalBSTMapFactory.buildOptimalBST(rawBaum);
        double expectedSearchCost = OptBSTUtil.expectedSearchCost(map, rawBaum);
        double mine = 4.536161564841866;
        System.out.println("Expected cost: Yours: " + expectedSearchCost + " Mine: " + mine );
        System.out.println("Off by " + ((Math.abs(mine-expectedSearchCost)/mine)*100) + "%");
        assertEquals(mine, expectedSearchCost, .001);
    }
    
    
    @Test
    public void baumWWOOCorrectnessPutGet() {
            OptimalBSTMap map = OptimalBSTMapFactory.buildOptimalBST(rawBaum);
            
            String[] actual = { "Kansas", "great", "with", "prairies"};
            String[] spurious = { "pancake", "popcorn", "walrus", "syzygy" };
            
            for (String s : actual) {
                assertTrue(map.containsKey(s));
                assertEquals(reverse(s), map.get(s));
            }
            
            for (String s : spurious) {
                assertFalse(map.containsKey(s));
                assertEquals(null, map.get(s));
            }
        
      
    }

     

    @Test
    public void baumWWOOtreeCorrectBST() {
        OptimalBSTMap map = OptimalBSTMapFactory.buildOptimalBST(rawBaum);
        Iterator<String> it = map.iterator();
        String previous = it.next();
        int count = 1;
        while (it.hasNext()) {
            String current = it.next();
            count++;
            assertTrue(previous.compareTo(current) < 0);
            previous = current;
        }
        assertEquals(count, rawBaum.keys.length);
    }
    
    @Test
    public void stressTest() {
    	// the maximum size of a test tree 
    	final int MAX_SIZE = 15;
    	// the number of tests run
    	final int TESTS = 20;
    	for (int t = 0; t < TESTS; t++) {
    		
			// pick a size between 1 and MAX_SIZE
			int n = rand.nextInt(MAX_SIZE-1)+1;
			
			//generate the probabilities
			double[] hitProbs = new double[n];
			double[] missProbs = new double[n+1];
			double totalSoFar = 0.0;
			for (int i = 0; i < n; i++) {
				hitProbs[i] = rand.nextDouble()*(1.0 - totalSoFar);
				totalSoFar += hitProbs[i];
				missProbs[i] = rand.nextDouble()*(1.0 - totalSoFar);
				totalSoFar += missProbs[i];
			}
			missProbs[n] = 1 - totalSoFar;
			// shuffle the probabilities
			// from taocp 3.4.2
			for (int i = n-1; i > 0; i--) {
				double u = rand.nextDouble();
				int k = ((int) Math.floor(i*u)) +1;
				double tmp = hitProbs[k];
				hitProbs[k] = hitProbs[i];
				hitProbs[i] = tmp;
			}
			for (int i = n; i > 0; i--) {
				double u = rand.nextDouble();
				int k = ((int) Math.floor(i*u)) +1;
				double tmp = missProbs[k];
				missProbs[k] = missProbs[i];
				missProbs[i] = tmp;
			}
			// generate keys and values
			String[] keys = new String[n];
			String[] values = new String[n];
			for (int i = 0; i < n; i++) {
				keys[i] = "" + (char)('a'+i*2);
				values[i] = "" + (char)('a'+i*2);
			}
			OptimalBSTData bundle = new OptimalBSTData(keys,values,hitProbs,missProbs);
			
			// build the BST
			OptimalBSTMap correct = NaiveOptimalBSTMapFactory.buildOptimalBST(keys,values,hitProbs,missProbs);
			OptimalBSTMap test = OptimalBSTMapFactory.buildOptimalBST(keys,values,hitProbs,missProbs);
			// makes sure the tree is correct
			for (int i = 0; i < n; i++) {
				if (i%2 == 0) {
					String result = test.get(""+(char)('a'+i));
					assert(result != null);
					assert(result.equals(""+(char)('a'+i)));
				} else {
					assert(!test.containsKey(""+(char)('a'+i)));
				}
			}
					
			// make sure the tree is optimal
			assertEquals(OptBSTUtil.expectedSearchCost(correct, bundle), OptBSTUtil.expectedSearchCost(test, bundle),.001);
    	}
    }
    		



    		
    		
    	
    	
    	
    

    private Object reverse(String s) {
        String toReturn = "";
        for (char c : s.toCharArray())
            toReturn = c + toReturn;
        return toReturn;
    }
    


}
