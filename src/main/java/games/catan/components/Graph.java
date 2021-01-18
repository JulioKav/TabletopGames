package games.catan.components;

import java.util.*;

/* Implementation of a generic Graph using generic edges */
public class Graph<N, E> {
    private Map<N, List<Edge<N, E>>> map;

    public Graph(){
        map = new HashMap<>();
    }

    public void addEdge(N src, N dest, E value){
        Edge<N, E> edge = new Edge<>(src, dest, value);
        if (map.get(src) == null){
            ArrayList<Edge<N, E>> list = new ArrayList<>();
            list.add(edge);
            map.put(src, list);
        }
        else{
            List<Edge<N, E>> edges = map.get(src);
            if (!edges.contains(edge)){
                edges.add(edge);
            }
        }
    }

    /* Returns the connected nodes from a node
    *  */
    public List<N> getNeighbourNodes(N src){
        List<Edge<N, E>> edges = map.get(src);
        ArrayList<N> destinations = new ArrayList<>();
        for (Edge<N, E> edge: edges){
            destinations.add(edge.dest);
        }
        return destinations;
    }

    /* Returns the edges starting from the current node
     *  */
    public List<E> getConnections(N src){
        List<Edge<N, E>> edges = map.get(src);
        ArrayList<E> nodes = new ArrayList<>();
        for (Edge<N, E> edge: edges){
            nodes.add(edge.value);
        }
        return nodes;
    }

    /* Returns the the edges [src, dest, edge]
    *  */
    public List<Edge<N, E>> getEdges(N src){
        return map.get(src);
    }

    /* Iterates over all entries and prints the result */
    public void printGraph(){
        Set<N> set = map.keySet();
        for (N vertex : set) {
            System.out.println("Vertex " + vertex + " is connected to ");
            List<Edge<N, E>> list = map.get(vertex);
            for (Edge<N, E> neEdge : list) {
                System.out.print(neEdge + " ");
            }
            System.out.println();
        }
    }

}

