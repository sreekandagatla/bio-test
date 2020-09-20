package com.biomatch.test.payload;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class AggsDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4488615344212295060L;
	
	private Map<String, Integer> color_space;
	private Map<Integer, Integer> mark_types;
	private Map<Integer, Integer> design_codes;
	private Map<String, Integer> colors;
	
}
