package com.biomatch.test.payload;

import java.io.Serializable;

import lombok.Data;

@Data
public class BookmarkContextDto implements Serializable {
	private static final long serialVersionUID = -6635500285971865342L;

	private String user;

	private String[] contexts;
}
