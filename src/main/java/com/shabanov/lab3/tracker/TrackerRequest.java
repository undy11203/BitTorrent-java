package com.shabanov.lab3.tracker;

import com.shabanov.lab3.data.TrackerEvent;
import com.shabanov.lab3.data.PeerId;
import com.shabanov.lab3.data.Sha1Hash;

import java.net.InetAddress;

public class TrackerRequest {

    private final Sha1Hash infoHash;

    private final int port;

    private final long uploaded;

    private final long downloaded;

    private final PeerId peerId;

    private final long left;

    private final TrackerEvent trackerEvent;

    private final InetAddress ip;

    private final int numWant;

    public TrackerRequest (
            Sha1Hash infoHash, int port, long uploaded, long downloaded, long left, TrackerEvent trackerEvent, InetAddress ip, int numWant, PeerId peerId)
    {
        if (infoHash == null)
            throw new NullPointerException("Null Hash");
        if (trackerEvent == null)
            throw new NullPointerException("Null Event");

        if (port < 0 | port >= 65535 | uploaded < 0 | downloaded < 0 | left < 0)
            throw new IllegalArgumentException();

        this.peerId = peerId;
        this.infoHash = infoHash;
        this.port = port;
        this.uploaded = uploaded;
        this.downloaded = downloaded;
        this.left = left;
        this.trackerEvent = trackerEvent;
        this.ip = ip;
        this.numWant = numWant;
    }

    public Sha1Hash getInfoHash() {
        return infoHash;
    }

    public int getPort() {
        return port;
    }

    public long getUploaded() {
        return uploaded;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public long getLeft() {
        return left;
    }

    public TrackerEvent getEvent() {
        return trackerEvent;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getNumWant() {
        return numWant;
    }

    public PeerId getPeerId () {
        return peerId;
    }
}
