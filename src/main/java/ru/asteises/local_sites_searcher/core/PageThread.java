package ru.asteises.local_sites_searcher.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PageThread implements Runnable {

    private final Thread thrd;
    private final String url;
    private Document document;

    public PageThread(String name, String url) {
        thrd = new Thread(this, name);
        this.url = url;
    }

    public static PageThread createAndStart(String url) {
        PageThread pageThread = new PageThread("Поток для: " + url, url);
        pageThread.thrd.start();
        return pageThread;
    }

    public String getUrl() {
        return url;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public void run() {
        try {
            setDocument(Jsoup.connect(getUrl()).get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
