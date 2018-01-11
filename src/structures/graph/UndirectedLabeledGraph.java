package structures.graph;

import java.util.*;

import structures.Pair;
import structures.Triple;

/**
 * UndirectedLabeledGraph.<br><br>
 * 
 * Created: 25.09.2006<br>
 * Last modified: 25.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class UndirectedLabeledGraph<V,E> extends AbstractGraph<V> implements LabeledGraph<V,E> {

    private Set<Edge> edges;
    
    public UndirectedLabeledGraph() {
        super();
        edges = new LinkedHashSet<Edge>();
    }
    
    public UndirectedLabeledGraph(Set<V> n) {
        super(n);
        edges = new LinkedHashSet<Edge>();
    }
    
    public UndirectedLabeledGraph(Set<V> n, Set<Pair<V,V>> e) {
        super(n);
        edges = new LinkedHashSet<Edge>();
        for (Pair<V,V> edge : e) {
            if (!n.contains(edge.getFirst()) || !n.contains(edge.getLast())) {
                throw new IllegalArgumentException("Undefined edge detected!");
            }
            edges.add(new Edge(edge));
        }
    }
    
    public boolean addEdge(Pair<V, V> edge) {
        return this.addEdge(edge.getFirst(), edge.getLast());
    }

    public boolean addEdge(V from, V to) {
        if (!this.contains(from) || !this.contains(to)) {
            throw new IllegalArgumentException("Undefined node!");
        }
        return edges.add(new Edge(from, to));
    }

    public boolean removeEdge(Pair<V, V> edge) {
        return edges.remove(new Edge(edge));
    }

    public boolean removeEdge(V from, V to) {
        return edges.remove(new Edge(from, to));
    }

    public void removeAllEdges() {
        edges.clear();
    }

    public E getLabel(Pair<V, V> edge) {
        return this.getLabel(edge.getFirst(), edge.getLast());
    }

    public E getLabel(V from, V to) {
        if (!containsEdge(from, to)) {
            throw new IllegalArgumentException("There is no such edge!");
        }
        E label = null;
        for (Edge e : edges) {
            if (e.matches(from, to)) {
                label = e.label;
                break;
            }
        }
        return label;
    }

    public Collection<Triple<V, E, V>> getLabeledEdges() {
        Collection<Triple<V,E,V>> res = new LinkedHashSet<Triple<V,E,V>>();
        for (Edge edge : edges) {
            res.add(new Triple<V,E,V>(edge.from, edge.label, edge.to));
        }
        return res;
    }

    public void setLabel(Pair<V, V> edge, E label) {
        this.setLabel(edge.getFirst(), label, edge.getLast());
    }

    public void setLabel(V from, E label, V to) {
        if (!containsEdge(from, to)) {
            throw new IllegalArgumentException("There is no such edge!");
        }
        for (Edge edge : edges) {
            if (edge.matches(from, to)) {
                edge.label = label;
                break;
            }
        }
    }

    public void setLabel(Triple<V, E, V> edge) {
        this.setLabel(edge.getX(), edge.getY(), edge.getZ());
    }

    public boolean addLabeledEdge(Triple<V, E, V> edge) {
        return this.addLabeledEdge(edge.getX(), edge.getY(), edge.getZ());
    }

    public boolean addLabeledEdge(V from, E label, V to) {
        if (!this.contains(from) || !this.contains(to)) {
            throw new IllegalArgumentException("Undefined node!");
        }
        return edges.add(new Edge(from, label, to));
    }

    public Collection<Pair<V, V>> getEdges() {
        Collection<Pair<V,V>> res = new LinkedHashSet<Pair<V,V>>();
        for (Edge edge : edges) {
            res.add(new Pair<V,V>(edge.from, edge.to));
        }
        return res;
    }

    private class Edge {
        
        private V from, to;
        private E label;
        
        private Edge(V f, E l, V t) {
            if (f == null || t == null) {
                throw new NullPointerException();
            }
            if (f.equals(t)) {
                throw new IllegalArgumentException("In a simple graph there are no loops on one node!");
            }
            from = f;
            to = t;
            label = l;
        }
        
        private Edge(Triple<V,E,V> edge) {
            this(edge.getX(), edge.getY(), edge.getZ());
        }
        
        private Edge(Pair<V,V> edge) {
            this(edge.getFirst(), null, edge.getLast());
        }
        
        private Edge(V f, V t) {
            this(f, null, t);
        }
        
        private boolean matches(V f, V t) {
            return from.equals(f) && to.equals(t);
        }
        
        public boolean equals(Object o) {
            if (o instanceof UndirectedLabeledGraph.Edge) {
                Edge e = (Edge)o;
                return (from.equals(e.from) && to.equals(e.to)) || (from.equals(e.to) && to.equals(e.from));
            }
            return false;
        }
        
        public String toString() {
            return from.toString() + " <--" + (label == null ? "-> " : (" " + label.toString() + " --> ")) + to.toString();
        }
        
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#getNeighboursWithLabel(V, E)
     */
    public Set<V> getNeighboursWithLabel(V node, E label) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#getOutgoingLabeledEdges(V)
     */
    public Collection<Triple<V, E, V>> getOutgoingLabeledEdges(V node) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#getIncomingLabeledEdges(V)
     */
    public Collection<Triple<V, E, V>> getIncomingLabeledEdges(V node) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#getOutgoingEdgesWithLabel(V, E)
     */
    public Collection<Triple<V, E, V>> getOutgoingEdgesWithLabel(V node, E label) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#getIncomingEdgesWithLabel(V, E)
     */
    public Collection<Triple<V, E, V>> getIncomingEdgesWithLabel(V node, E label) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#containsLabeledEdge(V, E, V)
     */
    public boolean containsLabeledEdge(V from, E label, V to) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#containsLabeledEdge(structures.Triple)
     */
    public boolean containsLabeledEdge(Triple<V, E, V> edge) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see structures.graph.LabeledGraph#containsAllLabeledEdges(java.util.Collection)
     */
    public boolean containsAllLabeledEdges(Collection<Triple<V, E, V>> edges) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see structures.graph.Graph#hasLoops()
     */
    public boolean hasLoops() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
