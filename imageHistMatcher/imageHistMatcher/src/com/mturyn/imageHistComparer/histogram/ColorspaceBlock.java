package com.mturyn.imageHistComparer.histogram;


/**
 * This class exists to simplify ordering rectangular solids in colorspace by their
 * value (population, their normalised frequencies (fractional population),
 * their entropies,....
 * 
 * TODO: Extend this to blocks corresponding to more than one 3-channel bin (give this 
 * an extent, as opposed to a default length of 1 in {bin-index}-space).
 * 
 * @author mturyn
 * 
 */
public class ColorspaceBlock implements Comparable<ColorspaceBlock> {

	// 'Bindex'=='bin index':
	public int[] binIndices ={-1,-1,-1} ;
	
	double value=-1d ;
	
	public ColorspaceBlock(int pBinChannel0,int pBinChannel1,int pBinChannel2,double pValue){
		binIndices[0] = pBinChannel0 ;
		binIndices[1] = pBinChannel1 ;
		binIndices[2] = pBinChannel2 ;
		value = pValue ;
	}
	

	public int compareTo(ColorspaceBlock pOther) {
		int result = -30 ;
		// Want descending order, by default:
		if(this.value < pOther.value){
			result = 1 ;
		} else if(this.value > pOther.value){
			result = -1 ;
		} else {
			result = 0 ;
		}
		return result ;
	}
	
	
	public boolean indicesMatch(ColorspaceBlock pBin) {
		return (
				( binIndices[0] == pBin.binIndices[0]) 
					&& (binIndices[1] == pBin.binIndices[1]) 
					&& (binIndices[2] == pBin.binIndices[2]) 				
				) ;
	}	
	
	public boolean equals(Object pO) {
		// Living dangerously again, so that 
		// many, many, repeats of this operation don't
		// take even longer than they might:
		ColorspaceBlock pBin = (ColorspaceBlock) pO ;
		return (
				indicesMatch(pBin)
				&& this.value == pBin.value
				) ;
	}		
	


	
}


