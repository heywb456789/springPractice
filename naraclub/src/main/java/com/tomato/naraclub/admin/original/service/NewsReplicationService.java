package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.entity.ArticleImage;
import java.util.List;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.service
 * @fileName : NewsReplicationService
 * @date : 2025-05-27
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface NewsReplicationService {

    void replicateToOtherSchema(Article savedArticle, List<ArticleImage> savedImages);

    void updateToOtherSchema(Article article, List<ArticleImage> newImages);

    void deleteToOtherSchema(Article article);
}
