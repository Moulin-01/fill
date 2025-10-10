package NewNodeArray.src;

/**
 * PILHA (LIFO) encadeada.
 * Usada no Flood Fill VERTICAL (scanline por COLUNAS) — estratégia DFS.
 */
final class MyStack<T> {

    /** Nó encadeado da pilha */
    private static final class Node<U> {
        U v;
        Node<U> next;
        Node(U v) { this.v = v; }
    }

    private Node<T> top; // topo da pilha (LIFO)
    private int size;

    boolean isEmpty() { return top == null; }

    int size() { return size; }

    void push(T v) {
        Node<T> n = new Node<>(v);
        n.next = top;   // novo aponta para o antigo topo
        top = n;        // vira o topo
        size++;
    }

    T peek() {
        return top.v;
    }

    T pop() {
        T v = top.v;
        top = top.next;
        size--;
        return v;
    }

    void clear() {
        top = null;
        size = 0;
    }
}
