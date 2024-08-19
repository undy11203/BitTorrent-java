package com.shabanov.lab3.peerWire.messages;

public final class HaveMessage extends BitTorrentMessage {

    private final int pieceIndex;

    public HaveMessage (int pieceIndex) {
        if (pieceIndex < 0) {
            throw new IllegalArgumentException();
        }

        this.pieceIndex = pieceIndex;
    }

    @Override
    public MessageType getMessageType () {
        return MessageType.HAVE;
    }

    public int getPieceIndex () {
        return pieceIndex;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("HaveMessage");
        sb.append("{Type=").append("Have");
        sb.append("; PieceIndex=").append(pieceIndex);
        return sb.append("}").toString();
    }

}
