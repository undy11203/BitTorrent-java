package com.shabanov.lab3.data;

import com.shabanov.lab3.bencode.model.BencodeDictionary;
import com.shabanov.lab3.bencode.model.BencodeInteger;
import com.shabanov.lab3.bencode.model.BencodeList;
import com.shabanov.lab3.bencode.model.BencodeString;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InfoSection {
    private long pieceLength;
    private List<Sha1Hash> pieceHashes;
    private String baseDir;
    private List<FileInfo> files;
    private Sha1Hash hash;
    private String name;
    private long totalLength;
    public InfoSection(BencodeDictionary info) throws IOException {
        pieceLength = ((BencodeInteger)info.get("piece length")).getValue();
        BencodeString piecesBen = (BencodeString) info.get("pieces");
        byte[] piecesByte = piecesBen.getBytes();
        if (piecesByte.length % 20 != 0) {
            throw new IllegalArgumentException();
        }
        pieceHashes = new ArrayList<Sha1Hash>();
        for (int i = 0; i < piecesByte.length; i += 20) {
            byte[] hashbytes = new byte[20];
            for (int j = 0; j < 20; j++) {
                hashbytes[j] = piecesByte[i + j];
            }
            pieceHashes.add(new Sha1Hash(hashbytes));
        }

        files = new ArrayList<FileInfo>();

        hash = new Sha1Hash(info);
        baseDir = ((BencodeString)info.get("name")).toString();
        if(!info.containsKey("files")){
            long length = ((BencodeInteger) info.get("length")).getValue();
            String fname = ((BencodeString) info.get("name")).toString();

            FileInfo file = new FileInfo(length, fname);

            files.add(file);
        }else{
            BencodeList list = (BencodeList) info.get("files");
            Iterator iterator = list.iterator();
            while(iterator.hasNext()){
                BencodeDictionary file = (BencodeDictionary) iterator.next();
                long length = ((BencodeInteger) file.get("length")).getValue();
                BencodeList flist = ((BencodeList) file.get("path"));

                FileInfo filel = new FileInfo(length, flist);

                files.add(filel);
            }
        }

        totalLength = 0;
        for (FileInfo fileInfo : files){
            totalLength += fileInfo.getLength();
        }

    }

    public Sha1Hash getHash() {
        return hash;
    }

    public long getPieceLength() {
        return pieceLength;
    }

    public List<Sha1Hash> getPieceHashes() {
        return pieceHashes;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public String getName() {
        return name;
    }

    public long getTotalLength() {
        return totalLength;
    }
}
