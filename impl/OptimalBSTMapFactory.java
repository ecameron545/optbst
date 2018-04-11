package impl;

import static impl.OptimalBSTMap.dummy;

import java.util.Arrays;

import impl.OptimalBSTMap.Internal;



/**
 * OptimalBSTMapFactory
 * 
 * Build an optimal BST, given the keys, values, key probabilities
 * and miss probabilities.
 * 
 * @author Thomas VanDrunen
 * CSCI 345, Wheaton College
 * Feb 25, 2015
 */

public class OptimalBSTMapFactory {
	
    static int parent = -1;


    /**
     * Exception to throw if the input to building an optimal BST
     * is not right: either the number of keys, values, key probs,
     * and miss probs aren't consistent, or the total probability
     * is not 1.
     */
    public static class BadOptimalBSTInputException extends RuntimeException {
        private static final long serialVersionUID = -444687298513060315L;

        private BadOptimalBSTInputException(String msg) {
            super(msg);
        }
    }
    
    /**
     * Build an optimal BST from given raw data, passed as a single object.
     * A convenient overloading of the other buildOptimalBST().
     * @param rawData The collection of data for building this BST
     * @return A BST with the given keys and values, optimal with the
     * given probabilities.
     */
    public static OptimalBSTMap buildOptimalBST(OptimalBSTData rawData) {
        return buildOptimalBST(rawData.keys, rawData.values, rawData.keyProbs, rawData.missProbs);
    }
    
    /**
     * Build an optimal BST from given raw data, passed as individual arrays.
     * @param rawData The collection of data for building this BST
     * @return A BST with the given keys and values, optimal with the
     * given probabilities.
     */
    public static OptimalBSTMap buildOptimalBST(String[] keys, String[] values, double[] keyProbs,
            double[] missProbs) {
    	

    	
        // keep these checks
        checkLengths(keys, values, keyProbs, missProbs);
        checkProbs(keyProbs, missProbs);        
        
        // The number of keys (so we don't need to say keys.length every time)
        int n = keys.length;
        
        Internal[][] nodes = new Internal[n][n];
        double[][] cost = new double[n][n]; // C[i][j]
        double[][] weight = new double[n][n]; // T[i][j]
       
        
        // initialize bottom diagonal with miss probabilities and leaf nodes
        for(int i = 0; i < n; i++) {
        	weight[i][i] = missProbs[i];
        	cost[i][i] = missProbs[i];
        }
        
        int space = 1;
        weight[0][n-1] = -1; // set the root to -1
        
        
        for(int i = 0; i < n; i++) {
        	for(int j = 0; j < n; j++) {
        		nodes[i][j] = new Internal(dummy, null, null, dummy);
        	}
        }

       
        // repeat the for loop until the root of the entire tree is discovered
        while(weight[0][n-1] == -1) {
        	// loop through each diagonal
        	for(int d = 0; d+space < n; d++) {
        		int s = d + space; // the second value for the matrix

        		// one node
        		if(space == 1) {
            		System.out.print(d + ",");
            		System.out.print(s + " ");
            		System.out.println();

                	nodes[d][s] = new Internal(dummy, keys[d], values[d], dummy);
            		weight[d][s] = weight[d][s-1] + keyProbs[s] + missProbs[s];
            		cost[d][s] = weight[d][s] + cost[d][d] + cost[s][s];
            		continue;
        		}
        		
        		
        		// multiple nodes
        		weight[d][s] = weight[d][s-1] + keyProbs[s] + missProbs[s];
        		double min = minCost(d,s, cost);
        		cost[d][s] = weight[d][s] + min;
        		
        		
        		
        		if(nodes[d][parent-1].key == null)
        			nodes[d][s] = new Internal(dummy, nodes[parent-1][parent].key, nodes[parent-1][parent].value, nodes[parent][s]);
        		else if(nodes[parent][s].key == null)
        			nodes[d][s] = new Internal(nodes[d][parent-1], nodes[parent-1][parent].key, nodes[parent-1][parent].value, dummy);
        		else
        			nodes[d][s] = new Internal(nodes[d][parent-1], nodes[parent-1][parent].key, nodes[parent-1][parent].value, nodes[parent][s]);

        		/*
        		nodes[d][s] = nodes[parent-1][parent];
        		nodes[d][s].left = nodes[d][parent-1];
        		nodes[d][s].right = nodes[parent][s];
        		*/
        	}
        	space++;
        }
        
        /*
        for(int i = 0; i < n; i++) {
        	for(int j = 0; j < n; j++) {
        		System.out.print(nodes[i][j].key + "|");
        	}
        	System.out.println("");
        }
        */
        
       
        for(int i = 0; i < n; i++) {
        	System.out.print(keys[i] + " ");
        }
        System.out.println();
		
        System.out.println("SDKL:FJSDL:F:     " + keys[n-1]);
        
        System.out.println(nodes[0][n-1].toString());
        System.out.println(nodes[0][n-1].key);


		return new OptimalBSTMap(nodes[0][n-1]);

    }
    
    public static double minCost(int d, int s, double[][] cost) {
    	double min = Double.MAX_VALUE;
    	int subRoot = -1;
    	
    	for(int i = d; i < s; i++) {
    		double mini = cost[d][i] + cost[i+1][s];
    		if(mini < min) {
    			min = mini;
    			subRoot = i + 1;
    		}	
    	}
    	
    	parent = subRoot;
    	return min;
    }
    
    

    /**
     * Check that the given probabilities sum to 1, throw an
     * exception if not.
     * @param keyProbs 
     * @param missProbs
     */
    public static void checkProbs(double[] keyProbs, double[] missProbs) {
        double[] allProbs = new double[keyProbs.length + missProbs.length];
        int i = 0;
        for (double keyProb : keyProbs)
            allProbs[i++] = keyProb;
        for (double missProb : missProbs)
            allProbs[i++] = missProb;
        // When summing doubles, sum from smallest to greatest
        // to reduce round-off error.
        Arrays.sort(allProbs);
        double totalProb = 0;
        for (double prob : allProbs)
            totalProb += prob;
        // Don't compare doubles for equality directly. Check that their
        // difference is less than some epsilon.
        if (Math.abs(1.0 - totalProb) > .0001)
            throw new BadOptimalBSTInputException("Probabilities total to " + totalProb);
    }

    /**
     * Check that the arrays have appropriate lengths (keys, values, and
     * keyProbs all the same, missProbs one extra), throw an exception
     * if not.
     * @param keys
     * @param values
     * @param keyProbs
     * @param missProbs
     */
    public static void checkLengths(String[] keys, String[] values,
            double[] keyProbs, double[] missProbs) {
        int n = keys.length;
        if (values.length != n || keyProbs.length != n || missProbs.length != n+1)
            throw new BadOptimalBSTInputException(n + "keys, " + values.length + " values, " +
                    keyProbs.length + " key probs, and " + missProbs.length + " miss probs");
    }
    
}
