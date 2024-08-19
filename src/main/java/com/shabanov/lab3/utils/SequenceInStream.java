package com.shabanov.lab3.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SequenceInStream {
    private final List<FixLenFile> files;

    public SequenceInStream(List<FixLenFile> files) {
        this.files = files;
    }

    public void readSegment(byte[] b, int offsetStream) throws IOException {
        int remainingBytes = b.length;
        Iterator<FixLenFile> iterator = files.iterator();

        while (remainingBytes > 0 && iterator.hasNext()) {
            FixLenFile file = iterator.next();
            int fileLength = file.length;

            if (offsetStream < fileLength) {
                file.file.seek(offsetStream);
                int bytesToRead = Math.min(remainingBytes, fileLength - offsetStream);
                file.file.read(b, b.length - remainingBytes, bytesToRead);
                remainingBytes -= bytesToRead;
            }
            offsetStream = Math.max(0, offsetStream - fileLength);
        }
    }
}
