package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PageServiceImpl implements PageService, WebSiteConnect {

    private final PageStorage pageStorage;
    private final WebSiteStorage webSiteStorage;

    @Override
    public Set<Page> renewPagesDataBase(List<UUID> webSiteIds) {
        List<WebSite> webSites = webSiteStorage.findAllById(webSiteIds);
        Set<Page> pages = new HashSet<>();
        for (WebSite webSite : webSites) {
            pages = getPages(Set.of(webSite.getName()), webSite);
        }
        return pages;
    }

    public Set<Page> getPages(Set<String> anchors, WebSite webSite) {
        log.info("income anchors: " + anchors.toString());
        try {
            for (String anchor : anchors) {
                Document document = Jsoup.connect(anchor).get();
                Page newPage = parseData(document, webSite, anchor);
                pageStorage.save(newPage);
                Set<String> newAnchors = new HashSet<>(getAnchors(document, webSite.getName()));
                log.info("new anchors: " + newAnchors);
                webSite.getPages().add(newPage);
                Set<Page> pages = webSite.getPages();
                List<String> onSiteAnchors = pages.stream().map(Page::getPath).toList();
                log.info("total site anchors: " + onSiteAnchors.toString());
                newAnchors.removeAll(new HashSet<>(onSiteAnchors));
                log.info("new anchors after remove on site anchors: " + newAnchors);
                if (!newAnchors.isEmpty()) {
                    getPages(newAnchors, webSite);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        webSiteStorage.save(webSite);
        return new HashSet<>(webSite.getPages());
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
    public Page parseData(Document document, WebSite webSite, String anchor) {
        Page page = new Page();
        page.setId(UUID.randomUUID());
        page.setPath(anchor);
        log.info("Base uri: " + document.baseUri());
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
    public List<String> getAnchors(Document document, String siteAnchor) {
        List<String> anchors;
        Elements tempAnchors = document.getAllElements();
        anchors = tempAnchors.stream()
                .map(a -> a.absUrl("href"))
                .collect(Collectors.toList());
        anchors.removeIf(newAnchor -> !newAnchor.contains(siteAnchor));
        anchors.removeIf(a -> a.contains("png"));
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
    public List<Page> savePages(List<Page> pages) {
        pageStorage.saveAll(pages);
        return pages;
    }
}
