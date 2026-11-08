package com.filesystem;

/**
 * Visitor interface defining operations for both concrete element types.
 */
public interface FileSystemVisitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}