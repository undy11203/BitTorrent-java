package com.shabanov.lab3.peerWire.messages;

import com.shabanov.lab3.data.PeerId;
import com.shabanov.lab3.data.Sha1Hash;

import java.util.BitSet;

public class HandShake extends BitTorrentMessage{
    private final String protocol;

    private final BitSet flags;

    private final Sha1Hash hash;

    private final PeerId id;

    public HandShake (BitSet flags, Sha1Hash hash, PeerId id) {
        this("BitTorrent protocol", flags, hash, id);
    }

    public HandShake (String protocol, BitSet flags, Sha1Hash hash, PeerId id) {
        this.id = id;
        this.protocol = protocol;
        this.flags = (BitSet) flags.clone();
        this.hash = hash;
    }

    @Override
    public MessageType getMessageType () {
        return MessageType.HANDSHAKE;
    }

    public String getProtocolName () {
        return protocol;
    }

    public BitSet getFlags () {
        return (BitSet) flags.clone();
    }

    public Sha1Hash getHash () {
        return hash;
    }

    public PeerId getPeerId () {
        return id;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("HandShake");
        sb.append("{Type=Handshake; ");
        sb.append("Protocol=").append(protocol);
        sb.append("; Flags=").append(flags);
        sb.append("; Hash=").append(hash);
        sb.append("; PeerId=").append(id.toUrlEncodedString());
        return sb.append("}").toString();
    }
}
