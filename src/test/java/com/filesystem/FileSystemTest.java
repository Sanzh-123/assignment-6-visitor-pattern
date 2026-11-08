package com.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite verifying checks T1 through T8.
 */
public class FileSystemTest {

    private DirectoryNode root;

    @BeforeEach
    void setUp() {
        // Root structure setup
        root = new DirectoryNode("/");
        
        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("resume.pdf", 1500));
        docs.addChild(new FileNode("notes.txt", 300));

        DirectoryNode src = new DirectoryNode("src");
        src.addChild(new FileNode("Main.java", 1200));
        src.addChild(new FileNode("App.java", 800));

        root.addChild(docs);
        root.addChild(src);
        root.addChild(new FileNode("README.md", 500));
    }

    @Test
    void t1_bothElementTypesReachCorrectVisitMethods() {
        // T1: Verify double dispatch reaches both file and directory visit methods
        TestVisitor visitor = new TestVisitor();
        root.accept(visitor);
        assertTrue(visitor.visitedRootDirectory);
        assertTrue(visitor.visitedSubdirectory);
        assertTrue(visitor.visitedFile);
    }

    @Test
    void t2_sizeAccumulationAcrossMultipleNestingLevels() {
        // T2: TotalSizeVisitor computes exact sum of all files across nested directories
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        // 1500 + 300 + 1200 + 800 + 500 = 4300
        assertEquals(4300L, sizeVisitor.getTotalSize());
    }

    @Test
    void t3_searchCombinesFilterConditions() {
        // T3 & T4: Search correctly combines name criterion and min size threshold
        SearchVisitor searchVisitor = new SearchVisitor(".java", 1000);
        root.accept(searchVisitor);
        List<String> results = searchVisitor.getMatchingPaths();
        
        assertEquals(1, results.size());
        assertEquals("/src/Main.java", results.get(0));
    }

    @Test
    void t4_searchProducesDeterministicRootRelativePaths() {
        // T4: Paths are root-relative, use '/', sorted lexicographically
        SearchVisitor searchVisitor = new SearchVisitor("", 0);
        root.accept(searchVisitor);
        List<String> results = searchVisitor.getMatchingPaths();

        assertEquals(5, results.size());
        assertEquals("/README.md", results.get(0));
        assertEquals("/docs/notes.txt", results.get(1));
        assertEquals("/docs/resume.pdf", results.get(2));
        assertEquals("/src/App.java", results.get(3));
        assertEquals("/src/Main.java", results.get(4));
    }

    @Test
    void t5_statisticsAreCorrectForNestingCountingAndDepth() {
        // T5: StatisticsVisitor counts files, directories (incl root), and max depth (root=0)
        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);

        assertEquals(5, statsVisitor.getFileCount());
        assertEquals(3, statsVisitor.getDirectoryCount()); // root, docs, src
        assertEquals(1, statsVisitor.getMaxDepth()); // root depth 0, children depth 1
    }

    @Test
    void t6_emptyStructuresAndNoMatchSearchHandled() {
        // T6: Empty directory and no-match search cases
        DirectoryNode emptyRoot = new DirectoryNode("/");
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        emptyRoot.accept(sizeVisitor);
        assertEquals(0L, sizeVisitor.getTotalSize());

        SearchVisitor searchVisitor = new SearchVisitor("nonexistent", 5000);
        root.accept(searchVisitor);
        assertTrue(searchVisitor.getMatchingPaths().isEmpty());
    }

    @Test
    void t7_repeatExecutionsDoNotLeakOrMultiplyState() {
        // T7: Repeat executions with reset do not accumulate stale state
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        assertEquals(4300L, sizeVisitor.getTotalSize());

        sizeVisitor.reset();
        root.accept(sizeVisitor);
        assertEquals(4300L, sizeVisitor.getTotalSize()); // No double counting
    }

    @Test
    void t8_invalidTreeMutationsRejectedWhileValidNestingSupported() {
        // T8: Prevent negative file sizes, duplicate sibling names, cycles, multiple parents
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -100));

        DirectoryNode dir = new DirectoryNode("dir");
        dir.addChild(new FileNode("file.txt", 100));
        assertThrows(IllegalArgumentException.class, () -> dir.addChild(new FileNode("file.txt", 200)));

        // Cycle and multi-parent check
        DirectoryNode parentA = new DirectoryNode("a");
        DirectoryNode parentB = new DirectoryNode("b");
        DirectoryNode childDir = new DirectoryNode("sub");
        parentA.addChild(childDir);
        assertThrows(IllegalArgumentException.class, () -> parentB.addChild(childDir));
        assertThrows(IllegalArgumentException.class, () -> childDir.addChild(parentA)); // Cycle
    }

    private static class TestVisitor implements FileSystemVisitor {
        boolean visitedRootDirectory = false;
        boolean visitedSubdirectory = false;
        boolean visitedFile = false;

        @Override
        public void visit(FileNode file) {
            visitedFile = true;
        }

        @Override
        public void visit(DirectoryNode directory) {
            if (directory.getName().equals("/")) {
                visitedRootDirectory = true;
            } else {
                visitedSubdirectory = true;
            }
            for (FileSystemElement child : directory.getChildren().values()) {
                child.accept(this);
            }
        }
    }
}