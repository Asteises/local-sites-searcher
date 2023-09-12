package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.model.Page;

import java.util.List;
import java.util.concurrent.RecursiveTask;

@Slf4j
@Service
@Getter
@AllArgsConstructor
public class PageParserPoolService extends RecursiveTask<List<Page>> {

    @Override
    protected List<Page> compute() {
        return null;
    }
}
