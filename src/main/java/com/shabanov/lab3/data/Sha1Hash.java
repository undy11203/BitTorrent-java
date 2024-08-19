package com.shabanov.lab3.data;

import com.shabanov.lab3.bencode.io.BencodeOutputStream;
import com.shabanov.lab3.bencode.model.BencodeDictionary;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Sha1Hash {
    private final byte[] bytes;

    private String string;

    private String urlEncodedString;
    public Sha1Hash (byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }

        if (bytes.length != 20) {
            throw new IllegalArgumentException();
        }

        this.bytes = Arrays.copyOf(bytes, bytes.length);

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int ub = (b) & 0xFF;

            if (ub < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(ub).toUpperCase());
        }
        string = sb.toString();

        String str = new String(bytes, Charset.forName("ISO-8859-1"));
        try {
            urlEncodedString = URLEncoder.encode(str, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    public Sha1Hash(BencodeDictionary value){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        BencodeOutputStream bos = new BencodeOutputStream(new DigestOutputStream(new VoidOutputStream(), md));

        try {
            bos.writeValue(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = md.digest();

        this.bytes = Arrays.copyOf(bytes, bytes.length);

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int ub = (b) & 0xFF;

            if (ub < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(ub).toUpperCase());
        }
        string = sb.toString();

        String str = new String(bytes, Charset.forName("ISO-8859-1"));
        try {
            urlEncodedString = URLEncoder.encode(str, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    public static Sha1Hash forByteBuffer (ByteBuffer buffer) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            // If the SHA1 algorithm doesn't exist, returns null
            return null;
        }

//        for (byte b : buffer.array()) {
//            System.out.print(String.format("%02X ", b)); // Форматирование байта как двухзначного шестнадцатеричного числа
//        }
//        System.out.println();
        ByteBuffer buf = buffer.duplicate();

        byte[] arr = new byte[64 * 1024];
        while (buf.remaining() > 0) {
            int length = Math.min(arr.length, buf.remaining());
            buf.get(arr, 0, length);
            md.update(arr, 0, length);
        }

        return new Sha1Hash(md.digest());
    }

    @Override
    public boolean equals (Object obj) {
        if (!(obj instanceof Sha1Hash)) {
            return false;
        }

        Sha1Hash h = (Sha1Hash) obj;
        return Arrays.equals(h.bytes, bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getString() {
        return string;
    }

    public String getUrlEncodedString() {
        return urlEncodedString;
    }

    private static final class VoidOutputStream extends OutputStream {

        public VoidOutputStream () {
        }

        @Override
        public void write (int b) {
        }

        @Override
        public void write (byte[] b) {
        }

        @Override
        public void write (byte[] b, int off, int len) {
        }

        @Override
        public void flush () {
        }

        @Override
        public void close () {
        }

    }
}
