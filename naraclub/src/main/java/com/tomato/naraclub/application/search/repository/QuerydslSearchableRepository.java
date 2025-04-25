package com.tomato.naraclub.application.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.search.repository
 * @fileName : SearchServiceRepository
 * @date : 2025-04-25
 * @description :
 * @AUTHOR : MinjaeKim
 */
@NoRepositoryBean
public interface QuerydslSearchableRepository<T, ID> extends JpaRepository<T, ID>,
    QuerydslPredicateExecutor<T> {

}
