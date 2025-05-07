package com.tomato.naraclub.admin.original.dto;

import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleRequest {

    @NotBlank(message = "제목은 필수 입력 항목입니다")
    private String title;

    private String subtitle;

    @NotBlank(message = "내용은 필수 입력 항목입니다")
    private String content; // HTML 형식의 전체 내용

    @NotNull(message = "카테고리는 필수 선택 항목입니다")
    private OriginalCategory category;

    @NotNull(message = "기사 타입은 필수 선택 항목입니다")
    private OriginalType type;

    private MultipartFile thumbnailFile;

    private String thumbnailUrl;

    private String tags;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishDate;

    private boolean isPublic;

    private boolean isHot;

    private String externalId;

    // 필요한 경우 추가 메서드
    public boolean hasThumbnail() {
        return thumbnailFile != null && !thumbnailFile.isEmpty() ||
            thumbnailUrl != null && !thumbnailUrl.isBlank();
    }

    /**
     * 발행일이 null인 경우 현재 시간 반환
     */
    public LocalDateTime getPublishDateOrNow() {
        return publishDate != null ? publishDate : LocalDateTime.now();
    }
    // Base64 이미지 추출 로직
    public List<Base64ImageData> extractBase64Images() {
        List<Base64ImageData> images = new ArrayList<>();

        // img 태그 찾기 + src가 data:image로 시작하는 패턴
        Pattern pattern = Pattern.compile("<img[^>]*?src=[\"']data:image/(.*?);base64,([^\"']*?)[\"'][^>]*?>");
        Matcher matcher = pattern.matcher(content);

        int index = 0;
        while (matcher.find()) {
            String imgTag = matcher.group(0);       // 전체 img 태그
            String imageType = matcher.group(1);    // 이미지 타입 (jpeg, png 등)
            String base64Data = matcher.group(2);   // 실제 Base64 데이터

            // 이미지 태그에서 추가 속성 추출 (선택 사항)
            String className = extractAttribute(imgTag, "class");
            String style = extractAttribute(imgTag, "style");
            String alt = extractAttribute(imgTag, "alt");

            Base64ImageData imageData = new Base64ImageData();
            imageData.setOriginalTag(imgTag);
            imageData.setImageType(imageType);
            imageData.setBase64Data(base64Data);
            imageData.setClassName(className);
            imageData.setStyle(style);
            imageData.setAlt(alt);
            imageData.setIndex(index++);

            images.add(imageData);
        }

        return images;
    }

    // HTML 속성 추출 헬퍼 메서드
    private String extractAttribute(String htmlTag, String attributeName) {
        Pattern pattern = Pattern.compile(attributeName + "=[\"'](.*?)[\"']");
        Matcher matcher = pattern.matcher(htmlTag);
        return matcher.find() ? matcher.group(1) : "";
    }

    // Base64 이미지를 실제 URL로 대체
    public void replaceBase64ImagesWithUrls(List<Base64ImageData> images) {
        for (Base64ImageData image : images) {
            if (image.getNewImageUrl() != null) {
                content = content.replace(
                    image.getOriginalTag(),
                    "<img src=\"" + image.getNewImageUrl() + "\"" +
                        (image.getClassName().isEmpty() ? ""
                            : " class=\"" + image.getClassName() + "\"") +
                        (image.getStyle().isEmpty() ? "" : " style=\"" + image.getStyle() + "\"") +
                        (image.getAlt().isEmpty() ? "" : " alt=\"" + image.getAlt() + "\"") +
                        ">"
                );
            }
        }
    }
}