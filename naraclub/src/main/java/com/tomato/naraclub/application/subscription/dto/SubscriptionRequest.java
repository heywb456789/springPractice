package com.tomato.naraclub.application.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * $mid       = $this->security->xss_clean($this->input->post('mid'));        //고객사 mid
 * $license   = $this->security->xss_clean($this->input->post('license'));    //고객사 라이센스
 * $userid    = $this->security->xss_clean($this->input->post('userid'));     //결제고객사 유저ID
 * $prodname  = $this->security->xss_clean($this->input->post('prodname'));   //결제상품명
 * $phone     = $this->security->xss_clean($this->input->post('phone'));      //결제고객사 유저 휴대폰
 * $username  = $this->security->xss_clean($this->input->post('username'));   //결제고객사 유저명
 * $prodprice = $this->security->xss_clean($this->input->post('prodprice'));  //상품금액
 * $returnurl = $this->security->xss_clean($this->input->post('returnurl'));  //리턴URL
 * $seq       = $this->security->xss_clean($this->input->post('seq'));        //리턴Seq
 * $subDate   = $this->security->xss_clean($this->input->post('subDate'));    // 구독 결제 시작일
 * paymentLimit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequest {
    private String name;
    private String phoneNumber; // 결제 고객사 유저 휴대폰

    private String userid;
    private String seq;
    private String tid;
    private String point;
}
