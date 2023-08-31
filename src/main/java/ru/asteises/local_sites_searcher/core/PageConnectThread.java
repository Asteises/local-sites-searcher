package ru.asteises.local_sites_searcher.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalTime;

public class PageConnectThread implements Runnable {

    private final Thread thrd;
    private final String url;
    private Document document;

    public PageConnectThread(String name, String url) {
        thrd = new Thread(this, name);
        this.url = url;
    }

    public static PageConnectThread createAndStart(String url) {
        System.out.println("Create and start new Thread for: " + url);
        PageConnectThread pageConnectThread = new PageConnectThread("Thread: " + url, url);
        pageConnectThread.getThrd().start();
        System.out.println("Поток начал свою работу в :" + LocalTime.now());
        return pageConnectThread;
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
            System.out.println("Run new Thread: " + getThrd().getState() + ", Thread num: " + getThrd().getId());
            Document doc = Jsoup.connect(getUrl()).get();
            setDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Thread getThrd() {
        return thrd;
    }
}
