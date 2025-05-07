package com.tomato.naraclub.admin.original.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Base64ImageData {

    private String originalTag;   // 원본 img 태그 전체
    private String imageType;     // 이미지 타입 (jpeg, png 등)
    private String base64Data;    // Base64 인코딩된 데이터
    private String className;     // 클래스 속성
    private String style;         // 스타일 속성
    private String alt;           // 대체 텍스트
    private int index;            // 순서
    private String newImageUrl;   // 저장 후 새 이미지 URL

    // Base64 데이터를 바이트 배열로 변환
    public byte[] decodeBase64() {
        return java.util.Base64.getDecoder().decode(base64Data);
    }


}