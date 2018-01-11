package structures.math;

import java.util.*;

import structures.Pair;

/**
 * RealPolynomial.<br><br>
 * 
 * Created: 24.05.2008<br>
 * Last modified: 24.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class RealPolynomial extends Polynomial {

    private double[] coefficients;

    public RealPolynomial(int grade) {
        if (grade < 0) {
            throw new IllegalArgumentException("Grade must not be negative!");
        }
        this.coefficients = new double[grade + 1];
        this.coefficients[grade] = 1.0;
        if (grade > 0) {
            Arrays.fill(this.coefficients,0,grade,0.0);
        }
    }
    
    public RealPolynomial(double[] coefficients) {
        this.coefficients = coefficients;
    }
    
    public RealPolynomial(Collection<Double> coefficients) {
        this.coefficients = new double[coefficients.size()];
        int i = 0;
        for (Double d : coefficients) {
            this.coefficients[i++] = d;
        }
    }
    
    public static RealPolynomial calculateFromHints(Collection<Pair<Double,Double>> points, Collection<Pair<Double, Double>> diffs, Collection<Pair<Double,Double>> secondDiffs) {
        return null;
        //TODO
    }
    
    public double getCoefficient(int grade) {
        this.checkGrade(grade);
        return this.coefficients[grade];
    }
    
    public void setCoefficient(int grade, double coefficient) {
        this.checkGrade(grade);
        this.coefficients[grade] = coefficient;
    }
    
    /* (non-Javadoc)
     * @see structures.math.Polynomial#getGrade()
     */
    @Override
    public int getGrade() {
        return this.coefficients.length - 1;
    }

    /* (non-Javadoc)
     * @see structures.math.Polynomial#getCoefficients()
     */
    @Override
    public double[] getCoefficients() {
        return this.coefficients;
    }

}
