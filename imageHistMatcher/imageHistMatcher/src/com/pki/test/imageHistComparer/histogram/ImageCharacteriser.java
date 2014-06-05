package com.pki.test.imageHistComparer.histogram;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashMap;

// I want to use an enum, since an integer is too specific and not descriptive enough,
// but I don't want to index by arbitrary String instances:
import static com.pki.test.imageHistComparer.histogram.HistogramScale.COARSE ;
import static com.pki.test.imageHistComparer.histogram.HistogramScale.FINE ;


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
 * instance to a stringified hist-based)
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

	

	
	// Index histogram views by pre-defined scales:
	private HashMap<HistogramScale,RGBPixelHist> dataRaw ;
	private HashMap<HistogramScale,RGBPixelHist> dataNormalised ;

	public int getDepth(){
		return dataRaw.keySet().size() ;
	}		
	

	
	/**
	 * @param pRed [0,255] pixel red value
	 * @param pGreen [0,255] pixel green value
	 * @param pBlue[0,255] pixel blue value
	 */
	public void addPixel(int pRed,int pGreen,int pBlue){
		/*
		if( ++c<1 ){
			for(int si=0;si<datas.length;++si){
				System.err.println(meshes[si][0]+","+meshes[si][1]+","+ meshes[si][2]) ;
			}		
		}
		*/
		
		// si for 'scaleIndex'; just trying to sow the seeds of more than two scales later: 
		for(int si=0;si<datas.length;++si){
			++(datas[si])[pRed/meshes[si][0]][pGreen/meshes[si][1]][pBlue/meshes[si][2]] ;
		}
		++nTotalCount ;
	}
	
	
	public String getNormalisedString(int pScaleIndex, int pMax, int pThreshold){
		StringBuilder sb = new StringBuilder() ;
		int valPlaces = 1 + (int) Math.round(Math.log10(pMax)) ;
		for(int r=0; r<nBinses[pScaleIndex][0]; ++r){
			for(int g=0; g<nBinses[pScaleIndex][1]; ++g){
				for(int b=0; b<nBinses[pScaleIndex][2]; ++b){
					int val = (int)((double)pMax*getNormalisedValue(pScaleIndex,r,g,b)) ;
					if( val >= pThreshold ){
						sb.append(padded(r,3)).append(',') ;
						sb.append(padded(g,3)).append(',') ;
						sb.append(padded(b,3)).append(" :") ;
						sb.append(padded(val,valPlaces)).append('\r') ;
					}
				}
			}
		}				
		return sb.toString();
	}
	
	public String getNormalisedString(int pScaleIndex,double pThreshold){
		StringBuilder sb = new StringBuilder() ;
		sb.append("Scale Index: ").append(pScaleIndex).append('\r') ;
		sb.append("Mesh sizes:   ").append('(');
		for(int i=0;i<meshes[pScaleIndex].length;++i){
			sb.append(meshes[pScaleIndex][i]);
			if(i<meshes[pScaleIndex].length-1){
				sb.append(',') ;
			}
		}
		sb.append(")\r") ;
		for(int r=0; r<nBinses[pScaleIndex][0]; ++r){
			for(int g=0; g<nBinses[pScaleIndex][1]; ++g){
				for(int b=0; b<nBinses[pScaleIndex][2]; ++b){
					double val = getNormalisedValue(pScaleIndex,r,g,b) ;
					if( val >= pThreshold ){
						String sVal = padded(val,10).substring(0,10) ;
						sb.append(padded(r,3)).append(',') ;
						sb.append(padded(g,3)).append(',') ;
						sb.append(padded(b,3)).append(" :") ;
						sb.append(val).append('\r') ;
					}
				}
			}
		}				
		return sb.toString();
	}
	
	
	static String padded(Number n,int places){
		// TODO: Put this into a UTilities class if keep:
		String s = String.valueOf(n) ;
		while(s.length()<places){
			s = " "+ s ;
		}
		return s ;
	}

	public String getRawString(int pScaleIndex){
		StringBuilder sb = new StringBuilder() ;
		for(int r=0; r<nBinses[pScaleIndex][0]; ++r){
			for(int g=0; g<nBinses[pScaleIndex][1]; ++g){
				for(int b=0; b<nBinses[pScaleIndex][2]; ++b){
					sb.append(r).append(',').append(g).append(',').append(b).append(": ");
					// Do the lookup here, as opposed to putting it into a variable (more debugging-friendly),
					// so that we don't have to care about the type of the value (int,double,...)
					sb.append(datas[pScaleIndex][r][g][b]).append('\n') ;
				}
			}
		}
		
		
		return sb.toString();
	}	
	
	
}


