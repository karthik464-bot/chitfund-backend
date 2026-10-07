package com.chitfund.payload.request;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}