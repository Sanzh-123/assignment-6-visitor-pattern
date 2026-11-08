package com.filesystem;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a directory in the hierarchical file system, holding named child elements.
 */
public class DirectoryNode implements FileSystemElement {
    private final String name;
    private DirectoryNode parent;
    // TreeMap maintains deterministic lexicographical order of children (R4, R6)
    private final Map<String, FileSystemElement> children = new TreeMap<>();

    public DirectoryNode(String name) {
        // Allow root directory to have empty or root name, but validate normal names
        if (name == null || (name.contains("/") && !name.equals("/"))) {
            throw new IllegalArgumentException("Invalid directory name: " + name);
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        // Directories themselves have 0 byte size; size is accumulated via files (R3)
        return 0L;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    /**
     * Adds a child element while enforcing tree integrity rules (R6).
     */
    public void addChild(FileSystemElement child) {
        if (child == null) {
            throw new IllegalArgumentException("Cannot add null child.");
        }
        if (child == this) {
            throw new IllegalArgumentException("A directory cannot contain itself.");
        }
        // Check for cycles: ensure 'this' is not a descendant of 'child' if 'child' is a directory
        if (child instanceof DirectoryNode) {
            DirectoryNode current = this.parent;
            while (current != null) {
                if (current == child) {
                    throw new IllegalArgumentException("Cycle detected: Cannot add an ancestor as a child.");
                }
                current = current.getParent();
            }
        }
        // Check multiple parent ownership
        if (child.getParent() != null && child.getParent() != this) {
            throw new IllegalArgumentException("Element '" + child.getName() + "' already belongs to another directory.");
        }
        // Check duplicate sibling names (TreeMap handles key collision)
        if (children.containsKey(child.getName())) {
            throw new IllegalArgumentException("Duplicate sibling name '" + child.getName() + "' in directory '" + name + "'.");
        }

        child.setParent(this);
        children.put(child.getName(), child);
    }

    public void removeChild(String childName) {
        FileSystemElement child = children.remove(childName);
        if (child != null) {
            child.setParent(null);
        }
    }

    public Map<String, FileSystemElement> getChildren() {
        return Collections.unmodifiableMap(children); // Encapsulation (R4.3)
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this); // Double dispatch step 1
    }
}