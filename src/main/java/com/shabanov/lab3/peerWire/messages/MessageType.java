package com.shabanov.lab3.peerWire.messages;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    KEEP_ALIVE(null, "KeepAlive"),

    HANDSHAKE(null, "Handshake"),

    CHOKE(0, "Choke"),

    UNCHOKE(1, "Unchoke"),

    INTERESTED(2, "Interested"),

    UNINTERESTED(3, "NotInterested"),

    HAVE(4, "Have"),

    BITFIELD(5, "BitField"),

    REQUEST(6, "Request"),

    PIECE(7, "Piece"),

    CANCEL(8, "Cancel");

    private static Map<Integer,MessageType> types;

    static {
        types = new HashMap<Integer, MessageType>();

        for (MessageType mt : values()) {
            types.put(Integer.valueOf(mt.getId()), mt);
        }

    }

    private Integer id;

    private String name;

    private MessageType (Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId () {
        return id == null ? -1 : id.intValue();
    }

    public static MessageType getMessage(int id) {
        Integer oid = Integer.valueOf(id);
        return types.get(oid);
    }

    @Override
    public String toString () {
        return name;
    }
}
