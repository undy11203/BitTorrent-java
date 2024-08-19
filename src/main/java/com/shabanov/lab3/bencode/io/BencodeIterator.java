package com.shabanov.lab3.bencode.io;

import com.shabanov.lab3.bencode.model.*;

import java.io.IOException;
import java.io.InputStream;

public class BencodeIterator {
    private BencodeObjectReader bor;
    private boolean isInObjectWork;

    public BencodeIterator(InputStream is) {
        this.bor = new BencodeObjectReader(is);
        this.isInObjectWork = false;
    }

    public BencodeIterator(BencodeObjectReader bor){
        this.bor = bor;
        this.isInObjectWork = true;
    }

    public boolean hasNext() throws IOException {
        final int streamByte = bor.read();
        bor.unread(streamByte);

        return isInObjectWork ? ((char) streamByte != BencodeObject.SUFFIX) : ((byte) streamByte != -1);
    }

    public BencodeObject next() throws IOException {

        if (!hasNext()) {
            return null;
        }

        int objectPrefix = bor.read();
        bor.unread(objectPrefix);

        if ((byte) objectPrefix == -1) {
            return null;
        }

        BencodeObject bencodedObject;
        switch (objectPrefix) {
            case BencodeInteger.PREFIX:
                bencodedObject = new BencodeInteger(bor);
                break;

            case BencodeList.PREFIX:
                bencodedObject = new BencodeList(bor);
                break;

            case BencodeDictionary.PREFIX:
                bencodedObject = new BencodeDictionary(bor);
                break;

            default:
                if (Character.isDigit(objectPrefix)) {
                    bencodedObject = new BencodeString(bor);
                } else{
                    return null;
                }

                break;
        }

        return bencodedObject;
    }
}
