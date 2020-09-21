package com.biomatch.test.controller.v1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.ComparedSourceImageFace;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteFacesRequest;
import com.amazonaws.services.rekognition.model.DeleteFacesResult;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.amazonaws.services.rekognition.model.ListFacesRequest;
import com.amazonaws.services.rekognition.model.ListFacesResult;
import com.amazonaws.services.rekognition.model.QualityFilter;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.biomatch.test.payload.FaceMatchResult;
import com.biomatch.test.payload.Score;
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
		String collectionId = "TestImagesCollection";
		String bucketName = "100cloud100";


		//cleanExistingCollection(collectionId);
		//createCollection(collectionId);

		//addFacesToCollection(collectionId,bucketName);
		List<FaceMatchResult> faceMatchResultList = compareFaces(bucketName);
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
	
	public List<FaceMatchResult> compareFaces(String bucketName) {	
		
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		ListObjectsV2Result result = s3.listObjectsV2(bucketName);
		List<S3ObjectSummary> s3objectsummaryList = result.getObjectSummaries();
		List<FaceMatchResult> faceMatchResultList = new ArrayList<FaceMatchResult>();
		s3objectsummaryList.forEach(x -> {
			FaceMatchResult faceMatchResult = new FaceMatchResult();
			List<Score> scoreList = new ArrayList<Score>();
			faceMatchResult.setReference_face(x.getKey());	
			
			s3objectsummaryList.forEach(y -> {
				Score score = new Score();
				score.setMatched_face(y.getKey());		
				Float similarity = compareTest(bucketName, x.getKey(), y.getKey());
				score.setScore(similarity);
				scoreList.add(score);
			});
			faceMatchResult.setScores(scoreList);
			faceMatchResultList.add(faceMatchResult);
		});
		return faceMatchResultList;
	}
	
	public Float compareTest(String bucketName, String sourceImage, String targetImage) {
		AmazonRekognition client = AmazonRekognitionClientBuilder.standard().build();
		CompareFacesRequest request = new CompareFacesRequest()
		        .withSourceImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(sourceImage)))
		        .withTargetImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(targetImage))).withSimilarityThreshold(0f);
		CompareFacesResult response = client.compareFaces(request);
		ComparedSourceImageFace y = response.getSourceImageFace();
		List<CompareFacesMatch> matchList = response.getFaceMatches();
		Float similarity = 0f;
		if(!matchList.isEmpty() && matchList.size()>0) {
		CompareFacesMatch faceMatch = matchList.get(0);
		similarity = faceMatch.getSimilarity();
		}
		return similarity;
		
		/*
		 * ComparedFace x = faceMatch.getFace(); x.getConfidence();
		 */
	}
	



}
