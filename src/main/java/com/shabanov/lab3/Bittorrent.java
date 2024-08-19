package com.shabanov.lab3;

import com.shabanov.lab3.data.*;
import com.shabanov.lab3.peerWire.*;
import com.shabanov.lab3.peerWire.messages.*;
import com.shabanov.lab3.tracker.TrackerRequest;
import com.shabanov.lab3.tracker.TrackerResponse;
import com.shabanov.lab3.tracker.UrlTracker;
import com.shabanov.lab3.utils.FixLenFile;
import com.shabanov.lab3.utils.SequenceOutputStream;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Hello world!
 *
 */
public class Bittorrent {

    public static void createDirectories(String path) {
        String[] directories = path.split("/"); // Разделяем путь на отдельные директории
        StringBuilder currentPath = new StringBuilder(); // Используем StringBuilder для построения текущего пути

        for (String directory : directories) {
            currentPath.append(directory).append("/"); // Добавляем текущую директорию к текущему пути
            File dir = new File(currentPath.toString());
            dir.mkdir(); // Создаем новую директорию
        }
    }

    public static void main( String[] args ) {
        try {
            TorrentMetaInfo metaInfo = new TorrentMetaInfo(args[0]);
            List<FileInfo> files = metaInfo.getInfoSection().getFiles();
            for (FileInfo fileInfo : files) {
                createDirectories(fileInfo.getPath());
            }
            List<FixLenFile> fixLenFiles = new ArrayList<>();
            for(FileInfo file: files){
                RandomAccessFile outputStream = new RandomAccessFile((file.getPath().length() > 0? file.getPath() + "/": "") + file.getName(), "rw");
                fixLenFiles.add(new FixLenFile(outputStream, (int)file.getLength()));
            }
            SequenceOutputStream sequenceOutputStream = new SequenceOutputStream(fixLenFiles);

            int allLength = (int) metaInfo.getInfoSection().getTotalLength();
            int pieceLength = (int) metaInfo.getInfoSection().getPieceLength();
            List<Sha1Hash> sha1Hashes = metaInfo.getInfoSection().getPieceHashes();
            int allSegment = metaInfo.getInfoSection().getPieceHashes().size();
            Sha1Hash sha = metaInfo.getInfoHash();
            System.out.println("Pieces count: " + allSegment);
            System.out.println("Hash: " + sha.getString());
            PeerId peerId = new PeerId("--TEST--012345678911");
            List<PeerInfo> peerInfoList = new ArrayList<>();
            if(args.length == 1) {
                TrackerRequest request = new TrackerRequest(sha, 8080, 0,
                        0, allLength,
                        TrackerEvent.STARTED, InetAddress.getLocalHost(),
                        50, peerId);
                UrlTracker urlTracker = new UrlTracker(new URL(metaInfo.getAnnounce()));
                TrackerResponse response = urlTracker.sendRequest(request);
                peerInfoList = response.getPeers();
                System.out.println(response.toString());
            }else if(args.length == 2){
                for (int i = 1; i < args.length; i++){
                    peerInfoList.add(new PeerInfo(args[i]));
                }
            }

            PeerSessionPool pool = new PeerSessionPool();
            final boolean[] isEnd = {false};
            pool.addPeerListener(new PeerListener() {
                BitSet capSegments = new BitSet(allSegment);
                @Override
                public void onReceive(PeerSession peer, BitTorrentMessage message){
                    switch (message.getMessageType()){
                        case BITFIELD -> {
                            peer.sendMessage(EmptyMessage.INTERESTED);
                        }
                        case UNCHOKE -> {
                            if(!capSegments.get(allSegment-1)){
                                int lastLength = allLength - pieceLength*(allSegment-1);
                                int length = Math.min(1<<14, lastLength);
                                capSegments.set(allSegment-1);
                                peer.sendMessage(new RequestMessage(allSegment-1, 0, length));
                                peer.offset += length;
                            }else{
                                int index = capSegments.nextClearBit(0);
                                capSegments.set(index);
                                int length = 1<<14;
                                peer.sendMessage(new RequestMessage(index, 0, length));
                            }
                        }
                        case PIECE -> {
                            ByteBuffer buff = ((PieceMessage) message).getPieceContents();

                            int index = ((PieceMessage) message).getIndex();
                            int off = ((PieceMessage) message).getOffset();
                            int length = buff.remaining();

                            peer.offset -= length;
//                            if(peer.offset < 0 || peer.offset > 1<<18) System.out.println(peer.offset);
                            if(peer.offset == 0){
                                int limit = (index == allSegment - 1) ? allLength - pieceLength*(allSegment-1): pieceLength;
                                if(peer.equalsSegment(sha1Hashes.get(index), limit)) {
                                    System.out.println("Download [" + peer.getLocalClaimedPieces().cardinality() + ", " + allSegment + "]");
                                    try{
                                        sequenceOutputStream.writeSegment(peer.getSegment(limit), index*pieceLength);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    peer.sendMessage(new HaveMessage(index));
                                }else{
                                    capSegments.set(index, false);
                                }
                                peer.offset = 0;

                                if(capSegments.cardinality() != allSegment){
                                    int reqIndex = capSegments.nextClearBit(0);
                                    capSegments.set(reqIndex);
                                    int currentPieceSize = (reqIndex == allSegment - 1) ?
                                            allLength - pieceLength*(allSegment-1) : pieceLength;
                                    length = Math.min(currentPieceSize, 1<<14);
                                    peer.sendMessage(new RequestMessage(reqIndex, 0, length));
                                    peer.offset += length;
                                }
                                else if(peer.getLocalClaimedPieces().cardinality() != allSegment){
                                    int reqIndex = peer.getLocalClaimedPieces().nextClearBit(0);
                                    int currentPieceSize = (reqIndex == allSegment - 1) ?
                                            allLength - pieceLength*(allSegment-1) : pieceLength;
                                    length = Math.min(currentPieceSize, 1<<14);
                                    peer.sendMessage(new RequestMessage(reqIndex, 0, length));
                                    peer.offset += length;
                                }else if(peer.getLocalClaimedPieces().cardinality() == allSegment){
                                    isEnd[0] = true;
                                }
                            }
                        }
                        case CHOKE -> {
                            if(peer.getLocalClaimedPieces().cardinality() == allSegment){
                                isEnd[0] = true;
                            }
                        }
                    }

                }

                @Override
                public void onSend(PeerSession peer, BitTorrentMessage message) {
                    switch (message.getMessageType()){
                        case REQUEST -> {
                            int index = ((RequestMessage)message).getIndex();
                            int offset = ((RequestMessage)message).getOffset();
                            int length = ((RequestMessage)message).getLength();
                            if(index == allSegment - 1){
                                int lastLength = allLength - pieceLength*(allSegment-1);
                                if(offset + length != lastLength){
                                    int newlength = (lastLength - offset - length) - length >= 0?
                                            length :
                                            lastLength - offset - length;
                                    peer.sendMessage(new RequestMessage(index, offset+length, newlength));
                                    peer.offset += newlength;
                                }
                            }else{
                                if(offset + length != pieceLength){
                                    peer.sendMessage(new RequestMessage(index, offset+length, length));
                                    peer.offset += length;
                                }
                            }
                        }
                        case HAVE -> {

                        }
                    }
                }

                @Override
                public void onClose(PeerSession peer) {
                    System.out.println("Session close " + peer.toString());
                    int set = peer.getLocalClaimedPieces().size();
                    if(peer.getLocalClaimedPieces().size() == allSegment){
                        pool.close();
                    }
                }
            });

            Selector selector = Selector.open();
            for (PeerInfo peer : peerInfoList) {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.connect(peer.getAddress());
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                int maxPeer = 5;
                int nowPeer = pool.size();

                while (iterator.hasNext() && nowPeer <= maxPeer) {
                    SelectionKey key = iterator.next();
                    try {
                        if (key.isConnectable()) {
                            ((SocketChannel)key.channel()).finishConnect();
                            System.out.println("Connect to: " + ((SocketChannel) key.channel()).getRemoteAddress());
                            SocketChannel channel = (SocketChannel) key.channel();
                            PeerConnection peerConnection = new PeerConnection(channel, pieceLength, pieceLength);
                            pool.newSession(peerConnection);
                            BitTorrentMessage msg = new HandShake(new BitSet(8 * 8), sha, peerId);
                            peerConnection.sendMessage(msg);
                            iterator.remove();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        key.channel().close();
                        key.cancel();
                        iterator.remove();
                    }
                }
                if(isEnd[0] == true){
                    break;
                }
            }
            pool.close();
            if(args.length == 1){
                TrackerRequest request = new TrackerRequest(sha, 8080, 0,
                        0, allLength,
                        TrackerEvent.COMPLETED, InetAddress.getLocalHost(),
                        50, peerId);
                UrlTracker urlTracker = new UrlTracker(new URL(metaInfo.getAnnounce()));
                TrackerResponse response = urlTracker.sendRequest(request);
                System.out.println(response.toString());
            }


        }catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
