package com.biomatch.test.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.biomatch.test.payload.FaceMatchResult;

/**
 * interface for compare faces API
 * @author KandagS1
 *
 */
public interface BiometricService {
	 List<FaceMatchResult> compareFaces(String bucketName);

}
