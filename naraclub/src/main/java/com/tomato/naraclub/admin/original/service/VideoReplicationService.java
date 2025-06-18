package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.application.original.entity.Video;
import jakarta.validation.constraints.NotNull;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.service
 * @fileName : VideoReplicationService
 * @date : 2025-05-27
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VideoReplicationService {

    void replicateToOtherSchema(Video video);

    void updateToOtherSchema(Video video);

    void softDeleteFromOtherSchema(Long id);

    void updateIsPublicInOtherSchema(Video video);
}
