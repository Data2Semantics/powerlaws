package nl.peterbloem.powerlaws;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import nl.peterbloem.util.Series;
import nl.peterbloem.util.NumberComparator;

public class Functions
{
	/**
	 * A relatively arbitrary choice for epsilon
	 */
	private static final double EPSILON = 1E-12;
	
	
	/**
	 * Implementation of the Hurwitz zeta function (also known as the 
	 * generalized zeta function).
	 * 
	 * This implementation was ported from the cephes math library as included 
	 * in the scipy library (scipy/special/cephes/zeta.c)
	 * 
	 * TODO: memoize for low integer values
	 * 
	 * @param s
	 * @param k
	 * @return
	 */
	public static double zeta(double x, double q)
	{
		// * Check arguments
		if(x == 1.0)
			return Double.POSITIVE_INFINITY;
		if(q < 1.0)
			return Double.NaN;
		if(q <= 0.0)
			if(q == floor(q))
				return Double.POSITIVE_INFINITY;
			else
				return Double.NaN;
			
		double s = pow(q, -x);
		double a = q;
		double b = 0.0;
		
		int i = 0;
		
		boolean done = false;
		while( (i < 9 || a <= 9.0) && ! done)
		{
			i++;
			
			a++;
			b = pow(a, -x);
			s += b;
			
			if(abs(b/s) < EPSILON)
				done = true;
		}
		
		double k = 0.0;
		double w = a;
		s += b * w / (x - 1.0);
		s -= 0.5 * b;
		a = 1.0;

		double t;
		for(i = 0; i < 12 && ! done; i ++)
		{
			a *= x + k;
			b /= w;
			t = a * b / m[i];
			s += t;
			
			t = abs(t/s);
			if(t < EPSILON)
				done = true;
			
			k += 1.0;
			a *= x + k;
			b /= w;
			k += 1.0;
		}
		
		return s;
	}
	
	private static double m[] = {
		 12.0,
		-720.0,
		 30240.0,
		-1209600.0,
		 47900160.0,
		-1.8924375803183791606e9,  /*1.307674368e12/691*/
		 7.47242496e10,
		-2.950130727918164224e12,  /*1.067062284288e16/3617*/
		 1.1646782814350067249e14,  /*5.109094217170944e18/43867*/
		-4.5979787224074726105e15, /*8.028576626982912e20/174611*/
		 1.8152105401943546773e17,  /*1.5511210043330985984e23/854513*/
		-7.1661652561756670113e18  /*1.6938241367317436694528e27/236364091*/
		};

	
	/**
	 * The Riemann zeta function, ported from the python mpmath library.
	 * 
	 * @param s
	 * @return
	 */
	public static double zeta(double s)
	{
		if(s == 1.0)
			return Double.POSITIVE_INFINITY;
		if(s >= 27.0)
			return 1.0 + pow(2.0,-s) + pow(3.0, -s);
		
		int n = (int)s;
		if(Math.floor(s) == s)
		{
			if(n >= 0)
				return zetaInt[n];
			else
				if(n % 2 == 0)
					return 0.0;
		}
		
		if(s <= 0.0)
			return 0.0;
		
		if(s <= 1.0)
			return polynomial(s, zeta0) / (s - 1.0);
		
		if(s <= 2.0)
			return polynomial(s, zeta1) / (s - 1.0);
		
		double z = polynomial(s, zetaP) / polynomial(s, zetaQ);
		return 1.0 + pow(2.0, -s) + pow(3.0, -s) + pow(4.0, -s) * z;
	}
	
	/**
	 * Evaluates the polynomial defined by the given coefficients for argument x.
	 * 
	 * @param x
	 * @param coefficients
	 * @return
	 */
	public static double polynomial(double x, double[] coefficients)
	{
		double p = coefficients[0];
		for(int i : Series.series(1,coefficients.length))
			p = coefficients[i] + x * p;
		
		return p;
	}
	

	private static double[] zetaInt = {
		-0.5,
		0.0,
		1.6449340668482264365,1.2020569031595942854,1.0823232337111381915,
		1.0369277551433699263,1.0173430619844491397,1.0083492773819228268,
		1.0040773561979443394,1.0020083928260822144,1.0009945751278180853,
		1.0004941886041194646,1.0002460865533080483,1.0001227133475784891,
		1.0000612481350587048,1.0000305882363070205,1.0000152822594086519,
		1.0000076371976378998,1.0000038172932649998,1.0000019082127165539,
		1.0000009539620338728,1.0000004769329867878,1.0000002384505027277,
		1.0000001192199259653,1.0000000596081890513,1.0000000298035035147,
		1.0000000149015548284};
	
	private static double[] zetaP = {
			-1.85231868742346722e-11,
			-1.68030037095896287e-9,
			-1.02078104417700585e-7, 
			-4.67633010038383371e-6,
			-0.000160948723019303141, 
			-0.00398731457954257841,
			-0.0672313458590012612, 
			-0.701274355654678147,
			-3.50000000087575873, 
		}; 

	private static double[] zetaQ = {
			-1.83527919681474132e-11,
			-1.72963791443181972e-9,
			-9.58813053268913799e-8, 
			-5.10691659585090782e-6,
			-0.000143416758067432622, 
			-0.00441498861482948666,
			-0.0588835413263763741, 
			-0.936552848762465319,	
			1.00000000000000000, 
		}; 
	
	
	private static double[] zeta1 = {
			3.03768838606128127e-10,
			-1.21924525236601262e-8,
			2.01201845887608893e-7, 
			-1.53917240683468381e-6,
			-5.09890411005967954e-7, 
			0.000122464707271619326,
			-0.000905721539353130232, 
			-0.00239315326074843037,
			0.084239750013159168, 
			0.418938517907442414, 
			0.500000001921884009 
		};
	
	private static double[] zeta0 = {
			-3.46092485016748794e-10, 
			-6.42610089468292485e-9,
			1.76409071536679773e-7, 
			-1.47141263991560698e-6, 
			-6.38880222546167613e-7,
			0.000122641099800668209,
			-0.000905894913516772796,
			-0.00239303348507992713,
			0.0842396947501199816, 
			0.418938533204660256, 
			0.500000000000000052 };
	
	public static <T extends Number> List<T> unique(Collection<T> data)
	{
		Set<T> set = new LinkedHashSet<T>(data);
		List<T> list = new ArrayList<T>(set);
		
		Collections.sort(list, new NumberComparator());
		
		return list;
	}
	
	/**
	 * Shorthand for the global logger
	 * @return
	 */
	public static Logger log() { return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); }
		
}
