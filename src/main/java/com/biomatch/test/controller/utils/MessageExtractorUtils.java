package com.biomatch.test.controller.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.biomatch.test.payload.PayloadWrapper;

@Component
public class MessageExtractorUtils {
	private static String UKV_REGEX = "^Unique index or primary key violation.*ON\\s+\\w+.(\\w+\\(.+?\\)).*$";
	private Pattern ukvRegexPattern = null;

	private Map<String, String> tableFieldUniqueKeyToMessageMap = new HashMap<>();

	@PostConstruct
	public void initUtils() {
		ukvRegexPattern = Pattern.compile(UKV_REGEX, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

		tableFieldUniqueKeyToMessageMap.put("TEAM(TEAM_NAME)", "Duplicate TeamName Found!");
		tableFieldUniqueKeyToMessageMap.put("TEAM_MEMBER(TEAM_ID, MEMBER_EMAIL_ID)", "Duplicate Team Member Found!");
	}

	public String extractErrorMessage(DataIntegrityViolationException exp) {
		Throwable cause = exp.getMostSpecificCause();
		if (cause == null) {
			cause = exp;
		}

		String causeMessage = cause.getMessage();

		Matcher matcher = ukvRegexPattern.matcher(causeMessage);
		if (!matcher.matches() || matcher.groupCount() < 1) {
			return causeMessage;
		}

		String tableFieldExpression = matcher.group(1);
		if (tableFieldUniqueKeyToMessageMap.containsKey(tableFieldExpression)) {
			return tableFieldUniqueKeyToMessageMap.get(tableFieldExpression);
		}

		return causeMessage;
	}

	public <T extends Serializable> PayloadWrapper<T> extractValidationErrors(T payload, BindingResult result) {
		PayloadWrapper<T> payloadWrapper = new PayloadWrapper<>(payload);

		payloadWrapper.setHasError(true);

		payloadWrapper.setMessage("One or more errors found!");

		Map<String, String> errorMap = new HashMap<>();
		result.getAllErrors().forEach(err -> {
			if (err instanceof FieldError) {
				FieldError fldErr = (FieldError) err;
				errorMap.put(String.format("%s", fldErr.getField()), fldErr.getDefaultMessage());
			} else {
				if (!errorMap.containsKey(err.getObjectName())) {
					errorMap.put(err.getObjectName(), err.getDefaultMessage());
				} else {
					String errors = errorMap.get(err.getObjectName()) + ",\n" + err.getDefaultMessage();
					errorMap.put(err.getObjectName(), errors);
				}
			}
		});

		payloadWrapper.setErrorMap(errorMap);

		return payloadWrapper;
	}

}
