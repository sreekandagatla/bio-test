package com.biomatch.test.payload;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class BookmarkDto implements Serializable {
	private static final long serialVersionUID = 4742000918453832674L;

	private Long id;

	@NotNull
	@Size(min = 3, max = 30)
	private String user;

	@NotNull
	@Size(min = 1, max = 4000)
	private String context;

	@Size(min = 1, max = 80)
	private String serialNumber;

	@Size(min = 3, max = 16000)
	private String item;

	@Size(max = 255)
	private String note;
}
