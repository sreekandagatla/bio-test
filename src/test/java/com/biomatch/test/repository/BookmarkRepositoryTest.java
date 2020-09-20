package com.biomatch.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.biomatch.test.domain.Bookmark;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
public class BookmarkRepositoryTest {
	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Test
	public void testSave() {
		String userName = RandomStringUtils.randomAlphabetic(20);
		String context = RandomStringUtils.randomAlphanumeric(255);
		String item = RandomStringUtils.randomAlphanumeric(2000);
		String note = RandomStringUtils.randomAlphanumeric(255);

		Bookmark bookmark = new Bookmark();
		bookmark.setUser(userName);
		bookmark.setContext(context);
		bookmark.setItem(item);
		bookmark.setNote(note);

		Bookmark savedBookmark = bookmarkRepository.save(bookmark);

		assertNotNull("Invalid Id!", bookmark.getId());

		assertEquals("Invalid User Name!", userName, savedBookmark.getUser());
		assertEquals("Invalid Item!", item, savedBookmark.getItem());
		assertEquals("Invalid Note!", note, savedBookmark.getNote());
	}

	@Test
	public void testFindByUser() {
		String context = RandomStringUtils.randomAlphanumeric(255);
		String user01 = RandomStringUtils.randomAlphabetic(20);
		String user02 = RandomStringUtils.randomAlphabetic(20);

		for (int lv = 0; lv < 10; lv++) {
			String serialNum = RandomStringUtils.randomAlphanumeric(80);
			String note = RandomStringUtils.randomAlphanumeric(255);

			Bookmark bookmark = new Bookmark();
			bookmark.setContext(context);
			if (lv % 2 == 0) {
				bookmark.setUser(user01);
			} else {
				bookmark.setUser(user02);
			}
			bookmark.setSerialNumber(serialNum);
			bookmark.setNote(note);
			bookmarkRepository.save(bookmark);
		}

		List<Bookmark> allBookmarks = bookmarkRepository.findAll();
		assertEquals("Invalid Total Count!", 10, allBookmarks.size());

		Set<Bookmark> user01Bookmark = bookmarkRepository.findByUser(user01);
		assertEquals("Invalid Count for User 01!", 5, user01Bookmark.size());

		Set<Bookmark> user02Bookmark = bookmarkRepository.findByUser(user02);
		assertEquals("Invalid Count for User 02!", 5, user02Bookmark.size());
	}
	
	@Test
	public void testFindContextByUser() {
		String user = RandomStringUtils.randomAlphanumeric(20);
		String context01 = RandomStringUtils.randomAlphabetic(255);
		String context02 = RandomStringUtils.randomAlphabetic(255);

		for (int lv = 0; lv < 10; lv++) {
			String serialNum = RandomStringUtils.randomAlphanumeric(80);

			Bookmark bookmark = new Bookmark();
			bookmark.setUser(user);
			if (lv % 2 == 0) {
				bookmark.setContext(context01);
			} else {
				bookmark.setContext(context02);
			}
			bookmark.setSerialNumber(serialNum);
			bookmarkRepository.save(bookmark);
		}

		Set<String> contexts = bookmarkRepository.findDistinctContextByUser(user);
		
		assertEquals("Incorrect number of contexts!", 2, contexts.size());
		assertTrue("Missing context 01!", contexts.contains(context01));
		assertTrue("Missing context 02!", contexts.contains(context02));
	}
}
