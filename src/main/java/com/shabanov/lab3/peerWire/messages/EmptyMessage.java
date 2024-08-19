package com.shabanov.lab3.peerWire.messages;

public class EmptyMessage extends BitTorrentMessage {

    public static final EmptyMessage KEEP_ALIVE = new EmptyMessage(MessageType.KEEP_ALIVE);

    public static final EmptyMessage CHOKE = new EmptyMessage(MessageType.CHOKE);

    public static final EmptyMessage UNCHOKE = new EmptyMessage(MessageType.UNCHOKE);

    public static final EmptyMessage INTERESTED = new EmptyMessage(MessageType.INTERESTED);

    public static final EmptyMessage UNINTERESTED = new EmptyMessage(MessageType.UNINTERESTED);

    private final MessageType type;

    private EmptyMessage (MessageType type) {
        this.type = type;
    }

    @Override
    public MessageType getMessageType () {
        return type;
    }

}
