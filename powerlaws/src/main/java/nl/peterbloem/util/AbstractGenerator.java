package nl.peterbloem.util;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGenerator<P> implements Generator<P>
{	
	@Override
	public List<P> generate(int n)
	{
		List<P> points = new ArrayList<P>(n);
		for(int i = 0; i < n; i++)
			points.add(generate());
		
		return points;
	}
}
