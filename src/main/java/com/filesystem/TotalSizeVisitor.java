package com.filesystem;

/**
 * Computes the sum of file sizes for an entire subtree.
 * Traversal Owner: Orchestrates traversal via visitor-driven recursion.
 */
public class TotalSizeVisitor implements FileSystemVisitor {
    private long totalSize = 0L;

    public void reset() {
        this.totalSize = 0L;
    }

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public void visit(FileNode file) {
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Traversal recursion: visit each child exactly once
        for (FileSystemElement child : directory.getChildren().values()) {
            child.accept(this);
        }
    }
}