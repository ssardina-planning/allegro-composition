package structures;

/**
 * Pair.<br><br>
 * 
 * Created: 23.09.2006<br>
 * Last modified: 23.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Pair<E,V> {

    private E first;
    private V last;
    
    public Pair() {
        this(null,null);
    }
    
    public Pair (E a, V b) {
        first = a;
        last = b;
    }

    /**
     * Returns the first.
     * @return The first.
     */
    public E getFirst() {
        return first;
    }

    /**
     * Sets the first.
     * @param first The first to set.
     */
    public void setFirst(E first) {
        this.first = first;
    }

    /**
     * Returns the last.
     * @return The last.
     */
    public V getLast() {
        return last;
    }

    /**
     * Sets the last.
     * @param last The last to set.
     */
    public void setLast(V last) {
        this.last = last;
    }
    
    public int hashCode() {
        return 3 * this.first.hashCode() + 5 * this.last.hashCode() + 1;
    }
    
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            Pair p = (Pair)o;
            return (this.first == null ? p.first == null : this.first.equals(p.first))
                   && (this.last == null ? p.last == null : this.last.equals(p.last));
        }
        return false;
    }
    
    public String toString() {
        return "(" + (first == null ? "null" : first.toString()) + ", " + (last == null ? "null" : last.toString()) + ")";
    }
    
}
