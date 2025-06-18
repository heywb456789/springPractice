package com.tomato.naraclub.application.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtcoResponse {

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Msg")
    private String msg;
}
