package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.PageConnectThread;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.repo.PageStorage;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.WebSiteConnect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PageServiceImpl implements PageService, WebSiteConnect {

    private final PageStorage pageStorage;

    /**
     * Метод принимает список URL сайтов, разбивает на отдельные записи
     * и передает их по одному в getConnection().
     * @param urls - список входящих URL.
     * @param word
     * @return
     */
    @Override
    public List<Page> searchData(List<String> urls, String word) {

        List<Page> pages = new ArrayList<>();
        for (String url: urls) {
            Document document = getConnection(url);
            Page page = parseData(document);
            savePage(page);
            pages.add(page);
        }
        return pages;
    }

    /**
     * Метод принимает Document JSOUP и парсит данные.
     * @param document
     * @return
     */
    @Override
    public Page parseData(Document document) {
        Page page = new Page();
        page.setId(UUID.randomUUID());
        page.setPath(document.baseUri());
        page.setCode(document.connection().response().statusCode());
        page.setContent(document.text());
        return page;
    }

    /**
     * Метод принимает Document JSOUP и сохраняет его в БД.
     * @param page - принимаем Document JSOUP.
     * @return - возвращаем Page.
     */
    @Override
    public Page savePage(Page page) {
        pageStorage.save(page);
        return page;
    }


    /**
     * Метод создающий соединение со страницей в новом потоке.
     * Создает и запускает новый поток.
     * @param url - входящий адрес страницы.
     * @return - возвращаем Document JSOUP.
     */
    @Override
    public Document getConnection(String url) {
        PageConnectThread pageConnectThread = PageConnectThread.createAndStart(url);
        pageConnectThread.run();
        return pageConnectThread.getDocument();
    }
}
