package structures.graph;

import java.util.*;

import structures.Pair;
import structures.Triple;

/**
 * LabeledGraph.<br><br>
 * 
 * Created: 25.09.2006<br>
 * Last modified: 25.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface LabeledGraph<V,E> extends Graph<V> {

    public E getLabel(Pair<V,V> edge);
    public E getLabel(V from, V to);
    public Collection<Triple<V,E,V>> getLabeledEdges();
    public void setLabel(Pair<V,V> edge, E label);
    public void setLabel(V from, E label, V to);
    public void setLabel(Triple<V,E,V> edge);
    public boolean addLabeledEdge(Triple<V,E,V> edge);
    public boolean addLabeledEdge(V from, E label, V to);
    public Set<V> getNeighboursWithLabel(V node, E label);
    public Collection<Triple<V,E,V>> getOutgoingLabeledEdges(V node);
    public Collection<Triple<V,E,V>> getIncomingLabeledEdges(V node);
    public Collection<Triple<V,E,V>> getOutgoingEdgesWithLabel(V node, E label);
    public Collection<Triple<V,E,V>> getIncomingEdgesWithLabel(V node, E label);
    public boolean containsLabeledEdge(V from, E label, V to);
    public boolean containsLabeledEdge(Triple<V,E,V> edge);
    public boolean containsAllLabeledEdges(Collection<Triple<V,E,V>> edges);
//    protected final Set<Graph<V>.Edge> toLabeledEdgesFromPairs(Collection<Pair<V,V>> e) {
//        Set<Graph<V>.Edge> res = new LinkedHashSet<Graph<V>.Edge>();
//        for (Pair<V,V> edge : e) {
//            res.add(new LabeledEdge(edge.getFirst(), edge.getLast()));
//        }
//        return res;
//    }
//    
//    protected final Set<Graph<V>.Edge> toLabeledEdgesWithMap(Collection<Pair<V,V>> e, Map<Pair<V,V>,E> map) {
//        Set<Graph<V>.Edge> res = new LinkedHashSet<Graph<V>.Edge>();
//        for (Pair<V,V> edge : e) {
//            res.add(new LabeledEdge(edge.getFirst(), map.get(edge), edge.getLast()));
//        }
//        return res;
//    }
//    
//    protected final Set<Graph<V>.Edge> toLabeledEdgesFromTriples(Collection<Triple<V,E,V>> e) {
//        Set<Graph<V>.Edge> res = new LinkedHashSet<Graph<V>.Edge>();
//        for (Triple<V,E,V> edge : e) {
//            res.add(new LabeledEdge(edge.getX(),edge.getY(),edge.getZ()));
//        }
//        return res;
//    }
//    
//    protected class LabeledEdge extends Graph<V>.Edge {
//        
//        private E label;
//        
//        public LabeledEdge(V from, V to) {
//            this(from,null,to);
//        }
//        
//        public LabeledEdge(Pair<V,V> edge) {
//            this(edge.getFirst(),null,edge.getLast());
//        }
//        
//        public LabeledEdge(V from, E l, V to) {
//            super(from,to);
//            label = l;
//        }
//        
//        public LabeledEdge(Pair<V,V> edge, E l) {
//            this(edge.getFirst(),l,edge.getLast());
//        }
//
//        /**
//         * Returns the label.
//         * @return The label.
//         */
//        public E getLabel() {
//            return label;
//        }
//
//        /**
//         * Sets the label.
//         * @param label The label to set.
//         */
//        public void setLabel(E label) {
//            this.label = label;
//        }
//        
//    }
    
}
