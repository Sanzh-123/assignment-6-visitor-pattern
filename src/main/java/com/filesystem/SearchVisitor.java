package com.filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Searches for files matching a filename criterion and minimum size threshold,
 * returning root-relative paths in deterministic lexicographical order.
 */
public class SearchVisitor implements FileSystemVisitor {
    private final String filenameCriterion;
    private final long minSizeThreshold;
    private final List<String> matchingPaths = new ArrayList<>();
    private final List<String> currentPathSegments = new ArrayList<>();

    public SearchVisitor(String filenameCriterion, long minSizeThreshold) {
        if (filenameCriterion == null) {
            throw new IllegalArgumentException("Filename criterion cannot be null.");
        }
        if (minSizeThreshold < 0) {
            throw new IllegalArgumentException("Minimum size threshold cannot be negative.");
        }
        this.filenameCriterion = filenameCriterion;
        this.minSizeThreshold = minSizeThreshold;
    }

    public void reset() {
        matchingPaths.clear();
        currentPathSegments.clear();
    }

    public List<String> getMatchingPaths() {
        Collections.sort(matchingPaths); // Ensure deterministic order
        return Collections.unmodifiableList(matchingPaths);
    }

    @Override
    public void visit(FileNode file) {
        boolean nameMatches = filenameCriterion.isEmpty() || file.getName().contains(filenameCriterion);
        boolean sizeMatches = file.getSize() >= minSizeThreshold;

        if (nameMatches && sizeMatches) {
            StringBuilder pathBuilder = new StringBuilder();
            for (String segment : currentPathSegments) {
                pathBuilder.append("/").append(segment);
            }
            pathBuilder.append("/").append(file.getName());
            matchingPaths.add(pathBuilder.toString());
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        if (!directory.getName().equals("/")) {
            currentPathSegments.add(directory.getName());
        }

        // Traverse children in deterministic order (TreeMap ensures sorted order)
        for (FileSystemElement child : directory.getChildren().values()) {
            child.accept(this);
        }

        if (!directory.getName().equals("/")) {
            currentPathSegments.remove(currentPathSegments.size() - 1);
        }
    }
}