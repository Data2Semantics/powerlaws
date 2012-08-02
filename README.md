Power laws
=========

This is a small java library for the analysis of power law distributed data. The
methods implemented are all taken form the paper _Power-Law Distributions in 
Empirical Data_ by Clauset, Shalizi and Newman (2007) and its reference 
implementations available at http://tuvalu.santafe.edu/~aaronc/powerlaws/

It contains facilities for estimating parameters, uncertainty and significance 
and for generating power law distributed data. All methods are implemented for 
continuous data, discrete data and the approximation of discrete data with a 
continuous distribution.

If you use maven and Git, you can check the project out directly from GitHub. If
not, you can download the whole repository as a zip file or tar ball. A JAR
 download is provided at https://github.com/pbloem/powerlaws/downloads although 
this may not contain all the most recent code.
 
# The Basics

The main thing you're likely to want to do is estimate the exponent for 
some data. Say you have your data as a Collection of double values called 'data'.
You can estimate the exponent as follows:

```java
double exponent = Continuous.fit(data).fit().exponent();
```

The second fit() method above actually returns a representation of a whole 
continuous power law distribution, which you can use to do various things:

```java
Continuous distribution = Continuous.fit(data).fit();

// * Retrieve the distribution's parameters
double exponent = distribution.exponent();
double xMin = distribution.xMin();

// * Generate some synthetic data
List<Double> generated = distribution.generate(1000);
```

Of course, if you have a specific distribution in mind, you can create the 
Continuous object directly:
 
 ```java
 // * Create a continuous power law distribution with xMin 340 and exponent 2.5 
 Continuous distribution = new Continuous(340, 2.5);
 ```

If your data is discrete (ie. integers) you can use the class Discrete in an 
analogous way. Since the methods in Discrete are a bit slower than those in 
Continuous, you can also use DiscreteApproximate, which approximates a discrete
power law with a continuous one. According to Clauset, this use is justified if
xMin is larger than 6. For xMin > 1000 you can safely use the approximate 
version.

#The Details

## Uncertainty

The uncertainty measures, in a sense, the robustness of our estimates. If a 
small change in the data leads to a massively different value, then we cannot 
rely on it, or we must gather more data.

We estimate the uncertainty by _bootstrapping_. We repeat the experiment a large 
number of times (1000 to 10000) with data sets that are sampled with replacement
from the original data set. For each we record the measured exponent, the xMin 
parameter and the number of points in the tail. In each case the sample standard 
deviation of the recorded values is the uncertainty.

## Varying xMin

The method of fitting described above uses a maximum likelihood estimator to 
estimate the exponent for a given xMin parameter. It then chooses as xMin the 
data point for which the resulting distribution has the smallest KS statistic to 
the data. If you want to test different values of xMin, you can use the Fit 
object which represents the intermediate stage of fitting a power law to data:

```java
// * Create a Fit object
Fit<Double, PowerLaw<Double>> fit = Continuous.fit(data);

// * Print out the distances for all of the data points
for(double datum : data)
	System.out.println(datum + ": " + fit.fit(datum)); 
```

## Significance

Any data can have a power law fit to it. To ascertain whether the power law is
a proper model, we must calculate the significance. To do this, we generate 
a large number of synthetic data sets like our original data set and fit a model
to it. We then calculate the KS statistic between the generated data and its 
model. The ratio of generated data sets with a distance higher than that of our 
fit is the probability of seeing a fit as 'bad' as ours given that the data comes
from a power law distribution.

```java
Continuous model = Continuous.fit(data).fit();

// * Calculate the significance based on 100 trials
double significance = model.significance(data, 1000);
// * Calculate the significance to an accuracy of 0.01
double significance = model.significance(data, 0.01);
```

The calculation of the significance is by far the most expensive process. For a 
large data set, the process an easily take hours. Note that this is a 
significance test for the the power law hypothesis (rather than the null 
hypothesis) so that _high_ values mean that the power law is a good fit. Clauset 
et al. suggest that for values below 0.01 the power law hypothesis should be 
rejected. 

## Randomness

All random numbers come from PowerLaws.random. By default this Random has a 
fixed seed so that runs can be deterministically repeated. If this behaviour
is not desired, you can give the Random a random seed:
```java
PowerLaws.random = new Random();
```

## The KS Test

The Kolmogorov-Smirnov test is used to estimate the xMin parameter. There is a
small difference between the reference implementation of Clauset 2007 and the 
official definition of the KS test. The change does not cause a great difference 
in measured values (within uncertainties) but it is important when replicating 
the results in Clauset 2007. 

The constant PowerLaws.KS_CORRECT determines which version of the KS test is 
used. If true, the theoretically correct version is used, if false, the version 
based on the reference implementation is used.

# Contact

For bug fixes and suggestions, GitHub is preferred (send pbloem a message or 
create a ticket). If you don't have a GitHub account you can email to 
p & peterbloem & nl. (replacing the ampersands with an at symbol and a dot 
respectively).

# License

This library is released under the MIT license. You are free to create derivative
works, including for commercial purposes, and to re-release under another license.
Attribution is not required, but it is always appreciated. See the file 
LICENSE.txt for the full text of the license.

![githalytics.com alpha](https://cruel-carlota.pagodabox.com/fbbc7703db32ced91f2edd767774a2dc)

