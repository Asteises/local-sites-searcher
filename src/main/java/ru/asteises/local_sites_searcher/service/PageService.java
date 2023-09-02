package ru.asteises.local_sites_searcher.service;

import org.jsoup.nodes.Document;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PageService {

    Set<Page> renewPagesDataBase(List<UUID> webSiteIds);

    Set<Page> getPages(Set<String> anchors, WebSite webSite);

    Page parseData(Document document, WebSite webSite, String anchor);

    List<String> getAnchors(Document document, String anchor);

    List<String> getAnchors(String url);

    boolean checkPageExist(String url);

    Page savePage(Page pages);

    List<Page> savePages(List<Page> pages);
}
