package com.biomatch.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.biomatch.test.payload.FaceMatchResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AlgorithmControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void tesInfoAPI() throws Exception {
		List<FaceMatchResult> resultList = new ArrayList<FaceMatchResult>();
		this.mockMvc.perform(get("/v1/info").content(asJsonString(resultList)).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.algorithmName",is("Amazon Rekognition")))
		.andExpect(jsonPath("$.companyName",is("AWS")));
	}
	
	public String asJsonString(final Object details) {
        try {
            return new ObjectMapper().writeValueAsString(details);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
