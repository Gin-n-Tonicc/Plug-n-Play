package com.example.plug_n_play.services;

import com.example.plug_n_play.model.Page;
import com.example.plug_n_play.repository.PageRepository;
import com.example.plug_n_play.utils.MapUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    public void savePageToVectorDatabase(Page page) {
        var metadata = MapUtils.stringToMap(page.getMetaData());
        metadata.put("id", page.getId());
        metadata.put("title", page.getTitle());
        metadata.put("url", page.getUrl());
        metadata.put("siteUrl", page.getSite().getUrl());

        Document document = new Document(
                page.getTitle() + " " + page.getUrl() + '\n' + page.getData(),
                metadata
        );

        vectorStore.add(tokenTextSplitter.apply(List.of(document)));
    }
}