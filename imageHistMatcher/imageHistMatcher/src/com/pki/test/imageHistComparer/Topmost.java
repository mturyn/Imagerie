package com.pki.test.imageHistComparer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


//I want to use an enum, since an integer is too specific and not descriptive enough,
//but I don't want to index by arbitrary String instances:
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale.COARSE ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale.FINE ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.RAW;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.NORMALISED ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.FREQUENCIES ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType.ENTROPIES ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramType ;
import static com.pki.test.imageHistComparer.histogram.Utilities.HistogramScale ;


import com.pki.test.imageHistComparer.histogram.ImageCharacteriser ;

import static com.pki.test.imageHistComparer.histogram.ImageCharacteriser.OUR_SCALES ;
import static com.pki.test.imageHistComparer.histogram.ImageCharacteriser.OUR_TYPES ;

public class Topmost extends Component { 
	/**
	 * 
	 */
	private static final long serialVersionUID = "com.pki.test.imageHistComparer".hashCode();
	BufferedImage img;

	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {
			System.err.println("WxH:" + img.getWidth(null) + ','
					+ img.getHeight(null));
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}

	ImageCharacteriser characteriser;

	public Topmost() {
		try {
			img = ImageIO.read(new File("c:/lychees-and-lasers_0.jpg"));
		} catch (IOException ioe) {
			System.err.println("Error i/o: " + ioe);
		}

		characteriser = new ImageCharacteriser(this.img);

	}
	
	public Topmost(String pFileSpec) {
		try {
			img = ImageIO.read(new File(pFileSpec));
		} catch (IOException ioe) {
			System.err.println("Error i/o: " + ioe);
		}
		characteriser = new ImageCharacteriser(img) ;
	}	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		 * JFrame f = new JFrame("Load Image Sample"); f.addWindowListener(new
		 * WindowAdapter(){ public void windowClosing(WindowEvent e) {
		 * System.exit(0); } });
		 * 
		 * f.add(new Topmost()); f.pack(); f.setVisible(true);
		 */

		/*
		System.out.println(hist.getNormalisedString(0,100,1)) ;
		System.out.println("");
		System.out.println(hist.getNormalisedString(1,100,1)) ;	
		*/
		
