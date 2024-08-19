package com.shabanov.lab3.bencode.model;

import com.shabanov.lab3.bencode.io.BencodeIterator;
import com.shabanov.lab3.bencode.io.BencodeObjectReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BencodeDictionary extends BencodeObject {
    public static final char PREFIX = 'd';

    private final Map<BencodeString, BencodeObject> dictionary = new TreeMap<>();

    public BencodeDictionary() {
        super();
    }

    public BencodeDictionary(Map<BencodeString, BencodeObject> initialMap) {
        super();

        if (initialMap == null) {
            return;
        }

        for (Map.Entry<BencodeString, BencodeObject> entry: initialMap.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public BencodeDictionary(BencodeObjectReader bor) throws IOException {
        super();

        if (bor.read() != PREFIX) {

        }

        BencodeIterator bi = new BencodeIterator(bor);
        while (bi.hasNext()) {

            BencodeObject bbsKeyObject = bi.next();
            if (bbsKeyObject instanceof BencodeString) {
                BencodeString bbsKey = (BencodeString) bbsKeyObject;

                if (bi.hasNext()) {
                    BencodeObject bencodedObject = bi.next();

                    dictionary.put(bbsKey, bencodedObject);

                } else {

                }
            } else {

            }
        }

        bor.read();
    }

    public boolean isCompositeObject() {
        return true;
    }

    public BencodeObject get(BencodeString keyObject) {
        checkNullKey(keyObject);
        return dictionary.get(keyObject);
    }

    public BencodeObject get(String key) throws IOException {
        checkNullKey(key);
        return dictionary.get(new BencodeString(key));
    }

    @Override
    Collection<BencodeObject> getCompositeValues() {
        return dictionary.values();
    }

    public void put(BencodeString keyObject, BencodeObject bencodedObject) {
        checkPutParameters(keyObject, bencodedObject);
        dictionary.put(keyObject, bencodedObject);
    }
    public void put(String key, BencodeObject bencodedObject) {
        checkPutParameters(key, bencodedObject);

        BencodeString bbsKey = new BencodeString(key);
        dictionary.put(bbsKey, bencodedObject);
    }

    public boolean containsKey(BencodeString key) {
        checkNullKey(key);
        return this.dictionary.containsKey(key);
    }

    public boolean containsKey(String key) {
        checkNullKey(key);
        return this.dictionary.containsKey(new BencodeString(key));
    }

    public Iterator<BencodeString> getKeysIterator() {
        return dictionary.keySet().iterator();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        Iterator<BencodeString> keysIterator = this.getKeysIterator();
        while (keysIterator.hasNext()) {
            BencodeString key = keysIterator.next();
            BencodeObject value = dictionary.get(key);
            sb.append(key.toString()).append(":").append(value.toString()).append("\n");
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toObjectString(){
        StringBuilder sb = new StringBuilder();
        sb.append("d");

        Iterator<BencodeString> keysIterator = this.getKeysIterator();
        while (keysIterator.hasNext()) {
            BencodeObject key = keysIterator.next();
            BencodeObject value = dictionary.get(key);
            sb.append(key.toObjectString()).append(value.toObjectString());
        }
        sb.append("e");

        return sb.toString();
    }

    @Override
    public byte[] toBytes(){
        StringBuilder sb = new StringBuilder("d");

        Iterator<BencodeString> keysIterator = this.getKeysIterator();
        while (keysIterator.hasNext()) {
            BencodeString key = keysIterator.next();
            BencodeObject value = dictionary.get(key);
            sb.append(key.toObjectString()).append(value.toObjectString());
        }
        sb.append("e");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void checkPutParameters(Object keyObject, BencodeObject bencodedObject) {
        if (null == keyObject) {
            throw new IllegalArgumentException("'keyObject' value for BencodedDictionary cannot be null!");
        }

        if (null == bencodedObject) {
            throw new IllegalArgumentException("'bencodedObject' value for BencodedDictionary cannot be null!");
        }
    }

    private void checkNullKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys are not allowed for BencodedDictionary instances");
        }
    }
}
