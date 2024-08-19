package com.shabanov.lab3.peerWire;

import com.shabanov.lab3.peerWire.messages.*;

import java.nio.ByteBuffer;
import java.util.BitSet;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class BitTorrentMessageEncoder {
    public static void encodeMessageToBuffer(ByteBuffer buffer, BitTorrentMessage msg) {
        switch (msg.getMessageType()) {
            case KEEP_ALIVE:
                buffer.putInt(0);
                break;

            case CHOKE:
            case UNCHOKE:
            case INTERESTED:
            case UNINTERESTED:
                buffer.putInt(1);
                buffer.put((byte) msg.getMessageType().getId());
                break;

            case HANDSHAKE:
                encodeHandShake(buffer, (HandShake) msg);
                break;

            case HAVE:
                encodeHave(buffer, (HaveMessage) msg);
                break;

            case BITFIELD:
                encodeBitField(buffer, (BitFieldMessage) msg);
                break;

            case REQUEST:
                encodeRequest(buffer, (RequestMessage) msg);
                break;

            case PIECE:
                encodePiece(buffer, (PieceMessage) msg);
                break;

            case CANCEL:
                encodeCancel(buffer, (CancelMessage) msg);
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    private static void encodeHandShake(ByteBuffer buffer, HandShake msg){
        // Protocol
        buffer.put((byte) msg.getProtocolName().length());
        buffer.put(msg.getProtocolName().getBytes(ISO_8859_1));

        // Reserved Bits
        byte[] bits = new byte[8];

        buffer.put(bits);

        buffer.put(msg.getHash().getBytes());
        buffer.put(msg.getPeerId().getBytes());

    }

    private static void encodeHave (ByteBuffer buffer, HaveMessage msg) {
        buffer.putInt(5);
        buffer.put((byte) msg.getMessageType().getId());
        buffer.putInt(msg.getPieceIndex());
    }

    private static void encodeBitField (ByteBuffer buffer, BitFieldMessage msg) {
        BitSet bitfield = msg.getBitField();
        int nbytes = (bitfield.size() + 7) / 8;
        byte[] bytes = new byte[nbytes];
        for (int i = 0; i < nbytes * 8; i++) {
            if (bitfield.get(i)) {
                int bInd = i % 8;
                int bMask = 1 << (7 - (i / 8));
                bytes[bInd] |= bMask;
            }
        }

        buffer.putInt(nbytes + 1);
        buffer.put((byte) msg.getMessageType().getId());
        buffer.put(bytes);
    }

    private static void encodeRequest (ByteBuffer buffer, RequestMessage msg) {
        buffer.putInt(13);
        buffer.put((byte) msg.getMessageType().getId());
        buffer.putInt(msg.getIndex());
        buffer.putInt(msg.getOffset());
        buffer.putInt(msg.getLength());
    }

    private static void encodePiece (ByteBuffer buffer, PieceMessage msg) {
        ByteBuffer contents = msg.getPieceContents();
        contents.rewind().limit(contents.capacity());

        buffer.putInt(9 + contents.capacity());
        buffer.put((byte) msg.getMessageType().getId());
        buffer.putInt(msg.getIndex());
        buffer.putInt(msg.getOffset());
        buffer.put(contents);
    }

    private static void encodeCancel (ByteBuffer buffer, CancelMessage msg) {
        buffer.putInt(13);
        buffer.put((byte) msg.getMessageType().getId());
        buffer.putInt(msg.getIndex());
        buffer.putInt(msg.getOffset());
        buffer.putInt(msg.getLength());
    }


}
