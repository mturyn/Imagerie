package com.mturyn.imageHistComparer;


public class Utilities {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";

	public static enum HistogramScale {
		COARSE,FINE
	}
	
	public static enum HistogramType {
		RAW,NORMALISED,FREQUENCIES,ENTROPIES
	}
	
	public static enum HistogramDistanceMethod {
		MATCHES,VECTOR_DISTANCE,VECTOR_EUCLIDEAN_DISTANCE,ANGLE,ONE_MINUS_COSINE
	}
		
	public static enum PixelRep {
		RGB,HSV,YUV
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
