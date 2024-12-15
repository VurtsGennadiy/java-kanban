package service;

import model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodes;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        nodes = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        int taskId = task.getId();
        if (nodes.containsKey(taskId)) {
            remove(taskId);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodes.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(getTasks());
    }

    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newTail = new Node(oldTail, null, task);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        nodes.put(tail.data.getId(), tail);
    }

    public int getSize() {
        return nodes.size();
    }

    public Task getFirst() {
        if (head == null) {
            return null;
        } else {
            return head.data;
        }
    }

    public Task getLast() {
        if (tail == null) {
            return null;
        } else {
            return tail.data;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node curNode = head;
        while (curNode != null) {
            tasks.add(curNode.data);
            curNode = curNode.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) return;
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
