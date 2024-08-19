package com.shabanov.lab3.peerWire;

import com.shabanov.lab3.data.Sha1Hash;
import com.shabanov.lab3.peerWire.messages.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public final class PeerSession implements Closeable {

    final PeerConnection connection;

    final ExecutorService eventExecutor;

    final boolean shutdownEventExecutor;

    final ExecutorService connectionExecutor;

    final boolean shutdownConnectionExecutor;

    BlockingQueue<BitTorrentMessage> outQueue = new LinkedBlockingQueue<BitTorrentMessage>();

    Set<PeerListener> listeners = new CopyOnWriteArraySet<PeerListener>();

    public int offset = 0;

    private static BitSet localClaimedPieces = new BitSet();

    private final BitSet remoteClaimedPieces = new BitSet();

    private ByteBuffer segment = ByteBuffer.allocate(1<<18);

    private boolean isChoke = true;
    private boolean isInterested = false;


    private PeerSession (PeerConnection conn, ExecutorService evExec, boolean sdEv, ExecutorService clExec, boolean sdCl)
    {
        this.connection = Objects.requireNonNull(conn);
        this.eventExecutor = Objects.requireNonNull(evExec);
        this.shutdownEventExecutor = sdEv;
        this.connectionExecutor = Objects.requireNonNull(clExec);
        this.shutdownConnectionExecutor = sdCl;

        connectionExecutor.submit(new ReceiveThread());
        connectionExecutor.submit(new SendThread());
    }

    public BitSet getLocalClaimedPieces () {
        return localClaimedPieces;
    }

    public BitSet getRemoteClaimedPieces () {
        return remoteClaimedPieces;
    }

    public boolean isChoke() {
        return isChoke;
    }

    public boolean isInterested() {
        return isInterested;
    }


    public boolean sendMessage (BitTorrentMessage message) {
        return outQueue.offer(message);
    }

    public void addPeerListener (PeerListener pl) {
        listeners.add(Objects.requireNonNull(pl));
    }

    public void removePeerListener (PeerListener pl) {
        listeners.remove(pl);
    }

    @Override
    public void close() throws IOException {
        synchronized (connection) {
            if (!connection.isClosed()) {
                connection.close();
//                outQueue.offer(EmptyMessage.KEEP_ALIVE);
                fireCloseEvent();
            }
        }
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("PeerSession{");

        sb.append("Connection=").append(connection.toString());

        return sb.append("}").toString();
    }

    public boolean isClosed () {
        return connection.isClosed();
    }

    public static PeerSession newSession (PeerConnection conn, ExecutorService eventExec, ExecutorService connExec) {
        boolean shutdownEvent = false;
        ExecutorService ee = eventExec;
        ExecutorService ce = connExec;
        boolean shutdownConn = false;

        if (ee == null) {
            ee = Executors.newSingleThreadExecutor();
            shutdownEvent = true;
        }

        if (ce == null) {
            ce = Executors.newFixedThreadPool(2);
            shutdownConn = true;
        }


        return new PeerSession(conn, ee, shutdownEvent, ce, shutdownConn);
    }

    void processInputMessage (BitTorrentMessage msg) {
        assert msg != null;

        switch (msg.getMessageType()) {
            case HANDSHAKE:
                HandShake hss = (HandShake) msg;
                break;

            case CHOKE:
                isChoke = true;
                break;

            case UNCHOKE:
                isChoke = false;
                break;

            case INTERESTED:
                break;

            case UNINTERESTED:
                break;

            case HAVE:
                remoteClaimedPieces.set(((HaveMessage) msg).getPieceIndex());
                break;

            case BITFIELD:
                remoteClaimedPieces.or(((BitFieldMessage) msg).getBitField());
                break;
            case PIECE:
                synchronized (segment){
                    segment.position(((PieceMessage)msg).getOffset());
                    segment.put(((PieceMessage)msg).getPieceContents().array());
                    segment.rewind();
                }
                break;
        }
    }


    void processOutputMessage (BitTorrentMessage msg) {
        assert msg != null;

        switch (msg.getMessageType()) {
            case HANDSHAKE:
                HandShake hss = (HandShake) msg;
                break;

            case CHOKE:
                break;

            case UNCHOKE:
                break;

            case INTERESTED:
                isInterested = true;
                break;

            case UNINTERESTED:
                isInterested = false;
                break;

            case HAVE:
                localClaimedPieces.set(((HaveMessage) msg).getPieceIndex());
                break;

            case BITFIELD:
                localClaimedPieces.or(((BitFieldMessage) msg).getBitField());
                break;

            case PIECE:
                remoteClaimedPieces.set(((PieceMessage) msg).getIndex());
                break;
            case REQUEST:
                break;
        }
    }

    void fireReceiveEvent (BitTorrentMessage msg) {
        eventExecutor.submit(new ReceiveFirer(msg));
    }

    void fireSendEvent (BitTorrentMessage msg) {
        eventExecutor.submit(new SendFirer(msg));
    }

    private void fireCloseEvent () {
        eventExecutor.submit(new CloseFirer());
    }

    public boolean equalsSegment(Sha1Hash sha1Hash, int limit) {
        Sha1Hash newSha;
//        synchronized (segment){
            segment.limit(limit);
            newSha = Sha1Hash.forByteBuffer(this.segment);
            segment.limit(1<<18);
            segment.rewind();
//        }
        if(newSha.equals(sha1Hash)){
            return true;
        }
        return false;
    }

    public byte[] getSegment(int limit){
        byte[] res = new byte[limit];
//        synchronized (segment){
            segment.limit(limit);
            segment.get(res);
            segment.limit(1<<18);
            segment.rewind();
//        }
        return res;
    }

    final class ReceiveThread implements Runnable {

        @Override
        public void run () {
            try {
                int messages = 0;
                while (!isClosed()) {

                    BitTorrentMessage msg = null;
                    if (messages == 0) {
                        msg = connection.receiveHandShake();
                    } else {
                        msg = connection.receiveMessage();
                    }
                    // Process it
                    if (msg != null) {
                        processInputMessage(msg);
                        fireReceiveEvent(msg);
                        messages++;
                    }
                }
            } catch (IOException e) {

            } finally {
                try {
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    final class SendThread implements Runnable {

        @Override
        public void run () {
            try {
                while (!isClosed()) {

                    BitTorrentMessage msg = outQueue.poll(1, TimeUnit.MINUTES);
                    if (msg != null) {
                        connection.sendMessage(msg);
                        fireSendEvent(msg);
                    }
                }
            } catch (Exception e) {

            } finally {
                try {
                    close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    final class ReceiveFirer implements Runnable {

        private final BitTorrentMessage message;
        ReceiveFirer (BitTorrentMessage message) {
            this.message = message;
        }

        @Override
        public void run () {
            processInputMessage(message);
            for (PeerListener pl : listeners) {
                pl.onReceive(PeerSession.this, message);
            }
        }
    }

    final class SendFirer implements Runnable {

        private final BitTorrentMessage message;

        SendFirer (BitTorrentMessage message) {
            this.message = message;
        }

        @Override
        public void run () {
            processOutputMessage(message);
            for (PeerListener pl : listeners) {
                pl.onSend(PeerSession.this, message);
            }
        }
    }

    final class CloseFirer implements Runnable {

        @Override
        public void run () {
            for (PeerListener pl : listeners) {
                pl.onClose(PeerSession.this);
                if (shutdownEventExecutor) {
                    eventExecutor.shutdown();
                }
                if (shutdownConnectionExecutor) {
                    connectionExecutor.shutdown();
                }
            }
        }
    }
}
