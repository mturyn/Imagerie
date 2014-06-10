package com.mturyn.imageHistComparer.histogram;

import static com.mturyn.imageHistComparer.Utilities.LN2;
import static com.mturyn.imageHistComparer.Utilities.RAD_TO_INTEGRAL_DEGREES;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import com.mturyn.imageHistComparer.IHistogram;
import com.mturyn.imageHistComparer.Utilities;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;

public abstract class AbstractPixelHist implements IHistogram {

	protected static int[] NBINS_COARSE = {2,2,2};
	protected static int[] NBINS_FINE = {4,4,4};
	protected static HashMap<HistogramScale,int[]> SCALE_DETAILS_NBINS = new HashMap<HistogramScale,int[]>();
	protected static int[] MESHES_COARSE = {128,128,128};
	protected static int[] MESHES_FINE = {64,64,64};
	protected static HashMap<HistogramScale,int[]> SCALE_DETAILS_MESHES = new HashMap<HistogramScale,int[]>();
	protected int[] meshes;
	protected int[] nBins;
	protected double[][][] data;
	TreeSet<ColorspaceBlock> descendingOrderedBins;
	protected int nTotalCount = 0;
	static int c = -1;

	public void initialise(int[] pNBins, int[] pMeshes){
		nBins = Arrays.copyOf(pNBins,pNBins.length) ;
		meshes = Arrays.copyOf(pMeshes,pMeshes.length) ;		
		
		// These would need be initialised if I decide I need to start each bin with
		// a count of (say) 1 in order to avoid divides-by-zero without testing for them,		
		data = new double[nBins[0]][nBins[1]][nBins[2]] ;
		// ...in which example I would initialise the total count to 64:
		nTotalCount = 0 ;		
	}
		
	

	@Override
	public double getValue(int pBinRed, int pBinGreen, int pBinBlue) {
		return data[pBinRed][pBinGreen][pBinBlue] ;
	}

	@Override
	public IHistogram setValue(int pBinRed, int pBinGreen, int pBinBlue, double pVal) {
		data[pBinRed][pBinGreen][pBinBlue] = pVal ;
		return this ;
	}

	public int[] getMeshes(){
		return meshes ;
	}
	public int[] getNBins(){
		return meshes ;
	}	
	
	
	public abstract int[] getBinIndices(int[] pChannelValues) ;
	
	public void addPixel(int[] pChannelValues){
		int[] binIndices =  this.getBinIndices(pChannelValues) ;
		++data[binIndices[0]][binIndices[1]][binIndices[2]] ;
		++nTotalCount ;
	}	
	
	
	@Override
	public double distance(IHistogram pOtherHist) {	
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
	 *  Return the number of matches (value within a given limit from our value)
	 *  expressed as a fraction of the total possible matches, where matches
	 *  are defined by equality within a given fractional value:
	 * @param pOtherHist
	 * @param dWindowHalfHeight
	 * @return
	 */
	@Override
	public int percentMatches(IHistogram pOtherHist, double dFractionaLWindow){
		
		int result = -29 ;
		
		double matchesCount = 0 ;
		double dTotalBins = nBins[0]*nBins[1]*nBins[2] ;
		// Trivial case, use object reference equality:
		if(this==pOtherHist){
			result = 23 ;//100 ;
		} else {
			double maxDiff = Math.abs(dFractionaLWindow) ;
			// For this iteration, assume histograms are structurally identical
			// (scales, number of bins at different scales, data types at scale):
			// TODO: write iterator for instances:
			for(int binR=0;binR<nBins[0];++binR){
				for(int binG=0;binG<nBins[1];++binG){
					for(int binB=0;binB<nBins[2];++binB){							
							double thisValue = this.getValue(binR,binG,binB) ;
							double otherValue = pOtherHist.getValue(binR,binG,binB) ;
							double delta = otherValue - thisValue ;
							double mean = (thisValue+otherValue)/2.0d ;
							if(delta == 0){
								delta = 0d ;
								++matchesCount ;
							} else {
								delta = Math.abs(delta/mean) ;
								if( delta <= maxDiff){
									++matchesCount ;
								}
							}
					}
				}	
			}	
			result = (int) Math.round((100.0d*matchesCount)/dTotalBins) ;
		}
		return result ;
	}

	@Override
	public double dotProduct(IHistogram pOtherHist) {	
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

	@Override
	public double length() {	
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

	@Override
	public double cosine(IHistogram pOtherHist) {
		double _d = this.distance(pOtherHist) ;
		double _l = this.length() ;
		double _lO = pOtherHist.length() ;
		
		return this.dotProduct(pOtherHist)/(this.length()*pOtherHist.length() ) ;
	}

	@Override
	public double angle(IHistogram pOtherHist) {
		return Math.acos(this.cosine(pOtherHist) ) ;
	}

	@Override
	public int angleDegrees(IHistogram pOtherHist) {
		return (int) Math.round(RAD_TO_INTEGRAL_DEGREES(this.angle(pOtherHist))) ;
	}



	@Override
	public abstract AbstractPixelHist scaledCopy(double pScale) ;

	@Override
	public AbstractPixelHist getCopyNormalised() {		
		return this.scaledCopy(this.length());	
	}

	@Override
	public AbstractPixelHist getCopyFrequencies() {		
		return this.scaledCopy(this.nTotalCount);	
	}

	@Override
	public abstract AbstractPixelHist getCopyEntropies() ;

	@Override
	public TreeSet<ColorspaceBlock> orderedBinsDescending() {
		
		descendingOrderedBins = new TreeSet<ColorspaceBlock>() ;
	
		// 
		for(int binR=0;binR<nBins[0];++binR){
			for(int binG=0;binG<nBins[1];++binG){
				for(int binB=0;binB<nBins[2];++binB){
					descendingOrderedBins.add(new ColorspaceBlock(binR,binG,binB,getValue(binR,binG,binB)) ) ;
				}
			}	
		}
		return descendingOrderedBins ;
	}

	@Override
	public String getStringThresholded(double pThreshold) {
		StringBuilder sb = new StringBuilder() ;
		sb.append(this.toString()).append('\r') ;
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

	@Override
	public String toString() {
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

	@Override
	public String toCharacterString() {
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