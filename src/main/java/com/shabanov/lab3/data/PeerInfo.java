package com.shabanov.lab3.data;

import java.net.InetSocketAddress;

public class PeerInfo {
    private final InetSocketAddress address;

    public PeerInfo (InetSocketAddress address) {
        this.address = address;
    }

    public PeerInfo (String address) {
        String hostname = address.substring(0, address.indexOf(":"));
        int port = Integer.parseInt(address.substring(address.indexOf(":")+1, address.length()));
        this.address = new InetSocketAddress(hostname, port);
    }

    public InetSocketAddress getAddress () {
        return address;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder(PeerInfo.class.getSimpleName());

        sb.append("{Address=");
        sb.append(address);

        return sb.append("}").toString();
    }
}
