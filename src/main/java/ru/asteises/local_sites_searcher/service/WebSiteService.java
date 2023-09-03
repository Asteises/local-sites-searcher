package ru.asteises.local_sites_searcher.service;

import ru.asteises.local_sites_searcher.core.model.WebSite;

import java.util.List;
import java.util.Set;

public interface WebSiteService {

    List<WebSite> incomeNewWebSites(List<String> urls);

    boolean checkWebSiteExist(String name);

    List<String> checkWebSiteExist(List<String> urls);

    WebSite createWebSite(String url, String theme);

    List<WebSite> createWebSite(List<String> urls);

    WebSite saveWebSite(WebSite webSite);

    List<WebSite> saveWebSite(List<WebSite> newWebSites);
}
