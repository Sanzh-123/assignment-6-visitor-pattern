package com.filesystem;

/**
 * Collects file count, directory count (including root if directory), and max tree depth.
 * Implemented without modifying FileNode, DirectoryNode, or their accept methods (R8).
 */
public class StatisticsVisitor implements FileSystemVisitor {
    private int fileCount = 0;
    private int directoryCount = 0;
    private int maxDepth = 0;
    private int currentDepth = 0;

    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
        currentDepth = 0;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }

        for (FileSystemElement child : directory.getChildren().values()) {
            currentDepth++;
            child.accept(this);
            currentDepth--;
        }
    }
}