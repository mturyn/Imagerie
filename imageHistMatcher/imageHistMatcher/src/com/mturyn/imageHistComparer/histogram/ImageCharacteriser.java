package com.mturyn.imageHistComparer.histogram;

import static com.mturyn.imageHistComparer.Utilities.RAD_TO_INTEGRAL_DEGREES;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.FINE;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.ENTROPIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.FREQUENCIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.NORMALISED;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.RAW;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Arrays;
import java.util.HashMap;

import com.mturyn.imageHistComparer.IHistCharacteriserDistance;
import com.mturyn.imageHistComparer.IHistogram;
import com.mturyn.imageHistComparer.IImageCharacteriser;
import com.mturyn.imageHistComparer.IndexedValue;
import com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;
import com.mturyn.imageHistComparer.histogram.comparators.AbstractDistance;
// comparison modes

/**
 * @author mturyn 
 * A first pass at deriving a readily searchable <em>and</em>
 * comparable signature for an image...which could just be a raw
 * histogram with a given granularity, but likely won't be in the end.
 * (My guess: a spectrogram-like peak TODO: Turn this into an interface
 * so that plugin characterisers can be used from the top-level, as we
 * should be able to try anything that fulfills the same contract. TODO:
 * Abstract some of this into a superclass that does the basics...at
 * that point we will need an ImageSignature interface so that different
 * types of signatures can be compared (though only to signatures of the
 * same type---I'm not talking about comparing a wavelet-based signature
 * instance to a stringified hist-based one...).
 * 
 * In this case, I'm storing a coarse histogram (2 bins/channel) and a finer
 * (4 bins/channel) and considering each bin as giving the length of a projection
 * of the histogram in a vector space of dimension nBins^3, with similarity being 
 * determined by the cosine of the two vectors in that space.  This ignores 
 * correlations between bins, and is inferior to a cluster analysis, but I want to 
 * see if it's adequate...and (I believe) the correlations could be folded-in later via
 * a 
 * metric used to calculate the dot-product.
 */
public class ImageCharacteriser<T extends IHistogram> implements IImageCharacteriser {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";

	
	// Index histogram views by pre-defined scales and data-types:
	
	public static HistogramScale[] OUR_SCALES = {COARSE,FINE}  ;
	public static HistogramType[] OUR_TYPES = {RAW,NORMALISED,FREQUENCIES,ENTROPIES}  ;
	
	
	private HashMap<HistogramScale,HashMap<HistogramType,T>> data ;
	
	@Override
	public T getHistAtScaleOfType(HistogramScale pScale,HistogramType pType){
		return (data.get(pScale)).get(pType) ;
	}
	void setHistAtScaleOfType(T pHist,HistogramScale pScale,HistogramType pType){
		(data.get(pScale)).put(pType,pHist) ;
	}	
	
	int nTotalCount = -1 ;
	



	/*
	 * TODO: Replace this with a factory method once we have different sorts of characterisers.....
	 */
	public ImageCharacteriser(BufferedImage pImage,T pHist){
		this(pHist) ;
		populateFromBufferedImage(pImage) ;
	}	
	
	
	public ImageCharacteriser(T pHist){
		data = new HashMap<HistogramScale,HashMap<HistogramType,T>>() ;
		for( HistogramScale scale: OUR_SCALES){
			HashMap<HistogramType,T> byTypes = new HashMap<HistogramType,T>() ;
			data.put(scale, byTypes ) ;
			for(HistogramType typ: OUR_TYPES){
				(data.get(scale)).put( typ, (T)pHist.createHistAtScale(scale))  ; 
			}
		}	
	}	


	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#addPixel(int, int, int)
	 */
	@Override
	public void addPixel(int[] pChannelValues){
		for( HistogramScale scale: OUR_SCALES){
			(this.getHistAtScaleOfType(scale,RAW)).addPixel(pChannelValues) ;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#calculateAndStoreNormalised()
	 */
	@Override
	public void calculateAndStoreNormalised(){
		for( HistogramScale scale: OUR_SCALES){
			T rawHist = (this.getHistAtScaleOfType(scale,RAW)) ;
			this.setHistAtScaleOfType((T)rawHist.getCopyNormalised(), scale, NORMALISED) ;
		}		
	} 
	
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#calculateAndStoreFrequencies()
	 */
	@Override
	public void calculateAndStoreFrequencies(){
		for( HistogramScale scale: OUR_SCALES){
			IHistogram rawHist = (this.getHistAtScaleOfType(scale,RAW)) ;
			this.setHistAtScaleOfType((T)rawHist.getCopyFrequencies(), scale, FREQUENCIES) ;
		}		
	}	
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#calculateAndStoreEntropies()
	 */
	@Override
	public void calculateAndStoreEntropies(){
		for( HistogramScale scale: OUR_SCALES){
			T rawHist = (this.getHistAtScaleOfType(scale,RAW)) ;
			this.setHistAtScaleOfType((T)rawHist.getCopyEntropies(), scale, ENTROPIES) ;
		}		
	}		


	@Override
	public void populateFromBufferedImage(BufferedImage pImage){		
		int height = pImage.getHeight();
		int width = pImage.getWidth();

		int r;
		int g;
		int b;

		Raster raster = pImage.getRaster();
		int[] channelValues = {-1,-1,-1} ;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				channelValues[0] = raster.getSample(i, j, 0);
				channelValues[1]= raster.getSample(i, j, 1);
				channelValues[2] = raster.getSample(i, j, 2);
				this.addPixel( channelValues );
			}
		}
		calculateDerivedHistogramsFromRaw() ;
	}	
	
