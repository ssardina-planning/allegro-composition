package structures;

import java.util.*;

import javax.swing.*;

/**
 * StandardListModel.<br><br>
 * 
 * Created: 30.05.2008<br>
 * Last modified: 30.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class StandardListModel<V> extends AbstractListModel implements List<V> {

    private static final long serialVersionUID = 1464342753273313094L;

    private List<V> elements;
    
    public StandardListModel() {
        this.elements = new ArrayList<V>();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        return this.getElement(index);
    }

    public V getElement(int index) {
        return this.elements.get(index);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return this.elements.size();
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size() {
        return this.elements.size();
    }

    /* (non-Javadoc)
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return this.elements.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    public Iterator<V> iterator() {
        return this.elements.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        return this.elements.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        return this.elements.toArray(a);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(E)
     */
    public boolean add(V o) {
        int index = this.elements.size();
        boolean res = this.elements.add(o);
        if (res) {
            this.fireIntervalAdded(this, index, index);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        int index = this.elements.indexOf(o);
        if (index == -1) {
            return false;
        } else {
            boolean res = this.elements.remove(o);
            if (res) {
                this.fireIntervalRemoved(this, index, index);
            }
            return res;
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return this.elements.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends V> c) {
        int index = this.elements.size();
        boolean res = this.elements.addAll(c);
        if (res) {
            this.fireIntervalAdded(this, index, this.elements.size() - 1);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends V> c) {
        int start = this.elements.size();
        boolean res = this.elements.addAll(index, c);
        if (res) {
            this.fireIntervalAdded(this, start, this.elements.size() - 1);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        boolean res = false;
        for (Object item : c) {
            res |= this.remove(item);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        boolean res = false;
        for (int i = this.elements.size() - 1; i >= 0; i--) {
            V item = this.elements.get(i);
            if (!c.contains(item)) {
                res = true;
                this.remove(i);
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    public void clear() {
        int index = this.elements.size() - 1;
        this.elements.clear();
        this.fireIntervalRemoved(this, 0, index);
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public V get(int index) {
        return this.elements.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, E)
     */
    public V set(int index, V element) {
        V item = this.elements.set(index, element);
        this.fireContentsChanged(this, index, index);
        return item;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, E)
     */
    public void add(int index, V element) {
        this.elements.add(index, element);
        this.fireIntervalAdded(this, index, index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public V remove(int index) {
        V item = this.elements.remove(index);
        this.fireIntervalRemoved(this, index, index);
        return item;
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        return this.elements.indexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o) {
        return this.lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    public ListIterator<V> listIterator() {
        return this.elements.listIterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<V> listIterator(int index) {
        return this.elements.listIterator(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    public List<V> subList(int fromIndex, int toIndex) {
        return this.elements.subList(fromIndex, toIndex);
    }

}
