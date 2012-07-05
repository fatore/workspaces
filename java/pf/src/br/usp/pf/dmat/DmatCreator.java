package br.usp.pf.dmat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import matrix.AbstractMatrix;
import matrix.MatrixFactory;
import br.usp.pf.util.CountProteinConformation;

public class DmatCreator {
	
	public void createDmat(String dyFile, String jumpsFile, String outputFolder) throws Exception {
		
		// create metric distance matrix
		DistanceMatrix dmat = new DistanceMatrix
				(MatrixFactory.getInstance(dyFile), new BinaryDistance());
		
		System.out.println("Max distance for static distance: " + dmat.getMaxDistance());
		System.out.println("Min distance for static distance: " + dmat.getMinDistance());
		normalize(dmat);

		if (jumpsFile != null) {
			DistanceMatrix jumps = jumpsCountParser(dmat, dmat.getElementCount(), jumpsFile);
			System.out.println("Max distance for dynamic distance: " + jumps.getMaxDistance());
			System.out.println("Min distance for dynamic distance: " + jumps.getMinDistance());
			normalize(jumps);
			merge(dmat, jumps);
		}
		
		String outfile = outputFolder + "dmats/";
		new File(outfile).mkdirs();
		if (jumpsFile == null) {
			outfile += "static";
		} else {
			outfile += "dynamic";
		}
		outfile += ".dmat";
		dmat.save(outfile);
		System.out.println("Done!");
	}

