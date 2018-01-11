package structures;

/**
 * Transformer.<br><br>
 * 
 * Created: 24.06.2008<br>
 * Last modified: 24.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface Transformer<From, To> {

    public To transform(From object);
    
}
