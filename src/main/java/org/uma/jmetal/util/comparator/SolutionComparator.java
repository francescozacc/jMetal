//  SolutionComparator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.util.comparator;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;

import java.util.Comparator;
import java.util.logging.Level;

/**
 * This class implements a Comparator (a method for comparing
 * Solution objects) based on the values of the variables.
 */
public class SolutionComparator implements Comparator<Solution> {

  /**
   * Establishes a value of allowed dissimilarity
   */
  private static final double EPSILON = 1e-25;

  /**
   * Compares two solutions.
   *
   * @param o1 Object representing the first solutiontype.
   * @param o2 Object representing the second solutiontype.
   * @return 0, if both solutions are equals with a certain dissimilarity, -1
   * otherwise.
   */
  @Override
  public int compare(Solution o1, Solution o2) {
    Solution solution1, solution2;
    solution1 = (Solution) o1;
    solution2 = (Solution) o2;

    if ((solution1.getDecisionVariables() != null) && (solution2.getDecisionVariables() != null)) {
      if (solution1.numberOfVariables() != solution2.numberOfVariables()) {
        return -1;
      }
    }

    try {
      if ((new Distance()).distanceBetweenSolutions(solution1, solution2) < EPSILON) {
        return 0;
      }
    } catch (JMetalException e) {
      Configuration.logger_.log(Level.SEVERE, "SolutionComparator.compare: JMException ", e);
    }

    return -1;
  }
}
