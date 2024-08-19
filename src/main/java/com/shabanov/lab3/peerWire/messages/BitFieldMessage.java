package com.shabanov.lab3.peerWire.messages;

import java.util.BitSet;

public final class BitFieldMessage extends BitTorrentMessage {

    private final BitSet bitField;

    public BitFieldMessage (BitSet bitField) {
        this.bitField = (BitSet) bitField.clone();
    }

    @Override
    public MessageType getMessageType () {
        return MessageType.BITFIELD;
    }

    public BitSet getBitField () {
        return (BitSet) bitField.clone();
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("BitFieldMessage");
        sb.append("{Type=").append("BitField");
        sb.append("; BitField=").append(bitField);
        return sb.append("}").toString();
    }
}
