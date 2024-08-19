package com.shabanov.lab3.bencode.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class BencodeObjectReader {
    PushbackInputStream torrentFile;
    public BencodeObjectReader(InputStream is) {
        this.torrentFile = new PushbackInputStream(is);
    }

    public int read() throws IOException {
        return torrentFile.read();
    }

    public void unread(int streamByte) throws IOException {
        torrentFile.unread(streamByte);
    }

    public int readByteSequence(byte[] sequence) throws IOException {
        if ((sequence == null) || (sequence.length == 0)) {
            return 0;
        }

        return torrentFile.read(sequence);
    }


    public String readCharSequence(char stopSymbol) throws IOException {
        boolean isNotStopSymbol = true;
        StringBuilder sb = new StringBuilder();

        int value;
        while (isNotStopSymbol && ((value = torrentFile.read()) != -1)) {
            isNotStopSymbol = ((char) value != stopSymbol);

            if (isNotStopSymbol) {
                sb.append((char) value);
            }
        }

        if (isNotStopSymbol) {
            return null;
        }

        return sb.toString();
    }
}
