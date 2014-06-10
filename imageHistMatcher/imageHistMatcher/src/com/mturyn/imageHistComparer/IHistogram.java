package com.mturyn.imageHistComparer;

import java.util.TreeSet;

import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.histogram.ColorspaceBlock;

// TODO: abstract the pixel class:

public interface IHistogram {

	/**
	 * Add an RGB pixel to whatever internal data representation is
	 * used---in memory, in a db (probably inadvisable), knotted into
	 * strings (doubly so):
	 * @param pRed [0,255] pixel red value
	 * @param pGreen [0,255] pixel green value
	 * @param pBlue[0,255] pixel blue value
	 */
	// Generalising to an int[] would be better, but not at all
	// necessary...and at that point, maybe we wouldn't want int[]s....
	//public abstract void addPixel(int pChannel0, int pChannel1, int pChannel2);
	public abstract void addPixel(int[] pChannels);

	public abstract double getValue(int pBinRed, int pBinGreen, int pBinBlue);
	
	public abstract int[] getMeshes() ;
	public abstract int[] getNBins() ;	

	public abstract IHistogram setValue(int pBinRed, int pBinGreen,
			int pBinBlue, double pVal);

	public abstract double distance(IHistogram pOtherHist);
	
	public double euclideanDistance(IHistogram pOtherHist) ;	
	
	public int percentMatches(IHistogram pOtherHist, double dWindowHalfHeight) ;

	/**
	 *  We're storing in a matrix, but we're treating it as a vector
	 * @param pOtherHist
	 * @return
	 */
	public abstract double dotProduct(IHistogram pOtherHist);

	public abstract double length();

	public abstract double cosine(IHistogram pOtherHist);

	public abstract double angle(IHistogram pOtherHist);

	public abstract int angleDegrees(IHistogram pOtherHist);

	public abstract IHistogram scaledCopy(double pScale);

	/**
	 * Turn this into a unit vector, put on stack for someone else to use.
	 * (I lost too much time working with parent-child schemesfor different
	 * histograms, and decided to have another object hold and keep track of them
	 * instead.)
	 * @return
	 */
	public abstract IHistogram getCopyNormalised();

	public abstract IHistogram getCopyFrequencies();

	public abstract IHistogram getCopyEntropies();
	
	public abstract IHistogram createHistAtScale(HistogramScale pScale) ;
	
	public abstract void initialise(int[] pNBins, int[] pMeshes) ;

	public abstract TreeSet<ColorspaceBlock> orderedBinsDescending();

	public abstract String getStringThresholded(double pThreshold);

	public abstract String toString();

	// Order the histogram entries by decreasing frequency,
	// generate a string representing them up to min(given length,total number of bins)
	public abstract String toCharacterString();

}