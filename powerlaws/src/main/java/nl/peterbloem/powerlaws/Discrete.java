package nl.peterbloem.powerlaws;

import static java.lang.Math.log;
import static java.lang.Math.pow;
import static nl.peterbloem.powerlaws.Functions.zeta;
import static nl.peterbloem.powerlaws.PowerLaws.KS_CORRECT;
import static nl.peterbloem.util.Series.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import nl.peterbloem.util.Series;


public class Discrete extends AbstractPowerLaw<Integer>
{	
	public static double ALPHA_MIN  = 1.5;
	public static double ALPHA_MAX  = 3.5;
	public static double ALPHA_STEP = 0.01;
	private double pdenum;
	
	public Discrete(int xMin, double exponent)
	{
		super(xMin, exponent);
		
		pdenum = zeta(exponent, xMin);
	}

	@Override
	public Integer generate()
	{
		double source = PowerLaws.random.nextDouble();

		return cdfInv(1.0 - source);
	}

	/**
	 * Returns a value x such that P(x) = q
	 * 
	 * @param q
	 * @return
	 */
	public int cdfInv(double q)
	{
		int x1, x2 = xMin();

		do 
		{
			x1 = x2;
			x2 = 2 * x1;
		} while (cdfComp(x2) >= q);
		
		return (int) binarySearch(x1, x2, q);
	}
	
	/**
	 * Returns the result of the Kolmogorov Smirnov test on this power law and the 
	 * distribution suggested by the data.
	 * 
	 * Note that unlike the continuous KS test, this version loops over all
	 * (integer) values of x between xMin and xMax rather than just the data 
	 * points.
	 * 
	 * @param data
	 * @param filter If true, the test is only performed using datapoints d with 
	 *   d >= xMin. If false, all data is used
	 * @return
	 */
	@Override
	public double ksTest(Collection<? extends Integer> data)
	{
		List<Integer> copy = new ArrayList<Integer>(data.size());
		int xMax = Integer.MIN_VALUE;
		for(int datum : data)
			if(datum >= xMin())
			{
				copy.add(datum);
				xMax = Math.max(xMax, datum);
			}
		Collections.sort(copy);
		List<Integer> counts = count(xMin(), xMax, copy);
		
		double max = Double.NEGATIVE_INFINITY;
		
		double plCDF = 0.0;
		for(int x : series(xMin(), xMax + 1))
		{
			int count = counts.get(x - xMin());
			double dataCDF = count / (double) copy.size();

			plCDF += pow(x, - exponent()) / zeta(exponent(), xMin());
			
			double diff = Math.abs(dataCDF - plCDF);
			max = Math.max(diff, max);
		}
		
		return max;
	}	
	
	/**
	 * Returns a cumulative histogram that counts for each integer from min
	 * to max (inclusive) the number of data points lower than or equal to that 
	 * integer. 
	 * 
	 * Data is expected to be sorted!
	 * 
	 * @param min
	 * @param max
	 * @param data
	 * @return
	 */
	protected static List<Integer> count(int min, int max, List<Integer> data)
	{
		List<Integer> counts = new ArrayList<Integer>(max + 1 - min);
		
		counts.add(0);
		int i = min;
		for(int datum : data)
		{
			if(datum > max)
				break;
			
			while(datum > i)
			{
				counts.add(counts.get(i - min));
				i++;
			}
			
			counts.set(i - min, counts.get(i - min) + 1);
		}
		
		return counts;
	}
	
	private double binarySearch(double lower, double upper, double target)
	{
		// * stop recursion when the interval falls within a single integer
		if(Math.floor(lower) == Math.floor(upper))
			return lower;
		
		double range = upper - lower;	
		double midpoint = range / 2.0 + lower;

		double pm = cdfCompDouble(midpoint);
		
		if(pm < target)
			return binarySearch(lower, midpoint, target);
		else
			return binarySearch(midpoint, upper, target);
	}
	
	/**
	 * The cumulative distribution function.
	 * 
	 * TODO test this, it does not agree with the method used in ksTest
	 * 
	 * @param x
	 * @return
	 */
	public double cdf(Integer x)
	{
		return 1.0 - cdfComp(x);
	}
	
	
	public double cdfComp(Integer x)
	{
		return zeta(exponent(), x) / pdenum;
	}
	
	protected double cdfCompDouble(double x)
	{
		return zeta(exponent(), x) / pdenum;
	}
	
	
	public static PowerLaw.Fit<Integer, Discrete> fit(Collection<? extends Integer> data)
	{
		return new Discrete.Fit(data);
	}
	
	@Override
	public double p(Integer x)
	{
		return pow(x, - exponent()) / zeta(exponent(), xMin());
	}

	@Override
	protected PowerLaw<Integer> fitInternal(Collection<? extends Integer> data)
	{
		return Discrete.fit(data).fit();
	}

	/**
	 * Represents the intermediate stage of fitting a power law to data. 
	 * 
	 * Call estimate() to get the best model.
	 * 
	 * @author Peter
	 *
	 */
	private static class Fit extends AbstractPowerLaw.AbstractFit<Integer, Discrete> 
	{
		public Fit(Collection<? extends Integer> data)
		{
			super(data);
		}
		
		/**
		 * Estimate a power law, discarding all data below the given xMin
		 * 
		 * Implementation of Clauset 2007 3.5
		 * 
		 * @param xMin
		 * @return
		 */
		public Discrete fit(Integer xMin)
		{
			double bestAlpha = - 1.0,
			       maxLL = Double.NEGATIVE_INFINITY;
			
			for(double alpha : Series.series(ALPHA_MIN, ALPHA_STEP, ALPHA_MAX))
			{
				double ll = logLikelihood(alpha, xMin);
				
				if(ll > maxLL)
				{
					bestAlpha = alpha;
					maxLL = ll;
				}
			}
			
			return new Discrete(xMin, bestAlpha);
		}
		
		/**
		 * The log likelihood function of the data for given parameters 
		 * 
		 * @return
		 */
		public double logLikelihood(double alpha, int xMin)
		{
			double sum = 0.0;
			double n = 0.0;
			for(int datum : data())
				if(datum >= xMin)
				{
					sum += log(datum);
					n++;
				}
			
			return - n * log(zeta(alpha, xMin)) - alpha * sum;
		}
	}
}
