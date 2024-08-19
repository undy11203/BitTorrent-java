package com.shabanov.lab3.bencode.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public abstract class BencodeObject{
    public static final char SUFFIX = 'e';

    public boolean isCompositeObject() {
        return false;
    }

    Collection<BencodeObject> getCompositeValues() {
        return Collections.emptyList();
    }

    public abstract String toObjectString();

    public abstract byte[] toBytes();
}