	private DistanceMatrix jumpsCountParser(DistanceMatrix dmat1, int size,
			String filename)
			throws IOException {
		
		DistanceMatrix dmat = new DistanceMatrix(size);
		BufferedReader in = null;
		String line;
		String[] linePieces;
		int x, y;

		try {
			in = new BufferedReader(new FileReader(filename));

			line = in.readLine();
			
			int liveStates = Integer.parseInt(in.readLine());
			
			System.out.println("Number of states: " + liveStates);
			
			while ((line = in.readLine()) != null) {
				
				linePieces = line.split(" ");
				
				if (linePieces.length < 3) {
					System.err.println(line);
				}

				x = Integer.parseInt(linePieces[0]);
				y = Integer.parseInt(linePieces[1]);
				int jumps = Integer.parseInt(linePieces[2]);
				dmat.setDistance(x, y, jumps);
			}
		} catch (FileNotFoundException ex) {
			throw new IOException(ex.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					Logger.getLogger(CountProteinConformation.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			}
		}

		return dmat;
	}
	
	protected DistanceMatrix jumpsSumParser(DistanceMatrix dmat1, int size,
			String filename) throws IOException {

		DistanceMatrix dmat = new DistanceMatrix(size);
		BufferedReader in = null;
		String line;
		String[] linePieces;
		int i, j, k, x, y, prev, next, incFactor;
		float distSum;

		try {
			in = new BufferedReader(new FileReader(filename));

			line = in.readLine();
			
			int liveStates = Integer.parseInt(in.readLine());
			
			while ((line = in.readLine()) != null) {

				linePieces = line.split(" ");

				for (i = 0; i < linePieces.length; i++) {
					for (j = i + 1; j < linePieces.length; j++) {
						x = Integer.parseInt(linePieces[i]);
						y = Integer.parseInt(linePieces[j]);
						distSum = 0;
						incFactor = j - i - 1;
						next = Integer.parseInt(linePieces[i]);
						for (k = i + 1; k < j; k++) {
							prev = next;
							next = Integer.parseInt(linePieces[k]);
							distSum += dmat1.getDistance(prev, next);
							// dist += dmat1.getDistance(prev, next) *
							// incFactor;
							incFactor--;
						}
						distSum += dmat1.getDistance(next, y);
						
						if (distSum > dmat.getDistance(x, y)) {
							dmat.setDistance(x, y, distSum);
						}
					}
				}
			}
		} catch (FileNotFoundException ex) {
			throw new IOException(ex.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					Logger.getLogger(CountProteinConformation.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			}
		}

		return dmat;
	}
	
	protected DistanceMatrix energyParser(AbstractMatrix matrix) throws IOException {
		DistanceMatrix dmat = new DistanceMatrix(matrix.getRowCount());

		for (int i = 0; i < matrix.getRowCount(); i++) {
			float klass1 = matrix.getRow(i).getKlass();

			for (int j = i + 1; j < matrix.getRowCount(); j++) {
				float klass2 = matrix.getRow(j).getKlass();
				dmat.setDistance(i, j, Math.abs(klass1 - klass2));
			}
		}

		return dmat;
	}

	protected DistanceMatrix jumpsPathCountParser(DistanceMatrix dmat1, int size,
			String filename)
			throws IOException {
		
		DistanceMatrix dmat = new DistanceMatrix(size);
		BufferedReader in = null;
		String line;
		String[] linePieces;
		int i, j, x, y;

		try {
			in = new BufferedReader(new FileReader(filename));

			line = in.readLine();
			
			int liveStates = Integer.parseInt(in.readLine());
			
			while ((line = in.readLine()) != null) {

				linePieces = line.split(" ");

				for (i = 0; i < linePieces.length; i++) {
					for (j = i + 1; j < linePieces.length; j++) {
						x = Integer.parseInt(linePieces[i]);
						y = Integer.parseInt(linePieces[j]);
						int jumps = j - i - 1;
						if (dmat.getDistance(x, y) != 0) {
							if (jumps != dmat.getDistance(x, y)) {
								System.err.println("error");
							}
						}
						if (jumps > dmat.getDistance(x, y)) {
							dmat.setDistance(x, y, jumps);
						}
					}
				}
			}
		} catch (FileNotFoundException ex) {
			throw new IOException(ex.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					Logger.getLogger(CountProteinConformation.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			}
		}

		return dmat;
	}
	
	protected DistanceMatrix jumpsMaxDist(DistanceMatrix dmat1, int size,
			String filename) throws IOException {

		DistanceMatrix dmat = new DistanceMatrix(size);
		BufferedReader in = null;
		String line;
		String[] linePieces;
		int i, j, k, x, y, next, incFactor;
		float maxDist;

		try {
			in = new BufferedReader(new FileReader(filename));

			line = in.readLine();
			
			int liveStates = Integer.parseInt(in.readLine());
						
			while ((line = in.readLine()) != null) {

				linePieces = line.split(" ");

				for (i = 0; i < linePieces.length; i++) {
					for (j = i + 1; j < linePieces.length; j++) {
						x = Integer.parseInt(linePieces[i]);
						y = Integer.parseInt(linePieces[j]);
						maxDist = dmat1.getDistance(x, y);
						incFactor = j - i - 1;
						next = Integer.parseInt(linePieces[i]);
						for (k = i + 1; k < j; k++) {
							next = Integer.parseInt(linePieces[k]);	
							if (dmat1.getDistance(x, next) > maxDist) {
								maxDist = dmat1.getDistance(x, next);
							}
							incFactor--;
						}
						if (maxDist > dmat.getDistance(x, y)) {
							dmat.setDistance(x, y, maxDist);
						}
					}
				}
			}
		} catch (FileNotFoundException ex) {
			throw new IOException(ex.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					Logger.getLogger(CountProteinConformation.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			}
		}

		return dmat;
	}
	
	
	private void normalize(DistanceMatrix dmat) {
		float min = dmat.getMinDistance();
		float max = dmat.getMaxDistance();
		
		for (int i = 0; i < dmat.getElementCount(); i++) {
			for (int j = i + 1; j < dmat.getElementCount(); j++) {
				// get distance
				float dist = dmat.getDistance(i, j);
				
				// normalize between 0 and 1
				dist = (dist - min) / (max - min);
				dmat.setDistance(i, j, dist);
			}
		}
	}

	private void merge(DistanceMatrix dmat, DistanceMatrix jumps) {
		for (int i = 0; i < dmat.getElementCount(); i++) {
			for (int j = i + 1; j < dmat.getElementCount(); j++) {
				// static distance
				float s = dmat.getDistance(i, j);
				// dynamic distance
				float d = jumps.getDistance(i, j);
				// combination
				float dist = s + (s * d);
				// normalization
				dist = dist / 2;
				// update
				dmat.setDistance(i, j, dist);
			}
		}
	}
}













