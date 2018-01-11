package structures.graph;

/**
 * Node.<br><br>
 * 
 * Created: 28.09.2006<br>
 * Last modified: 28.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Node<V> {

    private V object;
    
    /**
     * Constructs a new instance of Node with null as object.
     */
    public Node() {
        object = null;
    }
    
    /**
     * Constructs a new instance of Node with the specified object.
     * @param obj The node's object.
     */
    public Node(V obj) {
        object = obj;
    }

    /**
     * Returns the object.
     * @return The object.
     */
    public V getObject() {
        return object;
    }

    /**
     * Sets the object.
     * @param object The object to set.
     */
    public void setObject(V object) {
        this.object = object;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Node: " + object.toString();
    }
    
}
