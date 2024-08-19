package com.shabanov.lab3.peerWire;

import com.shabanov.lab3.data.PeerId;
import com.shabanov.lab3.data.Sha1Hash;
import com.shabanov.lab3.peerWire.messages.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class BitTorrentMessageDecoder {

    public static HandShake decodeHandShakeFromBuffer(ByteBuffer buffer){
        int protoNameLen = (buffer.get()) & 0xFF;

        byte[] pNameBytes = new byte[protoNameLen];
        buffer.get(pNameBytes);

        String pName = new String(pNameBytes, StandardCharsets.ISO_8859_1);

        byte[] resBitsArr = new byte[8];
        buffer.get(resBitsArr);
        BitSet flags = new BitSet();


        byte[] hashBytes = new byte[20];
        buffer.get(hashBytes);
        Sha1Hash hash = new Sha1Hash(hashBytes);

        byte[] peerId = new byte[20];
        try {
        buffer.get(peerId);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new HandShake(pName, flags, hash, new PeerId(peerId));
    }

    public static BitTorrentMessage decodeMessageFromBuffer (ByteBuffer buffer) {
        int length = buffer.getInt();
        return decodeMessageFromBuffer(buffer, length);
    }

    public static BitTorrentMessage decodeMessageFromBuffer (ByteBuffer buffer, int length) {
        if (length < 0) {
            throw new IllegalArgumentException();
        }

        if (length == 0) {
            return EmptyMessage.KEEP_ALIVE;
        }

        int msgId = (buffer.get()) & 0xFF;
        MessageType mt = MessageType.getMessage(msgId);

        switch (mt) {
            case CHOKE:
                return EmptyMessage.CHOKE;

            case UNCHOKE:
                return EmptyMessage.UNCHOKE;

            case INTERESTED:
                return EmptyMessage.INTERESTED;

            case UNINTERESTED:
                return EmptyMessage.UNINTERESTED;

            case HAVE:
                return decodeHave(buffer);

            case BITFIELD:
                return decodeBitField(buffer, length - 1);

            case REQUEST:
                return decodeRequest(buffer);

            case PIECE:
                return decodePiece(buffer, length - 9);

            case CANCEL:
                return decodeCancel(buffer);

            default:
                throw new IllegalArgumentException();
        }
    }

    private static BitTorrentMessage decodeHave (ByteBuffer buffer) {
        return new HaveMessage(buffer.getInt());
    }

    private static BitTorrentMessage decodeBitField (ByteBuffer buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);

        int bitlen = length * 8;
        BitSet bits = new BitSet();
        for (int i = 0; i < bitlen; i++) {
            int bInd = i / 8;
            int bMask = 1 << (7 - (i % 8));
            if ((bytes[bInd] & bMask) != 0) {
                bits.set(i);
            }
        }

        return new BitFieldMessage(bits);
    }

    private static BitTorrentMessage decodeRequest (ByteBuffer buffer) {
        return new RequestMessage(buffer.getInt(), buffer.getInt(), buffer.getInt());
    }

    private static BitTorrentMessage decodePiece (ByteBuffer buffer, int length) {
        return new PieceMessage(buffer.getInt(), buffer.getInt(), buffer, length);
    }

    private static BitTorrentMessage decodeCancel (ByteBuffer buffer) {
        return new CancelMessage(buffer.getInt(), buffer.getInt(), buffer.getInt());
    }

}