	private void calculateDerivedHistogramsFromRaw(){
		this.calculateAndStoreNormalised();
		this.calculateAndStoreFrequencies();
		this.calculateAndStoreEntropies() ;	
	}
	
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#findIndicesOfMostAndLeastSimilarAtScale(com.mturyn.imageHistComparer.histogram.ImageCharacteriser[], com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public IndexedValue[] 
		findIndicesOfMostAndLeastSimilarAtScale(
									IImageCharacteriser[] pCharacterisers,
									HistogramScale pScale,
									HistogramType pType
	){

		IndexedValue[] result = null ;
		if(pCharacterisers.length != 0){
			result = new IndexedValue[2] ;
		} 
		// 0<- min, 1<-max 
		//TODO: use a map and enum'd keys instead?
		// Min:
		result[0] = new IndexedValue(-1,Double.POSITIVE_INFINITY ) ;
		// Max:
		// Double.MIN_VALUE is the smallest <em>positive</em>value 
		// that can be held in a double, not -1*Double.MAX_VALUE:
		result[1] = new IndexedValue(-1, Double.NEGATIVE_INFINITY  ) ;
		
		double[] vals = new double[pCharacterisers.length] ;
		Arrays.fill(vals, -23.0) ;
		
		for(int i=0;i<pCharacterisers.length;++i){
			// Avoid treating self as another---note that this is not .equals(),
			// which could (and probably should) be true for separate characterisers
			// of the same class eating the same image....
			if(this == pCharacterisers[i]){
				continue ;
			}
			
			// How to specify the method used when we're (for example) 
			// training up the recogniser, e.g. find out that frequency distance works best at coarse
			// level but entropic angle best at fine?
			double val = //this.distanceAtScaleOfType(pCharacterisers[i], pScale, pType) ;
					this.getFrequencyMatchPercentageAtScale(pCharacterisers[i], pScale,0.025d) ;
			
			// System.out.println(""+i+": "+result[0].dValue+"---"+val+"---"+result[1].dValue) ;
			
			vals[i] = val ;
			
			// Bias toward first-found results, so "<"/">", not "<="/">" or ">="/"<":
			if( val < result[0].dValue){
				result[0].dValue = val ;
				result[0].nIndex = i ;
			}
			if( val > result[1].dValue){
					result[1].dValue = val ;
					result[1].nIndex = i ;
			} 
		}

		return result ;		
	}
	
	/**
	 * 
	 * @param pCharacterisers an array of IImageCharacteriser instances form which to
	 * calculate one of several sorts of difference metric
	 * @param pScale
	 * @return
	 */

