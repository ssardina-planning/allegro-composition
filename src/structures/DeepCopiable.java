package structures;

/**
 * DeepCopiable.<br><br>
 * 
 * Created: 16.06.2008<br>
 * Last modified: 16.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface DeepCopiable<T> {

    public T deepCopy();
    
}
