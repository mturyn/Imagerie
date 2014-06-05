package com.pki.test.imageHistComparer.histogram;


public class RGBPixelRangeBin {

	// Create with bad default values for better debugging:
	int nCount = -1 ;
	double dNormalisedCount = -1d ;
	
	void increment(){
		// prefix leaves stack alone:
		++nCount ;
	}

	void normaliseBy(double pFactor){
		// Would auto-cast, but I'd rather be explicit:
		dNormalisedCount = ((double)nCount)/pFactor ;
	}

}
