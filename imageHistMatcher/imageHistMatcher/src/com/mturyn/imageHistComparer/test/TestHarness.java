package com.mturyn.imageHistComparer.test;

import static com.mturyn.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.FINE;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.ENTROPIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.FREQUENCIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.NORMALISED;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.RAW;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod ;

import java.util.Map ;
import java.util.HashMap ;
import java.util.ArrayList ;

// We will probably want to display images at some point....
/*
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
*/

import com.mturyn.imageHistComparer.AnalysedImage ;
import com.mturyn.imageHistComparer.IHistogram;
import com.mturyn.imageHistComparer.IndexedValue;


import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;


import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.MATCHES ;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.VECTOR_DISTANCE ;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.VECTOR_EUCLIDEAN_DISTANCE ;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.ANGLE ;
import static com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod.ONE_MINUS_COSINE ;

// Test to write soon will want these, so leave alone:

import com.mturyn.imageHistComparer.IImageCharacteriser ;

import com.mturyn.imageHistComparer.histogram.RGBPixelHist ;
import com.mturyn.imageHistComparer.histogram.YUVPixelHist ;


//public class TestHarness<T extends RGBImageCharacteriser> extends Component {
public class TestHarness<T extends IHistogram> /*extends Component*/ {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";
	private static final long serialVersionUID = "com.mturyn.imageHistComparer".hashCode();
	
	
	public static HistogramScale[] OUR_SCALES = {COARSE,FINE}  ;
	public static HistogramType[] OUR_TYPES = {RAW,NORMALISED,FREQUENCIES,ENTROPIES}  ;		
	
	AnalysedImage<T> characteriser ;
	
	public TestHarness(String pFileSpec,T pHistInstance) {} ;
	
