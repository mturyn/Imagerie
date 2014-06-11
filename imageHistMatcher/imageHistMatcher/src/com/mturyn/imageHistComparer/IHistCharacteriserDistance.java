package com.mturyn.imageHistComparer;


import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;


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
public interface  IHistCharacteriserDistance {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";
	
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
