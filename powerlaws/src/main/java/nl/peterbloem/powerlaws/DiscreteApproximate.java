package nl.peterbloem.powerlaws;

import static java.lang.Math.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * This distribution models a discrete power law as a continuous one 
 * whose generated values are rounded to the nearest integer.
 * 
 * @author Peter
 *
 */
public class DiscreteApproximate extends AbstractPowerLaw<Integer>
{
	private Continuous approximation;
	
	public DiscreteApproximate(int xMin, double exponent)
	{	
		super(xMin, exponent);
		approximation = new Continuous(xMin, exponent);
	}
	
	private DiscreteApproximate(Continuous approximation)
	{
		super(approximation.xMin().intValue(), approximation.exponent());
		this.approximation = approximation;
	}

	@Override
	public Integer generate()
	{
		return (int) Math.round(approximation.generate());
	}
	
	@Override
	public double cdf(Integer x)
	{
		double xApp = ((double)x) + 0.5;
		return approximation.cdf(xApp);
	}
	
	@Override
	public double cdfComp(Integer x)
	{
		double xApp = ((double)x) - 0.5;
		return approximation.cdfComp(xApp);
	}


	public double ksTest(Collection<? extends Integer> data)
	{
		List<Double> doubleData = new ArrayList<Double>(data.size());
		for(int datum : data)
			doubleData.add((double)datum);
		
		return approximation.ksTest(doubleData);
	}
	
	@Override
	public double p(Integer x)
	{
		double xLower = x - 0.5, xHigher = x + 0.5;
		
		return approximation.cdf(xHigher) - approximation.cdf(xLower);
	}

	@Override
	protected PowerLaw<Integer> fitInternal(Collection<? extends Integer> data)
	{
		return DiscreteApproximate.fit(data).fit();
	}

	public static PowerLaw.Fit<Integer, DiscreteApproximate> fit(Collection<? extends Integer> data)
	{
		return new Fit(data);
	}
	
	public static class Fit extends AbstractPowerLaw.AbstractFit<Integer, DiscreteApproximate>
	{
		private PowerLaw.Fit<Double, Continuous> approximation;

		
		public Fit(Collection<? extends Integer> data)
		{
			super(data);
			
			List<Double> doubleData = new ArrayList<Double>(data.size());
			for(int datum : data)
				doubleData.add((double)datum);
			
			approximation = Continuous.fit(doubleData);
		}
		
		/**
		 * Estimate a power law, discarding all data below the given xMin
		 * 
		 * Implementation of Clauset 2007 3.5
		 * 
		 * @param xMin
		 * @return
		 */
		public DiscreteApproximate fit(Integer xMin)
		{
			double xApp = xMin - .5;
			return new DiscreteApproximate(approximation.fit(xApp));
		}
	}	
}
