package com.mturyn.imageHistComparer.histogram.comparators;

import com.mturyn.imageHistComparer.IHistogram;

/**
 * A singleton used to be able to pick a particular 'distance' operator for histograms:
 * @author mturyn
 *
 */
public class MatchesCountDistance extends AbstractDistance {

	static MatchesCountDistance INSTANCE  ; 
	private MatchesCountDistance(){} ;
	
	// Not worried about synchronisation for now...but why not?
	public static MatchesCountDistance getInstance(){
			if(INSTANCE==null){
				INSTANCE = new MatchesCountDistance() ;
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
		return pH0.percentMatches(pH1,dFractionalFuzziness) ;
	}

}
