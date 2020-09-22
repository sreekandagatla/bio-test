package com.biomatch.test.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedSourceImageFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.biomatch.test.payload.FaceMatchResult;
import com.biomatch.test.payload.Score;
@Service
public class BiometricServiceImpl  implements BiometricService{
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
				if(!y.getKey().equalsIgnoreCase(x.getKey())) {
					Score score = new Score();
					score.setMatched_face(y.getKey());		
					Float similarity = compareFace(bucketName, x.getKey(), y.getKey());
					score.setScore(similarity);
					scoreList.add(score);
				}
				
			});
			faceMatchResult.setScores(scoreList);
			faceMatchResultList.add(faceMatchResult);
		});
		return faceMatchResultList;
	}

	private Float compareFace(String bucketName, String sourceImage, String targetImage) {
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
