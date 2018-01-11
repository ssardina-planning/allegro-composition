package structures.graph;

import java.util.*;

import structures.Pair;

/**
 * Graph.<br><br>
 * 
 * Created: 24.09.2006<br>
 * Last modified: 24.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public interface Graph<V> {
    
    public Set<V> getNodes();
    public Collection<Pair<V,V>> getEdges();
    public Collection<Pair<V,V>> getOutgoingEdges(V node);
    public Collection<Pair<V,V>> getIncomingEdges(V node);
    public Set<V> getNeighbours(V node);
    public Set<V> getReachableNodes(V node, int steps);
    public Set<V> getReachableNodes(V node);
    public boolean isReachable(V from, V to);
    public boolean isReachable(V from, V to, int steps);
    public boolean isSimple();
    public boolean hasLoops();
    public boolean addNode(V node);
    public boolean addAllNodes(Collection<V> nodes);
    public boolean addEdge(Pair<V,V> edge);
    public boolean addEdge(V from, V to);
    public boolean addAllEdges(Collection<Pair<V,V>> edges);
    public boolean contains(V node);
    public boolean containsAll(Collection<V> nodes);
    public boolean containsEdge(V from, V to);
    public boolean containsEdge(Pair<V,V> edge);
    public boolean containsAllEdges(Collection<Pair<V,V>> edges);
    public boolean removeNode(V node);
    public boolean removeAllNodes(Collection<V> nodes);
    public boolean retainAllNodes(Collection<V> nodes);
    public boolean removeEdge(Pair<V,V> edge);
    public boolean removeEdge(V from, V to);
    public boolean removeAllEdges(Collection<Pair<V,V>> toDel);
    public boolean retainAllEdges(Collection<Pair<V,V>> retain);
    public void removeNodeWithEdges(V node);
    public void removeAllNodesWithEdges(Collection<V> nodes);
    public void retainAllNodesWithEdges(Collection<V> nodes);
    public void removeAllEdges();
    public void clear();
//    protected final boolean check(Set<V> n, Set<Edge> e) {
//        if (n == null || e == null || n.contains(null)) {
//            throw new NullPointerException();
//        }
//        for (Edge edge : e) {
//            if (!n.contains(edge.from) || !n.contains(edge.to)) {
//                return false;
//            }
//        }
//        return true;
//    }
//    
//    protected final Set<Edge> toEdges(Collection<Pair<V,V>> e) {
//        Set<Edge> res =  new LinkedHashSet<Edge>();
//        for (Pair<V,V> edge : e) {
//            res.add(new Edge(edge.getFirst(), edge.getLast()));
//        }
//        return res;
//    }
//    
//    protected final void setNodes(Set<V> n) {
//        if (!check(n,edges)) {
//            throw new IllegalArgumentException("The collection of edges contains nodes that do not exist in the set of nodes!");
//        }
//        nodes = n;
//    }
//    
//    protected final void setEdges(Collection<Pair<V,V>> e) {
//        if (!check(nodes,toEdges(e))) {
//            throw new IllegalArgumentException("The collection of edges contains nodes that do not exist in the set of nodes!");
//        }
//        edges.clear();
//        for (Pair<V,V> edge : e) {
//            edges.add(new Edge(edge));
//        }
//    }
//    
//    protected final void setGraph(Set<V> n, Collection<Pair<V,V>> e) {
//        setGraph(n,toEdges(e));
//    }
//    
//    protected final void setGraph(Set<V> v, Set<Edge> e) {
//        if (!check(v,e)) {
//            throw new IllegalArgumentException("The set of edges contains nodes that do not exist in the set of nodes!");
//        }
//        nodes = v;
//        edges = e;
//    }
//    
//    protected final void setEdges(Set<Edge> e) {
//        if (e == null) {
//            throw new NullPointerException();
//        }
//        for (Edge edge : e) {
//            if (!nodes.contains(edge.from) || !nodes.contains(edge.to)) {
//                throw new IllegalStateException("The new set of edges contains nodes that do not exist in the set of nodes!");
//            }
//        }
//        edges = e;
//    }
//    
//    protected final Set<Edge> getRealEdges() {
//        return edges;
//    }
//    
//    protected final Set<V> getRealNodes() {
//        return nodes;
//    }
    
//    protected class Edge {
//        
//        private V from, to;
//        
//        public Edge(V first, V last) {
//            if (first == null || last == null) {
//                throw new NullPointerException();
//            }
//            from = first;
//            to = last;
//        }
//
//        public Edge(Pair<V, V> edge) {
//            this(edge.getFirst(),edge.getLast());
//        }
//
//        public boolean equals(Object o) {
//            return false;
//        }
//        
//        public String toString() {
//            return from.toString() + " --> " + to.toString();
//        }
//
//        /**
//         * Returns the from node.
//         * @return The from node.
//         */
//        public V getFrom() {
//            return from;
//        }
//
//        /**
//         * Sets the from node.
//         * @param from The from node to set.
//         */
//        public void setFrom(V from) {
//            this.from = from;
//        }
//
//        /**
//         * Returns the to node.
//         * @return The to node.
//         */
//        public V getTo() {
//            return to;
//        }
//
//        /**
//         * Sets the to node.
//         * @param to The to node to set.
//         */
//        public void setTo(V to) {
//            this.to = to;
//        }
//                
//    }
    
}
