package nl.peterbloem.util;

import java.util.Comparator;

public class NumberComparator implements Comparator<Number>
{
	@Override
	public int compare(Number first, Number second)
	{
		return Double.compare(first.doubleValue(), second.doubleValue());
	}
	
}