package com.pki.test.imageHistComparer.histogram;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Arrays;
import java.util.HashMap;

import static com.pki.test.imageHistComparer.histogram.Utilities.RAD_TO_INTEGRAL_DEGREES ;
//What entropy means to me:
import static com.pki.test.imageHistComparer.histogram.Utilities.LN2 ;

import static com.pki.test.imageHistComparer.histogram.Utilities.PADDED ;

//I want to use an enum, since an integer is too specific and not descriptive enough,
//but I don't want to index by arbitrary String instances:
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale.COARSE ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale.FINE ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.RAW;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.NORMALISED ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.FREQUENCIES ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.ENTROPIES ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale ;

/**
 * @author mturyn
 * I'm purposely _not_ making this general except where I can see
 * that I might have to tune within this demo period, e.g. nBins,
 * where I can see wanting to go finer with G than R&B, or later V than with 
 * S and V...but not now.
 * 
 * A much more general version of this class would use HashMaps to identify the
 * dimensions, but fast as Hashing/lookup is, it's still not as fast as arrays.
 * 
 * For the first pass, the data are stored in a 3-D array, but treated as a vector---
 * this is equivalent to deciding that the colour of pixel[a,b] isn't well correlated
 * with those of pixels[a+-1,b], pixels[a,b+-1],pixels[a+-1,b+-1]...this is not a good
 * assumption, but a crude partition of the colorspace, then choosing the (say) 3
 * most populated sectors to compare to another image's, is a quick substitute for what
 * I'd really like: a cluster analysis to determine a short signature for the image:
 * 
 */
public class RGBPixelHist {

	// Partition the RGB [0,255]^3 colourpace
	// evenly...for now:
	private static int[] NBINS_COARSE = {2,2,2} ;
	private static int[] NBINS_FINE = {4,4,4} ;
	private static HashMap<HistogramScale,int[]> SCALE_DETAILS_NBINS = new HashMap<HistogramScale,int[]>() ;
	static {
		SCALE_DETAILS_NBINS.put(COARSE, NBINS_COARSE ) ;
		SCALE_DETAILS_NBINS.put(FINE, NBINS_FINE ) ;
	}

	// ...but leave room for uneven partition later,
	// most likely in (say) YUV or HSV space:
	private static int[] MESHES_COARSE = {128,128,128} ;
	private static int[] MESHES_FINE = {64,64,64} ;
	private static HashMap<HistogramScale,int[]> SCALE_DETAILS_MESHES = new HashMap<HistogramScale,int[]>() ;
	static {
		SCALE_DETAILS_MESHES.put(COARSE, MESHES_COARSE ) ;
		SCALE_DETAILS_MESHES.put(FINE, MESHES_FINE ) ;
	}

	int[] meshes ;
	int[] nBins ;
		
	double[][][] data ;
	
	int nTotalCount = 0 ;
	static int c = -1 ;
	
	/**
	 * @param pRed [0,255] pixel red value
	 * @param pGreen [0,255] pixel green value
	 * @param pBlue[0,255] pixel blue value
	 */
	public void addPixel(int pRed,int pGreen,int pBlue){
		++data[pRed/meshes[0]][pGreen/meshes[1]][pBlue/meshes[2]] ;
		++nTotalCount ;
	}
	
	public double getValue(int pBinRed,int pBinGreen,int pBinBlue){
		return data[pBinRed][pBinGreen][pBinBlue] ;
	}
	
	public RGBPixelHist setValue(double pVal, int pBinRed,int pBinGreen,int pBinBlue){
		data[pBinRed][pBinGreen][pBinBlue] = pVal ;
		return this ;
	}	
	

	public double distance(RGBPixelHist pOtherHist){	
		double result = -1d ;
		double accumulator = 0d ;
		
		// For this iteration, assume histograms are structurally identical
		// (scales, number of bins at different scales, data types at scale):
		double d=-1 ;
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					double delta = getValue(binR,binG,binB) -  pOtherHist.getValue(binR,binG,binB);
					accumulator += delta*delta ;
				}
			}	
		}
		result = Math.sqrt(accumulator) ;
		return result ;
	}
	
	/**
	 *  We're storing in a matrix, but we're treating it as a vector
	 * @param pOtherHist
	 * @return
	 */
	public double dotProduct(RGBPixelHist pOtherHist){	
		double result = 0d ;
		
		// For this iteration, assume histograms are structurally identical
		// (scales, number of bins at different scales, data types at scale):
		double d=-1 ;
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					result += getValue(binR,binG,binB)*pOtherHist.getValue(binR,binG,binB);
				}
			}	
		}
		return result ;
	}	
	
	
	public double length(){	
		double result = -1d ;
		double accumulator = 0d ;

		double d=-1 ;
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					double delta = getValue(binR,binG,binB) ;
					accumulator += delta*delta ;
				}
			}	
		}
		result = Math.sqrt(accumulator) ;
		return result ;
	}
	
	public double cosine(RGBPixelHist pOtherHist){
		double _d = this.distance(pOtherHist) ;
		double _l = this.length() ;
		double _lO = pOtherHist.length() ;
		
		return this.dotProduct(pOtherHist)/(this.length()*pOtherHist.length() ) ;
	}
	
	
	public double angle(RGBPixelHist pOtherHist){
		return Math.acos(this.cosine(pOtherHist) ) ;
	}	
	
	public int angleDegrees(RGBPixelHist pOtherHist){
		return (int) Math.round(RAD_TO_INTEGRAL_DEGREES(this.angle(pOtherHist))) ;
	}
	
