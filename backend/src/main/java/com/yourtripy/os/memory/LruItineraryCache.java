package com.yourtripy.os.memory;

import java.util.HashMap;
import java.util.Map;

/**
 * =========================================================================
 *  OPERATING SYSTEMS CONCEPT: Page Replacement / LRU Cache Algorithm
 * =========================================================================
 *  🎯 PURPOSE IN SIMPLE WORDS:
 *  Saves popular trip itineraries in memory so if 100 people search "Goa 3 Days",
 *  we don't waste money calling Gemini AI 100 times!
 *
 *  📚 THE STUDY DESK ANALOGY:
 *  Imagine your study desk only has space for 5 textbooks:
 *   - When you read a book, you place it at the front of your desk (Head = Most Recently Used).
 *   - When you need a 6th book, your desk is full! You must pick the book you haven't 
 *     touched for the longest time (Tail = Least Recently Used) and put it away.
 *
 *  ⚡ WHY IS THIS SPECIAL?
 *  Both get() and put() run in O(1) CONSTANT TIME because:
 *   1. HashMap gives O(1) lookup.
 *   2. Doubly Linked List gives O(1) insertion and removal of nodes!
 *
 *  🎓 COLLEGE SUBJECT: Operating Systems (Virtual Memory Management, Page Replacement Policies).
 * =========================================================================
 */
public class LruItineraryCache<K, V> {

    // Node representing each book on our study desk
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head; // Most Recently Used (Front of desk)
    private final Node<K, V> tail; // Least Recently Used (Back of desk)

    public LruItineraryCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.map = new HashMap<>();

        // Dummy head & tail nodes prevent null-pointer checks at boundaries
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * O(1) Get: Fetches item from cache and moves it to head (Most Recently Used).
     */
    public synchronized V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null; // Cache Miss! (Must generate fresh from Gemini)
        }
        // Cache Hit! Move to front of desk
        moveToHead(node);
        return node.value;
    }

    /**
     * O(1) Put: Inserts item. If capacity exceeded, evicts the Least Recently Used (tail).
     */
    public synchronized void put(K key, V value) {
        Node<K, V> node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            if (map.size() >= capacity) {
                // 🗑️ OS Page Eviction: Remove the oldest book from tail
                Node<K, V> oldest = removeTail();
                map.remove(oldest.key);
            }
            Node<K, V> newNode = new Node<>(key, value);
            map.put(key, newNode);
            addToHead(newNode);
        }
    }

    public synchronized int size() {
        return map.size();
    }

    private void addToHead(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    private Node<K, V> removeTail() {
        Node<K, V> lru = tail.prev;
        removeNode(lru);
        return lru;
    }
}
