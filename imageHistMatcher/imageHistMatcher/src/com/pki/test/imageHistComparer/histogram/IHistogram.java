package com.pki.test.imageHistComparer.histogram;

import java.util.TreeSet;

public interface IHistogram {

	/**
	 * Add an RGB pixel to whatever internal data representation is
	 * used---in memory, in a db (probably inadvisable), knotted into
	 * strings (doubly so):
	 * @param pRed [0,255] pixel red value
	 * @param pGreen [0,255] pixel green value
	 * @param pBlue[0,255] pixel blue value
	 */
	public abstract void addPixel(int pRed, int pGreen, int pBlue);

	public abstract double getValue(int pBinRed, int pBinGreen, int pBinBlue);

	public abstract IHistogram setValue(int pBinRed, int pBinGreen,
			int pBinBlue, double pVal);

	public abstract double distance(IHistogram pOtherHist);

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

	public abstract TreeSet<RGBPixelsBin> orderedBinsDescending();

	public abstract String getStringThresholded(double pThreshold);

	public abstract String toString();

	// Order the histogram entries by decreasing frequency,
	// generate a string representing them up to min(given length,total number of bins)
	public abstract String toCharacterString();

}