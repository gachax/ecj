## Forked ECJ to add Genetic Network Programming

Disclaimer: There are performance issues and too few tests to consider this a properly working solution. Another important point to consider is that it was implemented by someone with just a hobby level of knowledge of all the used concepts (EC, GP, GNP, RL, SARSA etc.), even knowledge of Java itself. Another point of consideration is the design approach to base individuals on DoubleVectorIndividual which adds overhead.

Short description:
Implementation in branch gnp.
GnpIndividual extends the DoubleVectorIndividual so it's based on the genome defined as an array with segments and all the mutation/crossover functionality of DoubleVectorIndividual can be re-used. On top of the gene array a corresponding GnpNetwork is generated and kept up to date, in case of changing genes. Individual keeps the track of it's evaluations and stores the execution paths with results so they can be used in reward distribution and reporting. Evaluation depends on the network parameters like judgement and processing node count and evaluation time constraints set up in properties file, but in general it starts at the start node of the network, evaluates node by node until it runs out of time.
Interface GnpRewardDistributor is the main part of the learning process of genetic network. Implementation examples are GnpSarsa and GnpSarsaWithEligibilityTraces.

Only one example is available at this point - AntGnp, which is a copy of Ant but using Gnp functions.
Some tests can be found under test/java/ec/gnp. TestAntGPvsGnp generates out_gnp_ant.stat file which contains visual .dot representations of the GnpIndividual and evaluation paths during a describe run.

# The ECJ Evolutionary Computation Toolkit

<!--- [![Build status](https://travis-ci.org/GMUEClab/ecj.svg?branch=master)](https://travis-ci.org/GMUEClab/ecj) -->

ECJ is an evolutionary computation framework written in Java. The system was designed for large, heavyweight experimental needs and provides tools which provide many popular EC algorithms and conventions of EC algorithms, but with a particular emphasis towards genetic programming. ECJ is free open-source with a BSD-style academic license (AFL 3.0).

ECJ is now well over fifteen years old and is a mature, stable framework which has (fortunately) exhibited relatively few serious bugs over the years. Its design has readily accommodated many later additions, including multiobjective optimization algorithms, island models, master/slave evaluation facilities, coevolution, steady-state and evolution strategies methods, parsimony pressure techniques, and various new individual representations (for example, rule-sets). The system is widely used in the genetic programming community and is reasonably popular in the EC community at large, where it has formed the basis of many theses, publications, and commercial products.

## ECJ's Website

This is ECJ's repository, but [ECJ's official website](http://cs.gmu.edu/~eclab/projects/ecj/) is elsewhere.  Before doing anything else, we'd recommend you started there.

## Getting Started

For instructions on how to begin using the ECJ binary distribution and/or build the source package, take a look at the readme in the '[ecj/](ecj/)' subdirectory.

Going forward, you may also want to avail yourself of
 * the extensive [ECJ Manual](https://cs.gmu.edu/~eclab/projects/ecj/docs/manual/manual.pdf), which explains most of ECJ's features and algorims in detail, with instructions on how to use them,
 * the [ECJ tutorials](ecj/docs/tutorials),
 * and the built-in collectin of example applications (source code [here](ecj/src/main/java/ec/app), parameter files [here](ecj/src/main/resources/ec/app)).

## Citing ECJ

The preferred way to cite ECJ is

 > Sean Luke. ECJ Evolutionary Computation Library (1998).  Available for free at http://cs.gmu.edu/~eclab/projects/ecj/

or in BibTex like so:
```
@misc { Luke1998ECJSoftware,
author       = { Sean Luke },
title        = { {ECJ} Evolutionary Computation Library },
year         = { 1998 },
note         = { Available for free at http://cs.gmu.edu/$\sim$eclab/projects/ecj/  }
}
```