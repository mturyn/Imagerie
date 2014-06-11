package com.mturyn.imageHistComparer.histogram.comparators;

import com.mturyn.imageHistComparer.IHistogram;

/**
 * A singleton used to be able to pick a particular 'distance' operator for histograms.
 * 
 * Yes, it really should be OrthogonalVectorDistance, but that's too long, and
 * I haven't introduced and metric-involved dot-products yet (though correlations 
 * between near colours might be expressible thereby...)
 *
 * @author mturyn
 *
 */
public class VectorDistance extends AbstractDistance {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";

	static VectorDistance INSTANCE  ; 
	private VectorDistance(){} ;
	
	// Not worried about synchronisation for now...but why not?
	public static VectorDistance getInstance(){
			if(INSTANCE==null){
				INSTANCE = new VectorDistance() ;
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
		return pH0.distance(pH1) ;
	}

}
