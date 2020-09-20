package com.biomatch.test.payload;

import java.io.Serializable;

import lombok.Data;

@Data
public class Image implements Serializable
{

	public String imageData;
	private final static long serialVersionUID = 445294844413659260L;

}