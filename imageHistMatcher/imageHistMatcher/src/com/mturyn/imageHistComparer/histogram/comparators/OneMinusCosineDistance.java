package com.mturyn.imageHistComparer.histogram.comparators;

import com.mturyn.imageHistComparer.IHistogram;

/**
 * A singleton used to be able to pick a particular 'distance' operator for histograms:
 * @author mturyn
 *
 */
public class OneMinusCosineDistance extends AbstractDistance {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";

	static OneMinusCosineDistance INSTANCE  ; 
	private OneMinusCosineDistance(){} ;
	
	// Not worried about synchronisation for now....
	public static OneMinusCosineDistance getInstance(){
			if(INSTANCE==null){
				INSTANCE = new OneMinusCosineDistance() ;
			}
		return INSTANCE ;
	}
	
	
	/**
	 * Take two histograms (implementors of IHistorgram) and return a scalar
	 * expressing the notional 'distance' between them in a way charactertic
	 * of instances of this class:
	 */
	@Override
	public double getCharacteristicDistance(IHistogram pH0, IHistogram pH1,double dFractionalFuzziness) {
		// Within (100*dFractionalDifference)% of each other:
		return ( 1.0d - pH0.cosine(pH1)) ;
	}

}
