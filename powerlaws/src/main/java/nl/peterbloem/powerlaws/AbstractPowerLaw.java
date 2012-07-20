package nl.peterbloem.powerlaws;

import static nl.peterbloem.powerlaws.PowerLaws.KS_CORRECT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import nl.peterbloem.util.AbstractGenerator;
import nl.peterbloem.util.NumberComparator;
import nl.peterbloem.util.Series;

public abstract class AbstractPowerLaw<T extends Number> 
	extends AbstractGenerator<T>
	implements PowerLaw<T>
{

	private double exponent;
	private T xMin;
	
	public AbstractPowerLaw(T xMin, double exponent)
	{
		this.exponent = exponent;
		this.xMin = xMin;
	}
	
	@Override
	public abstract T generate();

	@Override
	public double exponent()
	{
		return exponent;
	}

	@Override
	public T xMin()
	{
		return xMin;
	}

	@Override
	public double cdfComp(T x)
	{
		return 1.0 - cdf(x);
	}

	@Override
	public List<T> generate(Collection<? extends T> observed, int number)
	{
		int n = observed.size();
		List<T> head = new ArrayList<T>(observed.size());
		for(T datum : observed)
			if(datum.doubleValue() < xMin.doubleValue())
				head.add(datum);
		
		List<T> result = new ArrayList<T>(number);
		for(int i : Series.series(number))
			if(PowerLaws.random.nextDouble() < head.size() / (double)n)
				result.add(head.get(PowerLaws.random.nextInt(head.size())));
			else 
				result.add(generate());
		
		return result;
	}	

	@Override
	public double ksTest(Collection<? extends T> data)
	{
		List<T> copy = new ArrayList<T>(data.size());
		for(T datum : data)
			if(datum.doubleValue() >= xMin.doubleValue())
				copy.add(datum);
		
		Collections.sort(copy, new NumberComparator());
		
		double max = Double.NEGATIVE_INFINITY;
		
		for(int i : Series.series(copy.size()))
		{
			T x = copy.get(i);
			
			double dataCDF = (i + (KS_CORRECT ? 1 : 0)) / (double) copy.size();
			double plCDF = cdf(x);
			
			double diff = Math.abs(dataCDF - plCDF);
			max = Math.max(diff, max);
		}
		
		return max;
	}
	
	@Override
	public double significance(Collection<? extends T> data, int n)
	{
		double threshold = ksTest(data);
		
		int above = 0;
		for(int i : Series.series(n))
		{
			if(i % 500 == 0 && i != 0)
				Functions.log().info("* finished " + i + " trials of "+n+".");
			
			List<T> generated = generate(data, data.size());
		
			PowerLaw<T> generatedPL = fitInternal(generated);
			
			if(generatedPL.ksTest(generated) >= threshold)
				above ++;
		}
		
		return above / (double) n;
	}
	
	/**
	 * Fit a power law to data in the same way this model might be created. 
	 * 
	 * @param data
	 * @return
	 */
	protected abstract PowerLaw<T> fitInternal(Collection<? extends T> data);
	
	@Override
	public double significance(Collection<? extends T> data, double epsilon)
	{
		return significance(data, (int)(0.25 * Math.pow(epsilon, -2.0)));
	}
	
	public static abstract class AbstractFit<T extends Number, P extends PowerLaw<T>> 
		implements Fit<T, P>
	{

		private List<T> data;
		private List<T> unique;

		public AbstractFit(Collection<? extends T> data)
		{
			this.data = new ArrayList<T>(data);
			Collections.sort(this.data, new NumberComparator());
			
			LinkedHashSet<T> set = new LinkedHashSet<T>(data);
			unique = new ArrayList<T>(set);
			
			Collections.sort(unique, new NumberComparator());
		}
		
		@Override
		public P fit()
		{
			P best = null;
			double bestDistance = Double.POSITIVE_INFINITY;
			
			for(T datum : unique)
			{
				P current = fit(datum);
				double currentDistance = current.ksTest(data);
				
				if(currentDistance < bestDistance)
				{
					bestDistance = currentDistance;
					best = current;
				}
			}

			return best;
		}

		public List<T> data()
		{
			return data;
		}

		public List<T> unique()
		{
			return unique;
		}

	}
}
