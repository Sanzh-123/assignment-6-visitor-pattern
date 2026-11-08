package com.filesystem;

/**
 * Common Element interface for the Visitor pattern in the file system hierarchy.
 */
public interface FileSystemElement {
    String getName();
    long getSize();
    void accept(FileSystemVisitor visitor);
    void setParent(DirectoryNode parent);
    DirectoryNode getParent();
}