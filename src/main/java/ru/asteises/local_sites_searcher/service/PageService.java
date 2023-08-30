package ru.asteises.local_sites_searcher.service;

import org.jsoup.nodes.Document;
import ru.asteises.local_sites_searcher.core.model.Page;

import java.io.IOException;
import java.util.List;

public interface PageService {
    List<Page> searchData(List<String> urls, String word) throws IOException;

    Page parseData(Document document);
    Page savePage(Page pages);
}
