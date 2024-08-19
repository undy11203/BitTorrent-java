package com.shabanov.lab3.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PeerId {

    private byte[] bytes;
    private final String string;

    private final String urlEncodedString;

    public PeerId (byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }

        if (bytes.length != 20) {
            throw new IllegalArgumentException();
        }

        this.bytes = Arrays.copyOf(bytes, bytes.length);
        this.string = new String(bytes, StandardCharsets.ISO_8859_1);

        try {
            urlEncodedString = URLEncoder.encode(string, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    public PeerId (String string) throws UnsupportedEncodingException {
        if (string == null) {
            throw new NullPointerException();
        }

        if (string.length() != 20) {
            throw new IllegalArgumentException();
        }

        this.string = string;
        this.bytes = string.getBytes(StandardCharsets.ISO_8859_1);

        try {
            urlEncodedString = URLEncoder.encode(string, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }
    @Override
    public String toString () {
        return string;
    }

    public byte[] getBytes () {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public String toUrlEncodedString () {
        return urlEncodedString;
    }
}