		Topmost instance_0 = new Topmost("c:/lychees-and-lasers_0.jpg");
		Topmost instance_1 = new Topmost("c:/lychees-and-lasers_1.jpg");
		Topmost instance_2 = new Topmost("c:/lychees-and-lasers_1-inverted.jpg");			
		
		
		// In a real system this would turn into part of a unit-test:
		System.out.println("Self-distance:\r") ;
		for(HistogramScale scale:OUR_SCALES){
			System.out.println("\tScale: "+scale) ;
			//for(HistogramType typ: OUR_TYPES){
				HistogramType typ = NORMALISED ;
				System.out.println("\tType: "+typ) ;
				System.out.println("\t\timage 0: "+ instance_0.characteriser.distanceAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\timage 1: "+ instance_1.characteriser.distanceAtScaleOfType(instance_1.characteriser,scale,typ)) ;
				System.out.println("\t\timage 2: "+ instance_2.characteriser.distanceAtScaleOfType(instance_2.characteriser,scale,typ)+'\r') ;
//}
		}			
		
		System.out.println("Length:\r") ;		
		for(HistogramScale scale:OUR_SCALES){
			System.out.println("\tScale: "+scale) ;
			//for(HistogramType typ: OUR_TYPES){
				HistogramType typ = NORMALISED ;
				System.out.println("\tType: "+typ) ;
				System.out.println("\t\timage 0: "+ instance_0.characteriser.lengthAtScaleOfType(scale,typ)) ;
				System.out.println("\t\timage 1: "+ instance_1.characteriser.lengthAtScaleOfType(scale,typ)) ;
				System.out.println("\t\timage 2: "+ instance_2.characteriser.lengthAtScaleOfType(scale,typ)+'\r') ;
			//}
		}				

		// In a real system this would turn into part of a unit-test:
		System.out.println("Distances:\r") ;
		for(HistogramScale scale:OUR_SCALES){
			System.out.println("\tScale: "+scale) ;
			//for(HistogramType typ: OUR_TYPES){
				HistogramType typ = NORMALISED ;
				System.out.println("\tType: "+typ) ;
				System.out.println("\t\tdistance 0-1: "+ instance_0.characteriser.distanceAtScaleOfType(instance_1.characteriser,scale,typ)) ;
				System.out.println("\t\tdistance 1-0: "+ instance_1.characteriser.distanceAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tdistance 2-0: "+ instance_2.characteriser.distanceAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tdistance 2-1: "+ instance_2.characteriser.distanceAtScaleOfType(instance_1.characteriser,scale,typ)+'\r') ;
			//}
		}			
		
		// In a real system this would turn into part of a unit-test:
		System.out.println("Cosines:\r") ;
		for(HistogramScale scale:OUR_SCALES){
			System.out.println("\tScale: "+scale) ;
			//for(HistogramType typ: OUR_TYPES){
				HistogramType typ = NORMALISED ;
				System.out.println("\tType: "+typ) ;
				System.out.println("\t\tcosine 0-1: "+ instance_0.characteriser.cosineAtScaleOfType(instance_1.characteriser,scale,typ)) ;
				System.out.println("\t\tcosine 1-0: "+ instance_1.characteriser.cosineAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tcosine 2-0: "+ instance_2.characteriser.cosineAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tcosine 2-1: "+ instance_2.characteriser.cosineAtScaleOfType(instance_1.characteriser,scale,typ)+'\r') ;
						//}
		}		

		// In a real system this would turn into part of a unit-test:
		System.out.println("Angles:\r") ;
		for(HistogramScale scale:OUR_SCALES){
			System.out.println("\tScale: "+scale) ;
			//for(HistogramType typ: OUR_TYPES){
				HistogramType typ = NORMALISED ;
				System.out.println("\tType: "+typ) ;
				System.out.println("\t\tangle 0-1: "+ instance_0.characteriser.angleDegreesAtScaleOfType(instance_1.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 0-0: "+ instance_0.characteriser.angleDegreesAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 1-1: "+ instance_1.characteriser.angleDegreesAtScaleOfType(instance_1.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 1-0: "+ instance_1.characteriser.angleDegreesAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 2-0: "+ instance_2.characteriser.angleDegreesAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 2-1: "+ instance_2.characteriser.angleDegreesAtScaleOfType(instance_1.characteriser,scale,typ)+'\r') ;
				
			//}
		}			


		System.out.println("Entropies:") ;
		System.out.println("\timage 0\r\t"+instance_0.characteriser.getEntropiesString(COARSE)) ;
		System.out.println("\timage 1\r\t"+instance_1.characteriser.getEntropiesString(COARSE)) ;
		System.out.println("\timage 2\r\t"+instance_2.characteriser.getEntropiesString(COARSE)) ;		
		
		System.out.println("Cosines:") ;
		System.out.println(" (entropies)") ; 		
		for(HistogramScale scale:OUR_SCALES){
			System.out.println("\tScale: "+scale) ;
			//for(HistogramType typ: OUR_TYPES){
				HistogramType typ = ENTROPIES ;
				System.out.println("\tType: "+typ) ;
				System.out.println("\t\tcosine 0-1: "+ instance_0.characteriser.cosineAtScaleOfType(instance_1.characteriser,scale,typ)) ;
				System.out.println("\t\tcosine 1-0: "+ instance_1.characteriser.cosineAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tcosine 2-0: "+ instance_2.characteriser.cosineAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tcosine 2-1: "+ instance_2.characteriser.cosineAtScaleOfType(instance_1.characteriser,scale,typ)+'\r') ;
						//}
		}			
		
		
		System.out.println("Angles:") ;
		System.out.println(" (entropies)") ; 		
		for(HistogramScale scale:OUR_SCALES){
			System.out.println("\tScale: "+scale) ;
			//for(HistogramType typ: OUR_TYPES){
				HistogramType typ = ENTROPIES ;
				System.out.println("\tType: "+typ) ;
				System.out.println("\t\tangle 0-1: "+ instance_0.characteriser.angleDegreesAtScaleOfType(instance_1.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 1-0: "+ instance_1.characteriser.angleDegreesAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 2-0: "+ instance_2.characteriser.angleDegreesAtScaleOfType(instance_0.characteriser,scale,typ)) ;
				System.out.println("\t\tangle 2-1: "+ instance_2.characteriser.angleDegreesAtScaleOfType(instance_1.characteriser,scale,typ)+'\r') ;
						//}
		}					
		
		
	}

}
