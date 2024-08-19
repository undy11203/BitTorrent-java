package com.shabanov.lab3.bencode.model;

import com.shabanov.lab3.bencode.io.BencodeObjectReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BencodeString extends BencodeObject implements Comparable<BencodeString>{
    public static final char DELIMITER = ':';

    private byte[] value;

    public BencodeString(BencodeObjectReader bor) throws IOException {

        String valueLengthInStr = bor.readCharSequence(DELIMITER);

        try {
            int valueLength = Integer.parseInt(valueLengthInStr);
            this.value = new byte[valueLength];
            int bytesRead = bor.readByteSequence(this.value);
            if (bytesRead != valueLength) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }

    public BencodeString(String string) {
        if ((string != null) && (string.length() > 0)) {
            value = string.getBytes(StandardCharsets.UTF_8);
        } else {
            value = new byte[0];
        }
    }

    public int getLength(){
        return  value.length;
    }

    public String getValue(){
        return new String(value, StandardCharsets.UTF_8);
    }

    public byte[] getBytes() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public String toObjectString() {
        return value.length + ":" + getBytes();
    }

    @Override
    public byte[] toBytes(){
        return new String(value.length + ":" + toString()).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BencodeString that = (BencodeString) obj;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public int compareTo(BencodeString anotherObject) {
        return this.toString().compareTo(anotherObject.toString());
    }
}
