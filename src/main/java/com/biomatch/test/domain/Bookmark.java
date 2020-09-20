package com.biomatch.test.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "BOOKMARK")
public class Bookmark {
	public static final String GLOBAL_USER = "GLOBAL";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BOOKMARK_ID")
	private Long id;

	@Column(name = "BOOKMARK_USER", nullable = false, length = 30, columnDefinition = "Varchar(30) NULL DEFAULT 'GLOBAL'")
	private String user;

	@Column(name = "BOOKMARK_CONTEXT", nullable = false, length = 4000)
	private String context;

	@Column(name = "BOOKMARK_SERIAL_NUMBER", length = 80)
	private String serialNumber;

	@Column(name = "BOOKMARK_ITEM", length = 16000)
	private String item;

	@Column(name = "BOOKMARK_NOTE", length = 255)
	private String note;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BOOKMARK_CREATED_DATE", insertable = false)
	private Date createdDate;
}
