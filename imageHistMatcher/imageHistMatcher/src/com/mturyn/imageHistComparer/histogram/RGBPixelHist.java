package com.mturyn.imageHistComparer.histogram;

import static com.mturyn.imageHistComparer.Utilities.LN2;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.FINE;

import com.mturyn.imageHistComparer.IHistogram;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;



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
public class RGBPixelHist extends AbstractPixelHist implements IHistogram {

	static {
		SCALE_DETAILS_NBINS.put(COARSE, NBINS_COARSE ) ;
		SCALE_DETAILS_NBINS.put(FINE, NBINS_FINE ) ;
	}

	static {
		SCALE_DETAILS_MESHES.put(COARSE, MESHES_COARSE ) ;
		SCALE_DETAILS_MESHES.put(FINE, MESHES_FINE ) ;
	}
	
	@Override
	public int[] getBinIndices(int[] pChannel){
		int[] bindex = java.util.Arrays.copyOf(pChannel, pChannel.length) ;
		for(int i=0;i<bindex.length;++i){
			bindex[i] /= meshes[i] ;
		} 
		return bindex ;
	}

	
	private RGBPixelHist(int[] pNBins, int[] pMeshes){
		this() ;
		initialise(pNBins,pMeshes) ;
	}
	

	@Override
	public IHistogram  createHistAtScale(HistogramScale pScale){
		return new RGBPixelHist(SCALE_DETAILS_NBINS.get(pScale),SCALE_DETAILS_MESHES.get(pScale)) ;
	}
	
	/* Used in testing
	public RGBPixelHist(BufferedImage pImage, int[] pBins, int[] pMeshes){
		this( pBins, pMeshes ) ;
		int height = pImage.getHeight();
		int width = pImage.getWidth();

		int[] pChannelValues ={-1,-1,-1} ;

		Raster raster = pImage.getRaster();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pChannelValues[0] = raster.getSample(i, j, 0);
				pChannelValues[1] = raster.getSample(i, j, 1);
				pChannelValues[2] = raster.getSample(i, j, 2);
				this.addPixel( pChannelValues );

			}
		}		
	}
	*/
	
	public RGBPixelHist scaledCopy(double pScale) {
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
	
	public RGBPixelHist getCopyEntropies() {
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
	
	
	public RGBPixelHist(){
		meshes=new int[3] ;
		nBins=new int[3] ;
		for(int i=0;i<3;++i){
			meshes[i] = 128 ;
			nBins[i]=2 ;
		}
	}
	
	

	// Unit test start:
	public static void main(String[] argv){
		int[] maxx = {255,255,255} ;
		
		RGBPixelHist inst = new RGBPixelHist() ;
		int[] trans = inst.getBinIndices(maxx) ;
		
		int[] bins = {2,2,2} ;
		int[] meshes = {128,128,128} ;
		IHistogram instance_0 = new RGBPixelHist(bins,meshes) ;
		int[] reddish = {255, 0, 0} ;
		int[] bluish  = {0,0,255} ;
		for(int i=0;i<4;++i){
			instance_0.addPixel(reddish) ;
		}
		for(int i=0;i<3;++i){
			instance_0.addPixel(bluish) ;
		}		

		IHistogram norm_0 = instance_0.getCopyNormalised();
		
		System.out.println("Raw: "+instance_0.length()+"\r"+instance_0.getStringThresholded(0d)) ;
		System.out.println("Normed: "+norm_0.length()+"\r"+norm_0.getStringThresholded(0d)) ;
		
		IHistogram instance_1 = new RGBPixelHist(bins,meshes) ;
		for(int i=0;i<3;++i){
			instance_1.addPixel(reddish) ;
		}
		for(int i=0;i<4;++i){
			instance_1.addPixel(bluish) ;
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
	
}


