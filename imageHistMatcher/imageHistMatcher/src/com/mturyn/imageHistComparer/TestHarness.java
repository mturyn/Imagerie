package com.mturyn.imageHistComparer;

import static com.mturyn.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.mturyn.imageHistComparer.Utilities.HistogramScale.FINE;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.ENTROPIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.FREQUENCIES;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.NORMALISED;
import static com.mturyn.imageHistComparer.Utilities.HistogramType.RAW;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;
import com.mturyn.imageHistComparer.IImageCharacteriser ;
// import com.mturyn.imageHistComparer.histogram.RGBImageCharacteriser;
// import com.mturyn.imageHistComparer.histogram.YUVImageCharacteriser;
import com.mturyn.imageHistComparer.histogram.ImageCharacteriser;
import com.mturyn.imageHistComparer.histogram.RGBPixelHist ;
import com.mturyn.imageHistComparer.histogram.YUVPixelHist ;


//public class TestHarness<T extends RGBImageCharacteriser> extends Component {
public class TestHarness<T extends IHistogram> extends Component {
	private static final long serialVersionUID = "com.mturyn.imageHistComparer".hashCode();
	
	ImageCharacteriser<T> characteriser ;
	
	public TestHarness(String pFileSpec,T pHistInstance) {

		try {
			img = ImageIO.read(new File(pFileSpec));
		} catch (IOException ioe) {
			System.err.println("Error i/o: " + ioe+": reading"+pFileSpec);
		}
		//characteriser = pBlankCharacteriser ; 
		characteriser = new ImageCharacteriser<T>(pHistInstance) ;
		characteriser.populateFromBufferedImage( img ) ;
		
		fileName = pFileSpec;
		System.out/*err*/.println(fileName);
	}	

	BufferedImage img;

	String fileName;

	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {
			System.out/*err*/.println("WxH:" + img.getWidth(null) + ','
					+ img.getHeight(null));
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}
	
