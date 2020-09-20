package com.biomatch.test.service.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biomatch.test.domain.Bookmark;
import com.biomatch.test.payload.BookmarkContextDto;
import com.biomatch.test.payload.BookmarkDto;
import com.biomatch.test.repository.BookmarkRepository;
import com.biomatch.test.service.BookmarkService;

@Service
public class BookmarkServiceImpl implements BookmarkService {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Override
	@Transactional
	public BookmarkDto addBookmark(BookmarkDto aBookmarkDto) {
		ModelMapper modelMapper = new ModelMapper();

		Bookmark bookmark = modelMapper.map(aBookmarkDto, Bookmark.class);

		bookmark = bookmarkRepository.save(bookmark);

		aBookmarkDto.setId(bookmark.getId());

		return aBookmarkDto;
	}

	@Override
	@Transactional
	public void deleteBookmark(BookmarkDto aBookmarkDto) {
		if (aBookmarkDto.getId() != null && aBookmarkDto.getId().longValue() > 0) {
			bookmarkRepository.deleteById(aBookmarkDto.getId());
		} else if (aBookmarkDto.getUser() != null && aBookmarkDto.getContext() != null
				&& aBookmarkDto.getSerialNumber() != null) {
			bookmarkRepository.deleteByUserAndContextAndSerialNumber(aBookmarkDto.getUser(), aBookmarkDto.getContext(),
					aBookmarkDto.getSerialNumber());
		} else if (aBookmarkDto.getUser() != null && aBookmarkDto.getContext() != null
				&& aBookmarkDto.getItem() != null) {
			bookmarkRepository.deleteByUserAndContextAndItem(aBookmarkDto.getUser(), aBookmarkDto.getContext(),
					aBookmarkDto.getItem());
		} else {
			throw new InvalidDataAccessApiUsageException(
					"Missing Id or combination of user, context and Serial Number or Item!");
		}
	}

	@Override
	@Transactional
	public void deleteBookmark(long aBookmarId) {
		bookmarkRepository.deleteById(aBookmarId);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<BookmarkDto> getBookmarks(String aUser, String aContext) {
		ModelMapper modelMapper = new ModelMapper();

		Set<Bookmark> bookmarks = bookmarkRepository.findByUserAndContext(aUser, aContext);

		Set<BookmarkDto> dtos = bookmarks.stream().map(bm -> modelMapper.map(bm, BookmarkDto.class))
				.collect(Collectors.toSet());

		return dtos;
	}

	@Override
	@Transactional(readOnly = true)
	public BookmarkDto getBookmark(Long aBookmarId) {
		Optional<Bookmark> optBM = bookmarkRepository.findById(aBookmarId);

		if (optBM.isPresent()) {
			ModelMapper modelMapper = new ModelMapper();
			return modelMapper.map(optBM.get(), BookmarkDto.class);
		}

		throw new DataRetrievalFailureException(String.format("Bookmark with id [%d] cannot be found!", aBookmarId));
	}

	@Override
	@Transactional
	public BookmarkDto updateBookmark(BookmarkDto aBookmarkDto) {
		if (aBookmarkDto.getId() == null || aBookmarkDto.getId().longValue() <= 0) {
			throw new InvalidDataAccessApiUsageException("Unable to Update Bookmark. Id is not Specified!");
		}

		Optional<Bookmark> optBM = bookmarkRepository.findById(aBookmarkDto.getId());
		if (optBM.isEmpty()) {
			throw new InvalidDataAccessApiUsageException("Unable to Update Bookmark. Invalid Id Specified!");
		}

		ModelMapper modelMapper = new ModelMapper();

		Bookmark bookmark = modelMapper.map(aBookmarkDto, Bookmark.class);

		bookmark = bookmarkRepository.save(bookmark);

		return aBookmarkDto;
	}

	@Override
	@Transactional(readOnly = true)
	public BookmarkContextDto getBookmarkContextsForUser(String aUser) {
		Set<String> contexts = bookmarkRepository.findDistinctContextByUser(aUser);

		BookmarkContextDto contextDto = new BookmarkContextDto();
		contextDto.setUser(aUser);
		contextDto.setContexts(contexts.toArray(new String[contexts.size()]));

		return contextDto;
	}

}
