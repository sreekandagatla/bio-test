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
import com.biomatch.test.payload.FaceMatchResult;
import com.biomatch.test.payload.Score;
/**
 * @author KandagS1
 * This is implementation class for face compare 
 *
 */
@Service
public class BiometricServiceImpl  implements BiometricService{
	private static Logger logger = LoggerFactory.getLogger(BiometricServiceImpl.class);
	final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	AmazonRekognition client = AmazonRekognitionClientBuilder.standard().build();
	

	public List<FaceMatchResult> compareFaces(String bucketName) {	
		logger.debug(" face mactch starts for bucketName="+bucketName);

		List<S3ObjectSummary> s3objectsummaryList = getS3Objects(bucketName);
		List<FaceMatchResult> faceMatchResultList = new ArrayList<FaceMatchResult>();
		if(s3objectsummaryList != null && s3objectsummaryList.size()>0) {
			s3objectsummaryList.forEach(x -> {
				FaceMatchResult faceMatchResult = new FaceMatchResult();
				List<Score> scoreList = new ArrayList<Score>();
				faceMatchResult.setReference_face(x.getKey());	

				s3objectsummaryList.forEach(y -> {
					if(!y.getKey().equalsIgnoreCase(x.getKey())) {
						Score score = new Score();
						score.setCompared_face(y.getKey());		
						Float similarity = compareFace(bucketName, x.getKey(), y.getKey());
						score.setScore(similarity);
						scoreList.add(score);
					}

				});
				calculateNormalizedScore(scoreList);			

				faceMatchResult.setScores(scoreList);

				faceMatchResultList.add(faceMatchResult);
			});
		}
		logger.debug(" face mactch ends for bucketName="+bucketName);
		return faceMatchResultList;
	}

	/**
	 * Returns list of  the summary of  objects stored in an Amazon S3 bucket
	 * @param bucketName
	 * @return
	 */
	public List<S3ObjectSummary> getS3Objects(String bucketName) {
		ListObjectsV2Result result = s3.listObjectsV2(bucketName);
		List<S3ObjectSummary> s3objectsummaryList = result.getObjectSummaries();
		return s3objectsummaryList;
	}

	/**
	 * calculates nomralized score based on max-min normalization
	 * @param scoreList
	 */
	public  void calculateNormalizedScore(List<Score> scoreList) {
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

	/**
	 * compares faces with source image with target image from s3 bucket
	 * @param bucketName
	 * @param sourceImage
	 * @param targetImage
	 * @return
	 */
	public Float compareFace(String bucketName, String sourceImage, String targetImage) {		
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
