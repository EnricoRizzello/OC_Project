package Grafi.Strutture_Dati;

import java.util.Objects;

public class Arco<K, E> {
    private K from;
    private K to;
    private E label;

    public Arco(K from, K to, E label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }

    public K getFrom() {
        return from;
    }

    public K getTo() {
        return to;
    }

    public E getLabel() {
        return label;
    }

    public Arco<K, E> inverse() {
        return new Arco<>(to, from, label);
    }

    public boolean isIncidentFor(K node){
        return from.equals(node) || to.equals(node);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", label=" + label +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arco<?, ?> arco = (Arco<?, ?>) o;
        return from.equals(arco.from) && to.equals(arco.to) && label.equals(arco.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, label);
    }
}