	/**
	 * @param args
	 */
	public static void main(String[] argv) {
		/* Display code:
		 * JFrame f = new JFrame("Load Image Sample"); f.addWindowListener(new
		 * WindowAdapter(){ public void windowClosing(WindowEvent e) {
		 * System.exit(0); } });
		 * 
		 * f.add(new TestHarness()); f.pack(); f.setVisible(true);
		 */

		int argsOffsetPostParsing =  0 ;
		// Switch this to an enum once it works...but then again, it's going to come in as a string,
		// so really a final String instance, perhaps in a resource file, would be best:
		IHistogram histInstance = new RGBPixelHist();
		// starting here at argsOffsetPostParsing==0 (now) so that
		// we can fold this into a longer parser, where that variable would become
		// in essence a cursor in the args list:
		if(argv[argsOffsetPostParsing].equals("--representation") && argv[argsOffsetPostParsing+1].equals("YUV")){
			histInstance = new YUVPixelHist();
			// '+=' instead of assigning '2' because that would
			// work for other args that might need parsing:
			argsOffsetPostParsing += 2 ;
		}
		
		
		
		int nImages = Math.min(4, argv.length - argsOffsetPostParsing );
		// Using unchecked type here, not really a problem...
		AnalysedImage[] instances = new AnalysedImage[nImages];
		String[] labels = new String[nImages];

		for (int j = 0; j < nImages; ++j) {
			int offsetJ = j + argsOffsetPostParsing ;
			instances[j] = new AnalysedImage(argv[offsetJ],histInstance) ;
			//instances[j] = new AnalysedImage<YUVPixelHist>(argv[j],T.getInstance());
			//instances[j] = new TestHarness<RGBPixelHist>(argv[j],new RGBPixelHist());
			labels[j] = argv[offsetJ].substring(1 + argv[offsetJ].lastIndexOf('/'));
			System.out.println("Image " + j + "<--" + argv[offsetJ]);
		}
		System.out.println('\r');
		
		HistogramDistanceMethod[] meths = {VECTOR_DISTANCE,ONE_MINUS_COSINE,MATCHES,VECTOR_EUCLIDEAN_DISTANCE} ;
		
		
		// Scoring the methods:		
		Map<HistogramDistanceMethod,Integer> methodScoresMapCoarse = new HashMap<HistogramDistanceMethod,Integer>() ;
		Map<HistogramDistanceMethod,Integer> methodScoresMapFine = new HashMap<HistogramDistanceMethod,Integer>() ;

		for(HistogramDistanceMethod meth:meths){
			methodScoresMapCoarse.put(meth,0) ;
			methodScoresMapFine.put(meth,0) ;
		}
		
		// This is a first test of scoring the methods, 
		// and I'm not of a mood to add more arguments to parse at the moment:
		HashMap<String,HashMap<String,Integer>> closestMap = new HashMap<String,HashMap<String,Integer>>() ;
	
		String[] rembrandts = {"rembrandt126.JPG","joodsebruidje.jpeg"} ;
		String[] picassos =  {"Old_guitarist_chicago.jpg","picasso-0.jpg","picasso-1.jpg"} ;
		String[] lychees = {"lychees-and-lasers_0.jpg","lychees-and-lasers_1.jpg"} ; 
		
		//lychees: --like each other, a little like rembrandts, not much like picassos:
		HashMap<String,Integer> scoresL = new HashMap<String,Integer>() ;
		
		
		//
		for(String p:picassos){
			scoresL.put(p,-1) ;
		}
		for(String r:rembrandts){
			scoresL.put(r,1) ;
		}
		for(String l:lychees){
			scoresL.put(l,2) ;
		}
		for(String l:lychees){
			closestMap.put(l,scoresL) ;
		}
		
		
		
		//picassos: --like each other, not much like lychees, not much like rembrandts:
		HashMap<String,Integer> scoresP = new HashMap<String,Integer>() ;
		//
		for(String p:picassos){
			scoresP.put(p,2) ;
		}
		for(String r:rembrandts){
			scoresP.put(r,-2) ;
		}
		for(String l:lychees){
			scoresP.put(l,-1) ;
		}
		for(String p:picassos){
			closestMap.put(p,scoresP) ;
		}
		
		//rembrandts: --like each other, a little like lychees, not much like picassos:
		HashMap<String,Integer> scoresR = new HashMap<String,Integer>() ;
		//
		for(String p:picassos){
			scoresR.put(p,-2) ;
		}
		for(String r:rembrandts){
			scoresR.put(r,2) ;
		}
		for(String l:lychees){
			scoresR.put(l,1) ;
		}
		for(String r:rembrandts){
			closestMap.put(r,scoresR) ;
		}
		
		
		// Testing method much like this distance-matrix we'd want to score against 
		// human evaluations of difference:
		StringBuilder sb = new StringBuilder() ;
		sb.append("\rvar result={") ;
		boolean firstImage = true ;
		boolean firstMethod = true;
		double totalPotentialMatches = 0 ;
		for (HistogramDistanceMethod method : meths ) {		
			for (int j = 0; j < nImages; ++j) {
				if (firstImage) {
					firstImage = false;
				} else {
					sb.append(',');
				}
				sb.append("\r\t{image:'").append(labels[j])
					.append("',\r\t\textrema:{");
				for (HistogramType typ : OUR_TYPES) {
					for (HistogramScale scale : OUR_SCALES) {						
						IndexedValue[] analysis = instances[j]
								.findIndicesOfMostAndLeastSimilarAtScaleOfType(
										instances, method, 0.00005d, scale, typ);
						++totalPotentialMatches ;
						String nearestImageName = labels[analysis[0].nIndex] ;
						String furthestImageName = labels[analysis[1].nIndex] ;
						
						// What do we think of the match
						int closeScore =  (closestMap.get(labels[j])).get(nearestImageName) ;
						// We're taking score(farthest) = -1 score(nearest)
						int farScore =    -1*(closestMap.get(labels[j])).get(furthestImageName) ;
						
						// TODO: avoid this if/else, map these...but I'm making maps too many
						// levels deep, better later to create classes that hide that:
						if( scale==HistogramScale.COARSE){
							methodScoresMapCoarse.put(method, methodScoresMapCoarse.get(method) +closeScore+farScore) ;
						} else {
							methodScoresMapFine.put(method, methodScoresMapFine.get(method) +closeScore+farScore) ;							
						}
						
						
						if (firstMethod) {
							firstMethod = false;
						} else {
							sb.append(',');
						}
						sb.append("\r\t\t\t\t");
						sb.append("{method:'").append(method)
								.append("',scale:'").append(scale)
								.append("',type:'").append(typ).append("',");
						// Redundant, but helpful when looking at each individual extrema-set,
						// so that in looking at a pretty-print
						// you can see the target file and its closest and furthest
						// near each other:
						sb.append("\r\t\t\t\t\timage:'").append(labels[j]).append("',") ;
						sb.append("\r\t\t\t\t\tclosestImage:'")
								.append(nearestImageName)
								.append("',minDistance:")
								.append((int) (analysis[0].dValue)).append(",");
						sb.append("\r\t\t\t\t\tfurthestImage:'")
								.append(furthestImageName)
								.append("',maxDistance:")
								.append((int) (analysis[1].dValue));
						sb.append("\r\t\t\t\t}");
					}
				}	
			}
			sb.append("\r\t\t\t}\r\t\t}") ;		
		}

		sb.append("\r\t}") ;
		System.out.println("Web service version of this test might return:\r") ;

		System.out.println( sb ) ;
			
		System.out.println("How well did each of the distance methods match (as percentage of perfect score)?") ;

		System.out.println("COARSE:") ;
		for(HistogramDistanceMethod m:methodScoresMapCoarse.keySet()){
			// So far, amx possible score for any trial is 2.0:
			System.out.println("\t"+m+":\t"
						+Math.round(100.0d*methodScoresMapCoarse.get(m))/(2.0*totalPotentialMatches)) ;
		}
		
		System.out.println("FINE:") ;
		for(HistogramDistanceMethod m:methodScoresMapFine.keySet()){
			// So far, amx possible score for any trial is 2.0:
			System.out.println("\t"+m+":\t"
						+Math.round(100.0d*methodScoresMapFine.get(m))/(2.0*totalPotentialMatches)) ;
		}
		
}
	
	
	
	
	
		
	}



