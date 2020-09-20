package com.biomatch.test.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import com.biomatch.test.domain.Bookmark;
import com.biomatch.test.payload.BookmarkContextDto;
import com.biomatch.test.payload.BookmarkDto;
import com.biomatch.test.repository.BookmarkRepository;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkServiceImplTest {
	@Mock
	private BookmarkRepository repository;

	@InjectMocks
	private BookmarkServiceImpl bookmarkService;

	@Test
	public void testAddBookmark() {
		BookmarkDto mockDto = Mockito.mock(BookmarkDto.class);

		Bookmark mockBM = Mockito.mock(Bookmark.class);
		Mockito.when(repository.save(Mockito.any(Bookmark.class))).thenReturn(mockBM);

		bookmarkService.addBookmark(mockDto);

		Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Bookmark.class));
	}

	@Test
	public void testDeleteBookmark() {
		BookmarkDto bmDto = new BookmarkDto();
		bmDto.setId(RandomUtils.nextLong());

		bookmarkService.deleteBookmark(bmDto);

		Mockito.verify(repository, Mockito.times(1)).deleteById(bmDto.getId());

		bmDto.setId(null);
		bmDto.setUser(RandomStringUtils.random(16));
		bmDto.setContext(RandomStringUtils.random(80));
		bmDto.setSerialNumber(RandomStringUtils.random(80));

		bookmarkService.deleteBookmark(bmDto);

		Mockito.verify(repository, Mockito.times(1)).deleteByUserAndContextAndSerialNumber(bmDto.getUser(),
				bmDto.getContext(), bmDto.getSerialNumber());

		bmDto.setSerialNumber(null);
		bmDto.setItem(RandomStringUtils.random(4000));

		bookmarkService.deleteBookmark(bmDto);

		Mockito.verify(repository, Mockito.times(1)).deleteByUserAndContextAndItem(bmDto.getUser(), bmDto.getContext(),
				bmDto.getItem());

		long id = RandomUtils.nextLong();
		bookmarkService.deleteBookmark(id);

		Mockito.verify(repository, Mockito.times(1)).deleteById(id);
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void testDeleteBookmarkNegative() {
		BookmarkDto bmDto = new BookmarkDto();
		bookmarkService.deleteBookmark(bmDto);
	}

	@Test
	public void testGetBookmarks() {
		Set<Bookmark> bookmarks = new HashSet<>();
		for (int lv = 0; lv < 10; lv++) {
			bookmarks.add(Mockito.mock(Bookmark.class));
		}

		String user = RandomStringUtils.random(16);
		String context = RandomStringUtils.random(32);

		Mockito.when(repository.findByUserAndContext(user, context)).thenReturn(bookmarks);

		bookmarkService.getBookmarks(user, context);

		Mockito.verify(repository, Mockito.times(1)).findByUserAndContext(user, context);
	}

	@Test
	public void testGetBookmark() {
		long bmId = RandomUtils.nextLong();

		Mockito.when(repository.findById(bmId)).thenReturn(Optional.of(Mockito.mock(Bookmark.class)));

		bookmarkService.getBookmark(bmId);

		Mockito.verify(repository, Mockito.times(1)).findById(bmId);
	}

	@Test(expected = DataRetrievalFailureException.class)
	public void testGetBookmarkNegative() {
		long bmId = RandomUtils.nextLong();

		Mockito.when(repository.findById(bmId)).thenReturn(Optional.empty());

		bookmarkService.getBookmark(bmId);
	}

	@Test
	public void testUpdateBookmark() {
		Bookmark bookmark = Mockito.mock(Bookmark.class);

		BookmarkDto bmDto = new BookmarkDto();
		bmDto.setId(RandomUtils.nextLong());

		Mockito.when(repository.findById(bmDto.getId())).thenReturn(Optional.of(bookmark));

		bookmarkService.updateBookmark(bmDto);

		Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Bookmark.class));
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void testUpdateBookmarkNeg01() {
		BookmarkDto bmDto = new BookmarkDto();
		bookmarkService.updateBookmark(bmDto);
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void testUpdateBookmarkNeg02() {
		BookmarkDto bmDto = new BookmarkDto();
		bmDto.setId(RandomUtils.nextLong());

//		Mockito.when(repository.findById(bmDto.getId())).thenReturn(Optional.empty());

		bookmarkService.updateBookmark(bmDto);
	}

	@Test
	public void testGetBookmarkContextsForUser() {
		String user = RandomStringUtils.randomAlphanumeric(20);

		String context01 = RandomStringUtils.randomAlphabetic(255);
		String context02 = RandomStringUtils.randomAlphabetic(255);

		Set<String> contexts = new HashSet<>();
		contexts.add(context01);
		contexts.add(context02);

		Mockito.when(repository.findDistinctContextByUser(user)).thenReturn(contexts);

		BookmarkContextDto bcDto = bookmarkService.getBookmarkContextsForUser(user);

		assertEquals("Incorrect number of contexts!", 2, bcDto.getContexts().length);

		boolean ctx01Found = false;
		boolean ctx02Found = false;
		for (String ctx : bcDto.getContexts()) {
			if (StringUtils.equals(ctx, context01)) {
				ctx01Found = true;
			} else if (StringUtils.equals(ctx, context02)) {
				ctx02Found = true;
			}
		}

		assertTrue("Unable to find context01!", ctx01Found);
		assertTrue("Unable to find context02!", ctx02Found);
	}

}
