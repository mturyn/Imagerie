package com.pki.test.imageHistComparer.histogram;



//I want to use an enum, since an integer is too specific and not descriptive enough,
//but I don't want to index by arbitrary String instances:

/**
 * @author mturyn
 * 
 */
public class RGBPixelsBin implements Comparable {

	// 'Bindex'=='bin index':
	public int rBindex=-1 ;
	public int gBindex=-1 ;
	public int bBindex=-1 ;
	
	double value=-1d ;
	
	public RGBPixelsBin(int pRBin,int pGBin,int pBBin,double pValue){
		rBindex = pRBin ;
		gBindex = pGBin ;
		bBindex = pBBin ;
		value = pValue ;
	}
	
	@Override
	public int compareTo(Object pO) {
		int result = 0 ;
		// Live dangerously for the demo, at least:
		RGBPixelsBin bin = (RGBPixelsBin) pO ;
		// Want descending order, by default:
		if(this.value < bin.value){
			result = 1 ;
		} else if(this.value > bin.value){
			result = -1 ;
		} else {
			result = 0 ;
		}
		return result ;
	}
	
	
	public boolean indicesMatch(RGBPixelsBin pBin) {
		return (
				(rBindex == pBin.rBindex) 
					&& (gBindex == pBin.gBindex) 
					&& (bBindex == pBin.bBindex) 				
				) ;
	}	
	
	public boolean equals(Object pO) {
		// Living dangerously again, so that 
		// many, many, repeats of this operation don't
		// take even longer than they might:
		RGBPixelsBin pBin = (RGBPixelsBin) pO ;
		return (
				indicesMatch(pBin)
				&& this.value == pBin.value
				) ;
	}		
	
	
	// Remember, these are bins, not pixels,
	// and we're not going above 4 of them per channel yet:
	public char character(){
		short val = 0 ;
		return (char)(val + (short)(rBindex<<4)+(short)(gBindex<<2) + (short)bBindex ) ; 
	}
	
}


