package com.biomatch.test.payload;

import java.io.Serializable;

import lombok.Data;
@Data
public class Score implements Serializable
{

	public String matched_face;
	public Float score;
	public Float normalizedScore;
	private final static long serialVersionUID = -3009962481467314107L;

}