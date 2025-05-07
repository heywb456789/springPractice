package com.tomato.naraclub.application.original.repository;

import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.repository.custom.NewsArticleCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<Article, Long>, NewsArticleCustomRepository {
}
