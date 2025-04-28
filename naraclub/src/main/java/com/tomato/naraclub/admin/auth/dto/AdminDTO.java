package com.tomato.naraclub.admin.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.auth.dto
 * @fileName : AdminDTO
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {
    private Long id;
    private LocalDateTime createdAt;
    private String password;
    private String phoneNumber;
    private String role;
    private String email;
    private String name;
    private LocalDateTime lastAccessAt;
}
