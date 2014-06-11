package com.mturyn.imageHistComparer;

import java.awt.image.BufferedImage;

import com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;
//import com.mturyn.imageHistComparer.histogram.RGBImageCharacteriser;

public interface  IImageCharacteriser {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";

	// Generalising to an int[] would be better, but not at all
	// necessary...and at that point, maybe we wouldn't want ints....
	public abstract void addPixel(int[] pChannels);
	
	// Separating-out initialisation for better subclassing and 
	// more useful generics interaction (can invoke argless constructor,
	// then call this:
	public void populateFromBufferedImage(BufferedImage pImage) ;	

	public abstract void calculateAndStoreNormalised();

	public abstract void calculateAndStoreFrequencies();

	public abstract void calculateAndStoreEntropies();

	
	/**
	 * 
	 * @param pCharacterisers an array of IImageCharacteriser instances from which to
	 * calculate one of several sorts of difference metric
	 * @param pScale COARSE, FINE
	 * @param pType  FREQUENCIES,... see com.mturyn.imageHistComparer.Utilities.HistogramType ;
	 * @return
	 */
	public abstract IndexedValue[] findIndicesOfMostAndLeastSimilarAtScale(
			IImageCharacteriser[] pCharacterisers, HistogramScale pScale,
			HistogramType pType);
	
	/**
	 * 
	 * @param pCharacterisers an array of IImageCharacteriser instances from which to
	 * calculate one of several sorts of difference metric
	 * @param pScale COARSE, FINE
	 * @return
	 */
	public IndexedValue[] 
			findIndicesOfMostAndLeastSimilarAtScaleOfType(
										IImageCharacteriser[] pCharacterisers,
										HistogramDistanceMethod pMethod,
										double dFractionalFuzziness,
										HistogramScale pScale,
										HistogramType pType
	) ;

	public abstract String getStringAtScaleOfType(HistogramScale pScale,
			HistogramType pType);

	public abstract String getRawString(HistogramScale pScale);

	public abstract String getNormalisedString(HistogramScale pScale);

	public abstract String getFrequenciesString(HistogramScale pScale);

	public abstract String getEntropiesString(HistogramScale pScale);
	
	public abstract IHistogram getHistAtScaleOfType(HistogramScale pScale, HistogramType pType) ;	

	public abstract double dotProductAtScaleOfType(
			IImageCharacteriser pOther, HistogramScale pScale,
			HistogramType pType);

	public abstract double distanceAtScaleOfType(IImageCharacteriser pOther,
			HistogramScale pScale, HistogramType pType); 
	
	public int getFrequencyMatchPercentageAtScale(IImageCharacteriser pOther,HistogramScale pScale, double dWindowHalfHeight) ;
	

	public abstract double lengthAtScaleOfType(HistogramScale pScale,
			HistogramType pType);

	public abstract double cosineAtScaleOfType(IImageCharacteriser pOther,
			HistogramScale pScale, HistogramType pType);

	public abstract double angleAtScaleOfType(IImageCharacteriser pOther,
			HistogramScale pScale, HistogramType pType);

	public abstract int angleDegreesAtScaleOfType(IImageCharacteriser pOther,
			HistogramScale pScale, HistogramType pType);

}