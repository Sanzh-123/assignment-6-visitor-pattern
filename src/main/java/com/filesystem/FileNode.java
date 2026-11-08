package com.filesystem;

import java.util.Objects;

/**
 * Represents a file in the hierarchical file system.
 */
public class FileNode implements FileSystemElement {
    private final String name;
    private final long size;
    private DirectoryNode parent;

    public FileNode(String name, long size) {
        if (name == null || name.isBlank() || name.contains("/")) {
            throw new IllegalArgumentException("Invalid file name: " + name);
        }
        if (size < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + size);
        }
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this); // Double dispatch
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileNode)) return false;
        FileNode fileNode = (FileNode) o;
        return size == fileNode.size && name.equals(fileNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size);
    }
}