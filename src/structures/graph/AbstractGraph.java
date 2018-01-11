package structures.graph;

import java.util.*;

import structures.Pair;

/**
 * AbstractGraph.<br><br>
 * 
 * Created: 28.09.2006<br>
 * Last modified: 28.09.2006
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public abstract class AbstractGraph<V> implements Graph<V> {

    /**
     * The set of nodes in this graph.
     */
    private Set<V> nodes;
    
    /**
     * Constructs a new instance of AbstractGraph with an empty
     * set of nodes.
     */
    public AbstractGraph() {
        nodes = new LinkedHashSet<V>();
    }
    
    /**
     * Constructs a new instance of AbstractGraph with the
     * specified set of nodes.
     * @param n The set of nodes for this graph.
     */
    public AbstractGraph(Set<V> n) {
        if (n == null || n.contains(null)) {
            throw new NullPointerException();
        }
        nodes = n;
    }
    
    /* (non-Javadoc)
     * @see structures.Graph#contains(null)
     */
    public boolean contains(V node) {
        return nodes.contains(node);
    }
    
    /* (non-Javadoc)
     * @see structures.Graph#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<V> n) {
        return nodes.containsAll(n);
    }
    
    /* (non-Javadoc)
     * @see structures.Graph#containsEdge(structures.Pair)
     */
    public boolean containsEdge(Pair<V,V> edge) {
        return this.containsEdge(edge.getFirst(), edge.getLast());
    }
    
    /* (non-Javadoc)
     * @see structures.Graph#containsAllEdges(java.util.Collection)
     */
    public boolean containsAllEdges(Collection<Pair<V,V>> e) {
        boolean res = true;
        for (Pair<V,V> edge : e) {
            res &= this.containsEdge(edge);
        }
        return res;
    }
    
    /* (non-Javadoc)
     * @see structures.Graph#getNodes()
     */
    public Set<V> getNodes() {
        return new LinkedHashSet<V>(nodes);
    }

    /* (non-Javadoc)
     * @see structures.Graph#getOutgoingEdges(V)
     */
    public Collection<Pair<V,V>> getOutgoingEdges(V node) {
        if (node == null) {
            throw new NullPointerException();
        }
        Collection<Pair<V,V>> res = new ArrayList<Pair<V,V>>();
        for (Pair<V,V> edge : this.getEdges()) {
            if (edge.getFirst().equals(node)) {
                res.add(edge);
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#getIncomingEdges(V)
     */
    public Collection<Pair<V,V>> getIncomingEdges(V node) {
        if (node == null) {
            throw new NullPointerException();
        }
        Collection<Pair<V,V>> res = new ArrayList<Pair<V,V>>();
        for (Pair<V,V> edge : this.getEdges()) {
            if (edge.getLast().equals(node)) {
                res.add(edge);
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#getNeighbors(V)
     */
    public Set<V> getNeighbours(V node) {
        Set<V> res = new LinkedHashSet<V>();
        for (Pair<V,V> edge : this.getOutgoingEdges(node)) {
            res.add(edge.getLast());
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#getReachableNodes(V, int)
     */
    public Set<V> getReachableNodes(V node, int steps) {
        if (node == null) {
            throw new NullPointerException();
        }
        if (steps < 0) {
            throw new IllegalArgumentException("Steps must not be negative!");
        }
        Set<V> res = new LinkedHashSet<V>();
        Queue<V> q = new LinkedList<V>();
        q.offer(node);
        res.add(node);
        for (int i = 0; !q.isEmpty() && i < steps; i++) {
            Queue<V> level = new LinkedList<V>();
            while(!q.isEmpty()) {
                level.offer(q.poll());
            }
            while(!level.isEmpty()) {
                V actNode = level.poll();
                for (Pair<V,V> edge : getOutgoingEdges(actNode)) {
                    if (res.add(edge.getLast())) {
                        q.offer(edge.getLast());
                    }
                }
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#getReachableNodes(V)
     */
    public Set<V> getReachableNodes(V node) {
        if (node == null) {
            throw new NullPointerException();
        }
        Set<V> res = new LinkedHashSet<V>();
        Queue<V> q = new LinkedList<V>();
        q.offer(node);
        res.add(node);
        while (!q.isEmpty()) {
            V actNode = q.poll();
            for (Pair<V,V> edge : getOutgoingEdges(actNode)) {
                if (res.add(edge.getLast())) {
                    q.offer(edge.getLast());
                }
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#isReachable(V, V)
     */
    public boolean isReachable(V from, V to) {
        if (from == null || to == null) {
            throw new NullPointerException();
        }
        if (from.equals(to)) {
            return true;
        }
        Set<V> visited = new LinkedHashSet<V>();
        Queue<V> q = new LinkedList<V>();
        q.offer(from);
        visited.add(from);
        while (!q.isEmpty()) {
            V actNode = q.poll();
            for (Pair<V,V> edge : getOutgoingEdges(actNode)) {
                V next = edge.getLast();
                if (visited.add(next)) {
                    if (next.equals(to)) {
                        return true;
                    }
                    q.offer(next);
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see structures.Graph#isReachable(V, V, int)
     */
    public boolean isReachable(V from, V to, int steps) {
        if (from == null || to == null) {
            throw new NullPointerException();
        }
        if (steps < 0) {
            throw new IllegalArgumentException("Steps must not be negative!");
        }
        if (from.equals(to)) {
            return true;
        }
        Set<V> visited = new LinkedHashSet<V>();
        Queue<V> q = new LinkedList<V>();
        q.offer(from);
        visited.add(from);
        for (int i = 0; !q.isEmpty() && i < steps; i++) {
            Queue<V> level = new LinkedList<V>();
            while (!q.isEmpty()) {
                level.offer(q.poll());
            }
            while (!level.isEmpty()) {
                V actNode = level.poll();
                for (Pair<V,V> edge : getOutgoingEdges(actNode)) {
                    V next = edge.getLast();
                    if (visited.add(next)) {
                        if (next.equals(to)) {
                            return true;
                        }
                        q.offer(next);
                    }
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see structures.Graph#isSimple()
     */
    public boolean isSimple() {
        Set<Pair<V,V>> test = new LinkedHashSet<Pair<V,V>>();
        for (Pair<V,V> edge : this.getEdges()) {
            if (edge.getFirst().equals(edge.getLast()) || !test.add(new Pair<V,V>(edge.getFirst(),edge.getLast()))) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see structures.Graph#addNode(V)
     */
    public boolean addNode(V node) {
        return nodes.add(node);
    }

    /* (non-Javadoc)
     * @see structures.Graph#existEdge(V, V)
     */
    public boolean containsEdge(V from, V to) {
        if (from == null || to == null) {
            throw new NullPointerException();
        }
        for (Pair<V,V> edge : this.getOutgoingEdges(from)) {
            if (edge.getLast().equals(to)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see structures.Graph#removeNode(V)
     */
    public boolean removeNode(V node) {
        if (node == null) {
            throw new NullPointerException();
        }
        if (!nodes.contains(node) || !this.getOutgoingEdges(node).isEmpty() || !this.getIncomingEdges(node).isEmpty()) {
            return false;
        }
        return nodes.remove(node);
    }

    /* (non-Javadoc)
     * @see structures.Graph#removeAllNodes(java.util.Collection)
     */
    public boolean removeAllNodes(Collection<V> n) {
        boolean res = false;
        for (V node : n) {
            res |= this.removeNode(node);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#removeNodeWithEdges(V)
     */
    public void removeNodeWithEdges(V node) {
        if (node == null) {
            throw new NullPointerException();
        }
        this.removeAllEdges(this.getOutgoingEdges(node));
        this.removeAllEdges(this.getIncomingEdges(node));
        nodes.remove(node);
    }

    /* (non-Javadoc)
     * @see structures.Graph#removeAllNodesWithEdges(java.util.Collection)
     */
    public void removeAllNodesWithEdges(Collection<V> n) {
        for (V node : n) {
            this.removeNodeWithEdges(node);
        }
    }

    /* (non-Javadoc)
     * @see structures.Graph#removeAllEdges(java.util.Collection)
     */
    public boolean removeAllEdges(Collection<Pair<V, V>> toDel) {
        boolean res = false;
        for (Pair<V,V> edge : toDel) {
            res |= this.removeEdge(edge);
        }
        return res;
    }
    
    /* (non-Javadoc)
     * @see structures.Graph#addAllNodes(java.util.Collection)
     */
    public boolean addAllNodes(Collection<V> n) {
        boolean res = false;
        for (V node : n) {
            res |= this.addNode(node);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#addAllEdges(java.util.Collection)
     */
    public boolean addAllEdges(Collection<Pair<V, V>> e) {
        boolean res = false;
        for (Pair<V,V> edge : e) {
            res |= this.addEdge(edge);
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#retainAllNodes(java.util.Collection)
     */
    public boolean retainAllNodes(Collection<V> n) {
        boolean res = false;
        for (V node : nodes) {
            if (!n.contains(node)) {
                res |= this.removeNode(node);
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#retainAllEdges(java.util.Collection)
     */
    public boolean retainAllEdges(Collection<Pair<V, V>> retain) {
        boolean res = false;
        for (Pair<V,V> edge : this.getEdges()) {
            if (!retain.contains(edge)) {
                res |= this.removeEdge(edge);
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see structures.Graph#retainAllNodesWithEdges(java.util.Collection)
     */
    public void retainAllNodesWithEdges(Collection<V> n) {
        for (V node : nodes) {
            if (!n.contains(node)) {
                this.removeNodeWithEdges(node);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see structures.Graph#clear()
     */
    public void clear() {
        this.removeAllEdges();
        nodes.clear();
    }

}
