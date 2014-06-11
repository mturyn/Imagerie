package com.mturyn.imageHistComparer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mturyn.imageHistComparer.Utilities.HistogramDistanceMethod;
import com.mturyn.imageHistComparer.Utilities.HistogramScale;
import com.mturyn.imageHistComparer.Utilities.HistogramType;
import com.mturyn.imageHistComparer.histogram.ImageCharacteriser;
// import com.mturyn.imageHistComparer.histogram.RGBImageCharacteriser;
// import com.mturyn.imageHistComparer.histogram.YUVImageCharacteriser;


//public class AnalysedImage<T extends RGBImageCharacteriser> extends Component {
public class AnalysedImage<T extends IHistogram> extends Component implements IAnalysedImage<T> {	public static final String COPYRIGHT_STRING ="'I won't throw down my gun until everyone else throws down theirs.'\r---some guy who got shot.\rCopyright (c) 2014 Michael Turyn; all rights reserved.";
	private static final long serialVersionUID = "com.mturyn.imageHistComparer".hashCode();

	ImageCharacteriser<T> mCharacteriser ;

	public AnalysedImage(String pFileSpec,T pHistInstance) {

		try {
			// On two lines, for debugging:
			BufferedImage img = ImageIO.read(new File(pFileSpec));
			this.setImg(img) ;
		} catch (IOException ioe) {
			System.err.println("Error i/o: " + ioe+": reading"+pFileSpec);
		}
		//mCharacteriser = pBlankCharacteriser ; 
		setCharacteriser( new ImageCharacteriser<T>(pHistInstance) ) ;
		getCharacteriser().populateFromBufferedImage( getImg() ) ;

		setFilename(pFileSpec) ;
		//System.out/*err*/.println(getFilename());
	}	

	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.IAnalysedImage#getCharacteriser()
	 */
	@Override
	public ImageCharacteriser<T> getCharacteriser() {
		return mCharacteriser;
	}


	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.IAnalysedImage#setCharacteriser(com.mturyn.imageHistComparer.histogram.ImageCharacteriser)
	 */
	@Override
	public void setCharacteriser(ImageCharacteriser<T> characteriser) {
		this.mCharacteriser = characteriser;
	}


	private BufferedImage mImg;
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.IAnalysedImage#getImg()
	 */
	@Override
	public BufferedImage getImg() {
		return mImg;
	}
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.IAnalysedImage#setImg(java.awt.image.BufferedImage)
	 */
	@Override
	public void setImg(BufferedImage img) {
		mImg = img;
	}


	private String mFilename;
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.IAnalysedImage#getFilename()
	 */
	@Override
	public String getFilename() {
		return mFilename;
	}
	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.IAnalysedImage#setFilename(java.lang.String)
	 */
	@Override
	public void setFilename(String fileName) {
		mFilename = fileName;
	}



	public Dimension getPreferredSize() {
		return new Dimension(getImg().getWidth(null), getImg().getHeight(null));
	}



	/* (non-Javadoc)
	 * @see com.mturyn.imageHistComparer.IAnalysedImage#findIndicesOfMostAndLeastSimilarAtScale(com.mturyn.imageHistComparer.AnalysedImage[], com.mturyn.imageHistComparer.Utilities.HistogramScale, com.mturyn.imageHistComparer.Utilities.HistogramType)
	 */
	@Override
	public IndexedValue[] findIndicesOfMostAndLeastSimilarAtScale(
			AnalysedImage[] pTopmosts,
			HistogramScale pScale,
			HistogramType pType
			){
		IImageCharacteriser[] characterisers = new IImageCharacteriser[pTopmosts.length];
		for(int i=0;i<characterisers.length;++i){
			characterisers[i] = pTopmosts[i].mCharacteriser ;  
		}
		return this.mCharacteriser.findIndicesOfMostAndLeastSimilarAtScale(characterisers,pScale,pType) ;
	}	
	
	
	public IndexedValue[] 
			findIndicesOfMostAndLeastSimilarAtScaleOfType(
										IAnalysedImage<T>[] pAnalysedImages,
										HistogramDistanceMethod pMethod,
										double dFractionalFuzziness,
										HistogramScale pScale,
										HistogramType pType
	){
		IImageCharacteriser[] characterisers = new IImageCharacteriser[pAnalysedImages.length];
		for(int i=0;i<characterisers.length;++i){
			characterisers[i] = pAnalysedImages[i].getCharacteriser() ;  
		}
		return 
			this.getCharacteriser().findIndicesOfMostAndLeastSimilarAtScaleOfType(
										characterisers, 
										pMethod, 
										dFractionalFuzziness, 
										pScale, 
										pType
										) ;
}	


}










