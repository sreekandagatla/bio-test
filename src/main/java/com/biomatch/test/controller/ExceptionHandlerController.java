package com.biomatch.test.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.biomatch.test.payload.ErrorResponse;

@ControllerAdvice
public class ExceptionHandlerController {
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public ErrorResponse  handleError(HttpServletResponse response) throws IOException {
		return new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected internal server error occured");
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ErrorResponse  handleBadRequest(HttpServletResponse response) throws IOException {
		return new ErrorResponse("BAD_REQUEST", "Bad Request");
	}
	
	
	
	
}
