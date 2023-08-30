package ru.asteises.local_sites_searcher.service;

import java.io.IOException;
import java.util.List;

public interface PageService {

    List<String> searchInTitle(List<String> urls, String word) throws IOException;
}
