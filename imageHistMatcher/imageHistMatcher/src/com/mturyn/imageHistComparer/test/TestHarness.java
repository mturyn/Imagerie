package com.mturyn.imageHistComparer.test;

import static com.mturyn.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.FINE;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.ENTROPIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.FREQUENCIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.NORMALISED;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.RAW;

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

// Test to write soon will want these, so leave alone:
import com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod ;
import com.mturyn.imageHistComparer.IImageCharacteriser ;

import com.mturyn.imageHistComparer.histogram.RGBPixelHist ;
import com.mturyn.imageHistComparer.histogram.YUVPixelHist ;


//public class TestHarness<T extends RGBImageCharacteriser> extends Component {
public class TestHarness<T extends IHistogram> /*extends Component*/ {
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
		if(argv[argsOffsetPostParsing].equals("--representation") && argv[argsOffsetPostParsing+1].equals("YUV")){
			histInstance = new YUVPixelHist();
			// '+=' instead of assigning '2' because that would
			// work for other args that might need parsing:
			argsOffsetPostParsing += 2 ;
		}
		
		
		
		int nImages = Math.min(6, argv.length - argsOffsetPostParsing );
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
		

		// Testing method much like this distance-matrix we'd want to score against 
		// human evaluations of difference:
		for (int j = 0; j < nImages; ++j) {
			System.out.println(labels[j]+':') ;			
			for(HistogramType typ: OUR_TYPES){	
				for (HistogramScale scale : OUR_SCALES) {
					IndexedValue[] analysis 
						= instances[j].findIndicesOfMostAndLeastSimilarAtScale(instances,scale,typ) ;
					System.out.println("\t"+scale+" / "+typ  ) ;
					System.out.println("\t\tMin: " +labels[ analysis[0].nIndex]+": "+ (int)(analysis[0].dValue) ) ;	
					System.out.println("\t\tMax: " +labels[ analysis[1].nIndex]+": "+ (int)(analysis[1].dValue) ) ;
					System.out.println("") ;
				}
			}
			System.out.println("") ;
	}


			
		
		
		
		
		
}
	
	
	
	
	
		
	}



