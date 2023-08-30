package ru.asteises.local_sites_searcher.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.PageThread;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.WebSiteConnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PageServiceImpl implements PageService, WebSiteConnect {

    // TODO Написать классы сущностей, которые будет хранить данные 1 сайта - 1 страницы
    @Override
    public List<String> searchInTitle(List<String> urls, String word) throws IOException {
        List<String> titles = new ArrayList<>();
        // "https://bootlegbricks.ru/";
        for (String url: urls) {
            Document document = getConnection(url);
            int code = document.connection().response().statusCode();
            System.out.println(code);
            titles.add(getConnection(url).title());
        }
        return titles;
    }

    @Override
    public Document getConnection(String url) throws IOException {
        PageThread pageThread = PageThread.createAndStart(url);
        pageThread.run();
        return pageThread.getDocument();
    }
}
