package com.mturyn.imageHistComparer.histogram;

import java.util.TreeSet;

import com.mturyn.imageHistComparer.IHistogram;

;

/**
 * Find the peak of any local cluster 
 * TODO: Find out who's done this most
 * appropriately to our case.
 * 
 * @author mturyn
 * 
 */

public class ColorspaceBlockClusterSeeker {

	public IHistogram container;

	public ColorspaceBlockClusterSeeker(IHistogram pContainer) {
		container = pContainer;
	}

	TreeSet<ColorspaceBlock> visited = new TreeSet<ColorspaceBlock>();
	TreeSet<ColorspaceBlock> rejected = new TreeSet<ColorspaceBlock>();

	double dCurrentMax = -1.0;

	TreeSet<ColorspaceBlock> findClusterAboutBlock(ColorspaceBlock pSeed,
			double pFractionalDifference) {
		TreeSet<ColorspaceBlock> cluster = new TreeSet<ColorspaceBlock>();
		TreeSet<ColorspaceBlock> all = new TreeSet<ColorspaceBlock>();
		int[] mins = { 0, 0, 0 };
		int[] maxes = { 0, 0, 0 };

		// Need boundary conditions:
		for (int index = 0; index < 3; ++index) {
			mins[index] = Math.max(0, pSeed.binIndices[index] - 1);
			maxes[index] = Math.min(container.getNBins()[index],
					pSeed.binIndices[index] + 2);
		}

		for (int i = 0; i < container.getNBins()[0]; ++i) {
			for (int j = 0; j < container.getNBins()[0]; ++j) {
				for (int k = 0; k < container.getNBins()[0]; ++k) {

				}
			}
		}
		return cluster;
	}

}
