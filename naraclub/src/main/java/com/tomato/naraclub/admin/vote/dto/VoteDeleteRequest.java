package com.tomato.naraclub.admin.vote.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.dto
 * @fileName : VoteDeleteRequest
 * @date : 2025-05-09
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
public class VoteDeleteRequest {

    List<Long> ids;

}
