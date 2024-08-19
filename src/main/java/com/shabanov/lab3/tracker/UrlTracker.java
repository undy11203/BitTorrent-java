package com.shabanov.lab3.tracker;

import com.shabanov.lab3.bencode.io.BencodeIterator;
import com.shabanov.lab3.bencode.model.BencodeDictionary;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class UrlTracker{

    private final URL url;

    public UrlTracker (URL url) {
        this.url = url;
    }

    public TrackerResponse sendRequest (TrackerRequest request) {
        return sendRequest(request, 1, TimeUnit.MINUTES);
    }

    public TrackerResponse sendRequest (TrackerRequest request, long time, TimeUnit unit) {
        URL reqUrl = null;
        try {
            reqUrl = new URL(getRequestUrl(url.toString(), request));
            URLConnection conn = reqUrl.openConnection();
            conn.setConnectTimeout((int) unit.toMillis(time));
            conn.setReadTimeout((int) (unit.toMillis(time) * 2));
            BencodeIterator bi = new BencodeIterator(conn.getInputStream());
            return new TrackerResponse((BencodeDictionary) bi.next());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrlParams(TrackerRequest req) {
        StringBuilder sb = new StringBuilder("?");

        sb.append("info_hash=");
        sb.append(req.getInfoHash().getUrlEncodedString());

        sb.append("&port=");
        sb.append(req.getPort());

        sb.append("&uploaded=");
        sb.append(req.getUploaded());

        sb.append("&downloaded=");
        sb.append(req.getDownloaded());

        sb.append("&left=");
        sb.append(req.getLeft());

        sb.append("&event=");
        sb.append(req.getEvent().getEventString());

        if (req.getIp() != null) {
            sb.append("&ip=");
            sb.append(req.getIp().getHostAddress());
        }

        sb.append("&numwant=");
        sb.append(req.getNumWant());

        sb.append("&peerId=");
        sb.append(req.getPeerId().toUrlEncodedString());

        return sb.toString();
    }

    private String getRequestUrl(String announce, TrackerRequest req) {
        System.out.println(announce + getUrlParams(req));
        return announce + getUrlParams(req);
    }

    @Override
    public String toString () {
        return "URL-Tracker(" + url + ")";
    }
}
