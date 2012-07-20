package nl.peterbloem.powerlaws;

public class Uncertainties
{

	private double alphaUncertainty;
	private double xMinUncertainty;
	private double nTailUncertainty;
	
	public Uncertainties(double alphaUncertainty, double xMinUncertainty,
			double nTailUncertainty)
	{
		this.alphaUncertainty = alphaUncertainty;
		this.xMinUncertainty = xMinUncertainty;
		this.nTailUncertainty = nTailUncertainty;
	}

	/**
	 * Return the uncertainty value for the alpha parameter.
	 * @return
	 */
	public double alpha()
	{
		return alphaUncertainty;
	}

	/**
	 * Return the uncertainty value for the xMin parameter.
	 * @return
	 */
	public double xMin()
	{
		return xMinUncertainty;
	}

	/**
	 * Return the uncertainty parameter for nTail, the number of datapoints in 
	 * the tail of the distribution (ie. above or equal to xMin)
	 *   
	 * @return
	 */
	public double nTail()
	{
		return nTailUncertainty;
	}

}
