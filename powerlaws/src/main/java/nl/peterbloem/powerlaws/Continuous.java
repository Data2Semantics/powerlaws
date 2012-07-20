package nl.peterbloem.powerlaws;

import static nl.peterbloem.powerlaws.PowerLaws.KS_CORRECT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.peterbloem.util.AbstractGenerator;
import nl.peterbloem.util.Generator;
import nl.peterbloem.util.Series;

public class Continuous extends AbstractPowerLaw<Double>
{

	public Continuous(double xMin, double exponent)
	{
		super(xMin, exponent);
	}

	@Override
	public Double generate()
	{
		double source = PowerLaws.random.nextDouble();
		
		double p = - 1.0 / (exponent() - 1.0);
		return xMin() * Math.pow(1.0 - source, p);
	}
	
	public static PowerLaw.Fit<Double, Continuous> fit(Collection<? extends Double> data)
	{
		return new Fit(data);
	}
	
	/**
	 * Returns the probability density of x
	 * 
	 * @return
	 */
	public double p(Double x)
	{
		return (exponent() / xMin()) * Math.pow(x / xMin(), -exponent());
	}
	
	/**
	 * Returns p(X <= x), the cumulative distribution function 
	 * 
	 * 
	 * @param x
	 * @return
	 */
	public double cdf(Double x)
	{
		return 1.0 - cdfComp(x);
	}
	
	/**
	 * Returns p(X >= x), the complementary cdf
	 * 
	 * @param x
	 * @return
	 */
	public double cdfComp(Double x)
	{
		return Math.pow(x / xMin(), - exponent() + 1);
	}
	
	
	
	@Override
	protected PowerLaw<Double> fitInternal(Collection<? extends Double> data)
	{
		return Continuous.fit(data).fit();
	}

	/**
	 * Represents the intermediate stage of fitting a power law to data. 
	 * 
	 * Call estimate() to get the best model.
	 * 
	 * @author Peter
	 *
	 */
	static class Fit extends AbstractPowerLaw.AbstractFit<Double, Continuous>
	{

		public Fit(Collection<? extends Double> data)
		{
			super(data);
		}
		
		/**
		 * Estimate a power law, discarding all data below the given xMin
		 * 
		 * Implementation of Clauset 2007 formula 3.1
		 * 
		 * @param xMin
		 * @return
		 */
		@Override
		public Continuous fit(Double xMin)
		{
			double n = 0.0;
			double sum = 0.0;
			for(double datum : data())
				if(datum >= xMin)
				{
					sum += Math.log(datum / xMin);
					n++;
				}
			
			return new Continuous(xMin, 1.0 + n / sum);
		}
	}
}
