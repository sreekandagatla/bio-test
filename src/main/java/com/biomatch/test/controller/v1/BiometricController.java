package com.biomatch.test.controller.v1;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biomatch.test.payload.FaceMatchResult;
import com.biomatch.test.service.BiometricService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * This class provides REST API's for Biometric operations.
 * @author KandagS1
 *
 */
@RestController
@Api( tags = { "Biometric Operations" })
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class BiometricController {
	@Autowired
	BiometricService service;
	
	/**
	 * compare list api
	 * @return
	 */
	@ApiOperation(value = "Compares each image in a s3 bucket to every other image", notes="This endpoint  compares each image in a s3 bucket to every other image\r\n" + 
			"in the bucket. The result is a list of Comparison objects that holds  similarity score, normalized score for each comparison. <br><br> The returned comparison list MUST contain the same number of elements AND be in the same order as the provided list of templates.")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Server error"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 200, message = "Successful Response") })	
	@GetMapping("/compare-list")
	public String compareList() {
		String bucketName = "100cloud100";
		List<FaceMatchResult> faceMatchResultList = service.compareFaces(bucketName);
		ObjectMapper objectMapper = new ObjectMapper();
		String result =  null;
		
		try {
			result = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(faceMatchResultList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	
	



}
