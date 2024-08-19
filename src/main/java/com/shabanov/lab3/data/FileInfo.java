package com.shabanov.lab3.data;

import com.shabanov.lab3.bencode.model.BencodeList;
import com.shabanov.lab3.bencode.model.BencodeObject;
import com.shabanov.lab3.bencode.model.BencodeString;

import java.nio.file.Path;
import java.util.Iterator;

public class FileInfo {
    private String name;
    private long length;
    private String path;


    public FileInfo(long length, String path) {
        this.length = length;
        this.path = "output";
        this.name = path;
    }

    public FileInfo(long length, BencodeList flist) {
        this.path = "output/";
        Iterator<BencodeObject> pathPart = flist.iterator();
        int i = 0;
        int flistSize = flist.size();
        while(pathPart.hasNext()){
            BencodeString str = (BencodeString) pathPart.next();
            i++;
            if(i < flistSize){
                this.path += str.toString() + "/";
            }
        }
        if(this.path.contains("/")){
            this.path = this.path.substring(0, this.path.length()-1);
        }
        this.name = flist.get(i-1).toString();
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) { this.path = path; }
}
