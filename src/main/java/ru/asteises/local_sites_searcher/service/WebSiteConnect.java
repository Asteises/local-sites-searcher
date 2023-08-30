package ru.asteises.local_sites_searcher.service;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface WebSiteConnect {
    Document getConnection(String url) throws IOException;
}
