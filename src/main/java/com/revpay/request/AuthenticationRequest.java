package com.revpay.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
    private String token;
    private String username;
    private String password;
}
