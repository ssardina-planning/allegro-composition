package structures.graph;

import java.util.*;

import structures.Pair;

/**
 * UndirectedGraph.<br><br>
 * 
 * Created: 25.09.2006<br>
 * Last modified: 25.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class UndirectedGraph<V> extends AbstractGraph<V> {

    private Set<Edge> edges;
    
    public UndirectedGraph() {
        super();
        edges = new LinkedHashSet<Edge>();
    }
    
    public UndirectedGraph(Set<V> n) {
        super(n);
        edges = new LinkedHashSet<Edge>();
    }
    
    public UndirectedGraph(Set<V> n, Collection<Pair<V,V>> e) {
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
            res.add(new Pair<V,V>(edge.a, edge.b));
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
        
        private V a, b;
        
        private Edge(V p1, V p2) {
            if (p1 == null || p2 == null) {
                throw new NullPointerException();
            }
            if (p1.equals(p2)) {
                throw new IllegalArgumentException("In an undirected graph there are no loops on one node!");
            }
            a = p1;
            b = p2;
        }
        
        private Edge(Pair<V,V> edge) {
            this(edge.getFirst(), edge.getLast());
        }
        
        public boolean equals(Object o) {
            if (o instanceof UndirectedGraph.Edge) {
                Edge e = (Edge)o;
                return (a.equals(e.a) && b.equals(b)) || (a.equals(e.b) && b.equals(a));
            }
            return false;
        }
        
        public String toString() {
            return a.toString() + " <---> " + b.toString();
        }
        
    }

    /* (non-Javadoc)
     * @see structures.graph.Graph#hasLoops()
     */
    public boolean hasLoops() {
        for (Edge e : this.edges) {
            if (e.a.equals(e.b)) {
                return true;
            }
        }
        return false;
    }
    
}
