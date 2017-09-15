package org.jebtk.bioinformatics.genomic;

import java.util.List;

import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.math.MathUtils;
import org.jebtk.math.matrix.AnnotatableMatrix;
import org.jebtk.math.matrix.AnnotationMatrix;
import org.jebtk.math.matrix.ColMatrixFunction;
import org.jebtk.math.matrix.ColNumFunction;
import org.jebtk.math.matrix.DoubleMatrix;
import org.jebtk.math.matrix.MatrixUtils;
import org.jebtk.math.matrix.RowNumFunction;
import org.jebtk.math.statistics.Statistics;

public class MatrixNormalization {
	
	private static class GeoMeans implements RowNumFunction {
		
		private double[] mData;
		
		public GeoMeans(int rows) {
			mData = new double[rows];
		}
		
		@Override
		public void apply(double[] data, int offset, int l, int row) {
			mData[row] = Statistics.geometricMean(data, offset, l);
		}
		
		public double[] getMeans() {
			return mData;
		}
	}
	
	private static class Factors implements ColNumFunction {
		private double[] mFactors;
		private double[] mMeans;
		private double[] mData;
		
		public Factors(int cols, double[] geometricMeans) {
			mFactors = new double[cols];
			mMeans = geometricMeans;
			mData = new double[geometricMeans.length];
		}
		
		@Override
		public void apply(double[] data, int offset, int l, int col) {
			for (int i = 0; i < l; ++i) {
				mData[i] = data[i] / mMeans[i];
			}
			
			double med = Statistics.median(mData);
			
			if (med > 0) {
				mFactors[col] = med;
			} else {
				mFactors[col] = 1.0;
			}
		}
		
		public double[] getFactors() {
			return mFactors;
		}
	}
	
	private static class Scale extends ColMatrixFunction {
		private double[] mFactors;
		
		public Scale(double[] factors, DoubleMatrix matrix) {
			super(matrix);
			
			mFactors = factors;
		}

		@Override
		public void apply(double d, int row, int col, int p, double[] data) {
			data[p] = d / mFactors[col];
		}
	}
	
	/**
	 * Assuming each column represents a sample, calculate the geometric
	 * mean of each row and then use this to scale each sample's counts
	 * to normalize them for differential expression analysis. This is
	 * essentially the same technique that deseq2 uses.
	 * 
	 * @param m
	 * @return
	 */
	public static AnnotationMatrix medianRatio(final AnnotationMatrix m) {
		
		GeoMeans fmeans = new GeoMeans(m.getRowCount());
		MatrixUtils.rowApply(m, fmeans);
		
		double[] geometricMeans = fmeans.getMeans();
		
		
		Factors ffactors = new Factors(m.getColumnCount(), geometricMeans);
		MatrixUtils.colApply(m, ffactors);
		double[] factors = ffactors.getFactors();
		
		AnnotationMatrix ret = AnnotatableMatrix.createDoubleMatrix(m);
		
		Scale fscale = new Scale(factors, (DoubleMatrix)ret.getInnerMatrix());
		MatrixUtils.colApply(m, fscale);
		
		return ret;
	}
	
	private static class TPM extends ColMatrixFunction {
		private double[] mRpks;
		private double[] mWidthsKb;
		private double mFactor;

		public TPM(final double[] widths,
				DoubleMatrix ret) {
			super(ret);
			mWidthsKb = widths;
			mRpks = new double[ret.mRows];
		}
		
		@Override
		public void apply(double[] data, int offset, int l, int col) {
			mFactor = 0.0;
			
			for (int i = 0; i < l; ++i) {
				double rpk = data[i] * mWidthsKb[i];
				
				mRpks[i] = rpk;
				mFactor += rpk;
			}
			
			super.apply(data, offset, l, col);
		}

		@Override
		public void apply(double d, int row, int col, int p, double[] data) {
			// Scale each data point column wise
			data[p] = mRpks[row] / mFactor * 1000000;
		}
	}
	
	public static AnnotationMatrix tpm(final AnnotationMatrix m,
			final List<GenomicRegion> locations) {
		
		double[] widthsKb = getWidthsKb(locations);
		
		AnnotationMatrix ret = AnnotatableMatrix.createNumericalMatrix(m);
		
		MatrixUtils.colApply(m, new TPM(widthsKb, (DoubleMatrix)ret.getInnerMatrix()));
		
		/*
		for (int i = 0; i < m.getColumnCount(); ++i) {
			// reads per kilobase
			List<Double> rpks = new ArrayList<Double>(ret.getRowCount());
			
			double factor = 0;
			
			for (int j = 0; j < m.getRowCount(); ++j) {
				
				// length in kb
				double l = locations.get(j).getLength() / 1000.0;
				
				double rpk = m.getValue(j, i) * l;
				
				rpks.add(rpk);
				factor += rpk;
			}
			
			for (int j = 0; j < m.getRowCount(); ++j) {
				// tpm
				ret.set(j, i, (rpks.get(j) / factor) * 1000000);
			}
		}
		*/
		
		return ret;
	}
	
