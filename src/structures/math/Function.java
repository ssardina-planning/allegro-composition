package structures.math;

/**
 * Function.<br><br>
 * 
 * Created: 24.05.2008<br>
 * Last modified: 24.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface Function {

    public double getValue(double x);
    public boolean isDifferentiatable(double x);
    public double getDifferentiation(double x);
    
}