	public static HistogramScale[] OUR_SCALES = {COARSE,FINE}  ;
	public static HistogramType[] OUR_TYPES = {RAW,NORMALISED,FREQUENCIES,ENTROPIES}  ;	

	
	public IndexedValue[] findIndicesOfMostAndLeastSimilarAtScale(
			TestHarness[] pTopmosts,
			HistogramScale pScale,
			HistogramType pType
			){
		IImageCharacteriser[] characterisers = new IImageCharacteriser[pTopmosts.length];
		for(int i=0;i<characterisers.length;++i){
			characterisers[i] = pTopmosts[i].characteriser ;  
		}
		return this.characteriser.findIndicesOfMostAndLeastSimilarAtScale(characterisers,pScale,pType) ;
	}	

	
	/**
	 * @param args
	 */
	public static void main(String[] argv) {
		// TODO Auto-generated method stub

		/*
		 * JFrame f = new JFrame("Load Image Sample"); f.addWindowListener(new
		 * WindowAdapter(){ public void windowClosing(WindowEvent e) {
		 * System.exit(0); } });
		 * 
		 * f.add(new TestHarness()); f.pack(); f.setVisible(true);
		 */

		/*
		 * System.out.println(hist.getNormalisedString(0,100,1)) ;
		 * System.out.println("");
		 * System.out.println(hist.getNormalisedString(1,100,1)) ;
		 */
		int nImages = Math.min(6, argv.length);
		TestHarness[] instances = new TestHarness[nImages];
		String[] labels = new String[nImages];

		for (int j = 0; j < nImages; ++j) {
			instances[j] = new TestHarness<YUVPixelHist>(argv[j],new YUVPixelHist());
			//instances[j] = new TestHarness<YUVPixelHist>(argv[j],new YUVPixelHist());
			labels[j] = argv[j].substring(1 + argv[j].lastIndexOf('/'));
			System.out.println("Image " + j + "<--" + argv[j]);
		}
		System.out.println('\r');
/* <OLDER_TESTS>
		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;
		String minLabel = "Bad initial String instance.";
		String maxLabel = "Bad initial String instance.";

		// In a real system this would turn into part of a unit-test:
		System.out.println("\rSelf-distance:");
		for (com.mturyn.imageHistComparer.Utilities.HistogramScale scale : OUR_SCALES) {
			System.out.println("\tScale: " + scale);
			// for(HistogramType typ: OUR_TYPES){
			HistogramType typ = NORMALISED;
			System.out.println("\tType: " + typ);
			for (int j = 0; j < nImages; ++j) {
				System.out.println("\t\tSelf-distance: image "
						+ j
						+ ": "
						+ instances[j].characteriser.distanceAtScaleOfType(
								instances[j].characteriser, scale, typ));
			}
		}
		System.out.println('\r');

		System.out.println("Length:");
		for (HistogramScale scale : OUR_SCALES) {
			System.out.println("\tScale: " + scale);
			// for(HistogramType typ: OUR_TYPES){
			HistogramType typ = NORMALISED;
			System.out.println("\tType: " + typ);
			for (int j = 0; j < nImages; ++j) {
				System.out.println("\t\tLength: image "
						+ j
						+ ": "
						+ instances[j].characteriser.lengthAtScaleOfType(scale,
								typ));
			}
			// }
		}
		System.out.println('\r');

		// In a real system this might be part of a learning loop to
		// see which distances or combinations of them best characterise
		// 'nearest' and 'furthest' images:
		System.out.println("Distances:");
		for (HistogramScale scale : OUR_SCALES) {
			System.out.println("\tScale: " + scale);
			for (HistogramType typ : OUR_TYPES) {
				// HistogramType typ = NORMALISED ;
				minValue = Double.MAX_VALUE;
				maxValue = Double.MIN_VALUE;
				System.out.println("\tType: " + typ);
				for (int j = 0; j < nImages; ++j) {
					for (int k = 0; k < nImages; ++k) {
						double val = 
								instances[j].characteriser.distanceAtScaleOfType(
										instances[k].characteriser, 
										scale, 
										typ
										);
						System.out.println("\t\tDistance " + j + '-' 
								+ k + ": "	+ val
								);
						if ((j != k) && (val < minValue)) {
							minValue = val;
							minLabel = "Distance: " +  scale + "  /  " +typ + ": "  
									+ "Min:  " + labels[j] + " vs "
									+ labels[k] +": "+val ;
						}
						if ((j != k) && (val > maxValue)) {
							maxValue = val;
							maxLabel = "Distance: " +  scale + "  /  " +typ + ": "
									+ "Max:  " + labels[j] + " vs "
									+ labels[k] +": "+val ;
						}
					}
				}
				System.out.println(minLabel + '\r' + maxLabel + '\r');
			}
		}

		// In a real system this might be part of a learning loop to
		// see which distances or combinations of them best characterise
		// 'nearest' and 'furthest' images:
		System.out.println("Cosines:");
		for (HistogramScale scale : OUR_SCALES) {
			System.out.println("\tScale: " + scale);
			for (HistogramType typ : OUR_TYPES) {
				// HistogramType typ = NORMALISED ;
				minValue = Double.MAX_VALUE;
				maxValue = Double.MIN_VALUE;
				System.out.println("\tType: " + typ);
				for (int j = 0; j < nImages; ++j) {
					for (int k = 0; k < nImages; ++k) {
						double val = instances[j].characteriser
								.cosineAtScaleOfType(
										instances[k].characteriser, scale, typ);
						System.out.println("\t\tcosine " + j + "-" + k + ": "
								+ val);
						if ((j != k) && (val < minValue)) {
							minValue = val;
							minLabel = "Cosines: " +  scale + " / " +typ + ": " 
									+ "Min:  " + labels[j] + " vs " + labels[k] +": "+val ;
						}
						if ((j != k) && (val > maxValue)) {
							maxValue = val;
							maxLabel = "Cosines: " +  scale + " / " +typ + ": " 
									+ "Max:  " + labels[j] + " vs " + labels[k] +": "+val ;
						}
					}
				}
				//.err for red in Eclipse console window:
				System.out.println(minLabel + '\r' + maxLabel + '\r');
			}
			// }
		}

		// In a real system this might be part of a learning loop to
		// see which distances or combinations of them best characterise
		// 'nearest' and 'furthest' images:
		System.out.println("Angles:");
		for (HistogramScale scale : OUR_SCALES) {
			System.out.println("\tScale: " + scale);
			// for(HistogramType typ: OUR_TYPES){
			HistogramType typ = NORMALISED;
			minValue = Double.MAX_VALUE;
			maxValue = Double.MIN_VALUE;
			System.out.println("\tType: " + typ);
			for (int j = 0; j < nImages; ++j) {
				for (int k = 0; k < nImages; ++k) {
					double val = instances[j].characteriser
							.angleDegreesAtScaleOfType(
									instances[k].characteriser, scale, typ);
					System.out.println("\t\tangle " + j + '-' + k + ": " + val);
					if ((j != k) && (val < minValue)) {
						minValue = val;
						minLabel = "Angle: " +  scale + " / " +typ + ": " 
								+ "Min:  " + labels[j] + " vs " + labels[k] +": "+val ;
					}
					if ((j != k) && (val > maxValue)) {
						maxValue = val;
						maxLabel = "Angle: " +  scale + " / " +typ + ": " 
								+ "Max:  " + labels[j] + " vs " + labels[k] +": "+val ;
					}
				}
				System.out.println(minLabel + '\r' + maxLabel + '\r');
			}
			// }

		}
		System.out.println('\r');

		System.out.println("Entropies:");
		for(HistogramScale scale: OUR_SCALES){
			for (int j = 0; j < nImages; ++j) {
				System.out.println("\tEntropy: image " + j + ": scale: "+scale+":  "
						+ instances[j].characteriser.getEntropiesString(scale));
			}
		}
		System.out.println('\r');

		// In a real system this might be part of a learning loop to
		// see which distances or combinations of them best characterise
		// 'nearest' and 'furthest' images:
		System.out.println("Cosines:");
		System.out.println(" (entropies)");
		for (HistogramScale scale : OUR_SCALES) {
			System.out.println("\tScale: " + scale);
			// for(HistogramType typ: OUR_TYPES){
			HistogramType typ = ENTROPIES;
			minValue = Double.MAX_VALUE;
			maxValue = Double.MIN_VALUE;
			System.out.println("\tType: " + typ);
			for (int j = 0; j < nImages; ++j) {
				for (int k = 0; k < nImages; ++k) {
					double val = instances[j].characteriser
							.cosineAtScaleOfType(instances[k].characteriser,
									scale, typ);
					System.out
					.println("\t\tcosine " + j + '-' + k + ": " + val);
					if ((j != k) && (val < minValue)) {
						minValue = val;
						minLabel = "Cosine: " +  scale + " / " +typ + ": " 
								+ "Min:  " + labels[j] + " vs " + labels[k] +": "+val ;
					}
					if ((j != k) && (val > maxValue)) {
						maxValue = val;
						maxLabel = "Cosine: " +  scale + " / " +typ + ": " 
								+ "Max:  " + labels[j] + " vs " + labels[k] +": "+val ;
					}
				}
				System.out.println(minLabel + '\r' + maxLabel + '\r');
			}
			// }
		}
		System.out.println('\r');

		// In a real system this might be part of a learning loop to
		// see which distances or combinations of them best characterise
		// 'nearest' and 'furthest' images:
		System.out.println("Angles:");
		System.out.println(" (entropies)");
		for (HistogramScale scale : OUR_SCALES) {
			System.out.println("\tScale: " + scale);
			// for(HistogramType typ: OUR_TYPES){
			HistogramType typ = ENTROPIES;
			minValue = Double.MAX_VALUE;
			maxValue = Double.MIN_VALUE;
			System.out.println("\tType: " + typ);
			for (int j = 0; j < nImages; ++j) {
				for (int k = 0; k < nImages; ++k) {
					double val = instances[j].characteriser
							.angleDegreesAtScaleOfType(
									instances[k].characteriser, scale, typ);
					System.out.println("\t\tangle " + j + '-' + k + ": " + val);
					if ((j != k) && (val < minValue)) {
						minValue = val;
						minLabel = "Angle: " +  scale + " / " +typ + ": " 
								+ "Min:  " + labels[j] + " vs " + labels[k] +": "+val ;
					}
					if ((j != k) && (val > maxValue)) {
						maxValue = val;
						maxLabel = "Angle: " +  scale + " / " +typ + ": " 
								+ "Max:  " + labels[j] + " vs " + labels[k] +": "+val ;
					}
				}
			}
			// }
			System.out.println(minLabel + '\r' + maxLabel + '\r');
		}

		System.out.println('\r') ;

</OLDER_TESTS> 
	*/			

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








}
