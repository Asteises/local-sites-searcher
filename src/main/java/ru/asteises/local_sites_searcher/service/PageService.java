package ru.asteises.local_sites_searcher.service;

import org.jsoup.nodes.Document;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PageService {

    void renewPagesDataBase(UUID webSitesId);

    Set<Page> getPages(List<String> anchors, WebSite webSite);

    Page getPage(String anchor, WebSite webSite);

    Document getConnection(String url);

    Page parseData(Document document, WebSite webSite, String anchor);

    List<String> getAnchors(Document document, String siteAnchor);

    void savePages(Set<Page> pages);

}
