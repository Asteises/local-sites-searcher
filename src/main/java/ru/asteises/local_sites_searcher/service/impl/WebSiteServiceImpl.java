package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.model.WebSite;
import ru.asteises.local_sites_searcher.repo.WebSiteStorage;
import ru.asteises.local_sites_searcher.service.WebSiteService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WebSiteServiceImpl implements WebSiteService {

    private final WebSiteStorage webSiteStorage;

    @Override
    public Set<WebSite> incomeNewWebSites(List<String> urls) {
        Set<String> newCheckedUrls = checkWebSiteExist(urls);
        if (!newCheckedUrls.isEmpty()) {
            return createWebSite(newCheckedUrls);
        } else {
            throw new RuntimeException("All urls already exist");
        }
    }

    /**
     * Метод проверяет наличие сайта в БД.
     *
     * @param name
     * @return
     */
    @Override
    public boolean checkWebSiteExist(String name) {
        return webSiteStorage.findByName(name).isPresent();
    }

    /**
     * Метод проверяет, какие из пришедших URL сайтов уже есть в БД.
     *
     * @param urls - входящий список url.
     * @return - возвращаем сет из url, которых нет в БД.
     */
    @Override
    public Set<String> checkWebSiteExist(List<String> urls) {
        Set<String> incomeWebSiteNames = new HashSet<>(urls);
        List<String> webSiteNames = webSiteStorage.findAll().stream().map(WebSite::getName).toList();
        webSiteNames.forEach(incomeWebSiteNames::remove);
        return incomeWebSiteNames;
    }

    /**
     * Метод создает новый WebSite.
     *
     * @param url
     * @param theme
     * @return
     */
    @Override
    public WebSite createWebSite(String url, String theme) {
        // TODO Темы сделать в формате ENUM
        WebSite webSite = new WebSite();
        webSite.setId(UUID.randomUUID());
        webSite.setName(url);
        webSite.setTheme(theme);
        webSite.setPagesId(new HashSet<>());
        return webSite;
    }

    @Override
    public Set<WebSite> createWebSite(Set<String> urls) {
        Set<WebSite> newWebSites = new HashSet<>();
        for (String url : urls) {
            WebSite webSite = createWebSite(url, "none");
            newWebSites.add(webSite);
        }
        saveWebSite(newWebSites);
        return newWebSites;
    }

    /**
     * Метод сохраняет WebSite.
     *
     * @param webSite
     * @return
     */
    @Override
    public WebSite saveWebSite(WebSite webSite) {
        webSiteStorage.save(webSite);
        return webSite;
    }

    @Override
    public Set<WebSite> saveWebSite(Set<WebSite> newWebSites) {
        webSiteStorage.saveAll(newWebSites);
        return newWebSites;
    }
}
