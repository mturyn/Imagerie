package com.mturyn.imageHistComparer.histogram.comparators;

import com.mturyn.imageHistComparer.IHistogram;

/**
 * A singleton used to be able to pick a particular 'distance' operator for histograms:
 * @author mturyn
 *
 */
public class Angle extends AbstractDistance {

	static Angle INSTANCE  ; 
	private Angle(){} ;
	
	// Not worried about synchronisation for now...but why not?
	public static Angle getInstance(){
			if(INSTANCE==null){
				INSTANCE = new Angle() ;
			}
		return INSTANCE ;
	}
	
	
	/**
	 * Take two histograms (implementors of IHistorgram) and return a scalar
	 * expressing the notional 'distance' between them in a way charactertic
	 * of instances of this class:
	 */
	@Override
	public double getCharacteristicDistance(IHistogram pH0, IHistogram pH1, double dFractionalFuzziness) {
		return pH0.angle( pH1 ) ;
	}

}
