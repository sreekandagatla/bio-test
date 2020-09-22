package com.biomatch.test.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.biomatch.test.controller.v1.BiometricController;
import com.biomatch.test.payload.FaceMatchResult;
import com.biomatch.test.payload.Score;
@Service
public class BiometricServiceImpl  implements BiometricService{
	private static Logger logger = LoggerFactory.getLogger(BiometricServiceImpl.class);
	
	public List<FaceMatchResult> compareFaces(String bucketName) {	
		logger.debug(" face mactch starts for bucketName="+bucketName);

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
			calculateNormalizedScore(scoreList);			

			faceMatchResult.setScores(scoreList);

			faceMatchResultList.add(faceMatchResult);
		});
		logger.debug(" face mactch ends for bucketName="+bucketName);
		return faceMatchResultList;
	}

	private void calculateNormalizedScore(List<Score> scoreList) {
		logger.debug(" Normalization starts");
		List<Float> dataPoints = new ArrayList<Float>();
		scoreList.forEach(z-> dataPoints.add(z.getScore()));
		Collections.sort(dataPoints);
		float min = dataPoints.get(0);
		float max = dataPoints.get(dataPoints.size()-1);

		for (int i=0; i<scoreList.size(); i++) {
			Score scoreObject = scoreList.get(i);
			Float similarity = scoreObject.getScore();
			float normalizedScore = (similarity-min)/(max-min);
			scoreObject.setNormalizedScore(normalizedScore);				
		}
		logger.debug(" Normalization ends");
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
	}


}
