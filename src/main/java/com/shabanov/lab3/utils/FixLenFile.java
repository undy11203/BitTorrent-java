package com.shabanov.lab3.utils;

import java.io.OutputStream;
import java.io.RandomAccessFile;

public class FixLenFile {
    public RandomAccessFile file;
    public int length;

    public FixLenFile(RandomAccessFile file, int length) {
        this.file = file;
        this.length = length;
    }
}
