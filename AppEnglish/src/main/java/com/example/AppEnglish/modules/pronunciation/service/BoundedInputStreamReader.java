package com.example.appenglish.modules.pronunciation.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStreamReader {

    private BoundedInputStreamReader() {
    }

    public static byte[] readAllBytes(InputStream in, long maxBytes) throws IOException {
        if (maxBytes <= 0) {
            throw new IllegalArgumentException("maxBytes must be > 0");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        long total = 0;
        int r;
        while ((r = in.read(buf)) != -1) {
            total += r;
            if (total > maxBytes) {
                throw new IOException("Audio too large: " + total + " bytes (max " + maxBytes + ")");
            }
            baos.write(buf, 0, r);
        }
        return baos.toByteArray();
    }
}

