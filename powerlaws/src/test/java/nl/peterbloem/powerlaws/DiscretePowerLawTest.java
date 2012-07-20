package nl.peterbloem.powerlaws;

import static nl.peterbloem.util.Series.series;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import nl.peterbloem.powerlaws.Discrete;
import nl.peterbloem.util.Series;


public class DiscretePowerLawTest
{

	@Test
	public void testPInv()
	{
		Discrete dpl = new Discrete(5, 2.5);
		
		for(int i : series(5, 100))
			assertEquals(i, dpl.cdfInv(dpl.cdfComp(i)));
		
	}

	
	@Test
	public void testCount()
	{
		List<Integer> data = Arrays.asList(1,2,10, 12, 19);
		
		int i = 1;
		for(int count : Discrete.count(1, 19, data))
		{
			System.out.println(i + " " + count);
			i++;
		}
			
	}
}
