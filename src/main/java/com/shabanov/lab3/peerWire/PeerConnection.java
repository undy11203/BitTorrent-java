package com.shabanov.lab3.peerWire;

import com.shabanov.lab3.peerWire.messages.BitTorrentMessage;
import com.shabanov.lab3.peerWire.messages.HandShake;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class PeerConnection implements Closeable {
    private SocketChannel channel;

    private ByteBuffer inputBuffer;

    private ByteBuffer outputBuffer;
    private SocketAddress address;

    public PeerConnection (SocketChannel channel, int ibufSize, int obufSize) throws IOException {
        if (!channel.isConnected()) {
            throw new IllegalArgumentException("Unconnected Socket");
        }

        inputBuffer = ByteBuffer.allocate(ibufSize + 32);
        inputBuffer.order(ByteOrder.BIG_ENDIAN).clear().limit(1);
        outputBuffer = ByteBuffer.allocate(obufSize + 32);
        outputBuffer.order(ByteOrder.BIG_ENDIAN).clear();

        this.channel = channel;
        this.address = channel.getRemoteAddress();
        channel.configureBlocking(false);
    }

    public PeerConnection(SocketAddress address, int ibufSize, int obufSize) throws IOException {
        this.channel = SocketChannel.open(address);
        if (!channel.isConnected()) {
            throw new IllegalArgumentException("Unconnected Socket");
        }

        inputBuffer = ByteBuffer.allocate(ibufSize + 32);
        inputBuffer.order(ByteOrder.BIG_ENDIAN).clear().limit(1);
        outputBuffer = ByteBuffer.allocate(obufSize + 32);
        outputBuffer.order(ByteOrder.BIG_ENDIAN).clear();

        this.channel = channel;
        channel.configureBlocking(false);
    }

    public HandShake receiveHandShake() throws IOException {
        inputBuffer.clear().limit(1);
        while (inputBuffer.remaining() > 0) {
            channel.read(inputBuffer);
        }

        int length = 68;

        // Handshake itself
        inputBuffer.limit(length);
        while (inputBuffer.remaining() > 0) {
            channel.read(inputBuffer);
        }
        inputBuffer.flip();
        return BitTorrentMessageDecoder.decodeHandShakeFromBuffer(inputBuffer);
    }

    public BitTorrentMessage receiveMessage () throws IOException {
        try {
            inputBuffer.clear().limit(4);
            while (inputBuffer.remaining() > 0) {
                channel.read(inputBuffer);
            }
            inputBuffer.flip();
            int length = inputBuffer.getInt();

            inputBuffer.clear().limit(length);
            while (inputBuffer.remaining() > 0) {
                channel.read(inputBuffer);
            }
            inputBuffer.flip();
            return BitTorrentMessageDecoder.decodeMessageFromBuffer(inputBuffer, length);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public void sendMessage (BitTorrentMessage msg) throws IOException {
        outputBuffer.clear();
        BitTorrentMessageEncoder.encodeMessageToBuffer(outputBuffer, msg);
        outputBuffer.flip();
        channel.write(outputBuffer);
    }

    public static PeerConnection newConnection (SocketAddress addr, int ibufSize, int obufSize) throws IOException {
        return new PeerConnection(SocketChannel.open(addr), ibufSize, obufSize);
    }

    @Override
    public void close() throws IOException {
        try {
            channel.close();
        } catch (IOException e) {

        }
    }

    public boolean isClosed () {
        return !channel.isOpen();
    }

    @Override
    public String toString(){
        return address.toString();
    }

}
