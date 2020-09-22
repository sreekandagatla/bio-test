package com.biomatch.test.controller.v1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.FaceDetail;
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
	
	
	@ApiOperation(value = "Generate a template from the provided biometric image", notes="This endpoint accepts a base64 encoded PNG and attempts"
			+ " to perform a 'feature extraction' operation producing a single template")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Server error"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 200, message = "Successful Response") })
	@PostMapping(value="/create-template",produces = "application/json", consumes = "application/json")
	public String createTemplate() {
		//public String createTemplate(@RequestBody  @Valid com.biomatch.test.payload.Image image, BindingResult result) {
		//String imageEncodedString = image.imageData;
		String imageEncodedString = "test";

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
		com.amazonaws.services.rekognition.model.Image modelImage = new com.amazonaws.services.rekognition.model.Image();
		//byte[] decodedBytes = Base64.getDecoder().decode(imageEncodedString);
		byte[] decodedBytes = Base64.getDecoder().decode(imageEncodedString);
		byte[] newBytes = null;
		String str = "C:\\general\\saic\\dhs\\example_imgs\\mytest2.png";
		try {
			new FileOutputStream(str).write(decodedBytes);
			newBytes = Files.readAllBytes(Paths.get("C:\\general\\saic\\dhs\\example_imgs\\example_imgs\\S309-04-t10_01.png"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		/*
		 * String str = "C:\\general\\saic\\dhs\\example_imgs\\mytest1.png"; try { new
		 * FileOutputStream(str).write(decodedBytes); } catch (FileNotFoundException e1)
		 * { // TODO Auto-generated catch block e1.printStackTrace(); } catch
		 * (IOException e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
		 */

		System.out.println("encodedString before3");


		//imageBytes = ByteBuffer.wrap(decodedBytes);

		modelImage.setBytes(ByteBuffer.wrap(newBytes));
		DetectFacesRequest detectFacesRequest = new DetectFacesRequest();
		detectFacesRequest.setImage(modelImage);
		//detectFacesRequest.
		DetectFacesResult detectFacesResult = rekognitionClient.detectFaces(detectFacesRequest);
		List<FaceDetail> faceDetails = detectFacesResult.getFaceDetails();
		for (FaceDetail face: faceDetails) {
			/*
			 * if (request.getAttributes().contains("ALL")) { AgeRange ageRange =
			 * face.getAgeRange();
			 * System.out.println("The detected face is estimated to be between " +
			 * ageRange.getLow().toString() + " and " + ageRange.getHigh().toString() +
			 * " years old."); System.out.println("Here's the complete set of attributes:");
			 * } else { // non-default attributes have null values.
			 * System.out.println("Here's the default set of attributes:"); }
			 */

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}
	@ApiOperation(value = "Compare a single template to a list of templates", notes="This endpoint accepts a template and a list of templates. "
			+ "It compares the single template to every template in the provided list. The result is a list of Comparison objects that holds a similarity score for each comparison. <br><br> The returned comparison list MUST contain the same number of elements AND be in the same order as the provided list of templates.")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Server error"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 200, message = "Successful Response") })	
	@PostMapping("/compare-list")
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