	public IndexedValue[] 
			findIndicesOfMostAndLeastSimilarAtScaleOfType(
										IImageCharacteriser[] pCharacterisers,
										HistogramDistanceMethod pMethod,
										double dFractionalFuzziness,
										HistogramScale pScale,
										HistogramType pType
	){

			IndexedValue[] result = null ;
			if(pCharacterisers.length != 0){
				result = new IndexedValue[2] ;
			} 
			// 0<- min, 1<-max 
			//TODO: use a map and enum'd keys instead?
			// Min:
			result[0] = new IndexedValue(-1,Double.POSITIVE_INFINITY ) ;
			// Max:
			// Double.MIN_VALUE is the smallest <em>positive</em>value 
			// that can be held in a double, not -1*Double.MAX_VALUE:
			result[1] = new IndexedValue(-1, Double.NEGATIVE_INFINITY  ) ;
			
			double[] vals = new double[pCharacterisers.length] ;
			Arrays.fill(vals, -23.0) ;
			
			for(int i=0;i<pCharacterisers.length;++i){
				// Avoid treating self as another---note that this is not .equals(),
				// which could (and probably should) be true for separate characterisers
				// of the same class eating the same image....
				if(this == pCharacterisers[i]){
					continue ;
				}
				
				// How to specify the method used when we're (for example) 
				// training up the recogniser, e.g. find out that frequency distance works best at coarse
				// level but entropic angle best at fine?
				// LATER: USed a Map:
				IHistCharacteriserDistance distancer = AbstractDistance.getDistancerForName(pMethod) ;
				
				double val = distancer.compare(this, pCharacterisers[i], pScale, pType, dFractionalFuzziness) ;
						
						
				
				// System.out.println(""+i+": "+result[0].dValue+"---"+val+"---"+result[1].dValue) ;
				
				vals[i] = val ;
				
				// Bias toward first-found results, so "<"/">", not "<="/">" or ">="/"<":
				if( val < result[0].dValue){
					result[0].dValue = val ;
					result[0].nIndex = i ;
				}
				if( val > result[1].dValue){
						result[1].dValue = val ;
						result[1].nIndex = i ;
				} 
			}

			return result ;		
		}
		
		

	

	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#getStringAtScaleOfType(com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public String getStringAtScaleOfType(HistogramScale pScale,HistogramType pType){
		return (this.getHistAtScaleOfType(pScale, pType)).toString() ;
	}		
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#getRawString(com.mturyn.imageHistComparer.Utilities.HistogramScale)
	 */
	@Override
	public String getRawString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,RAW) ;
	}	
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#getNormalisedString(com.mturyn.imageHistComparer.Utilities.HistogramScale)
	 */
	@Override
	public String getNormalisedString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,NORMALISED) ;
	}		
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#getFrequenciesString(com.mturyn.imageHistComparer.Utilities.HistogramScale)
	 */
	@Override
	public String getFrequenciesString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,FREQUENCIES) ;
	}		
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#getEntropiesString(com.mturyn.imageHistComparer.Utilities.HistogramScale)
	 */
	@Override
	public String getEntropiesString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,NORMALISED) ;
	}	
	
	
	/**
	 * Another way of getting a distance from another mCharacteriser's hist:
	 * How many of the frequency values are close to the other's?
	 */
	public int getFrequencyMatchPercentageAtScale(IImageCharacteriser pOther,HistogramScale pScale, double dWindowHalfHeight){
		return this.getHistAtScaleOfType(pScale, FREQUENCIES).percentMatches(pOther.getHistAtScaleOfType(pScale,FREQUENCIES),dWindowHalfHeight) ;
	}
	
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#dotProductAtScaleOfType(com.mturyn.imageHistComparer.histogram.ImageCharacteriser, com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public double dotProductAtScaleOfType(IImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).dotProduct(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#distanceAtScaleOfType(com.mturyn.imageHistComparer.histogram.ImageCharacteriser, com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public double distanceAtScaleOfType(IImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).distance(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}	
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#lengthAtScaleOfType(com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public double lengthAtScaleOfType(HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).length();
	}

	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#cosineAtScaleOfType(com.mturyn.imageHistComparer.histogram.ImageCharacteriser, com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public double cosineAtScaleOfType(IImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).cosine(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#angleAtScaleOfType(com.mturyn.imageHistComparer.histogram.ImageCharacteriser, com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public double angleAtScaleOfType(IImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).angle(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}	
	
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.histogram.IImageCharacteriser#angleDegreesAtScaleOfType(com.mturyn.imageHistComparer.histogram.ImageCharacteriser, com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public int angleDegreesAtScaleOfType(IImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return (int) Math.round(RAD_TO_INTEGRAL_DEGREES(this.angleAtScaleOfType(pOther,pScale,pType))) ;
	}		
	
}


