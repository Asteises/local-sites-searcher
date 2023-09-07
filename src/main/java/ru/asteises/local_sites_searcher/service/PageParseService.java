package ru.asteises.local_sites_searcher.service;

import org.jsoup.nodes.Document;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;

import java.util.List;
import java.util.Set;

public interface PageParseService {

    Set<Page> getPages(Set<String> anchors, WebSite webSite);

    Page getPage(String anchor, WebSite webSite);

    Page parseData(Document document, WebSite webSite, String anchor);

    List<String> getAnchors(Document document, String siteAnchor);

    List<String> getAnchors(String url);

    boolean checkPageExist(String url);

    List<Page> savePages(Set<Page> pages);
}
