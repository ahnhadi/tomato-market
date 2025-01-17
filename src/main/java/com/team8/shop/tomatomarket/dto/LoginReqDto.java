package com.team8.shop.tomatomarket.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqDto {
    @NonNull
    private String username;
    @NonNull
    private String password;
}
