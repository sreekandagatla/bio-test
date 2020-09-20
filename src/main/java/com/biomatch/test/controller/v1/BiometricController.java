package com.biomatch.test.controller.v1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
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
import com.amazonaws.services.rekognition.model.FaceRecord;
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
import com.biomatch.test.controller.utils.MessageExtractorUtils;
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
	private MessageExtractorUtils messageExtractorUtils;

	@ApiOperation(value = "Generate a template from the provided biometric image", notes="This endpoint accepts a base64 encoded PNG and attempts"
			+ " to perform a 'feature extraction' operation producing a single template")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Server error"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 200, message = "Successful Response") })
	@PostMapping(value="/create-template",produces = "application/json", consumes = "application/json")
	public String createTemplate(@RequestBody @Valid com.biomatch.test.payload.Image image, BindingResult result) {
		String imageEncodedString = image.imageData;

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
	public String  compareList() {
		String collectionId = "TestImagesCollection";
		String bucketName = "100cloud100";


		//cleanExistingCollection(collectionId);
		//createCollection(collectionId);

		//addFacesToCollection(collectionId,bucketName);
		searchFaces(collectionId,bucketName);

		return null;
	}
	/*
	 * @GetMapping(value = "/")
	 * 
	 * @CrossOrigin(origins = "*") public void redirect(HttpServletResponse
	 * response) throws IOException { response.sendRedirect("/swagger-ui.html"); }
	 */

	void createCollection(String collectionId) {
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();



		System.out.println("Creating collection: " +
				collectionId );

		CreateCollectionRequest request = new CreateCollectionRequest()
				.withCollectionId(collectionId);

		CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(request); 
		System.out.println("CollectionArn : " +
				createCollectionResult.getCollectionArn());
		System.out.println("Status code : " +
				createCollectionResult.getStatusCode().toString());
	}

	void addFacesToCollection(String collectionId, String bucketName) {
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		ListObjectsV2Result result = s3.listObjectsV2(bucketName);
		List<S3ObjectSummary> s3objectsummaryList = result.getObjectSummaries();
		s3objectsummaryList.forEach(x -> indexFaces(collectionId, bucketName, x.getKey()));
	}

	void indexFaces(String collectionId, String bucketName, String object) {


		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		Image image = new Image()
				.withS3Object(new S3Object()
						.withBucket(bucketName)
						.withName(object));


		IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
				.withImage(image)
				.withQualityFilter(QualityFilter.AUTO)
				.withMaxFaces(1)
				.withCollectionId(collectionId)
				.withExternalImageId(object)
				.withDetectionAttributes("DEFAULT");

		IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);


		System.out.println("Results for " + object);
		System.out.println("Faces indexed:"); 
		List<FaceRecord> faceRecords =		  indexFacesResult.getFaceRecords(); 
		for (FaceRecord faceRecord : faceRecords)
		{ System.out.println("  Face ID: " + faceRecord.getFace().getFaceId());
		System.out.println("  Location:" +
				faceRecord.getFaceDetail().getBoundingBox().toString()); }



	}

	void cleanExistingCollection(String collectionId) {
		boolean exists = false;
		AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder.defaultClient();


		System.out.println("Listing collections");
		int limit = 10;
		ListCollectionsResult listCollectionsResult = null;
		String paginationToken = null;
		do {
			if (listCollectionsResult != null) {
				paginationToken = listCollectionsResult.getNextToken();
			}
			ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest()
					.withMaxResults(limit)
					.withNextToken(paginationToken);
			listCollectionsResult=amazonRekognition.listCollections(listCollectionsRequest);

			List < String > collectionIds = listCollectionsResult.getCollectionIds();
			for (String resultId: collectionIds) {
				System.out.println(resultId);
				if(resultId.equalsIgnoreCase(collectionId)) {
					exists = true;
					break;
				}

			}
		} while (listCollectionsResult != null && listCollectionsResult.getNextToken() !=
				null);
		if(exists) {
			DeleteCollectionRequest request = new DeleteCollectionRequest()
					.withCollectionId(collectionId);
			DeleteCollectionResult deleteCollectionResult = amazonRekognition.deleteCollection(request);     
		}
	}

	void searchFaces(String collectionId, String bucketName) {
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		ListObjectsV2Result result = s3.listObjectsV2(bucketName);
		List<S3ObjectSummary> s3objectsummaryList = result.getObjectSummaries();
		s3objectsummaryList.forEach(x -> {
			cleanExistingCollection(collectionId);
			createCollection(collectionId);

			addFacesToCollection(collectionId,bucketName);
			deleteFaceFromCollection(collectionId,x.getKey());
			searchFaces(collectionId, bucketName, x.getKey());

		});



	}
	void searchFaces(String collectionId, String bucketName, String object) {
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
		ObjectMapper objectMapper = new ObjectMapper();

		// Get an image object from S3 bucket.
		Image image=new Image()
				.withS3Object(new S3Object()
						.withBucket(bucketName)
						.withName(object));

		// Search collection for faces similar to the largest face in the image.
		SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
				.withCollectionId(collectionId)
				.withImage(image)
				.withFaceMatchThreshold(70F)
				.withMaxFaces(1);

		SearchFacesByImageResult searchFacesByImageResult = 
				rekognitionClient.searchFacesByImage(searchFacesByImageRequest);

		System.out.println("Faces matching largest face in image from" + object);
		List < FaceMatch > faceImageMatches = searchFacesByImageResult.getFaceMatches();
		for (FaceMatch face: faceImageMatches) {
			try {
				System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(face));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
		}
	}

	void deleteFaceFromCollection(String collectionId, String object) {
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();


		DeleteFacesRequest deleteFacesRequest = new DeleteFacesRequest()
				.withCollectionId(collectionId)
				.withFaceIds(listFaces(collectionId, object));


		DeleteFacesResult deleteFacesResult=rekognitionClient.deleteFaces(deleteFacesRequest);


		List < String > faceRecords = deleteFacesResult.getDeletedFaces();
		System.out.println(Integer.toString(faceRecords.size()) + " face(s) deleted:");
		for (String face: faceRecords) {
			System.out.println("FaceID: " + face);
		}

	}

	String listFaces(String collectionId, String object) {
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		ObjectMapper objectMapper = new ObjectMapper();

		ListFacesResult listFacesResult = null;
		System.out.println("Faces in collection " + collectionId);

		String paginationToken = null;
		do {
			if (listFacesResult != null) {
				paginationToken = listFacesResult.getNextToken();
			}

			ListFacesRequest listFacesRequest = new ListFacesRequest()
					.withCollectionId(collectionId)
					.withMaxResults(1)
					.withNextToken(paginationToken);

			listFacesResult =  rekognitionClient.listFaces(listFacesRequest);
			List < Face > faces = listFacesResult.getFaces();
			for (Face face: faces) {
				try {
					System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
							.writeValueAsString(face));
					if(face.getExternalImageId().equalsIgnoreCase(object)) {
						return face.getFaceId();
					}
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (listFacesResult != null && listFacesResult.getNextToken() !=
				null);
		return null;
	}



}
