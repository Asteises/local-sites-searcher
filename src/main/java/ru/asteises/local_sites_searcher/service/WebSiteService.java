package ru.asteises.local_sites_searcher.service;

import ru.asteises.local_sites_searcher.core.model.WebSite;

import java.util.List;
import java.util.Set;

public interface WebSiteService {

    Set<WebSite> incomeNewWebSites(List<String> urls);

    boolean checkWebSiteExist(String name);

    Set<String> checkWebSiteExist(List<String> urls);

    WebSite createWebSite(String url, String theme);

    Set<WebSite> createWebSite(Set<String> urls);

    WebSite saveWebSite(WebSite webSite);

    Set<WebSite> saveWebSite(Set<WebSite> newWebSites);
}
