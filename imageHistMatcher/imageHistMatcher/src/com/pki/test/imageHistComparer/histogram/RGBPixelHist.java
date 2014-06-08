package com.pki.test.imageHistComparer.histogram;

import static com.pki.test.imageHistComparer.Utilities.LN2;
import static com.pki.test.imageHistComparer.Utilities.RAD_TO_INTEGRAL_DEGREES;
import static com.pki.test.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.pki.test.imageHistComparer.Utilities.HistogramScale.FINE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import com.pki.test.imageHistComparer.Utilities;
import com.pki.test.imageHistComparer.Utilities.HistogramScale;

import com.pki.test.imageHistComparer.IndexedValue;



//I want to use an enum, since an integer is too specific and not descriptive enough,
//but I don't want to index by arbitrary String instances:

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
public class RGBPixelHist implements IHistogram {

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
	
	// Order histogram by frequency, once we have it: 
	TreeSet<RGBPixelsBin> descendingOrderedBins ;
	
	
	int nTotalCount = 0 ;
	static int c = -1 ;
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#addPixel(int, int, int)
	 */
	@Override
	public void addPixel(int pRed,int pGreen,int pBlue){
		++data[pRed/meshes[0]][pGreen/meshes[1]][pBlue/meshes[2]] ;
		++nTotalCount ;
	}
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#getValue(int, int, int)
	 */
	@Override
	public double getValue(int pBinRed,int pBinGreen,int pBinBlue){
		return data[pBinRed][pBinGreen][pBinBlue] ;
	}
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#setValue(int, int, int, double)
	 */
	@Override
	public IHistogram setValue(int pBinRed, int pBinGreen,int pBinBlue,double pVal){
		data[pBinRed][pBinGreen][pBinBlue] = pVal ;
		return this ;
	}	
	

	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#distance(com.pki.test.imageHistComparer.histogram.IHistogram)
	 */
	@Override
	public double distance(IHistogram pOtherHist){	
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
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#dotProduct(com.pki.test.imageHistComparer.histogram.IHistogram)
	 */
	@Override
	public double dotProduct(IHistogram pOtherHist){	
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
	
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#length()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#cosine(com.pki.test.imageHistComparer.histogram.IHistogram)
	 */
	@Override
	public double cosine(IHistogram pOtherHist){
		double _d = this.distance(pOtherHist) ;
		double _l = this.length() ;
		double _lO = pOtherHist.length() ;
		
		return this.dotProduct(pOtherHist)/(this.length()*pOtherHist.length() ) ;
	}
	
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#angle(com.pki.test.imageHistComparer.histogram.IHistogram)
	 */
	@Override
	public double angle(IHistogram pOtherHist){
		return Math.acos(this.cosine(pOtherHist) ) ;
	}	
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#angleDegrees(com.pki.test.imageHistComparer.histogram.IHistogram)
	 */
	@Override
	public int angleDegrees(IHistogram pOtherHist){
		return (int) Math.round(RAD_TO_INTEGRAL_DEGREES(this.angle(pOtherHist))) ;
	}
	
/**
 * Factory method that returns a histogram of the desired granularity:
 * Will probably need more flexibility here, later, but for now we 
 * will stick to the scale-conditional creation implicit in the maps from scale
 * (e.g. COARSE)) to a mesh-set and a number of bins:
 * @param pScale One of the scale types enumerated in HistogramScale
 * @see Utilities.HistogramScale
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
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#scaledCopy(double)
	 */
	@Override
	public RGBPixelHist scaledCopy(double pScale){
		RGBPixelHist result = new RGBPixelHist(nBins,meshes) ;
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					result.setValue(binR,binG,binB,this.getValue(binR,binG,binB)/pScale) ;
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
	

	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#getCopyNormalised()
	 */
	@Override
	public RGBPixelHist getCopyNormalised(){		
		return this.scaledCopy(this.length());	
	}
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#getCopyFrequencies()
	 */
	@Override
	public RGBPixelHist getCopyFrequencies(){		
		return this.scaledCopy(this.nTotalCount);	
	}	
	
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#getCopyEntropies()
	 */
	@Override
	public RGBPixelHist getCopyEntropies(){
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
					val = -1.0*val*LN2(val) ;
					result.setValue(binR,binG,binB,val) ;
				}
			}	
		}		
		
		return result ;
	}		
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#orderedBinsDescending()
	 */
	@Override
	public TreeSet<RGBPixelsBin> orderedBinsDescending(){
		
		descendingOrderedBins = new TreeSet<RGBPixelsBin>() ;

		// 
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					descendingOrderedBins.add(new RGBPixelsBin(binR,binG,binB,getValue(binR,binG,binB)) ) ;
				}
			}	
		}
		return descendingOrderedBins ;
	}			
	
	
	// Unit test start:
	public static void main(String[] argv){
		int[] bins = {2,2,2} ;
		int[] meshes = {128,128,128} ;
		IHistogram instance_0 = new RGBPixelHist(bins,meshes) ;
		for(int i=0;i<4;++i){
			instance_0.addPixel(128, 0, 0) ;
		}
		for(int i=0;i<3;++i){
			instance_0.addPixel(0,0,128) ;
		}		

		IHistogram norm_0 = instance_0.getCopyNormalised();
		
		System.out.println("Raw: "+instance_0.length()+"\r"+instance_0.getStringThresholded(0d)) ;
		System.out.println("Normed: "+norm_0.length()+"\r"+norm_0.getStringThresholded(0d)) ;
		
		IHistogram instance_1 = new RGBPixelHist(bins,meshes) ;
		for(int i=0;i<3;++i){
			instance_1.addPixel(128, 0, 0) ;
		}
		for(int i=0;i<4;++i){
			instance_1.addPixel(0,0,128) ;
		}		

		IHistogram norm_1 = instance_1.getCopyNormalised();
		
		System.out.println("Raw: "+instance_1.length()+"\r"+instance_1.getStringThresholded(0d)) ;
		System.out.println("Normed: "+norm_1.length()+"\r"+norm_1.getStringThresholded(0d)) ;	
		
		System.out.println("Distance: 0-1: "+norm_0.distance(norm_1)) ;
		System.out.println("Distance: 1-0: "+norm_1.distance(norm_0)) ;

		System.out.println("Cosine: 0-0: "+norm_0.cosine(norm_0)) ;	
		System.out.println("Cosine: 1-1: "+norm_1.cosine(norm_1)) ;	
		
		
		System.out.println("Cosine: 0-1: "+norm_0.cosine(norm_1)) ;
		System.out.println("Cosine: 1-0: "+norm_1.cosine(norm_0)) ;
				
		System.out.println("Angle: 0-1: "+norm_0.angleDegrees(norm_1)) ;
		System.out.println("Angle: 1-0: "+norm_1.angleDegrees(norm_0)) ;		
				
	}
	
		
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#getStringThresholded(double)
	 */
	@Override
	public String getStringThresholded(double pThreshold){
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
	
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#toString()
	 */
	@Override
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
	
	// Order the histogram entries by decreasing frequency,
	// generate a string representing them up to min(given length,total number of bins)
	/* (non-Javadoc)
	 * @see com.pki.test.imageHistComparer.histogram.IHistogram#toCharacterString()
	 */
	@Override
	public String toCharacterString(){
		StringBuilder sb = new StringBuilder() ;
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


