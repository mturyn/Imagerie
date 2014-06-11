package com.mturyn.imageHistComparer.histogram.comparators;

import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.ANGLE;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.MATCHES;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.ONE_MINUS_COSINE;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.VECTOR_DISTANCE;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.VECTOR_EUCLIDEAN_DISTANCE;

import java.util.HashMap;
import java.util.Map;

import com.mturyn.imageHistComparer.IHistCharacteriserDistance;
import com.mturyn.imageHistComparer.IHistogram;
import com.mturyn.imageHistComparer.IImageCharacteriser;
import com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;


// MATCHES,ENTROPY,ANGLE,ONE_MINUS_COSINE,FREQUENCY

public abstract class AbstractDistance implements IHistCharacteriserDistance {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";
	
	public AbstractDistance(){} ;

	public double compare(IImageCharacteriser p0,IImageCharacteriser p1, HistogramScale pScale, HistogramType pType,double dFractionalWindow) {
		IHistogram h0 = p0.getHistAtScaleOfType(pScale, pType) ;
		IHistogram h1 = p1.getHistAtScaleOfType(pScale, pType) ;
		return this.getCharacteristicDistance(h0,h1,dFractionalWindow) ;
	}

	public abstract double getCharacteristicDistance(IHistogram h0,IHistogram h1,double dFractionalWindow) ;
	
	public static Map<HistogramDistanceMethod, IHistCharacteriserDistance> 
		DISTANCE_METHOD_MAP =
			new HashMap<HistogramDistanceMethod, IHistCharacteriserDistance>() ;
	static {
		DISTANCE_METHOD_MAP.put( MATCHES, MatchesCountDistance.getInstance() ) ;
		DISTANCE_METHOD_MAP.put( ANGLE, Angle.getInstance() ) ;
		DISTANCE_METHOD_MAP.put( ONE_MINUS_COSINE, OneMinusCosineDistance.getInstance() ) ;
		DISTANCE_METHOD_MAP.put( VECTOR_DISTANCE, VectorDistance.getInstance() ) ;
		DISTANCE_METHOD_MAP.put( VECTOR_EUCLIDEAN_DISTANCE, VectorEuclideanDistance.getInstance() ) ;
	}
	
	public static IHistCharacteriserDistance getDistancerForName(HistogramDistanceMethod pName ){
		return DISTANCE_METHOD_MAP.get( pName ) ;
	}

}
