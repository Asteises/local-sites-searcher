package ru.asteises.local_sites_searcher.service;

import org.jsoup.nodes.Document;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;

import java.io.IOException;
import java.util.Set;

public interface PageService {

    Set<Page> incomeNewPages(Set<WebSite> newWebSites) throws IOException, InterruptedException;

    Page parseData(Document document);

    Set<String> getAnchors(Document document);

    Page savePage(Page pages);
}
