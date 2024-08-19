package com.shabanov.lab3.bencode.model;

import com.shabanov.lab3.bencode.io.BencodeIterator;
import com.shabanov.lab3.bencode.io.BencodeObjectReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BencodeList extends BencodeObject  implements Iterable<BencodeObject>{
    public static final char PREFIX = 'l';

    private List<BencodeObject> listContents = new ArrayList<>();


    public BencodeList() {
        super();
    }

    public BencodeList(Iterable<BencodeObject> items) {
        super();

        if (items == null) {
            throw new IllegalArgumentException("Null argument is not allowed for BencodedList constructor");
        }

        for (BencodeObject item : items) {
            add(item);
        }
    }

    public BencodeList(BencodeObjectReader bor) throws IOException {
        super();

        if (bor.read() != PREFIX) {

        }

        BencodeIterator bi = new BencodeIterator(bor);
        while (bi.hasNext()) {
            listContents.add(bi.next());
        }

        bor.read();
    }

    public boolean isCompositeObject() {
        return true;
    }

    public void add(BencodeObject bo) {
        checkObjectToBeAdded(bo);
        listContents.add(bo);
    }

    public void add(int index, BencodeObject bo) {
        checkObjectToBeAdded(bo);
        listContents.add(index, bo);
    }

    public BencodeObject get(int index) {
        checkListIndex(index);
        return listContents.get(index);
    }

    public boolean contains(BencodeObject object) {
        return listContents.contains(object);
    }

    public int size() {
        return listContents.size();
    }

    @Override
    public Iterator<BencodeObject> iterator() {
        return listContents.iterator();
    }

    public int indexOf(BencodeObject object) {
        if (object == null) {
            throw new IllegalArgumentException("Null argument is not allowed for BencodedList.indexOf()");
        }

        return listContents.indexOf(object);
    }

    @Override
    Collection<BencodeObject> getCompositeValues() {
        return listContents;
    }

    private void checkListIndex(int index) {
        if (index < 0 || index >= listContents.size()) {
            throw new IllegalArgumentException("Incorrect index value: " + index +
                    " for collection with size: " + listContents.size());
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(listContents.toArray());
    }

    @Override
    public String toObjectString(){
        StringBuilder sb = new StringBuilder();
        sb.append("l");

       for (int i = 0; i < listContents.size(); i++){
           sb.append(listContents.get(i).toObjectString());
       }

        sb.append("e");

        return sb.toString();
    }

    @Override
    public byte[] toBytes(){
        StringBuilder sb = new StringBuilder("l");

        for (int i = 0; i <listContents.size(); i++){
            sb.append(listContents.get(i).toObjectString());
        }

        sb.append("e");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void checkObjectToBeAdded(BencodeObject bo) {
        if (bo == null) {
            throw new IllegalArgumentException("Null elements are not allowed for BencodedList");
        }
    }
}
