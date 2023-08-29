package ru.asteises.local_sites_searcher.service;

import java.io.IOException;

public interface PageService {

    String searchInTitle(String word) throws IOException;
}
