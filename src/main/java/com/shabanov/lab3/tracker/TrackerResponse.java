package com.shabanov.lab3.tracker;

import com.shabanov.lab3.bencode.model.BencodeDictionary;
import com.shabanov.lab3.bencode.model.BencodeInteger;
import com.shabanov.lab3.bencode.model.BencodeString;
import com.shabanov.lab3.data.PeerId;
import com.shabanov.lab3.data.PeerInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackerResponse {

    private final long interval;

    private final long minInterval;

    private final List<PeerInfo> peers;
    TrackerResponse (BencodeDictionary value) throws IOException {
        this.interval = ((BencodeInteger) value.get("interval")).getValue();
        this.minInterval = ((BencodeInteger) value.get("min interval")).getValue();
        this.peers = readPeers((BencodeString) value.get("peers"));
    }

    private List<PeerInfo> readPeers (BencodeString value) {
        if (value instanceof BencodeString) {
            try {
                byte[] str = ((BencodeString) value).getBytes();

                if ((str.length % 6) != 0) {
                    throw new IllegalArgumentException();
                }

                DataInputStream is = new DataInputStream(new ByteArrayInputStream(str));
                byte[] ipbuf = new byte[4];

                List<PeerInfo> peers = new ArrayList<PeerInfo>();

                while (is.available() > 0) {
                    is.readFully(ipbuf);
                    int port = is.readChar();

                    InetSocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(ipbuf), port);
                    PeerInfo pi = new PeerInfo(addr);

                    peers.add(pi);
                }

                return peers;
            } catch (IOException e) {
                // Should NEVER happen
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }

    public List<PeerInfo> getPeers() {
        return peers;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder(TrackerResponse.class.getSimpleName());

        sb.append("{");

        sb.append("\" Interval=");
        sb.append(interval);

        sb.append("; MinInterval=");
        sb.append(minInterval);

        sb.append("; Peers=");
        sb.append(peers);

        return sb.append("}").toString();
    }
}
