package structures;

import java.util.*;

import javax.swing.*;

/**
 * SetModel.<br><br>
 * 
 * Created: 16.06.2008<br>
 * Last modified: 16.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class SetModel<T extends DeepCopiable<T>> extends AbstractListModel implements Set<T> {

    private static final long serialVersionUID = -6662745144544897452L;
    private List<T> set;
    
    public SetModel() {
        this.set = new ArrayList<T>();
    }
    
    public SetModel(Collection<T> c) {
        this();
        this.addAll(c);
    }
    
    public SetModel(T[] array) {
        this();
        for (int i = 0; i < array.length; i++) {
            this.add(array[i]);
        }
    }
    
    public boolean add(T toAdd) {
        if (!this.set.contains(toAdd)) {
            int size = this.set.size();
            this.set.add(size, toAdd.deepCopy());
            this.fireIntervalAdded(this,size,size);
            return true;
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.util.Set#remove(java.lang.Object)
     */
    public boolean remove(Object toRemove) {
        for (int i = 0; i < this.set.size(); i++) {
            T item = this.set.get(i);
            if (item.equals(toRemove)) {
                this.set.remove(i);
                this.fireIntervalRemoved(this, i, i);
                return true;
            }
        }
        return false;
    }
    
    public void remove(int index) {
        this.set.remove(index);
        this.fireIntervalRemoved(this, index, index);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return this.set.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public T getElementAt(int index) {
        return this.set.get(index).deepCopy();
    }

    /* (non-Javadoc)
     * @see java.util.Set#size()
     */
    public int size() {
        return this.getSize();
    }

    /* (non-Javadoc)
     * @see java.util.Set#isEmpty()
     */
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Set#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return this.set.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.Set#iterator()
     */
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int pointer = 0;
            int limit = getSize();
            
            public boolean hasNext() {
                return pointer < limit;
            }

            public T next() {
                return set.get(pointer++).deepCopy();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
    }

    /* (non-Javadoc)
     * @see java.util.Set#toArray()
     */
    public Object[] toArray() {
        Object[] res = new Object[this.getSize()];
        for (int i = 0; i < this.getSize(); i++) {
            res[i] = this.getElementAt(i);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.Set#toArray(T[])
     */
    @SuppressWarnings({"hiding","unchecked"})
    public <T> T[] toArray(T[] a) {
        int size = this.getSize();
        if (a.length < size) {
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        for (int i = 0; i < a.length; i++) {
            if (i < size) {
                a[i] = (T)this.getElementAt(i);
            } else {
                a[i] = null;
            }
        }
        return a;
    }

    /* (non-Javadoc)
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Set#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends T> c) {
        boolean res = false;
        for (T item : c) {
            res |= this.add(item);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        Set<T> toRemove = new LinkedHashSet<T>();
        for (T item : this.set) {
            if (!c.contains(item)) {
                toRemove.add(item.deepCopy());
            }
        }
        if (toRemove.isEmpty()) {
            return false;
        }
        for (T item : toRemove) {
            this.remove(item);
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        boolean res = false;
        for (Object item : c) {
            res |= this.remove(item);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.Set#clear()
     */
    public void clear() {
        if (!this.set.isEmpty()) {
            int limit = this.getSize()-1;
            this.set.clear();
            this.fireIntervalRemoved(this,0,limit);
        }
    }

}
