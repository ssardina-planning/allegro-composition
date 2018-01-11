package structures.math;

/**
 * Polynomial.<br><br>
 * 
 * Created: 24.05.2008<br>
 * Last modified: 24.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public abstract class Polynomial implements DifferentiatableFunction, IntegratableFunction {

    public abstract int getGrade();
    
    public double getValue(double x) {
        double[] coefficients = this.getCoefficients();
        double res = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            res += coefficients[i] * Math.pow(x,i);
        }
        return res;
    }
    
    public abstract double[] getCoefficients();

    /**
     * @param grade
     */
    protected void checkGrade(int grade) {
        int thisGrade = this.getGrade();
        if (grade < 0 || grade > thisGrade) {
            throw new IndexOutOfBoundsException("Polynomial is of grade " + thisGrade + "!");
        }
    }

    /* (non-Javadoc)
     * @see structures.math.Function#isDifferentiatable(double)
     */
    public boolean isDifferentiatable(double x) {
        return true;
    }

    /* (non-Javadoc)
     * @see structures.math.Function#getDifferentiation(double)
     */
    public double getDifferentiation(double x) {
        return this.getDifferentiation().getValue(x);
    }
    
    public String toString() {
        double[] coefficients = this.getCoefficients();
        String res = "";
        boolean first = true;
        for (int i = coefficients.length - 1; i > 0; i--) {
            double coefficient = coefficients[i];
            if (coefficient != 0.0) {
                if (!first) {
                    res += " + ";
                }
                res += coefficient == 1.0 ? "x^" + i : "" + coefficient + "*x^" + i;
                first = false;
            }
        }
        double constant = coefficients[0];
        return res + (res.equals("") ? constant : (constant == 0.0 ? "" : " + " + constant));
    }
    
    /* (non-Javadoc)
     * @see structures.math.IntegratableFunction#getIntegral()
     */
    public RealPolynomial getIntegral() {
        double[] thisCoefficients = this.getCoefficients();
        double[] integralCoefficients = new double[thisCoefficients.length + 1];
        integralCoefficients[0] = 0.0;
        for (int i = 0; i < thisCoefficients.length; i++) {
            integralCoefficients[i+1] = thisCoefficients[i] * (1.0 / (i + 1));
        }
        return new RealPolynomial(integralCoefficients);
    }

    /* (non-Javadoc)
     * @see structures.math.IntegratableFunction#getIntegral(double)
     */
    public RealPolynomial getIntegral(double constant) {
        RealPolynomial integral = this.getIntegral();
        integral.setCoefficient(0, constant);
        return integral;
    }

    /* (non-Javadoc)
     * @see structures.math.DifferentiatableFunction#getDifferentiation()
     */
    public RealPolynomial getDifferentiation() {
        int grade = this.getGrade();
        double[] thisCoefficients = this.getCoefficients();
        double[] diffCoefficients = new double[grade == 0 ? 1 : grade];
        for (int i = 0; i < diffCoefficients.length; i++) {
            diffCoefficients[i] = thisCoefficients[i+1] * (i+1);
        }
        return new RealPolynomial(diffCoefficients);
    }

}
