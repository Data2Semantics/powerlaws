package nl.peterbloem.util;

import java.util.List;
import java.util.Random;

/**
 * Represents a class which can generate random real valued multivariate points.
 * 
 * @author Peter
 */
public interface Generator<P>
{
	public P generate();	
	
	public List<P> generate(int n);
}
