package com.focus.sv.ws.dso;

import java.util.List;

import com.focus.sv.ws.dto.BookDto;
import com.focus.sv.ws.model.Book;
import com.focus.sv.ws.model.User;
import com.focus.sv.ws.model.UserBook;


public interface BookService {
	 List<BookDto> findAllBooks();
	 void save(Book book);
	 List<BookDto> filterAllBooks(String searchTerm);
	 BookDto findById(Long id);
	 void requestBook(BookDto bookDto, User user);
	 List<UserBook> findByUserId(Long idUser);
	 List<UserBook> findByState(String state);
	 void returnBook(Long bookid);
}
