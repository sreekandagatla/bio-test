package com.biomatch.test.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biomatch.test.controller.utils.MessageExtractorUtils;
import com.biomatch.test.payload.BookmarkContextDto;
import com.biomatch.test.payload.BookmarkDto;
import com.biomatch.test.payload.PayloadWrapper;
import com.biomatch.test.service.BookmarkService;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class BookmarkController {

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private MessageExtractorUtils messageExtractorUtils;

	@PostMapping("/bookmarks")
	public PayloadWrapper<BookmarkDto> addBookmark(@RequestBody @Valid BookmarkDto aBookmarkDto, BindingResult result) {

		if (result.hasErrors()) {
			return messageExtractorUtils.extractValidationErrors(aBookmarkDto, result);
		}

		aBookmarkDto = bookmarkService.addBookmark(aBookmarkDto);

		PayloadWrapper<BookmarkDto> responsePayload = new PayloadWrapper<>(aBookmarkDto);
		responsePayload.setMessage("Bookmark Added.");

		return responsePayload;
	}

	@PostMapping("/addBookmark")
	public PayloadWrapper<BookmarkDto> addBookmark(@RequestParam(value = "persona", required = false) String persona,
			@RequestParam(value = "context", required = true) String context,
			@RequestParam(value = "serialNumber", required = false) String serialNumber,
			@RequestParam(value = "item", required = false) String item,
			@RequestParam(value = "note", required = false) String note) {

		BookmarkDto bookmarkDto = new BookmarkDto();

		bookmarkDto.setUser(persona);
		bookmarkDto.setContext(context);
		bookmarkDto.setSerialNumber(serialNumber);
		bookmarkDto.setItem(item);
		bookmarkDto.setNote(note);

		bookmarkDto = bookmarkService.addBookmark(bookmarkDto);

		PayloadWrapper<BookmarkDto> responsePayload = new PayloadWrapper<>(bookmarkDto);
		responsePayload.setMessage("Bookmark Added.");

		return responsePayload;
	}
	
	@PutMapping("/bookmarks")
	public PayloadWrapper<BookmarkDto> updateBookmark(@RequestBody @Valid BookmarkDto aBookmarkDto, BindingResult result) {

		if (result.hasErrors()) {
			return messageExtractorUtils.extractValidationErrors(aBookmarkDto, result);
		}

		aBookmarkDto = bookmarkService.updateBookmark(aBookmarkDto);

		PayloadWrapper<BookmarkDto> responsePayload = new PayloadWrapper<>(aBookmarkDto);
		responsePayload.setMessage("Bookmark Updated.");

		return responsePayload;
	}

	@GetMapping("/bookmarks/{id}")
	public PayloadWrapper<BookmarkDto> getBookmark(@PathVariable("id") Long aBookmarId) {
		BookmarkDto bookmarkDto = bookmarkService.getBookmark(aBookmarId);
		PayloadWrapper<BookmarkDto> responsePayload = new PayloadWrapper<>(bookmarkDto);
		responsePayload.setMessage("Bookmark Retrieved.");

		return responsePayload;
	}

	@DeleteMapping("/bookmarks/{id}")
	public PayloadWrapper<Void> removeBookmark(@PathVariable("id") Long aBookmarId) {
		bookmarkService.deleteBookmark(aBookmarId);

		PayloadWrapper<Void> responsePayload = new PayloadWrapper<>();
		responsePayload.setMessage("Bookmark Removed.");

		return responsePayload;
	}

	@GetMapping("/bookmarks")
	public PayloadWrapper<BookmarkDto> getBookmarks(
			@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "context", required = true) String context) {
		Set<BookmarkDto> bookmarkSet = bookmarkService.getBookmarks(user, context);
		
		List<BookmarkDto> bookmarks = new ArrayList<>(bookmarkSet);

		PayloadWrapper<BookmarkDto> responsePayload = new PayloadWrapper<>(bookmarks);
		responsePayload.setMessage("Bookmarks Retrieved.");

		return responsePayload;
	}
	
	@GetMapping("/bookmarks/contexts/{user}")
	public PayloadWrapper<BookmarkContextDto> getBookmarkContextForUser(@PathVariable("user") String user) {
		BookmarkContextDto contextsForUser = bookmarkService.getBookmarkContextsForUser(user);

		PayloadWrapper<BookmarkContextDto> responsePayload = new PayloadWrapper<>(contextsForUser);
		responsePayload.setMessage("Bookmark Context for User Retrieved.");

		return responsePayload;
	}
}
