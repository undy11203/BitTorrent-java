package com.shabanov.lab3.bencode.io;

import com.shabanov.lab3.bencode.model.*;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class BencodeOutputStream implements Closeable, Flushable {
    private final PrintStream stream;

    public BencodeOutputStream (final OutputStream out) {
        try {
            stream = new PrintStream(out, false, "UTF-8");

        } catch (UnsupportedEncodingException exc) {
            throw new AssertionError(exc);
        }
    }

    public void writeValue (BencodeObject value) throws IOException {
        if (value instanceof BencodeString) {
            final BencodeString sv = (BencodeString) value;
            stream.print(sv.getLength());
            stream.print(':');
            stream.write(sv.getBytes());

        } else if (value instanceof BencodeInteger) {
            BencodeInteger iv = (BencodeInteger) value;
            stream.print('i');
            stream.print(iv.getValue());
            stream.print('e');

        } else if (value instanceof BencodeList) {
            BencodeList lv = (BencodeList) value;
            stream.print('l');
            for (BencodeObject val : lv) {
                writeValue(val);
            }
            stream.print('e');

        } else if (value instanceof BencodeDictionary) {
            BencodeDictionary dv = (BencodeDictionary) value;
            stream.print('d');
            Iterator<BencodeString> keysIterator = dv.getKeysIterator();
            while (keysIterator.hasNext()) {
                BencodeString key = keysIterator.next();
                BencodeObject val = dv.get(key);
                writeValue(key);
                writeValue(val);
            }
            stream.print('e');

        } else {
            throw new IllegalArgumentException("Invalid value type");
        }
    }

    @Override
    public void close () {
        stream.close();
    }

    @Override
    public void flush () {
        stream.flush();
    }

}
