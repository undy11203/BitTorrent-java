package com.shabanov.lab3;

import com.shabanov.lab3.data.FileInfo;
import com.shabanov.lab3.data.PeerId;
import com.shabanov.lab3.data.Sha1Hash;
import com.shabanov.lab3.data.TorrentMetaInfo;
import com.shabanov.lab3.peerWire.*;
import com.shabanov.lab3.peerWire.messages.*;
import com.shabanov.lab3.utils.FixLenFile;
import com.shabanov.lab3.utils.SequenceInStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class BittorrentSeed {

    public static void main( String[] args ) {
        try {
            TorrentMetaInfo metaInfo = new TorrentMetaInfo(args[0]);
            List<FileInfo> files = metaInfo.getInfoSection().getFiles();
            List<FixLenFile> files1 = new ArrayList<>();
            for (FileInfo file : files) {
                String path = file.getPath();
                path = "input" + path.substring(path.indexOf("output")+6, path.length());
                String name = (file.getPath().length() > 0 ? path + "/" : "") + file.getName();
                files1.add(new FixLenFile(new RandomAccessFile(name, "r"), (int) file.getLength()));
            }
            SequenceInStream inputStream = new SequenceInStream(files1);

            int allLength = (int) metaInfo.getInfoSection().getTotalLength();
            int pieceLength = (int) metaInfo.getInfoSection().getPieceLength();
            int allSegment = metaInfo.getInfoSection().getPieceHashes().size();
            Sha1Hash sha = metaInfo.getInfoHash();
            PeerId peerId = new PeerId("--TEST--012345678911");

            SeedAccept seedAccept = new SeedAccept(8080);
            PeerSessionPool pool = new PeerSessionPool();
            pool.addPeerListener(new PeerListener() {
                @Override
                public void onReceive(PeerSession peer, BitTorrentMessage message) {
                    System.out.println("Recieve message: " + message.getMessageType());
                    switch (message.getMessageType()){
                        case HANDSHAKE -> {
                            peer.sendMessage(new HandShake(new BitSet(8*8), sha, peerId));
                            BitSet pieces = new BitSet(allSegment);
                            pieces.set(0, allSegment, true);
                            peer.sendMessage(new BitFieldMessage(pieces));
                            peer.sendMessage(EmptyMessage.UNCHOKE);
                        }
                        case REQUEST -> {
                            int index = ((RequestMessage)message).getIndex();
                            int offset = ((RequestMessage)message).getOffset();
                            int length = ((RequestMessage)message).getLength();
                            long position = (long) index * pieceLength + offset;

                            try{

                                byte[] content = new byte[length];
                                inputStream.readSegment(content, index*pieceLength+offset);
                                peer.sendMessage(new PieceMessage(index, offset, ByteBuffer.wrap(content)));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onSend(PeerSession peer, BitTorrentMessage message) {
                    System.out.println("Send message: " + message.getMessageType());
                    switch (message.getMessageType()){

                    }
                }

                @Override
                public void onClose(PeerSession peer) {

                }
            });
            while (true) {
                SocketChannel channel = seedAccept.waitAccept();
                PeerConnection connection = new PeerConnection(channel, pieceLength, pieceLength);
                pool.newSession(connection);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
