package com.tomato.naraclub.application.share.service;

import com.tomato.naraclub.application.share.dto.KakaoShareResponse;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.share.service
 * @fileName : ShareService
 * @date : 2025-05-11
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface ShareService {

    void saveShareHistory(KakaoShareResponse payload);
}
