package com.team8.shop.tomatomarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupReqDto {
    private String username;
    private String nickname;
    private String password;
    private String adminKey;
}
