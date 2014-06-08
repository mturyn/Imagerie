package com.pki.test.imageHistComparer;


public class Utilities {

	public enum HistogramScale {
		COARSE,FINE
	}
	
	public enum HistogramType {
		RAW,NORMALISED,FREQUENCIES,ENTROPIES
	}	

	public final static int RAD_TO_INTEGRAL_DEGREES(double pVal){
		return (int) Math.round(Math.toDegrees(pVal)) ; 
	}
	
	final static double LN2 = Math.log(2) ;
	public final static double LN2(double pVal) {
		return Math.log(pVal)/LN2 ;
	};

	public static String PADDED(Number n,int places){
		String s = String.valueOf(n) ;
		while(s.length()<places){
			s = " "+ s ;
		}
		return s ;
	}
	
	
}
