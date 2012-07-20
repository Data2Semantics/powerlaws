package nl.peterbloem.powerlaws;

import java.util.Random;

/**
 * Constants and helper functions
 * 
 * @author Peter
 *
 */
public class PowerLaws
{
	/**
	 * Whether to apply the correction to the KS test (true) or to use the 
	 * version that was used for the results reported in Clauset 2007. The 
	 * former if more correct, the latter is required to accurately reproduce
	 * Clauset 2007.
	 * 
	 */
	public static boolean KS_CORRECT = true;
	
	/**
	 * The default random seed. May be changed during runtime.
	 */
	public static final int RANDOM_SEED = 42;
	
	/**
	 *
	 */
	public static Random random = new Random(RANDOM_SEED);
	
}
