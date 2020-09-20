package com.biomatch.test.service;

import java.util.Set;

import com.biomatch.test.payload.BookmarkContextDto;
import com.biomatch.test.payload.BookmarkDto;

public interface BookmarkService {
	
	BookmarkDto addBookmark(BookmarkDto aBookmarkDto);
	
	void deleteBookmark(BookmarkDto aBookmarkDto);
	
	void deleteBookmark(long aBookmarId);
	
	Set<BookmarkDto> getBookmarks(String aUser, String aContext);

	BookmarkDto getBookmark(Long aBookmarId);

	BookmarkDto updateBookmark(BookmarkDto aBookmarkDto);
	
	BookmarkContextDto getBookmarkContextsForUser(String aUser);

}
