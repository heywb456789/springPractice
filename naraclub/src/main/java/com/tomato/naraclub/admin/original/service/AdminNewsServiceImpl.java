package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.admin.original.dto.Base64ImageData;
import com.tomato.naraclub.admin.original.dto.NewsArticleRequest;
import com.tomato.naraclub.admin.original.dto.NewsArticleResponse;
import com.tomato.naraclub.admin.original.dto.NewsListRequest;
import com.tomato.naraclub.admin.original.repository.AdminNewsImageRepository;
import com.tomato.naraclub.admin.original.repository.AdminNewsRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.entity.Admin;
import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.entity.ArticleImage;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.code.StorageCategory;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.util.FileStorageService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminNewsServiceImpl implements AdminNewsService {

    private final AdminNewsRepository adminNewsRepository;
    private final AdminNewsImageRepository adminNewsImageRepository;
    private final FileStorageService fileStorageService;

    @Value("${spring.app.display}")
    private String displayUrl;

    @Override
    public ListDTO<NewsArticleResponse> getNewsList(NewsListRequest request, AdminUserDetails user,
        Pageable pageable) {
        return adminNewsRepository.getNewsList(request, user, pageable);
    }

    @Override
    public Article getArticleById(Long id) {
        return adminNewsRepository.findByIdAndDeleted(id, false)
            .orElseThrow(() -> new APIException(ResponseStatus.ARTICLE_NOT_EXIST));
    }

    @Override
    @Transactional
    public NewsArticleResponse uploadNews(NewsArticleRequest request, AdminUserDetails user) {
        Admin admin = user.getAdmin();

        // 5. 기사 엔티티 생성 및 저장
        Article article = Article.builder()
            .author(admin)
            .title(request.getTitle())
            .subTitle(request.getSubtitle())
            .type(request.getType())
            .category(request.getCategory())
            .thumbnailUrl("")
            .viewCount(0L)
            .commentCount(0L)
            .isPublic(request.isPublic())
            .publishedAt(
                request.getPublishDate() != null ? request.getPublishDate() : LocalDateTime.now())
            .isHot(request.isHot())
            .build();

        Article savedArticle = adminNewsRepository.save(article);

        // 1. Base64 이미지 추출
        List<Base64ImageData> base64Images = request.extractBase64Images();

        // 2. 이미지 파일로 저장 및 URL 설정
        for (Base64ImageData imageData : base64Images) {
            String imageUrl = fileStorageService.uploadBase64Image(imageData,
                StorageCategory.NEWS_IMAGE,
                savedArticle.getId());
            imageData.setNewImageUrl(displayUrl + imageUrl);

            // 나중에 Article 엔티티와 연결할 때 사용할 ArticleImage 객체 생성
            ArticleImage articleImage = ArticleImage.builder()
                .article(savedArticle)
                .imageUrl(displayUrl + imageUrl)
                .build();

            adminNewsImageRepository.save(articleImage);
        }

        // 3. HTML 내용의 Base64 이미지를 URL로 교체
        request.replaceBase64ImagesWithUrls(base64Images);

        // 4. 썸네일 처리
        String thumbnailUrl = request.getThumbnailUrl();
        if (request.getThumbnailFile() != null && !request.getThumbnailFile().isEmpty()) {
            thumbnailUrl = fileStorageService.upload(request.getThumbnailFile(),
                StorageCategory.NEWS_IMAGE,
                savedArticle.getId());
        }

        if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
            throw new APIException(ResponseStatus.ARTICLE_NOT_EXIST);
        }
        savedArticle.setThumbnailUrl(displayUrl + thumbnailUrl);
        savedArticle.setContent(request.getContent());

        // 7. 응답 반환
        return savedArticle.convertDTO();
    }

    @Override
    @Transactional
    public NewsArticleResponse updateNews(Long newsId, NewsArticleRequest request,
        AdminUserDetails user) {
        Admin admin = user.getAdmin();

        // 1. 기존 Article 조회
        Article article = adminNewsRepository.findById(newsId)
            .orElseThrow(() -> new APIException(ResponseStatus.ARTICLE_NOT_EXIST));

        // 2. 기본 정보 업데이트
        article.setTitle(request.getTitle());
        article.setSubTitle(request.getSubtitle());
        article.setCategory(request.getCategory());
        article.setPublic(request.isPublic());
        article.setHot(request.isHot());
        article.setContent(request.getContent());

        // 발행일 설정
        if (request.getPublishDate() != null) {
            article.setPublishedAt(request.getPublishDate());
        }

        // 3. Base64 이미지 추출
        List<Base64ImageData> base64Images = request.extractBase64Images();

        // 4. 이미지 파일로 저장 및 URL 설정
        for (Base64ImageData imageData : base64Images) {
            String imageUrl = fileStorageService.uploadBase64Image(imageData,
                StorageCategory.NEWS_IMAGE,
                article.getId());
            imageData.setNewImageUrl(displayUrl + imageUrl);

            // 새 ArticleImage 객체 생성 및 저장
            ArticleImage articleImage = ArticleImage.builder()
                .article(article)
                .imageUrl(displayUrl + imageUrl)
                .build();

            adminNewsImageRepository.save(articleImage);
        }

        // 5. HTML 내용의 Base64 이미지를 URL로 교체
        request.replaceBase64ImagesWithUrls(base64Images);
        article.setContent(request.getContent());

        // 6. 썸네일 처리
        if (request.getThumbnailFile() != null && !request.getThumbnailFile().isEmpty()) {
            // 새 썸네일 파일이 업로드된 경우 교체

            fileStorageService.delete(article.getThumbnailUrl().substring(displayUrl.length()));

            String thumbnailUrl = fileStorageService.upload(request.getThumbnailFile(),
                StorageCategory.NEWS_IMAGE,
                article.getId());

            article.setThumbnailUrl(displayUrl + thumbnailUrl);
        }

        return article.convertDTO();
    }
}
