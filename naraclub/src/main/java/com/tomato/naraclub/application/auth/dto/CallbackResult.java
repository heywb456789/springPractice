package com.tomato.naraclub.application.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.dto
 * @fileName : CallbackResult
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallbackResult {
    @JsonProperty("resultcode")
    private String resultcode;

    @JsonProperty("requestno")
    private String requestno;

    @JsonProperty("enctime")
    private String enctime;

    @JsonProperty("sitecode")
    private String sitecode;

    @JsonProperty("responseno")
    private String responseno;

    @JsonProperty("authtype")
    private String authtype;

    @JsonProperty("name")
    private String name;

    @JsonProperty("utf8_name")
    private String utf8Name;

    @JsonProperty("birthdate")
    private String birthdate;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("nationalinfo")
    private String nationalinfo;

    @JsonProperty("mobileco")
    private String mobileco;

    @JsonProperty("mobileno")
    private String mobileno;

    @JsonProperty("ci")
    private String ci;

    @JsonProperty("di")
    private String di;

    @JsonProperty("businessno")
    private String businessno;

    @JsonProperty("receivedata")
    private String receivedata;
}
