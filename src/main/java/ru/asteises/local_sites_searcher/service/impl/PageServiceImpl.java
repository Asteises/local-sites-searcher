package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.PageConnectThread;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;
import ru.asteises.local_sites_searcher.repo.PageStorage;
import ru.asteises.local_sites_searcher.repo.WebSiteStorage;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.WebSiteConnect;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PageServiceImpl implements PageService, WebSiteConnect {

    private final PageStorage pageStorage;
    private final WebSiteStorage webSiteStorage;

    @Override
    public Set<Page> renewPagesDataBase(Set<UUID> webSiteIds) {
        List<WebSite> webSites = webSiteStorage.findAllById(webSiteIds);
        Set<Page> pages = new HashSet<>();
        for (WebSite webSite : webSites) {
            pages = getPages(Set.of(webSite.getName()), webSite);
        }
        return pages;
    }

    @Override
    public Set<Page> getPages(Set<String> anchors, WebSite webSite) {
        Set<Page> pages = new HashSet<>();
        Set<String> pageAnchors = new HashSet<>();
        Set<String> onSitePageAnchors = webSite.getPages().stream()
                .map(Page::getPath)
                .collect(Collectors.toSet());
        log.info("Anchors on site: " + onSitePageAnchors.toString());
        if (anchors.isEmpty()) {
            return Collections.emptySet();
        }
        for (String anchor : anchors) {
            if (onSitePageAnchors.contains(anchor)) {
                log.info("Anchor already exist in site pages " + anchor);
                continue;
            }
            Document document = getConnection(anchor);
            if (document != null) {
                Page page = parseData(document, webSite);
                pages.add(page);
                pageAnchors = getAnchors(document, anchor);
                log.info("New anchors: " + pageAnchors.toString());
                pageAnchors.removeAll(onSitePageAnchors);
                log.info("After removeAll new anchors: " + pageAnchors.toString());
            } else {
                System.out.println("Cant parse page: " + anchor);
            }
        }
//        savePages(pages);
        webSite.setPages(pages);
        webSiteStorage.save(webSite);
        getPages(pageAnchors, webSite);
        return pages;
    }

    /**
     * Метод создающий соединение со страницей в новом потоке.
     * Создает и запускает новый поток.
     *
     * @param url - входящий адрес страницы.
     * @return - возвращаем Document JSOUP.
     */
    @Override
    public Document getConnection(String url) {
        PageConnectThread pageConnectThread = PageConnectThread.createAndStart(url);
        Document document = null;
        try {
            pageConnectThread.getThrd().join();
            document = pageConnectThread.getDocument();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Метод принимает Document JSOUP и парсит данные.
     *
     * @param document
     * @return
     */
    @Override
    public Page parseData(Document document, WebSite webSite) {
        Page page = new Page();
        page.setId(UUID.randomUUID());
        page.setPath(document.baseUri());
        page.setCode(document.connection().response().statusCode());
        page.setContent(document.text());
        page.setWebSite(webSite);
        return page;
    }

    /**
     * Метод парсит ссылки из документа и составляет список тех, которых еще нет в БД.
     * Парсит только ссылки содержащие название сайта.
     *
     * @param document
     * @return
     */
    @Override
    public Set<String> getAnchors(Document document, String anchor) {
        Set<String> anchors;
        Elements tempAnchors = document.select("a");
        anchors = tempAnchors.stream()
                .map(a -> a.attr("href"))
                .collect(Collectors.toSet());
        anchors.removeIf(newAnchor -> !newAnchor.contains(anchor));
        return anchors;
    }

    public List<String> getAnchors(String url) {
        Document document = getConnection(url);
        Elements anchors = document.select("a");
        return anchors.stream()
                .map(anchor -> anchor.attr("href"))
                .toList();
    }

    /**
     * Метод проверяет наличие страницы в БД.
     *
     * @param url - принимает url страницы.
     * @return возвращает true если страница уже есть в БД.
     */
    @Override
    public boolean checkPageExist(String url) {
        Optional<Page> page = pageStorage.findByPath(url);
        return page.isPresent();
    }


    /**
     * Метод принимает Document JSOUP и сохраняет его в БД.
     *
     * @param page - принимаем Document JSOUP.
     * @return - возвращаем Page.
     */
    @Override
    public Page savePage(Page page) {
        pageStorage.save(page);
        return page;
    }

    @Override
    public Set<Page> savePages(Set<Page> pages) {
        pageStorage.saveAll(pages);
        return pages;
    }
}
