package com.swgas.ocs.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    private static final String CLASS = ZipUtils.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    private static final int LEN = 8192;

    public static void unZip(InputStream in, Path outputDirectory) {
        LOG.entering(CLASS, "unZip", Stream.of(in, outputDirectory).toArray());
        
        try(ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(in))){            
            ZipEntry entry;
            while((entry = zipStream.getNextEntry()) != null) {
                LOG.finest(String.format("Extracting: %s", entry));
                byte[] buff = new byte[LEN];
                int count;
                while((count = zipStream.read(buff)) != -1) {
                    Files.newBufferedWriter(outputDirectory.resolve(entry.getName())).append(new String(buff, 0, count)).close();
                }
            }
        } catch (Exception e) {
            LOG.severe(e.toString());
        }
        LOG.exiting(CLASS, "unZip");
    }
}
