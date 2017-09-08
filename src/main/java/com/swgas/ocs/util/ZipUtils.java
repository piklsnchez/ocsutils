package com.swgas.ocs.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    private static final String CLASS = ZipUtils.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    private static final Path TMP     = Paths.get(System.getProperty("java.io.tempdir"));

    public static void unZip(InputStream in, Path outputDirectory) {
        final int len = 8192;
        try(ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(in))){            
            ZipEntry entry;
            while((entry = zipStream.getNextEntry()) != null) {
                LOG.finest(String.format("Extracting: %s", entry));
                byte[] buff = new byte[len];
                int count;
                while((count = zipStream.read(buff)) != -1) {
                    Files.newBufferedWriter(outputDirectory.resolve(entry.getName())).append(new String(buff, 0, count)).close();
                }
            }
        } catch (Exception e) {
            LOG.severe(e.toString());
        }
    }
}
