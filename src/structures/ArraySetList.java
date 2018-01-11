package structures;

import java.util.*;

/**
 * SetList.<br><br>
 * 
 * Created: 23.06.2008<br>
 * Last modified: 23.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class ArraySetList<T> implements List<T>, Set<T> {

    private List<T> list;
    
    public ArraySetList() {
        this.list = new ArrayList<T>();
    }
    
    public ArraySetList(Collection<? extends T> c) {
        this.list = new ArrayList<T>(new LinkedHashSet<T>(c));
    }

    // For compatibility with new Java:
    //  https://stackoverflow.com/questions/22746948/class-inherits-unrelated-defaults-for-spliterator-from-types-java-util-set-and
    @Override
    public Spliterator<T> spliterator() {
        return List.super.spliterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size() {
        return this.list.size();
    }

    /* (non-Javadoc)
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        return this.list.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    @SuppressWarnings("hiding")
    public <T>T[] toArray(T[] a) {
        return this.list.toArray(a);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(E)
     */
    public boolean add(T o) {
        if (!this.list.contains(o)) {
            return this.list.add(o);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends T> c) {
        boolean res = false;
        for (T t : c) {
            res |= this.add(t);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends T> c) {
        Set<T> toAdd = new LinkedHashSet<T>(c);
        toAdd.removeAll(this.list);
        return this.list.addAll(index, toAdd);
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        return this.list.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        return this.list.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    public void clear() {
        this.list.clear();
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public T get(int index) {
        return this.list.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, E)
     */
    public T set(int index, T element) {
        if (!this.list.contains(element)) {
            return this.list.set(index, element);
        }
        throw new IllegalArgumentException("Element is already contained in this SetList!");
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, E)
     */
    public void add(int index, T element) {
        if (!this.list.contains(element)) {
            this.list.add(index, element);
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public T remove(int index) {
        return this.list.remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    public ListIterator<T> listIterator() {
        return this.list.listIterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<T> listIterator(int index) {
        return this.list.listIterator(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    public List<T> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

}
