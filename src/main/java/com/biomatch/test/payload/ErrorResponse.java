package com.biomatch.test.payload;

import lombok.Data;

@Data
public class ErrorResponse {
	private final String code;
    private final String message;
}
