package structures.math;

/**
 * DifferentiatableFunction.<br><br>
 * 
 * Created: 24.05.2008<br>
 * Last modified: 24.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface DifferentiatableFunction extends Function {

    public Function getDifferentiation();
    
}
