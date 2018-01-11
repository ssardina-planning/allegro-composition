package structures.graph;

import java.util.Set;

/**
 * TransitionSystem.<br><br>
 * 
 * Created: 17.06.2008<br>
 * Last modified: 17.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface TransitionSystem<V,E> extends LabeledGraph<V,E> {

    public Set<V> getStartNodes();
    public Set<V> getFinalNodes();
    public boolean isStartNode(V node);
    public boolean isFinalNode(V node);
    public boolean isDeterministic();

}
