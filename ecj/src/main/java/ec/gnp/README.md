# Genetic Network Programming

Disclaimer: There are performance issues and too few tests to consider this a properly working solution. Another important point to consider is that it was implemented by someone with just a hobby level of knowledge of all the used concepts (EC, GP, GNP, RL, SARSA etc.), even knowledge of Java itself. Another point of consideration is the design approach to base individuals on DoubleVectorIndividual which adds overhead.

Short description:
Implementation in branch gnp.
GnpIndividual extends the DoubleVectorIndividual so it's based on the genome defined as an array with segments and all the mutation/crossover functionality of DoubleVectorIndividual can be re-used. On top of the gene array a corresponding GnpNetwork is generated and kept up to date, in case of changing genes. Individual keeps the track of it's evaluations and stores the execution paths with results so they can be used in reward distribution and reporting. Evaluation depends on the network parameters like judgement and processing node count and evaluation time constraints set up in properties file, but in general it starts at the start node of the network, evaluates node by node until it runs out of time.
Interface GnpRewardDistributor is the main part of the learning process of genetic network. Implementation examples are GnpSarsa and GnpSarsaWithEligibilityTraces.

Only one example is available at this point - AntGnp, which is a copy of Ant but using Gnp functions.
Some tests can be found under test/java/ec/gnp. TestAntGPvsGnp generates out_gnp_ant.stat file which contains visual .dot representations of the GnpIndividual and evaluation paths during a describe run.
