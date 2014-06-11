package com.mturyn.imageHistComparer;

import java.awt.image.BufferedImage;

import com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;
import com.mturyn.imageHistComparer.histogram.ImageCharacteriser;

public interface  IAnalysedImage<T extends IHistogram> {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";

	public abstract ImageCharacteriser<T> getCharacteriser();

	public abstract void setCharacteriser(ImageCharacteriser<T> characteriser);

	public abstract BufferedImage getImg();

	public abstract void setImg(BufferedImage img);

	public abstract String getFilename();

	public abstract void setFilename(String fileName);

	public abstract IndexedValue[] findIndicesOfMostAndLeastSimilarAtScale(
			AnalysedImage<T>[] pTopmosts, HistogramScale pScale,
			HistogramType pType);

	public IndexedValue[] 
			findIndicesOfMostAndLeastSimilarAtScaleOfType(
										IAnalysedImage<T>[] pAnalysedImages,
										HistogramDistanceMethod pMethod,
										double dFractionalFuzziness,
										HistogramScale pScale,
										HistogramType pType
	) ;
	
}