	private static double[] getWidthsKb(List<GenomicRegion> locations) {
		double[] ret = new double[locations.size()];
		
		for (int i = 0; i < locations.size(); ++i) {
			ret[i] = locations.get(i).getLength() / 1000.0;
		}
		
		return ret;
	}
	
	private static class RPM extends ColMatrixFunction {
		private double mFactor;
		private double[] mCounts;

		public RPM(final double[] counts, DoubleMatrix ret) {
			super(ret);
			
			mCounts = counts;
		}
		
		@Override
		public void apply(double[] data, int offset, int l, int col) {
			mFactor = 1000000.0 / mCounts[col];
			
			super.apply(data, offset, l, col);
		}

		@Override
		public void apply(double d, int row, int col, int p, double[] data) {
			// Scale each data point column wise
			data[p] = d * mFactor;
		}
	}
	
	public static AnnotationMatrix rpm(final AnnotationMatrix m) {
		double[] counts = m.getColumnAnnotations("total-reads").rowAsDouble(0);
		
		return rpm(m, counts);
	}

	public static AnnotationMatrix rpm(final AnnotationMatrix m,
			final List<Integer> counts) {
		return rpm(m, CollectionUtils.toDoublePrimitive(counts));
	}
	
	public static AnnotationMatrix rpm(final AnnotationMatrix m,
			final double[] counts) {
		AnnotationMatrix ret = AnnotatableMatrix.createNumericalMatrix(m);
		
		MatrixUtils.colApply(m, new RPM(counts, (DoubleMatrix)ret.getInnerMatrix()));
		
		/*
		AnnotationMatrix ret = AnnotatableMatrix.createNumericalMatrix(m);
		
		for (int i = 0; i < m.getColumnCount(); ++i) {
			double f = 1000000.0 / counts.get(i);
			
			for (int j = 0; j < m.getRowCount(); ++j) {
				ret.set(j, i, m.getValue(j, i) * f);
			}
		}
		*/
		
		return ret;
	}
	
	private static class RPKM extends ColMatrixFunction {
		private double[] mFactors;

		public RPKM(final double[] factors, DoubleMatrix ret) {
			super(ret);
			
			mFactors = factors;
		}
		
		@Override
		public void apply(double d, int row, int col, int p, double[] data) {
			// Scale each data point column wise
			data[p] = d * mFactors[row];
		}
	}
	
	/**
	 * Convert a table of counts into rpkm values.
	 * @param m
	 * @param counts
	 * @param locations
	 * @return
	 */
	public static AnnotationMatrix rpkm(final AnnotationMatrix m,
			final List<GenomicRegion> locations) {
		AnnotationMatrix ret = rpm(m);
		
		double[] factors = getWidthsKb(locations);
		
		// Divide in place
		MathUtils.divide2(1.0, factors);
			
		MatrixUtils.colApply(m, new RPKM(factors, (DoubleMatrix)ret.getInnerMatrix()));
		
		/*
		for (int i = 0; i < m.getRowCount(); ++i) {
			double l = locations.get(i).getLength() / 1000.0;
			
			double f = 1.0 / l;
			
			for (int j = 0; j < m.getColumnCount(); ++j) {
				ret.set(j, i, m.getValue(j, i) * f);
			}
		}
		*/
		
		return ret;
	}
	
	public static AnnotationMatrix rpkm(final AnnotationMatrix m,
			final List<Integer> counts,
			final List<GenomicRegion> locations) {
		AnnotationMatrix ret = rpm(m, counts);
		
		double[] factors = getWidthsKb(locations);
		
		// Divide in place
		MathUtils.divide2(1.0, factors);
			
		MatrixUtils.colApply(m, new RPKM(factors, (DoubleMatrix)ret.getInnerMatrix()));
		
		/*
		for (int i = 0; i < m.getRowCount(); ++i) {
			double l = locations.get(i).getLength() / 1000.0;
			
			double f = 1.0 / l;
			
			for (int j = 0; j < m.getColumnCount(); ++j) {
				ret.set(j, i, m.getValue(j, i) * f);
			}
		}
		*/
		
		return ret;
	}
}
