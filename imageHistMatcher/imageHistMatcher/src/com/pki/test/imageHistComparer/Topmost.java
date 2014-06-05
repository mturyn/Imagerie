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

import com.pki.test.imageHistComparer.histogram.ImageCharacteriser;

public class Topmost extends Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = "com.pki.test.imageHistComparer"
			.hashCode();
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

	ImageCharacteriser histogram;

	public Topmost() {
		try {
			img = ImageIO.read(new File("c:/lychees-and-lasers_0.jpg"));
		} catch (IOException ioe) {
			System.err.println("Error i/o: " + ioe);
		}

		histogram = new ImageCharacteriser();

	}
	
	public Topmost(String pFileSpec) {
		try {
			img = ImageIO.read(new File(pFileSpec));
		} catch (IOException ioe) {
			System.err.println("Error i/o: " + ioe);
		}

		histogram = new ImageCharacteriser(img) ;

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
		
		ImageCharacteriser origin = new ImageCharacteriser() ;
		
		int nScales = instance_0.histogram.getNumberOfScales();
		assert nScales == instance_1.histogram.getNumberOfScales() : "As things stand, all histogram sets are structurally identical, but these two appear to have a different number of scales:" ;
		
		// In a real system this would turn into part of a unit-test:
		System.out.println("Self-distance:\r") ;
		for(int scaleIndex = 0; scaleIndex<nScales;++scaleIndex){
			System.out.println("\tScale: "+scaleIndex) ;
			System.out.println("\t\timage 0: "+ instance_0.histogram.distance(instance_0.histogram,scaleIndex)) ;
			System.out.println("\t\timage 1: "+ instance_1.histogram.distance(instance_1.histogram,scaleIndex)+'\r') ;	
		}			
		
		System.out.println("Distance to origin:\r") ;		
		for(int scaleIndex = 0; scaleIndex<nScales;++scaleIndex){
			System.out.println("\tScale: "+scaleIndex) ;			
			System.out.println("\t\timage 0: "+ instance_0.histogram.distance(origin,scaleIndex)) ;
			System.out.println("\t\timage 1: "+ instance_1.histogram.distance(origin,scaleIndex)+'\r') ;	
		}	

		// In a real system this would turn into part of a unit-test:
		System.out.println("Distances:\r") ;
		for(int scaleIndex = 0; scaleIndex<nScales;++scaleIndex){
			System.out.println("\tScale: "+scaleIndex) ;			
			System.out.println("\t\t0 to 1: "+ instance_0.histogram.distance(instance_1.histogram,scaleIndex)) ;
			System.out.println("\t\t1 to 0: "+ instance_1.histogram.distance(instance_0.histogram,scaleIndex)+'\r') ;	
		}	
		
		// In a real system this would turn into part of a unit-test:
		System.out.println("Cosines:\r") ;
		for(int scaleIndex = 0; scaleIndex<nScales;++scaleIndex){
			System.out.println("\tScale: "+scaleIndex) ;			
			System.out.println("\t\t0 to 1: "+ instance_0.histogram.cosine(instance_1.histogram,scaleIndex)) ;
			System.out.println("\t\t1 to 0: "+ instance_1.histogram.cosine(instance_0.histogram,scaleIndex)+'\r') ;	
		}			

		// In a real system this would turn into part of a unit-test:
		System.out.println("Angles:\r") ;
		for(int scaleIndex = 0; scaleIndex<nScales;++scaleIndex){
			System.out.println("\tScale: "+scaleIndex) ;			
			System.out.println("\t\t0 to 1: "+ instance_0.histogram.angleDegrees(instance_1.histogram,scaleIndex)) ;
			System.out.println("\t\t1 to 0: "+ instance_1.histogram.angleDegrees(instance_0.histogram,scaleIndex)+'\r') ;	
		}			
		
		
	}

}
