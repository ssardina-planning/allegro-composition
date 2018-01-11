package structures.graph;

import java.util.*;

import structures.Pair;

/**
 * SimpleDirectedGraph.<br><br>
 * 
 * Created: 25.09.2006<br>
 * Last modified: 25.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class SimpleGraph<V> extends AbstractGraph<V> {

    private Set<Edge> edges;
    
    public SimpleGraph() {
        super();
        edges = new LinkedHashSet<Edge>();
    }
    
    public SimpleGraph(Set<V> n) {
        super(n);
        edges = new LinkedHashSet<Edge>();
    }
    
    public SimpleGraph(Set<V> n, Set<Pair<V,V>> e) {
        super(n);
        edges = new LinkedHashSet<Edge>();
        for (Pair<V,V> edge : e) {
            if (!n.contains(edge.getFirst()) || !n.contains(edge.getLast())) {
                throw new IllegalArgumentException("Undefined edge detected!");
            }
            edges.add(new Edge(edge));
        }
    }
    
    public Collection<Pair<V, V>> getEdges() {
        Collection<Pair<V,V>> res = new LinkedHashSet<Pair<V,V>>();
        for (Edge edge : edges) {
            res.add(new Pair<V,V>(edge.from, edge.to));
        }
        return res;
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
    
    private class Edge {
        
        private V from, to;
        
        private Edge(V f, V t) {
            if (f == null || t == null) {
                throw new NullPointerException();
            }
            if (f.equals(t)) {
                throw new IllegalArgumentException("In a simple graph there are no loops on one node!");
            }
            from = f;
            to = t;
        }
        
        private Edge(Pair<V,V> edge) {
            this(edge.getFirst(), edge.getLast());
        }
        
        public boolean equals(Object o) {
            if (o instanceof SimpleGraph.Edge) {
                Edge e = (Edge)o;
                return from.equals(e.from) && to.equals(e.to);
            }
            return false;
        }
        
        public String toString() {
            return from.toString() + " ---> " + to.toString();
        }
        
    }

    /* (non-Javadoc)
     * @see structures.graph.Graph#hasLoops()
     */
    public boolean hasLoops() {
        for (Edge e : this.edges) {
            if (e.from.equals(e.to)) {
                return true;
            }
        }
        return false;
    }

}
