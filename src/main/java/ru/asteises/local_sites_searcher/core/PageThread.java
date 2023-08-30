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
        System.out.println("Create and start new Thread for: " + url);
        return new PageThread("Thread: " + url, url);
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
            System.out.println("Run new Thread: ");
            Document doc = Jsoup.connect(getUrl()).get();
            setDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
