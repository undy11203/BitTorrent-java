package com.shabanov.lab3.peerWire;

import com.shabanov.lab3.peerWire.messages.BitTorrentMessage;

import java.util.EventListener;

public interface PeerListener extends EventListener {

    public abstract void onReceive (PeerSession peer, BitTorrentMessage message);

    public abstract void onSend (PeerSession peer, BitTorrentMessage message);

    public abstract void onClose (PeerSession peer);

}
