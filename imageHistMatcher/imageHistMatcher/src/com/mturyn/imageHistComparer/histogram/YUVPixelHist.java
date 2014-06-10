package com.mturyn.imageHistComparer.histogram;

import static com.mturyn.imageHistComparer.Utilities.LN2;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.FINE;

import java.util.Arrays;

import com.mturyn.imageHistComparer.IHistogram;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;

public class YUVPixelHist extends AbstractPixelHist implements IHistogram {
	
	static {
		SCALE_DETAILS_NBINS.put(COARSE, NBINS_COARSE ) ;
		SCALE_DETAILS_NBINS.put(FINE, NBINS_FINE ) ;
	}

	static {
		SCALE_DETAILS_MESHES.put(COARSE, MESHES_COARSE ) ;
		SCALE_DETAILS_MESHES.put(FINE, MESHES_FINE ) ;
	}	

	public YUVPixelHist(){} ;
	
	private YUVPixelHist(int[] pNBins, int[] pMeshes){
		nBins = Arrays.copyOf(pNBins,pNBins.length) ;
		meshes = Arrays.copyOf(pMeshes,pMeshes.length) ;		
		
		// These would need be initialised if I decide I need to start each bin with
		// a count of (say) 1 in order to avoid divides-by-zero without testing for them,		
		data = new double[nBins[0]][nBins[1]][nBins[2]] ;
		// ...in which example I would initialise the total count to 64:
		nTotalCount = 0 ;
	}
	
	
	@Override
	public IHistogram  createHistAtScale(HistogramScale pScale){
		return new YUVPixelHist(SCALE_DETAILS_NBINS.get(pScale),SCALE_DETAILS_MESHES.get(pScale)) ;
	}	
	
	
	// Thanks, Wikipedia:
	static double WR = 0.299d ;
	static double WB = 0.114d ;
	static double WG = 1.0d - (WR+WB) ;
	
	static double uMax = 0.436d ;
	static double vMax = 0.615d ;
	
	/**
	 * RGB->YUV transformation matrix, scaled for 0-255:
	 * Thanks, Wikipedia editors.
	 */
	static final double[][] rgbYUV255 = 
		{ 
			{WR,WG,WB},
			{-0.1687270642201835,-0.3312614678899083,0.5},
			{0.5,-0.41869105691056907,-0.08130894308943089} 
		} ;
	
	@Override
	public int[] getBinIndices(int[] pChannelValues){
		// Make the to-be-overwritten values distinctive:
		int[] result = {-23,-24,-25} ;

		// Not only do we need to rescale U and V, we
		// need to offset them as well:
		double[] dResult = {0.0d,127.5d,127.5d} ;
		for(int i=0;i<3;++i){
			for(int j=0;j<3;++j){
				double val = rgbYUV255[i][j]*pChannelValues[j] ;
				dResult[i] +=  val ;
			}
			// ...reproducing integer arithmetic that previously
			// kept this from going over the total number of bins
			// in RGB, e.g. 255/128 == 1 : 
			result[i] = (int) Math.floor(dResult[i]/meshes[i]) ;
		}
		return result ;
	}
	
	/* Used in testing
	public YUVPixelHist(BufferedImage pImage, int[] pBins, int[] pMeshes){
		this( pBins, pMeshes ) ;
		int height = pImage.getHeight();
		int width = pImage.getWidth();

		int[] channelValues = {-1,-1,-1} ;

		Raster raster = pImage.getRaster();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				channelValues[0] = raster.getSample(i, j, 0);
				channelValues[1] = raster.getSample(i, j, 1);
				channelValues[2] = raster.getSample(i, j, 2);
				this.addPixel(r, g, b);

			}
		}		
	}
	*/
	
	public YUVPixelHist scaledCopy(double pScale) {
		YUVPixelHist result = new YUVPixelHist(nBins,meshes) ;
		for(int binY=0;binY<nBins[0];++binY){
			for(int binU=0;binU<nBins[1];++binU){
				for(int binV=0;binV<nBins[2];++binV){					
					result.setValue(binY,binU,binV,this.getValue(binY,binU,binV)/pScale) ;
				}
			}	
		}		
		
		return result ;
	}	
	
	public YUVPixelHist getCopyEntropies() {
		YUVPixelHist result = new YUVPixelHist(nBins,meshes) ;
	
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
	
	

}
