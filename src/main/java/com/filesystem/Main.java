package com.filesystem;


public class Main {
    public static void main(String[] args) {
        System.out.println("=== Advanced File System Visitor Pattern Demonstration ===");

        // 1. Build deterministic sample tree
        DirectoryNode root = new DirectoryNode("/");

        DirectoryNode documents = new DirectoryNode("documents");
        documents.addChild(new FileNode("resume.pdf", 2048));
        documents.addChild(new FileNode("budget.xlsx", 1024));

        DirectoryNode source = new DirectoryNode("source");
        source.addChild(new FileNode("Main.java", 1536));
        source.addChild(new FileNode("Helper.java", 512));

        root.addChild(documents);
        root.addChild(source);
        root.addChild(new FileNode("README.md", 256));

        System.out.println("File system structure initialized successfully.\n");

        // 2. Run Visitor 1: TotalSizeVisitor (R3)
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("[Visitor 1 - TotalSizeVisitor]");
        System.out.println(" -> Total storage size across subtree: " + sizeVisitor.getTotalSize() + " bytes\n");

        // 3. Run Visitor 2: SearchVisitor (R4)
        SearchVisitor searchVisitor = new SearchVisitor(".java", 500);
        root.accept(searchVisitor);
        System.out.println("[Visitor 2 - SearchVisitor]");
        System.out.println(" -> Matching files (*.java with size >= 500 bytes):");
        for (String path : searchVisitor.getMatchingPaths()) {
            System.out.println("    * " + path);
        }
        System.out.println();

        // 4. Run Visitor 3: StatisticsVisitor (R5, R8)
        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("[Visitor 3 - StatisticsVisitor]");
        System.out.println(" -> Total File Count: " + statsVisitor.getFileCount());
        System.out.println(" -> Total Directory Count (including root): " + statsVisitor.getDirectoryCount());
        System.out.println(" -> Maximum Tree Depth (root = 0): " + statsVisitor.getMaxDepth());

        System.out.println("\n=== Demonstration Completed Successfully ===");
    }
}