package com.biomatch.test.payload;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class FaceMatchResult implements Serializable
{

	public String reference_face;
	public List<Score> scores = null;
	private final static long serialVersionUID = -1056788236966969984L;

}