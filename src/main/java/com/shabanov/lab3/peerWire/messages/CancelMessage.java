package com.shabanov.lab3.peerWire.messages;

public final class CancelMessage extends BitTorrentMessage {

    private final int index;

    private final int offset;

    private final int length;

    public CancelMessage (int index, int offset, int length) {
        if (index < 0 | offset < 0 | length < 0) {
            throw new IllegalArgumentException();
        }
        this.index = index;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public MessageType getMessageType () {
        return MessageType.CANCEL;
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
        StringBuilder sb = new StringBuilder("CancelMessage");
        sb.append("{Type=").append("Cancel");
        sb.append("; Index=").append(index);
        sb.append("; Offset=").append(offset);
        sb.append("; Length=").append(length);
        return sb.append("}").toString();
    }
}
