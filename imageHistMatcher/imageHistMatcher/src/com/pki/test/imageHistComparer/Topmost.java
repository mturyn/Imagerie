package com.pki.test.imageHistComparer;

import static com.pki.test.imageHistComparer.Utilities.HistogramScale.COARSE;
import static com.pki.test.imageHistComparer.Utilities.HistogramScale.FINE;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.ENTROPIES;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.FREQUENCIES;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.NORMALISED;
import static com.pki.test.imageHistComparer.Utilities.HistogramType.RAW;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pki.test.imageHistComparer.Utilities.HistogramScale;
import com.pki.test.imageHistComparer.Utilities.HistogramType;
import com.pki.test.imageHistComparer.histogram.ImageCharacteriser;


public class Topmost extends Component {
	private static final long serialVersionUID = "com.pki.test.imageHistComparer".hashCode();
	

	
	public Topmost(String pFileSpec) {

		try {
			img = ImageIO.read(new File(pFileSpec));
		} catch (IOException ioe) {
			System.err.println("Error i/o: " + ioe);
		}
		characteriser = new ImageCharacteriser(img);
		fileName = pFileSpec;
		System.err.println(fileName);
	}	

	BufferedImage img;

	String fileName;

	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {
			System.err.println("WxH:" + img.getWidth(null) + ','
					+ img.getHeight(null));
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}
	
	public static HistogramScale[] OUR_SCALES = {COARSE,FINE}  ;
	public static HistogramType[] OUR_TYPES = {RAW,NORMALISED,FREQUENCIES,ENTROPIES}  ;	

	ImageCharacteriser characteriser ;
	
	public IndexedValue[] findIndicesOfMostAndLeastSimilarAtScale(
			Topmost[] pTopmosts,
			HistogramScale pScale,
			HistogramType pType
			){
		ImageCharacteriser[] characterisers = new ImageCharacteriser[pTopmosts.length];
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
		 * f.add(new Topmost()); f.pack(); f.setVisible(true);
		 */

		/*
		 * System.out.println(hist.getNormalisedString(0,100,1)) ;
		 * System.out.println("");
		 * System.out.println(hist.getNormalisedString(1,100,1)) ;
		 */
		int nImages = Math.min(6, argv.length);
		Topmost[] instances = new Topmost[nImages];
		String[] labels = new String[nImages];

		for (int j = 0; j < nImages; ++j) {
			instances[j] = new Topmost(argv[j]);
			labels[j] = argv[j].substring(1 + argv[j].lastIndexOf('/'));
			System.out.println("Image " + j + "<--" + argv[j]);
		}
		System.out.println('\r');

		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;
		String minLabel = "Bad initial String instance.";
		String maxLabel = "Bad initial String instance.";

		// In a real system this would turn into part of a unit-test:
		System.out.println("\rSelf-distance:");
		for (com.pki.test.imageHistComparer.Utilities.HistogramScale scale : OUR_SCALES) {
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
				System.err.println(minLabel + '\r' + maxLabel + '\r');
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
				System.err.println(minLabel + '\r' + maxLabel + '\r');
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
				System.err.println(minLabel + '\r' + maxLabel + '\r');
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
			System.err.println(minLabel + '\r' + maxLabel + '\r');
		}

		System.err.println('\r') ;
		// Testing method much like what we'll want soon:

		for (int j = 0; j < nImages; ++j) {		
			for(HistogramType typ: OUR_TYPES){	
				for (HistogramScale scale : OUR_SCALES) {


					System.err.println(labels[j]+':') ;
					IndexedValue[] analysis 
						= instances[j].findIndicesOfMostAndLeastSimilarAtScale(instances,scale,typ) ;
					System.err.println("\t"+scale+" / "+typ  ) ;
					System.err.println("\t\tMin: " +labels[ analysis[0].nIndex]+": "+ analysis[0].dValue) ;	
					System.err.println("\t\tMax: " +labels[ analysis[1].nIndex]+": "+ analysis[1].dValue) ;
					System.err.println("") ;
				}
			}
			System.err.println("") ;
		}
		
	}








}
