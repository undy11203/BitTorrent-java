package com.shabanov.lab3.peerWire.messages;

public final class RequestMessage extends BitTorrentMessage {

    private final int index;

    private final int offset;

    private final int length;

    public RequestMessage (int index, int offset, int length) {
        if (index < 0 | offset < 0 | length < 0) {
            throw new IllegalArgumentException();
        }
        this.index = index;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public MessageType getMessageType () {
        return MessageType.REQUEST;
    }

    public int getIndex () {
        return index;
    }

    public int getOffset () {
        return offset;
    }

    public int getLength () {
        return length;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("RequestMessage");
        sb.append("{Type=").append("Request");
        sb.append("; Index=").append(index);
        sb.append("; Offset=").append(offset);
        sb.append("; Length=").append(length);
        return sb.append("}").toString();
    }

}
