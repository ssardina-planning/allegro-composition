package structures;

/**
 * Triple.<br><br>
 * 
 * Created: 24.09.2006<br>
 * Last modified: 24.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Triple<A,B,C> {

    private A frst;
    private B scnd;
    private C thrd;
    
    public Triple() {
        this(null,null,null);
    }
    
    public Triple (A a, B b, C c) {
        frst = a;
        scnd = b;
        thrd = c;
    }

    /**
     * Returns the first value.
     * @return The first value.
     */
    public A getX() {
        return frst;
    }

    /**
     * Sets the first value.
     * @param frst The first value to set.
     */
    public void setX(A frst) {
        this.frst = frst;
    }

    /**
     * Returns the second value.
     * @return The second value.
     */
    public B getY() {
        return scnd;
    }

    /**
     * Sets the second value.
     * @param scnd The second value to set.
     */
    public void setY(B scnd) {
        this.scnd = scnd;
    }

    /**
     * Returns the third value.
     * @return The third value.
     */
    public C getZ() {
        return thrd;
    }

    /**
     * Sets the third value.
     * @param thrd The third value to set.
     */
    public void setZ(C thrd) {
        this.thrd = thrd;
    }
    
    public int hashCode() {
        return 3 * this.frst.hashCode() + 5 * this.scnd.hashCode() + 7 * this.thrd.hashCode() + 1;
    }
    
    public boolean equals(Object o) {
        if (o instanceof Triple) {
            Triple t = (Triple)o;
            return (this.frst == null ? t.frst == null : this.frst.equals(t.frst))
                   && (this.scnd == null ? t.scnd == null : this.scnd.equals(t.scnd))
                   && (this.thrd == null ? t.thrd == null : this.thrd.equals(t.thrd));
        }
        return false;
    }
    
    public String toString() {
        return "(" + (this.frst == null ? "null" : this.frst.toString()) + ", " + (this.scnd == null ? "null" : this.scnd.toString()) + ", " + (this.thrd == null ? "null" : this.thrd.toString()) + ")";
    }
    
    public Pair<A,B> getButZ() {
        return new Pair<A,B>(frst,scnd);
    }
    
    public Pair<A,C> getButY() {
        return new Pair<A,C>(frst,thrd);
    }
    
    public Pair<B,C> getButX() {
        return new Pair<B,C>(scnd,thrd);
    }
    
}
