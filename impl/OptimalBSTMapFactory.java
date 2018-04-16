package impl;

import static impl.OptimalBSTMap.dummy;

import java.util.Arrays;

import impl.OptimalBSTMap.Internal;

/**
 * OptimalBSTMapFactory
 * 
 * Build an optimal BST, given the keys, values, key probabilities and miss
 * probabilities.
 * 
 * @author Thomas VanDrunen CSCI 345, Wheaton College Feb 25, 2015
 * @author Evan Cameron, 88929
 */

public class OptimalBSTMapFactory {

	/**
	 * Exception to throw if the input to building an optimal BST is not right:
	 * either the number of keys, values, key probs, and miss probs aren't
	 * consistent, or the total probability is not 1.
	 */
	public static class BadOptimalBSTInputException extends RuntimeException {
		private static final long serialVersionUID = -444687298513060315L;

		private BadOptimalBSTInputException(String msg) {
			super(msg);
		}
	}

	/**
	 * Build an optimal BST from given raw data, passed as a single object. A
	 * convenient overloading of the other buildOptimalBST().
	 * 
	 * @param rawData
	 *            The collection of data for building this BST
	 * @return A BST with the given keys and values, optimal with the given
	 *         probabilities.
	 */
	public static OptimalBSTMap buildOptimalBST(OptimalBSTData rawData) {
		return buildOptimalBST(rawData.keys, rawData.values, rawData.keyProbs, rawData.missProbs);
	}

	/**
	 * Build an optimal BST from given raw data, passed as individual arrays.
	 * 
	 * @param rawData
	 *            The collection of data for building this BST
	 * @return A BST with the given keys and values, optimal with the given
	 *         probabilities.
	 */
	public static OptimalBSTMap buildOptimalBST(String[] keys, String[] values, double[] keyProbs, double[] missProbs) {

		// keep these checks
		checkLengths(keys, values, keyProbs, missProbs);
		checkProbs(keyProbs, missProbs);

		// The number of keys (so we don't need to say keys.length every time)
		int n = keys.length;

		Internal[][] nodes = new Internal[n + 1][n + 1]; // optimal subtrees
		double[][] cost = new double[n + 1][n + 1]; // cost of each subtree
		double[][] weight = new double[n + 1][n + 1]; // weight of each subtree

		// initialize bottom diagonal with miss probabilities and leaf nodes
		for (int i = 0; i <= n; i++) {
			weight[i][i] = missProbs[i];
			cost[i][i] = missProbs[i];
		}

		int level = 1; // the diagonal level that the algorithm is currently on
		weight[0][n] = -1; // set the root to -1


	
		// repeat until the last spot is contains and optimal root
		while (weight[0][n] == -1) {

			// loop through each diagonal on the current level
			for (int d = 0; d + level <= n; d++) {
				int s = d + level; // the second value for the matrix

				// subtree contain one node
				if (level == 1) {
					nodes[d][s] = new Internal(dummy, keys[s - 1], values[s - 1], dummy);
					weight[d][s] = weight[d][s - 1] + keyProbs[s - 1] + missProbs[s];
					cost[d][s] = weight[d][s] + cost[d][d] + cost[s][s];
					continue;
				}
				
				

				// subtrees contain multiple nodes
				weight[d][s] = weight[d][s - 1] + keyProbs[s - 1] + missProbs[s];
				int subRoot = minCost(d, s, cost);
				cost[d][s] = weight[d][s] + cost[d][subRoot - 1] + cost[subRoot][s];

				
				
				// left child of the current node needs to be null
				if (nodes[d][subRoot - 1] == null)
					nodes[d][s] = new Internal(dummy, nodes[subRoot - 1][subRoot].key,
							nodes[subRoot - 1][subRoot].value, nodes[subRoot][s]);

				// right child of the current node needs to be null
				else if (nodes[subRoot][s] == null)
					nodes[d][s] = new Internal(nodes[d][subRoot - 1], nodes[subRoot - 1][subRoot].key,
							nodes[subRoot - 1][subRoot].value, dummy);

				// neither left or right child of the current node are null
				else
					nodes[d][s] = new Internal(nodes[d][subRoot - 1], nodes[subRoot - 1][subRoot].key,
							nodes[subRoot - 1][subRoot].value, nodes[subRoot][s]);

			}
			level++; // increase the diagonal level
		}

		// return the root of the optimal bst
		return new OptimalBSTMap(nodes[0][n]);

	}

	/*
	 * Helper method to find the min cost of a subtree
	 * 
	 * @param left and right values of where the subtree is in the cost array
	 * 
	 * @return the root of the subtree
	 */

	private static int minCost(int d, int s, double[][] cost) {
		double oldMin = Double.MAX_VALUE;
		int subRoot = -1;

		// loop through possible subtree combinations and compare costs
		for (int i = d; i < s; i++) {

			// cost[d][i] is the left child and cost[i+1][s] is the right child
			double newMin = cost[d][i] + cost[i + 1][s];

			// if the new cost is better than the old cost
			if (newMin < oldMin) {
				oldMin = newMin;
				subRoot = i + 1;
			}
		}
		// return the root of the subtree
		return subRoot;
	}

	/**
	 * Check that the given probabilities sum to 1, throw an exception if not.
	 * 
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
	 * Check that the arrays have appropriate lengths (keys, values, and keyProbs
	 * all the same, missProbs one extra), throw an exception if not.
	 * 
	 * @param keys
	 * @param values
	 * @param keyProbs
	 * @param missProbs
	 */
	public static void checkLengths(String[] keys, String[] values, double[] keyProbs, double[] missProbs) {
		int n = keys.length;
		if (values.length != n || keyProbs.length != n || missProbs.length != n + 1)
			throw new BadOptimalBSTInputException(n + "keys, " + values.length + " values, " + keyProbs.length
					+ " key probs, and " + missProbs.length + " miss probs");
	}

}
