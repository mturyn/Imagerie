package com.mturyn.imageHistComparer;


import java.util.Map ;
import java.util.HashMap ;
import java.util.Collections ;

import com.mturyn.imageHistComparer.Utilities.HistogramScale ;
import com.mturyn.imageHistComparer.Utilities.HistogramType ;
import com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod ;


import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.MATCHES ;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.ANGLE  ;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.VECTOR_DISTANCE ;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.ONE_MINUS_COSINE ;


/*
 * Class that exists to hold different methods of 
 * comparing histograms; make this a generic to build-in  
 * requirement that hists be of the same class, particularly 
 * during testing or training-up to see which method or combinations
 * work best. One method might work best at a coarse scale to winnow
 * the large initial set quickly, then another at a finer scale 
 * to more accurately reflect differences...or, perhaps weighted combinations
 * of methods with weights, or even better, weighting functions, determined
 * by having humans decide which images look most and least alike and 
 * training a system to match that as well as possible. (Mechanical Turk?)
 */
public interface IHistCharacteriserDistance {
	
	/**
	 * 
	 * @param p0 an IImageCharacteriser implementor
	 * @param p1 an IImageCharacteriser implementor
	 * @param pScale The scale of the histograms to be compared (COARSE, FINE) so far
	 * @param pType  The type of the histograms to be compared (raw count, frequency, entropy,...)
	 * @return scalar value denoting the 'distance' between the two histograms 
	 */
	public abstract double compare(IImageCharacteriser p0,IImageCharacteriser p1, HistogramScale pScale, HistogramType pType, double dFractionalFuzziness) ;

}
