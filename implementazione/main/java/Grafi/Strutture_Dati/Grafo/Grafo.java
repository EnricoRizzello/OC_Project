package Grafi.Strutture_Dati.Grafo;

import Grafi.Strutture_Dati.Arco;
import Grafi.Strutture_Dati.Nodo;
import Grafi.Strutture_Dati.Grafo.Grafo_Eccezioni.Eccezione_Arco_Mancante;
import Grafi.Strutture_Dati.Grafo.Grafo_Eccezioni.Eccezione_Nodo_Mancante;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Grafo<K, V, E> implements Cloneable {
//deep copy dell'oggetto (nuova istanza identica alla precedente)
    private final HashMap<K, Nodo<K, V, E>> nodes;

    private final boolean directed;

    public Grafo(boolean directed) {
        nodes = new HashMap<>();
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public int nodeCount() {
        return nodes.size();
    }

    public int edgeCount() {
        int edgesCounter = nodes.values().stream().mapToInt(Nodo::getDegree).sum();
        return directed ? edgesCounter : edgesCounter / 2;
    }

    public Grafo<K, V, E> addNode(K key) {
        return addNode(key, null);
    }

    public Grafo<K, V, E> addNode(K key, V value) {
        if (!containsNode(key)) {
            nodes.put(key, new Nodo<>(key, value));
        }
        return this;
    }

    public Nodo<K, V, E> getNode(K key) {
        return nodes.get(key);
    }

    public boolean containsNode(K key) {
        return nodes.containsKey(key);
    }

    public ArrayList<Nodo<K, V, E>> getNodes() {
        return new ArrayList<>(this.nodes.values());
    }

    public Grafo<K, V, E> removeNode(K key) {
        for (Nodo<K, V, E> node : nodes.values()) {
            node.removeEdge(key);
        }

        nodes.remove(key);

        return this;
    }

    public Grafo<K, V, E> addEdge(Arco<K, E> arco) throws Eccezione_Nodo_Mancante {
        //System.out.println("Aggiungo arco: " + arco.toString());
        return this.addEdge(arco.getFrom(), arco.getTo(), arco.getLabel());
    }

    @SuppressWarnings("Duplicates")
    public Grafo<K, V, E> addEdge(K from,  K to,  E label) throws Eccezione_Nodo_Mancante {
        Nodo<K, V, E> fromNode = nodes.get(from);
        Nodo<K, V, E> toNode = nodes.get(to);

        if (fromNode == null) {
            if (toNode == null) {
                throw new Eccezione_Nodo_Mancante("Missing Nodes FROM and TO in graph.");
            } else {
                throw new Eccezione_Nodo_Mancante("Missing Node FROM in graph.");
            }
        } else {
            if (toNode == null) {
                throw new Eccezione_Nodo_Mancante("Missing Node TO in graph.");
            }
        }

        if (!fromNode.hasEdge(to)) {
            fromNode.addEdge(to, label);
            if (!directed) {
                toNode.addEdge(from, label);
            }
        }

        return this;
    }

    public Grafo<K, V, E> addNodesEdge( K fromKey,  K toKey,  E label) {
        return addNodesEdge(fromKey, null, toKey, null, label);
    }

    public Grafo<K, V, E> addNodesEdge( K fromKey,  V fromValue,  K toKey,  V toValue,
                                        E label) {

        Nodo<K, V, E> fromNode = nodes.get(fromKey);
        Nodo<K, V, E> toNode = nodes.get(toKey);

        if (fromNode == null) {
            addNode(fromKey, fromValue);
            fromNode = nodes.get(fromKey);
        }

        if (toNode == null) {
            addNode(toKey, toValue);
            toNode = nodes.get(toKey);
        }

        if (!fromNode.hasEdge(toKey)) {
            fromNode.addEdge(toKey, label);
            if (!directed) {
                toNode.addEdge(fromKey, label);
            }
        }

        return this;
    }

    public
    Arco<K, E> getEdge(K from, K to) {
        // Thanks prof.
        Nodo<K, V, E> fromNode = nodes.get(from);

        if (fromNode != null) {
            return fromNode.getEdge(to);
        } else {
            return null;
        }
    }

    public boolean containsEdge(K from, K to) {
        return getEdge(from, to) != null;
    }

    public ArrayList<Arco<K, E>> getEdges() {
        return getEdges(false);
    }

    /**
     * Returns all edges of the graph.
     *
     * @param returnAllEdges set to true if ALL edges are needed (useful with undirected graphs if both copies of an
     *                       arco are needed: (1,2) and (2,1) for example).
     * @implNote If the graph is undirected, only one arco is returned (this graph implementation uses two directed
     * edges for every undirected arco).
     */
    public ArrayList<Arco<K, E>> getEdges(boolean returnAllEdges) {
        ArrayList<Arco<K, E>> arcos = new ArrayList<>();
        if (directed || returnAllEdges) {
            for (Nodo<K, V, E> node : nodes.values()) {
                arcos.addAll(node.getEdges());
            }
        } else {
            HashMap<K, HashSet<K>> edgesPresent = new HashMap<>();

            // If the graph is undirected, an arco is sufficent; we don't need the inverse.
            for (Nodo<K, V, E> node : nodes.values()) {
                for (Arco<K, E> arco : node.getEdges()) {
                    K fromNode = arco.getFrom();
                    K toNode = arco.getTo();

                    boolean inversePresent = edgesPresent.containsKey(toNode) &&
                                             edgesPresent.get(toNode).contains(fromNode);

                    if (!inversePresent) {
                        HashSet<K> forwardStar = edgesPresent.computeIfAbsent(fromNode, k -> new HashSet<>());
                        forwardStar.add(toNode);
                        arcos.add(arco);
                    }
                }
            }
        }
        return arcos;
    }

    public ArrayList<Nodo<K, V, E>> getAdjacentNodes(K key) {
        ArrayList<Nodo<K, V, E>> adjacentNodes = new ArrayList<>();
        nodes.get(key).getAdjacentNodesKeys().forEach(k -> adjacentNodes.add(nodes.get(k)));
        return adjacentNodes;
    }

    @SuppressWarnings("Duplicates")

    public Grafo<K, V, E> updateEdge( K from,  K to,  E newLabel) throws Eccezione_Nodo_Mancante,
            Eccezione_Arco_Mancante {
        Nodo<K, V, E> fromNode = nodes.get(from);
        Nodo<K, V, E> toNode = nodes.get(to);

        if (fromNode == null) {
            if (toNode == null) {
                throw new Eccezione_Nodo_Mancante("Missing Nodes FROM and TO in graph.");
            } else {
                throw new Eccezione_Nodo_Mancante("Missing Node FROM in graph.");
            }
        } else {
            if (toNode == null) {
                throw new Eccezione_Nodo_Mancante("Missing Node TO in graph.");
            }
        }

        // We should update an arco only if it already exists.
        // This check is needed because we use a property of HashMaps:
        // if we add a value with a key that already exists, the old value is replaced.
        // This removed the need to make a custom method.
        if (fromNode.hasEdge(to)) {
            fromNode.addEdge(to, newLabel);
            if (!directed) {
                toNode.addEdge(from, newLabel);
            }
        } else {
            throw new Eccezione_Arco_Mancante("Missing EDGE in graph.");
        }

        return this;
    }

    public Grafo<K, V, E> removeEdge( K from,  K to) throws Eccezione_Nodo_Mancante {
        Nodo<K, V, E> fromNode = nodes.get(from);
        Nodo<K, V, E> toNode = nodes.get(to);

        if (fromNode != null) {
            fromNode.removeEdge(to);
            if (!directed) {
                if (toNode != null) {
                    toNode.removeEdge(from);
                } else {
                    throw new Eccezione_Nodo_Mancante("Missing Node TO in graph");
                }
            }
        } else {
            throw new Eccezione_Nodo_Mancante("Missing Node FROM in graph.");
        }

        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Grafo<K, V, E> clone() {
        Grafo<K, V, E> newGraph = new Grafo<>(this.directed);

        // copy all edges of all nodes.
        for (Nodo<K, V, E> node : getNodes()) {
            for (Arco<K, E> arco : node.getEdges()) {
                Nodo<K, V, E> toNode = getNode(arco.getTo());
                //noinspection ConstantConditions
                newGraph.addNodesEdge(node.getKey(), node.getValue(),
                                      toNode.getKey(), toNode.getValue(),
                                      arco.getLabel());
            }
        }

        return newGraph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grafo<?, ?, ?> graph = (Grafo<?, ?, ?>) o;
        return directed == graph.directed && nodes.equals(graph.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, directed);
    }

    public String toString(){
        StringBuilder r = new StringBuilder();

        for (Arco<K,E> arco : getEdges()) {
            r.append("(").append(arco.getFrom()).append(", ").append(arco.getTo()).append(")   ");
        }

        return r.toString();
    }
}
