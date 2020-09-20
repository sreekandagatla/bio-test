Drop Table If Exists BOOKMARK;

Create Table BOOKMARK
(
	BOOKMARK_ID Integer GENERATED BY DEFAULT AS IDENTITY,
	BOOKMARK_USER Varchar(30) NULL DEFAULT 'GLOBAL',
	BOOKMARK_CONTEXT Varchar(4000) NOT NULL,
	BOOKMARK_SERIAL_NUMBER Varchar(80) NULL,
	BOOKMARK_ITEM Varchar(16000) NULL,
	BOOKMARK_NOTE Varchar(255) NULL,
	BOOKMARK_CREATED_DATE Timestamp WITH Time Zone DEFAULT Current_Timestamp,
		PRIMARY KEY (BOOKMARK_ID),
		UNIQUE (BOOKMARK_USER, BOOKMARK_CONTEXT, BOOKMARK_SERIAL_NUMBER, BOOKMARK_ITEM)
);

Create Index BOOKMARK_USER_AND_CTX_NDX
	On BOOKMARK(BOOKMARK_USER, BOOKMARK_CONTEXT);