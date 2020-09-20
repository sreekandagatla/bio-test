package com.biomatch.test.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import com.biomatch.test.controller.utils.MessageExtractorUtils;
import com.biomatch.test.payload.PayloadWrapper;

@ControllerAdvice
public class ExceptionHandlerController {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@Autowired
	private MessageExtractorUtils exceptionMessageExtractorUtils;

	@ResponseBody
	@ResponseStatus(code = HttpStatus.OK)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public PayloadWrapper<Void> handleConstraintViolationException(DataIntegrityViolationException exp,
			HttpServletRequest req) {
		logger.error(String.format("Data Integrity Violation Exception Raised By Request [{}]!", req.getRequestURL()),
				exp);

		String errorMsg = exceptionMessageExtractorUtils.extractErrorMessage(exp);

		PayloadWrapper<Void> payLoad = new PayloadWrapper<>();
		payLoad.setHasError(true);
		payLoad.setMessage(errorMsg);

		return payLoad;
	}
	
	@ResponseBody
	@ResponseStatus(code = HttpStatus.OK)
	@ExceptionHandler({DataRetrievalFailureException.class, InvalidDataAccessApiUsageException.class})
	public PayloadWrapper<Void> handleDataAccessFailureException(DataAccessException exp,
			HttpServletRequest req) {
		logger.error(String.format("Data Access Violation Exception Raised By Request [{}]!", req.getRequestURL()),
				exp);

		String errorMsg = exp.getMessage();

		PayloadWrapper<Void> payLoad = new PayloadWrapper<>();
		payLoad.setHasError(true);
		payLoad.setMessage(errorMsg);

		return payLoad;
	}
	
	@ResponseBody
	@ResponseStatus(code = HttpStatus.OK)
	@ExceptionHandler(MultipartException.class)
	public PayloadWrapper<Void> handleMultipartException(HttpServletRequest req, MultipartException exp) {
		logger.error(String.format("Exception Raised By Request [%s]!", req.getRequestURL()), exp);

		PayloadWrapper<Void> payLoad = new PayloadWrapper<>();
		payLoad.setHasError(true);
		payLoad.setMessage("Failed to Upload Speicifed File!");

		return payLoad;
	}

	@ResponseBody
	@ResponseStatus(code = HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public PayloadWrapper<Void> handleError(HttpServletRequest req, Throwable exp) {
		// Nothing can be done. Log the message and just return the error page...
		logger.error(String.format("Exception Raised By Request [%s]!", req.getRequestURL()), exp);

		PayloadWrapper<Void> payLoad = new PayloadWrapper<>();
		payLoad.setHasError(true);
		payLoad.setMessage("Unexpected error occured!");

		return payLoad;
	}
}
