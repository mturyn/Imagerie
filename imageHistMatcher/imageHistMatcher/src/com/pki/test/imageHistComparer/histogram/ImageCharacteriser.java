package com.pki.test.imageHistComparer.histogram;

import static com.pki.test.imageHistComparer.Utilities.RAD_TO_INTEGRAL_DEGREES;
import static com.pki.test.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.pki.test.imageHistComparer.Utilities.HistogramScale.FINE;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.ENTROPIES;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.FREQUENCIES;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.NORMALISED;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.RAW;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashMap;

import com.pki.test.imageHistComparer.Utilities.HistogramScale;
import com.pki.test.imageHistComparer.Utilities.HistogramType;

import com.pki.test.imageHistComparer.IndexedValue;

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
public class ImageCharacteriser {

	
	// Index histogram views by pre-defined scales and data-types:
	
	public static HistogramScale[] OUR_SCALES = {COARSE,FINE}  ;
	public static HistogramType[] OUR_TYPES = {RAW,NORMALISED,FREQUENCIES,ENTROPIES}  ;
	
	
	private HashMap<HistogramScale,HashMap<HistogramType,RGBPixelHist>> data ;
	RGBPixelHist getHistAtScaleOfType(HistogramScale pScale,HistogramType pType){
		RGBPixelHist hist = (data.get(pScale)).get(pType) ;
		return hist ;
	}
	void setHistAtScaleOfType(RGBPixelHist pHist,HistogramScale pScale,HistogramType pType){
		(data.get(pScale)).put(pType,pHist) ;
	}	
	
	int nTotalCount = -1 ;
	

/*
 * How many raw histograms are we generating for each image? 
 */
	public int getDepth(){
		return ((data.get(RAW)).keySet()).size() ;
	}		
	
	public ImageCharacteriser(){
		data = new HashMap<HistogramScale,HashMap<HistogramType,RGBPixelHist>>() ;
		for( HistogramScale scale: OUR_SCALES){
			HashMap<HistogramType,RGBPixelHist> byTypes = new HashMap<HistogramType,RGBPixelHist>() ;
			data.put(scale, byTypes ) ;
			for(HistogramType typ: OUR_TYPES){
				(data.get(scale)).put( typ, RGBPixelHist.createHist(scale)) ; 
			}
		}	
	}	

	
	/**
	 * @param pRed [0,255] pixel red value
	 * @param pGreen [0,255] pixel green value
	 * @param pBlue[0,255] pixel blue value
	 */
	public void addPixel(int pRed,int pGreen,int pBlue){
		for( HistogramScale scale: OUR_SCALES){
			(this.getHistAtScaleOfType(scale,RAW)).addPixel(pRed,pGreen,pBlue) ;
		}
	}
	
	public void normalise(){
		for( HistogramScale scale: OUR_SCALES){
			RGBPixelHist rawHist = (this.getHistAtScaleOfType(scale,RAW)) ;
			this.setHistAtScaleOfType(rawHist.normalised(), scale, NORMALISED) ;
		}		
	}
	
	
	public void frequencies(){
		for( HistogramScale scale: OUR_SCALES){
			RGBPixelHist rawHist = (this.getHistAtScaleOfType(scale,RAW)) ;
			this.setHistAtScaleOfType(rawHist.frequencies(), scale, FREQUENCIES) ;
		}		
	}	
	
	public void entropies(){
		for( HistogramScale scale: OUR_SCALES){
			RGBPixelHist rawHist = (this.getHistAtScaleOfType(scale,RAW)) ;
			this.setHistAtScaleOfType(rawHist.entropies(), scale, ENTROPIES) ;
		}		
	}		

	/*
	 * TODO: Replace this with a factory method once we have different sorts of characterisers.....
	 */
	public ImageCharacteriser(BufferedImage pImage){
		this() ;
		int height = pImage.getHeight();
		int width = pImage.getWidth();

		int r;
		int g;
		int b;

		Raster raster = pImage.getRaster();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				r = raster.getSample(i, j, 0);
				g = raster.getSample(i, j, 1);
				b = raster.getSample(i, j, 2);
				this.addPixel(r, g, b);

			}
		}
		this.normalise();
		this.frequencies();
		this.entropies() ;
	}	

	
	public IndexedValue[] findIndicesOfMostAndLeastSimilarAtScale(ImageCharacteriser[] pCharacterisers,HistogramScale pScale,HistogramType pType){

		IndexedValue[] result = new IndexedValue[2] ;
		// 0<- min, 1<-max 
		//TODO: use a map and enum'd keys instead?
		result[0] = new IndexedValue(-1,Double.MAX_VALUE) ;
		result[1] = new IndexedValue(-1,Double.MIN_VALUE) ;
		
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
			double val = this.angleAtScaleOfType(pCharacterisers[i], pScale, pType) ;
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
	
	
	

	public String getStringAtScaleOfType(HistogramScale pScale,HistogramType pType){
		return (this.getHistAtScaleOfType(pScale, pType)).toString() ;
	}		
	
	public String getRawString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,RAW) ;
	}	
	
	public String getNormalisedString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,NORMALISED) ;
	}		
	
	public String getFrequenciesString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,FREQUENCIES) ;
	}		
	
	public String getEntropiesString(HistogramScale pScale){
		return this.getStringAtScaleOfType(pScale,NORMALISED) ;
	}		
		
	public double dotProductAtScaleOfType(ImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).dotProduct(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}
	
	public double distanceAtScaleOfType(ImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).distance(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}	
	
	public double lengthAtScaleOfType(HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).length();
	}

	public double cosineAtScaleOfType(ImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).cosine(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}
	public double angleAtScaleOfType(ImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return this.getHistAtScaleOfType(pScale, pType).angle(pOther.getHistAtScaleOfType(pScale, pType)) ;
	}	
	
	public int angleDegreesAtScaleOfType(ImageCharacteriser pOther,HistogramScale pScale,HistogramType pType){
		return (int) Math.round(RAD_TO_INTEGRAL_DEGREES(this.angleAtScaleOfType(pOther,pScale,pType))) ;
	}		
	
}


