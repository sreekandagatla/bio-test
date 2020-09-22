package com.biomatch.test.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.biomatch.test.payload.FaceMatchResult;
import com.biomatch.test.service.BiometricServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class BiometricServiceImplTest {
	@Test
	public void testCompareFaces() {
		String bucketName = "test";
		String image1 = "FirstImage.png";
		String image2 = "SecondImage.png";
		S3ObjectSummary s3Object1 = new S3ObjectSummary();
		s3Object1.setBucketName(bucketName);
		s3Object1.setKey(image1);

		S3ObjectSummary s3Object2 = new S3ObjectSummary();
		s3Object2.setBucketName(bucketName);
		s3Object2.setKey(image2);

		List<S3ObjectSummary> summaryList = new ArrayList<S3ObjectSummary>();
		summaryList.add(s3Object1);
		summaryList.add(s3Object2);
		final BiometricServiceImpl service = spy(new BiometricServiceImpl());
		Mockito.doReturn(summaryList).when(service).getS3Objects(bucketName);
		Mockito.doReturn(99.99944f).when(service).compareFace(bucketName, image1, image2);
		Mockito.doReturn(99.99971f).when(service).compareFace(bucketName, image2, image1);
		List<FaceMatchResult> result = service.compareFaces(bucketName);
		assertEquals("Invalid Response!", 2, result.size());
		assertEquals("Invalid Response!", image1, result.get(0).getReference_face());
		assertEquals("Invalid Response!", image2, result.get(1).getReference_face());
		verify(service).getS3Objects(bucketName);
		verify(service).compareFace(bucketName, image1, image2);
		verify(service).compareFace(bucketName, image2, image1);
	}
	
	/*@Test
	public void testNullCompareFaces() {
		String bucketName = "test";		
		final BiometricServiceImpl service = spy(new BiometricServiceImpl());
		Mockito.doReturn(null).when(service).getS3Objects(bucketName);
		List<FaceMatchResult> result = service.compareFaces(bucketName);
		assertEquals("Invalid Response!", 0, result.size());
		verify(service).getS3Objects(bucketName);
		
	}*/

}
