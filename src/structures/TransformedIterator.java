package structures;

import java.util.*;

/**
 * TransformedIterator.<br><br>
 * 
 * Created: 24.06.2008<br>
 * Last modified: 24.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class TransformedIterator<From, To> implements Iterator<To> {

    private Iterator<From> iterator;
    private Transformer<From, To> transformer;
    
    public TransformedIterator(Iterator<From> iterator, Transformer<From, To> transformer) {
        if (iterator == null || transformer == null) {
            throw new NullPointerException();
        }
        this.iterator = iterator;
        this.transformer = transformer;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public To next() {
        return this.transformer.transform(this.iterator.next());
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        this.iterator.remove();
    }

    public static <F,T> Iterable<T> transform(Iterable<F> iterable, Transformer<F, T> transformer) {
        final Iterable<F> i = iterable;
        final Transformer<F, T> t = transformer;
        return new Iterable<T>() {

            public Iterator<T> iterator() {
                return new TransformedIterator<F,T>(i.iterator(), t);
            }
            
        };
    }
    
    public static <F, T> List<T> transform(Collection<F> collection, Transformer<F, T> transformer) {
        List<T> res = new ArrayList<T>();
        for (T t : transform(collection, transformer)) {
            res.add(t);
        }
        return res;
    }
    
}
