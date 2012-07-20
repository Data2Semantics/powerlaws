package nl.peterbloem.powerlaws;

import static nl.peterbloem.powerlaws.Functions.zeta;
import static org.junit.Assert.*;

import org.junit.Test;

public class FunctionsTest
{

	@Test
	public void testZeta()
	{
		assertEquals(1.64493406684822, zeta(2.0, 1.0), 10E-12);
		assertEquals(0.13182783346272, zeta(3.456, 2.0), 10E-12);
		assertEquals(1.55869618375063E-26, zeta(20.0, 20.0), 10E-12);
		
//		for (int i : Series.series(20))
//		{
//			double s = Global.random.nextDouble() * 4.0 + 1.0;
//			double q = Global.random.nextDouble() * 4.0 + 1.0;
//			
//			
//			System.out.println(s + " " + q);
//			assertEquals(zetaBF(s, q), zeta(s, q), 0.0001);
//		}
	}
	
	/**
	 * Brute forc implementation of the zeta function
	 * @param x
	 * @param q
	 * @return
	 */
	public static double zetaBF(double x, double q)
	{
		double epsilon = 10E-12;
		
		double series = 0.0;
		double term = Double.POSITIVE_INFINITY;
		
		int i = 0;
		while(term > epsilon)
		{
			if(i % 100 == 0)
				System.out.println("* " + series);
			term = Math.pow(q + i, x);
			term = 1.0 / term;
			
			series += term;
			i++;
		}
		
		return series;
	}
	
	@Test
	public void testZetaSingle()
	{
		assertEquals(1.64493406684822, zeta(2.0), 10E-12);
		assertEquals(1.13182783346272, zeta(3.456), 10E-12);
		assertEquals(1.00000089006735, zeta(20.1), 10E-12);

		assertEquals(2.61237534868548, zeta(1.5), 10E-12);	
		assertEquals(1.34148725725091, zeta(2.5), 10E-12);
		assertEquals(1.18338365211190, zeta(3.1), 10E-12);
	}

}
