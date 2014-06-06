package com.pki.test.imageHistComparer.histogram;

import java.awt.image.BufferedImage ;
import java.awt.image.Raster ;
import java.util.HashMap ;

// I want to use an enum, since an integer is too specific and not descriptive enough,
// but I don't want to index by arbitrary String instances:
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale.COARSE ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale.FINE ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.RAW;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.NORMALISED ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.FREQUENCIES ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.ENTROPIES ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale ;
import static com.pki.test.imageHistComparer.histogram.Utilities.RAD_TO_INTEGRAL_DEGREES;


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

	public static HistogramScale[] OUR_SCALES = {COARSE,FINE}  ;
	public static HistogramType[] OUR_TYPES = {RAW,NORMALISED,FREQUENCIES,ENTROPIES}  ;
	
	// Index histogram views by pre-defined scales and data-types:
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


