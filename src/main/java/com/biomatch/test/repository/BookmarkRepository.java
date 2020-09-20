package com.biomatch.test.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.biomatch.test.domain.Bookmark;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	Set<Bookmark> findByUser(String user);

	default Set<Bookmark> findForAllUsers() {
		return findByUser(Bookmark.GLOBAL_USER);
	}

	Set<Bookmark> findByUserAndContext(String user, String context);

	void deleteByUserAndContextAndItem(String user, String context, String item);

	void deleteByUserAndContextAndSerialNumber(String user, String context, String serialNumber);

	@Query("Select Distinct bm.context From Bookmark bm Where bm.user = :user")
	Set<String> findDistinctContextByUser(String user);
}
