package com.biomatch.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.biomatch.test.payload.FaceMatchResult;
import com.biomatch.test.service.BiometricService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BiometricControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void tesCompareListAPI() throws Exception {
		List<FaceMatchResult> resultList = new ArrayList<FaceMatchResult>();
		this.mockMvc.perform(get("/v1/compare-list").content(asJsonString(resultList)).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());                
	}
	
	@Test
	public void tesCompareListAPI_JsonProcessing() throws Exception {
		List<FaceMatchResult> resultList = new ArrayList<FaceMatchResult>();
		FaceMatchResult result = new FaceMatchResult();
		result.setReference_face("<test");
		resultList.add(result);
		this.mockMvc.perform(get("/v1/compare-list").content(asJsonString(resultList)).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());                
	}
	
	@Test
	public void tesCompareListAPI_Exception() throws Exception {
		List<FaceMatchResult> resultList = null;
		this.mockMvc.perform(get("/v1/compare-list").content(asJsonString(resultList)).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());                
	}
	public String asJsonString(final Object details) {
        try {
            return new ObjectMapper().writeValueAsString(details);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
