package com.swgas.ocs.util;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Remove {
    private static final String CLASS = Remove.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    
    private static class TreeRemover implements FileVisitor<Path> {
        private final Path target;

        private TreeRemover(Path target) {
            this.target = target;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException{
            // fix up modification time of directory when done
            if (e == null) {
                Files.deleteIfExists(dir);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException{
            if (e instanceof FileSystemLoopException) {
                LOG.info(String.format("cycle detected: %s", file));
            } else {
                throw e;
            }
            return FileVisitResult.CONTINUE;
        }
    }

    public static boolean removeDirectory(String target) {
        Path destination = Paths.get(Objects.requireNonNull(target));
        if (!destination.toFile().getParentFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("must be a directory", target));
        }
        // check if target is a directory
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        TreeRemover tr = new TreeRemover(destination);
        try {
            Files.walkFileTree(destination, opts, Integer.MAX_VALUE, tr);
        } catch (IOException e) {
            LOG.throwing(CLASS, target, e);
            return false;
        }
        return true;
    }
    
    public static void main(String... arg){
        String tmp = System.getProperty("java.io.tmpdir").concat("/ffProf");
        boolean success = removeDirectory(tmp);
        LOG.info(String.format("success: (%s) and %s was deleted: %s", success, tmp, !Paths.get(tmp).toFile().exists()));
    }
}
