package Q2_Playlist_Collections;

import java.util.Arrays;

/**
 * Alternative Concept for Q2:
 * Demonstrating how the playlist would be implemented using a raw primitive Song[] array
 * with manual array growth and manual shifting using System.arraycopy().
 */
public class PlainArrayPlaylist {
    private Song[] elements;
    private int size;

    public PlainArrayPlaylist(int initialCapacity) {
        this.elements = new Song[initialCapacity];
        this.size = 0;
    }

    public void add(Song song) {
        ensureCapacity(size + 1);
        elements[size++] = song;
    }

    public void insertAt(int index, Song song) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        // Manual shift of elements to the right by 1 position: O(N)
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = song;
        size++;
    }

    public Song removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Song removed = elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            // Manual shift of elements to the left by 1 position: O(N)
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null; // Prevent memory leak
        return removed;
    }

    public Song get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return elements[index]; // O(1) random access
    }

    public int size() { return size; }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length + (elements.length >> 1); // 1.5x growth
            if (newCapacity < minCapacity) newCapacity = minCapacity;
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
}

