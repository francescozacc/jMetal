package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.component.createinitialsolutions.CreateInitialSolutions;
import org.uma.jmetal.auto.component.createinitialsolutions.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.auto.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.auto.util.observer.impl.RunTimeChartObserver;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.auto.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.component.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithRandomValue;
import org.uma.jmetal.util.AlgorithmDefaultOutputData;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;

import java.util.Arrays;

public class NSGAII {
  public static void main(String[] args) {
    DoubleProblem problem = new ZDT1();

    String pname = problem.getClass().toString() ;
    System.out.println(pname);

    System.exit(0) ;
    String referenceParetoFront = "pareto_fronts/ZDT1.pf" ;

    int populationSize = 100;
    int offspringPopulationSize = 1;
    int maxNumberOfEvaluations = 25000;

    RepairDoubleSolution crossoverSolutionRepair = new RepairDoubleSolutionWithRandomValue() ;
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    CrossoverOperator<DoubleSolution> crossover =
        new SBXCrossover(crossoverProbability, crossoverDistributionIndex, crossoverSolutionRepair);

    RepairDoubleSolution mutationSolutionRepair = new RepairDoubleSolutionWithRandomValue() ;
    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation =
        new PolynomialMutation(mutationProbability, mutationDistributionIndex, mutationSolutionRepair);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    CreateInitialSolutions<DoubleSolution> createInitialPopulation =
        new RandomSolutionsCreation<>(problem, populationSize);

    Termination termination = new TerminationByEvaluations(maxNumberOfEvaluations);

    Variation<DoubleSolution> variation =
        new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);

    Ranking<DoubleSolution> ranking = new DominanceRanking<>(new DominanceComparator<>()) ;
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>() ;

    MultiComparator<DoubleSolution> rankingAndCrowdingComparator = new MultiComparator<>(
        Arrays.asList(
                ranking.getSolutionComparator(),
                densityEstimator.getSolutionComparator()));

    /*
    MatingPoolSelection<DoubleSolution> selection =
        new NaryTournamentMatingPoolSelection<>(
            2, variation.getMatingPoolSize(), rankingAndCrowdingComparator);
            */
    MatingPoolSelection<DoubleSolution> selection =
            new RandomMatingPoolSelection<>(variation.getMatingPoolSize()) ;

    Replacement<DoubleSolution> replacement = new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator) ;

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new EvolutionaryAlgorithm<>(
                "NSGA-II",
                evaluation,
                createInitialPopulation,
                termination,
                selection,
                variation,
                replacement);

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>("NSGA-II", 80, referenceParetoFront) ;

    algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();

    AlgorithmDefaultOutputData.generateMultiObjectiveAlgorithmOutputData(
        algorithm.getResult(), algorithm.getTotalComputingTime());

    System.exit(0);
  }
}
