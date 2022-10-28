package Grafi.Strutture_Dati;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Nodo<K, V, E> {
    private K key;
    private V value;

    private HashMap<K, E> edges;

    public Nodo(K key, V value) {
        this.key = key;
        this.value = value;
        edges = new HashMap<>();
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public E getLabel(K to) {
        return edges.get(to);
    }

    public boolean hasEdge(K to) {
        return edges.containsKey(to);
    }

    public void addEdge(K to, E label) {
        edges.put(to, label);
    }

    public Arco<K, E> getEdge(K to) {
        if (edges.containsKey(to)) {
            return new Arco<>(this.key, to, edges.get(to));
        } else {
            return null;
        }
    }

    public ArrayList<Arco<K, E>> getEdges() {
        ArrayList<Arco<K, E>> arcoList = new ArrayList<>();
        this.edges.forEach((k, e) -> arcoList.add(new Arco<>(this.key, k, e)));
        return arcoList;
    }

    public ArrayList<K> getAdjacentNodesKeys() {
        return new ArrayList<>(this.edges.keySet());
    }

    public int getDegree() {
        return edges.size();
    }

    public void removeEdge(K to) {
        edges.remove(to);
    }

    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                ", value=" + value +
                ", edges=" + edges +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nodo<?, ?, ?> node = (Nodo<?, ?, ?>) o;
        return key.equals(node.key) && Objects.equals(value, node.value) && edges.equals(node.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, edges);
    }
}