package com.shabanov.lab3.data;

import com.shabanov.lab3.bencode.io.BencodeIterator;
import com.shabanov.lab3.bencode.model.BencodeDictionary;
import com.shabanov.lab3.bencode.model.BencodeString;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public final class TorrentMetaInfo {

    private String announce;

    private InfoSection info;

    public TorrentMetaInfo(String path) {
        try {
            InputStream file = new FileInputStream(path);
            BencodeIterator newIterator = new BencodeIterator(file);
            BencodeDictionary metainfo = (BencodeDictionary) newIterator.next();
            announce = ((BencodeString)metainfo.get("announce")).toString();
            info = new InfoSection((BencodeDictionary)metainfo.get("info"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getAnnounce () {
        return announce;
    }

    public InfoSection getInfoSection () {
        return info;
    }

    public Sha1Hash getInfoHash () {
        return info.getHash();
    }

}