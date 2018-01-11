package structures.math;

import java.util.*;

/**
 * IntegerPolynomial.<br><br>
 * 
 * Created: 24.05.2008<br>
 * Last modified: 24.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class IntegerPolynomial extends Polynomial {

    private int[] coefficients;

    public IntegerPolynomial(int grade) {
        if (grade < 0) {
            throw new IllegalArgumentException("Grade must not be negative!");
        }
        this.coefficients = new int[grade + 1];
        this.coefficients[grade] = 1;
        if (grade > 0) {
            Arrays.fill(this.coefficients,0,grade,0);
        }
    }
    
    /**
     * Constructs a new instance of IntegerPolynomial.
     * @param diffCoefficients
     */
    public IntegerPolynomial(int[] coefficients) {
        this.coefficients = coefficients;
    }

    public IntegerPolynomial(Collection<Integer> coefficients) {
        this.coefficients = new int[coefficients.size()];
        int i = 0;
        for (Integer c : coefficients) {
            this.coefficients[i++] = c;
        }
    }
    
    /* (non-Javadoc)
     * @see structures.math.Polynomial#getGrade()
     */
    @Override
    public int getGrade() {
        return this.coefficients.length - 1;
    }

    /* (non-Javadoc)
     * @see structures.math.Polynomial#getValue(double)
     */
    @Override
    public double getValue(double x) {
        double res = 0.0;
        for (int i = 0; i < this.coefficients.length; i++) {
            res += this.coefficients[i] * Math.pow(x,i);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.math.Polynomial#getCoefficients()
     */
    @Override
    public double[] getCoefficients() {
        int gradePlusOne = this.coefficients.length;
        double[] res = new double[gradePlusOne];
        for (int i = 0; i < gradePlusOne; i++) {
            res[i] = this.coefficients[i];
        }
        return res;
    }
    
    public int[] getCoefficientsAsInts() {
        return this.coefficients;
    }

    public IntegerPolynomial getDifferentiationAsIntegerPolynomial() {
        int grade = this.getGrade();
        int[] diffCoefficients = new int[grade == 0 ? 1 : grade];
        for (int i = 0; i < diffCoefficients.length; i++) {
            diffCoefficients[i] = this.coefficients[i+1] * (i+1);
        }
        return new IntegerPolynomial(diffCoefficients);
    }

}
