package com.shabanov.lab3.peerWire;

import java.io.IOException;
import java.io.SequenceInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SeedAccept {
    private ServerSocketChannel channel;

    public SeedAccept(int port) {
        try {
            this.channel = ServerSocketChannel.open();
//            channel.configureBlocking(false);
            this.channel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), port));
            System.out.println("Create socket on " + InetAddress.getLocalHost().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocketChannel getChannel() {
        return channel;
    }

    public SocketChannel waitAccept() {
        try {
            SocketChannel socketChannel = this.channel.accept();
            if(socketChannel.isConnected()){
                System.out.println("Принято входящее соединение: " + socketChannel.getRemoteAddress());
            }
            return socketChannel;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
