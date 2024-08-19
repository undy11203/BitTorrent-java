package com.shabanov.lab3.peerWire.messages;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public final class PieceMessage extends BitTorrentMessage {

    private final int index;

    private final int offset;

    private final ByteBuffer buffer;

    public PieceMessage (int index, int offset, ByteBuffer buf) {
        this(index, offset, buf, buf.remaining());
    }

    public PieceMessage (int index, int offset, ByteBuffer buf, int length) {
        if (index < 0 | offset < 0) {
            throw new IllegalArgumentException();
        }
        if (buf == null) {
            throw new NullPointerException();
        }
        if (length > buf.remaining()) {
            throw new BufferUnderflowException();
        }
        this.index = index;
        this.offset = offset;

        this.buffer = ByteBuffer.allocate(length);
        buffer.put(buf);
        buffer.flip();
    }

    @Override
    public MessageType getMessageType () {
        return MessageType.PIECE;
    }

    public int getIndex () {
        return index;
    }

    public int getOffset () {
        return offset;
    }

    public ByteBuffer getPieceContents () {
        return buffer;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("PieceMessage");
        sb.append("{Type=").append("Piece");
        sb.append("; Index=").append(index);
        sb.append("; Offset=").append(offset);
        sb.append("; Length=").append(buffer.capacity());
        return sb.append("}").toString();
    }

}