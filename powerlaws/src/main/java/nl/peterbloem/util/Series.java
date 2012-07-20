package nl.peterbloem.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * This class provides helpful functions for defining series of numbers as 
 * lists. When possible lists are defined as custom lightweight implementations 
 * of the list interface. For instance the list of numbers between a and b 
 * separated by steps of c is implemented as a class storing just those three 
 * values. 
 * 
 * One of the advantages of the methods is that they provide an elegant way of
 * iterating over a range of number, eg:
 * <code>
 * 	 for(int i : series(100) {
 *   }
 *   
 * 	 for(int i : series(-15, 15) {
 *   }
 *   
 * 	 for(int i : series(-25, 5, 25) {
 *   }
 * </code>
 * 
 * It should be noted, however, that this is slower than the traditional for 
 * loop, especially for many nested loops. This is in part due to compiler 
 * optimizations and partly due to the cost of creating iterators.
 * 
 * <h2>TODO</h2>
 * <ul>
 *   <li>TODO: Versions for long, float</li>
 * </ul>
 * 
 * @author peter
 *
 * @param <T>
 */
public class Series
{

	/**
	 * <p>
	 * Returns a series  of integers from 0 (inclusive) and up to 
	 * a given value (exclusive)
	 * </p><p>
	 * If to is negative, it is interpreted as an exclusive lower bound and 
	 * the series counts from 0 backwards.
	 * </p><p>
	 * If to is 0, an empty list is returned.
	 * </p><p>
	 * Examples:
	 * </p><code>
	 * Series.series(3) <br/>
	 * // [0, 1, 2]
	 * </code>
	 * 
	 * 
	 * @param to The upper bound for the series
	 * @return An unmodifiable list of integers
	 */
	public static List<Integer> series(int to)
	{
		if(to == 0)
			return Collections.emptyList();
		if(to < 0)
			return new IntSeries(0, -1, to);
		
		return new IntSeries(0, 1, to);
	}
	
	/**
	 * Returns a series of integers from a given value (inclusive) and up to 
	 * a given value (exclusive).
	 * 
	 * If from == to, the series will be empty.
	 * 
	 * If from > to, the series will count backwards. It will include the form 
	 * value and exclude the to value.
	 * 
	 * @param from The starting value. Always included in the series unless 
	 *        equal to to. 
	 * @param to The bound. Never included in the list
	 * @return An unmodifiable list of numbers containing all integers between 
	 *         and the parameters.
	 */
	public static List<Integer> series(int from, int to)
	{
		if(from > to)
			return new IntSeries(from, -1, to);
		if(from == to)
			return Collections.emptyList();
		
		return new IntSeries(from, 1, to);
	}
	
	/**
	 * Returns a series of integers from a given value to 
	 * a given value with a given step size.
	 * 
	 * @param from The starting value. Always included in the resulting series 
	 *        unless equal to to.
	 * @param step The distance between successive elements in the returned list.
	 * @param to The bound, never included in the resulting series.
	 * @return An unmodifiable list of numbers containing all integers between 
	 *         and including the parameters for the given step size.
	 * @throws IllegalArgumentException if 
	 *         <ul>
	 *            <li>the step size is 0</li>
	 *            <li>the step size is negative and from &lt; to </li>
	 *            <li>the step size is positive and from &gt; li </li>
	 *         </ul>          
	 */
	public static List<Integer> series(int from, int step, int to)
	{
		if(step == 0)
			throw new IllegalArgumentException("Step value ("+step+") cannot be zero");
		if(from < to && step < 0)
			throw new IllegalArgumentException("If from ("+from+") < to("+to+"), step("+step+") should be positive."); 
		if(from > to && step > 0)
			throw new IllegalArgumentException("If from ("+from+") > to("+to+"), step("+step+") should be negative."); 
	
		if(from == to)
			return Collections.emptyList();
		
		return new IntSeries(from, step, to);
	}

	/**
	 * Returns a series of double values from 0 (inclusive) and below 
	 * a given value, with a step size of 1.0.
	 * 
	 * @param to The upper bound for the series
	 * @return An unmodifiable list of integers
	 */
	public static List<Double> series(double to)
	{
		if(to == 0)
			return Collections.emptyList();
		if(to < 0)
			return new DoubleSeries(0, -1.0, to);
		
		return new DoubleSeries(0, 1.0, to);
	}
	
	/**
	 * Returns a series of double values from a given value (inclusive) and up to 
	 * a given value.
	 * 
	 * From must always be smaller than to.
	 * 
	 * @param from The starting value. The list will always 
	 *        include at least this value so long as to > from
	 * @param to The upper bound.
	 * @return An unmodifiable list of numbers containing all integers between 
	 *         and including the parameters. If to <= from then the method 
	 *         returns an empty list. 
	 * @throws IllegalArgumentException if from > to.
	 */
	public static List<Double> series(double from, double to)
	{
		if(from > to)
			return new DoubleSeries(from, -1, to);
		if(from == to)
			return Collections.emptyList();		
		
		return new DoubleSeries(from, 1, to);
	}
	
	/**
	 * Returns a series of double values from a given value (inclusive) and below to 
	 * a given value with a given step size.
	 * 
	 * @param from The 
	 * @param step
	 * @param to
	 * @return An unmodifiable list of numbers containing all integers between 
	 *         and including the parameters for the given step size. If 
	 *         to <= from then the method returns an empty list.
	 */
	public static List<Double> series(double from, double step,double to)
	{
		if(step == 0.0)
			throw new IllegalArgumentException("Step value ("+step+") cannot be zero");
		if(from < to && step < 0)
			throw new IllegalArgumentException("If from ("+from+") < to("+to+"), step("+step+") should be positive."); 
		if(from > to && step > 0)
			throw new IllegalArgumentException("If from ("+from+") > to("+to+"), step("+step+") should be negative."); 
	
		if(from == to)
			return Collections.emptyList();		
		
		return new DoubleSeries(from, step, to);
	}	
	
	private static final class IntSeries extends AbstractList<Integer>
	{
		private int from;
		private int to;
		private int step;

		public IntSeries(int from, int step, int to)
		{
			this.from = from;
			this.to = to;
			this.step = step;
		}

		@Override
		public int size()
		{
			return (to - from)/step;
		}

		@Override
		public boolean isEmpty()
		{
			// * The static factory methods return an empty list if the parameters
			//   require one, so this type of list is never empty
			return false;
		}

		@Override
		public boolean contains(Object o)
		{
			if(! (o instanceof Integer))
				return false;
			if(isEmpty())
				return false;
			int i = (Integer)o;
			if (step >= 0)
			{
				if(i < from || i >= to)
					return false;
			} else
			{
				if(i > from || i <= to)
					return false;
			}
				
			return (i - from) % step == 0;
		}
		
		@Override
		public java.util.Iterator<Integer> iterator()
		{
			return new Iterator();
		}
		
		private class Iterator implements java.util.Iterator<Integer>
		{
			private int next = from;

			public boolean hasNext()
			{
				if(step >= 0)
					return next < to;
				else
					return next > to;
			}

			@Override
			public Integer next()
			{
				int result = next;
				next += step;
				
				return result;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
			
		}

		@Override
		public Integer get(int index)
		{
			if(index > size() -1 || index < 0)
				throw new IndexOutOfBoundsException(
					"Index ("+index+") out of bounds (0, "+(size()-1)+")");
			
			return index * step + from;
		}
	}
	
	private static final class DoubleSeries extends AbstractList<Double>
	{
		private double from;
		private double to;
		private double step;

		public DoubleSeries(double from, double step, double to)
		{
			this.from = from;
			this.to = to;
			this.step = step;
		}

		@Override
		public int size()
		{
			return (int) Math.floor((to - from)/step);
		}

		@Override
		public boolean isEmpty()
		{
			// * The static factory methods return an empty list if the parameters
			//   require one, so this type of list is never empty			
			return false;
		}

		@Override
		public boolean contains(Object o)
		{
			if(! (o instanceof Double))
				return false;
			double d = (Double)o;
			
			if (step >= 0)
			{
				if(d < from || d >= to)
					return false;
			} else
			{
				if(d > from || d <= to)
					return false;
			}			
			
			return (d - from) % step == 0.0;
		}

		@Override
		public java.util.Iterator<Double> iterator()
		{			
			return new Iterator();
		}
		
		private class Iterator implements java.util.Iterator<Double>
		{
			private double next = from;

			public boolean hasNext()
			{
				if(step >= 0)
					return next < to;
				else
					return next > to;
			}

			@Override
			public Double next()
			{
				double result = next;
				next += step;
				
				return result;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
			
		}

		@Override
		public Double get(int index)
		{
			if(index > size() -1 || index < 0)
				throw new IndexOutOfBoundsException(
					"Index ("+index+") out of bounds (0, "+(size()-1)+")");
			
			return index * step + from;
		}
	}	
}