/**
 * Factory method that returns a histogram of the desired granularity:
 * Will probably need more flexibility here, later, but for now we 
 * will stick to the scale-conditional creation implicit in the maps from scale
 * (e.g. COARSE)) to a mesh-set and a number of bins:
 * @param pScale One of the scale types enumerated in HistogramScale
 * @see com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale
 */
	public static RGBPixelHist createHist(HistogramScale pScale){
		RGBPixelHist result = new RGBPixelHist(SCALE_DETAILS_NBINS.get(pScale),SCALE_DETAILS_MESHES.get(pScale)) ;
		return result ;
	}
	
	
	private RGBPixelHist(int[] pNBins, int[] pMeshes){
		nBins = Arrays.copyOf(pNBins,pNBins.length) ;
		meshes = Arrays.copyOf(pMeshes,pMeshes.length) ;		
		
		// These would need be initialised if I decide I need to start each bin with
		// a count of (say) 1 in order to avoid divides-by-zero without testing for them,		
		data = new double[nBins[0]][nBins[1]][nBins[2]] ;
		// ...in which example I would initialise the total count to 64:
		nTotalCount = 0 ;
	}
	
	public RGBPixelHist scaledCopy(double pScale){
		RGBPixelHist result = new RGBPixelHist(nBins,meshes) ;
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					result.setValue(this.getValue(binR,binG,binB)/pScale,binR,binG,binB) ;
				}
			}	
		}		
		
		return result ;
	}	
	
	/* Used in testing
	public RGBPixelHist(BufferedImage pImage, int[] pBins, int[] pMeshes){
		this( pBins, pMeshes ) ;
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
	}
	*/
	

	/**
	 * Turn this into a unit vector, put on stack for someone else to use.
	 * (I lost too much time working with parent-child schemesfor different
	 * histograms, and decided to have another object hold and keep track of them
	 * instead.)
	 * @return
	 */
	public RGBPixelHist normalised(){		
		return this.scaledCopy(this.length());	
	}
	
	public RGBPixelHist frequencies(){		
		return this.scaledCopy(this.nTotalCount);	
	}	
	
	
	public RGBPixelHist entropies(){
		RGBPixelHist result = new RGBPixelHist(nBins,meshes) ;

		// to avoid ln(0) problems, add 1 to every bin, adjust
		// totalCount accordingly
		int nAllBins = nBins[0]*nBins[1]*nBins[2] ;
		double fudgedTotalCount = this.nTotalCount + nAllBins ;
		
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					double val = this.getValue(binR,binG,binB) ;
					val = (1+val)/fudgedTotalCount ;
					val = 0 - val*LN2(val) ;
					result.setValue(val,binR,binG,binB) ;
				}
			}	
		}		
		
		return result ;
	}		
	
	// Unit test start:
	public static void main(String[] argv){
		int[] bins = {2,2,2} ;
		int[] meshes = {128,128,128} ;
		RGBPixelHist instance_0 = new RGBPixelHist(bins,meshes) ;
		for(int i=0;i<4;++i){
			instance_0.addPixel(128, 0, 0) ;
		}
		for(int i=0;i<3;++i){
			instance_0.addPixel(0,0,128) ;
		}		

		RGBPixelHist norm_0 = instance_0.normalised();
		
		System.out.println("Raw: "+instance_0.length()+"\r"+instance_0.getString(0d)) ;
		System.out.println("Normed: "+norm_0.length()+"\r"+norm_0.getString(0d)) ;
		
		RGBPixelHist instance_1 = new RGBPixelHist(bins,meshes) ;
		for(int i=0;i<3;++i){
			instance_1.addPixel(128, 0, 0) ;
		}
		for(int i=0;i<4;++i){
			instance_1.addPixel(0,0,128) ;
		}		

		RGBPixelHist norm_1 = instance_1.normalised();
		
		System.out.println("Raw: "+instance_1.length()+"\r"+instance_1.getString(0d)) ;
		System.out.println("Normed: "+norm_1.length()+"\r"+norm_1.getString(0d)) ;	
		
		System.out.println("Distance: 0-1: "+norm_0.distance(norm_1)) ;
		System.out.println("Distance: 1-0: "+norm_1.distance(norm_0)) ;

		System.out.println("Cosine: 0-0: "+norm_0.cosine(norm_0)) ;	
		System.out.println("Cosine: 1-1: "+norm_1.cosine(norm_1)) ;	
		
		
		System.out.println("Cosine: 0-1: "+norm_0.cosine(norm_1)) ;
		System.out.println("Cosine: 1-0: "+norm_1.cosine(norm_0)) ;
				
		System.out.println("Angle: 0-1: "+norm_0.angleDegrees(norm_1)) ;
		System.out.println("Angle: 1-0: "+norm_1.angleDegrees(norm_0)) ;		
				
	}
	
		
	public String getString(double pThreshold){
		StringBuilder sb = new StringBuilder() ;
		sb.append(super.toString()).append('\r') ;
		for(int r=0; r<nBins[0]; ++r){
			for(int g=0; g<nBins[1]; ++g){
				for(int b=0; b<nBins[2]; ++b){
					double val = this.getValue(r,g,b) ;
					if(val >= pThreshold ){
						sb.append(r).append(',').append(g).append(',').append(b).append(": ");
						sb.append(val).append('\r') ;
					}
				}
			}
		}
		sb.append('\r') ;
		return sb.toString();
	}		
	
	public String toString(){
		StringBuilder sb = new StringBuilder() ;
		sb.append(super.toString()).append('\r') ;
		for(int r=0; r<nBins[0]; ++r){
			for(int g=0; g<nBins[1]; ++g){
				for(int b=0; b<nBins[2]; ++b){
					sb.append(r).append(',').append(g).append(',').append(b).append(": ");
					sb.append(this.getValue(r,g,b) ).append('\r') ;
				}
			}
		}
		sb.append('\r') ;
		return sb.toString();
	}			
	
}


