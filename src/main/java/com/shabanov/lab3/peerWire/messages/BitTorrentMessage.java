package com.shabanov.lab3.peerWire.messages;

public abstract class BitTorrentMessage {

    public abstract MessageType getMessageType ();

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{Type=").append(getMessageType());
        return sb.append("}").toString();
    }
}