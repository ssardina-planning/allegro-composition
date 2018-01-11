package structures;

import java.util.*;

/**
 * FilteredIterator.<br><br>
 * 
 * Created: 24.06.2008<br>
 * Last modified: 24.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class FilteredIterator<T> implements Iterator<T> {

    private Iterator<T> iterator;
    private Filter<T> filter;
    private T next;
    
    public FilteredIterator(Iterator<T> iterator, Filter<T> filter) {
        if (iterator == null || filter == null) {
            throw new NullPointerException();
        }
        this.iterator = iterator;
        this.filter = filter;
        this.getNextFiltered();
    }

    /**
     * 
     */
    private void getNextFiltered() {
        if (this.iterator.hasNext()) {
            this.next = this.iterator.next();
            while (this.filter.filterOut(this.next) && this.iterator.hasNext()) {
                this.next = this.iterator.next();
            }
            if (this.filter.filterOut(this.next)) {
                this.next = null;
            }
        } else {
            this.next = null;
        }
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return this.next != null;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public T next() {
        T res = this.next;
        if (res == null) {
            throw new NoSuchElementException();
        }
        this.getNextFiltered();
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public static <T> Iterable<T> filterIterable(Iterable<T> iterable, Filter<T> filter) {
        final Iterable<T> i = iterable;
        final Filter<T> f = filter;
        return new Iterable<T>() {

            public Iterator<T> iterator() {
                return new FilteredIterator<T>(i.iterator(), f);
            }
            
        };
    }
    
    public static <T> List<T> filterCollection(Collection<T> collection, Filter<T> filter) {
        List<T> res = new ArrayList<T>();
        for (T t : filterIterable(collection, filter)) {
            res.add(t);
        }
        return res;
    }
    
}
