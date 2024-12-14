package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int HISTORY_LIST_SIZE = 10;
    private final List<Task> history;
    private final Map<Integer, Node> nodes;
    private Node head;
    private Node tail;
    private int size;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
        nodes = new HashMap<>();
        size = 0;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > HISTORY_LIST_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }

    public void linkLast(Task task) {
        Node oldTail = tail;
        Node newTail = new Node(oldTail, null, task);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        size++;
        nodes.put(tail.data.getId(), tail);
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node curNode = head;
        for (int i = 0; i < size; i++) {
            tasks.add(curNode.data);
            curNode = curNode.next;
        }
        return tasks;
    }

    public void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
            if (head != null) {
                head.prev = null;
            }
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
            if (tail != null) {
                tail.next = null;
            }
        }
        size--;
        nodes.remove(node.data.getId());
    }

    public static class Node {
        public Node prev;
        public Node next;
        public Task data;

        public Node(Node prev, Node next, Task data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }
}
