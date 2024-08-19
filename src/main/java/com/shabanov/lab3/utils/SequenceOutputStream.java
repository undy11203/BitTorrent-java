package com.shabanov.lab3.utils;

import com.shabanov.lab3.data.FileInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SequenceOutputStream {
    private final List<FixLenFile> files;

    public SequenceOutputStream(List<FixLenFile> files) {
        this.files = files;
    }

    public void writeSegment(byte[] b, int offsetStream) throws IOException {
        int newCor = 0;
        Iterator<FixLenFile> iterator = files.iterator();
        while(iterator.hasNext()) {
            FixLenFile file = (FixLenFile) iterator.next();
            newCor += file.length;
            if(newCor > offsetStream){
                if(offsetStream + b.length <= newCor){
                    file.file.seek(offsetStream-(newCor-file.length));
                    file.file.write(b);
                }else{
                    int nowFileSize = newCor - offsetStream;
                    file.file.seek(file.length-nowFileSize);
                    file.file.write(b, 0, nowFileSize);
                    file = (FixLenFile) iterator.next();
                    file.file.seek(0);
                    file.file.write(b, nowFileSize, b.length-nowFileSize);
                }
                break;
            }
        }
    }

}
