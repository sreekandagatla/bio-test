package com.biomatch.test.controller.v1;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provides REST API's for Algorithm specific operations.
 * @author KandagS1
 *
 */
@RestController
@Api( tags = { "Algorithm Information" })
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class AlgorithmController {

	@ApiOperation(value = "Returns basic information for the algorithm.", notes="This endpoint returns some basic information about the algorithm.")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Server error"),
			@ApiResponse(code = 200, message = "Successful Response") })
	@GetMapping("/info")
	public String getInfo() {
		return null;
	}
}