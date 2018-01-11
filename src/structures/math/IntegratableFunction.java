package structures.math;

/**
 * IntegratableFunction.<br><br>
 * 
 * Created: 24.05.2008<br>
 * Last modified: 24.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface IntegratableFunction extends Function {

    public Function getIntegral();
    public Function getIntegral(double constant);
    
}
