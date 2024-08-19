package com.shabanov.lab3.bencode.model;

import com.shabanov.lab3.bencode.io.BencodeObjectReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BencodeInteger extends BencodeObject{
    public static final char PREFIX = 'i';

    private long value;


    public BencodeInteger(BencodeObjectReader bor) throws IOException {
        super();

        bor.read();
        String charSequenceBody = bor.readCharSequence(SUFFIX);

        try {
            this.value = Long.parseLong(charSequenceBody);
            String checkString = String.valueOf(this.value);
            if (!charSequenceBody.equals(checkString)) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public BencodeInteger(long value) {
        super();
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }


    @Override
    public String toObjectString(){
        return "i" + String.valueOf(value) + "e";
    }

    @Override
    public byte[] toBytes(){
        return new String("i" + String.valueOf(value) + "e").getBytes(StandardCharsets.UTF_8);
    }
}
