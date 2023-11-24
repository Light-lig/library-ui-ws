package com.focus.sv.ws.dso.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focus.sv.ws.data.repository.BookRepository;
import com.focus.sv.ws.data.repository.UserBookRepository;
import com.focus.sv.ws.data.repository.UserRepository;
import com.focus.sv.ws.dso.BookService;
import com.focus.sv.ws.dto.BookDto;
import com.focus.sv.ws.model.Book;
import com.focus.sv.ws.model.User;
import com.focus.sv.ws.model.UserBook;

@Service
public class BookServiceImpl implements BookService{

	@Autowired
	BookRepository bookRepo;
	
	@Autowired
	UserBookRepository userBookRepo;


    @Autowired
    private ModelMapper modelMapper;
    
	@Override
	public List<BookDto> findAllBooks() {
		List<Book> books = bookRepo.findAll();
		List<BookDto> booksDtolist = books.stream().map(book -> modelMapper.map(book,BookDto.class)).collect(Collectors.toList());
		return booksDtolist;
	}
	
	@Override
	public void save(Book book) {
		bookRepo.save(book);
	}
	
	@Override
	public List<BookDto> filterAllBooks(String searchTerm) {
		List<Book> books = bookRepo.searchBooks(searchTerm);
		List<BookDto> booksDtolist = books.stream().map(book -> modelMapper.map(book,BookDto.class)).collect(Collectors.toList());
		return booksDtolist;
	}

	@Override
	public BookDto findById(Long id) {
		Optional<Book> bookOp = bookRepo.findById(id);
		if(bookOp.isPresent()) {
			Book book = bookOp.get();
			BookDto bookDto = modelMapper.map(book, BookDto.class);
			return bookDto;
		}
		return null;
	}

	@Override
	public void requestBook(BookDto bookDto, User user) {
		Book book = modelMapper.map(bookDto, Book.class);
		Integer stock = book.getStock();
		
		book.setStock(stock - 1);
		UserBook ub = new UserBook();
		bookRepo.save(book);
		ub.setBook(book);
		ub.setUser(user);
		ub.setState("REQUESTED");
		
		userBookRepo.save(ub);
	}

	@Override
	public List<UserBook> findByUserId(Long idUser) {
		return userBookRepo.findByUser(idUser);
	}

	@Override
	public List<UserBook> findByState(String state) {
		return userBookRepo.findByState(state);
	}

	@Override
	public void returnBook(Long bookid) {
		Optional<UserBook> ub = userBookRepo.findById(bookid);
		if(ub.isPresent()) {
			UserBook userBook = ub.get();
			Book book = userBook.getBook();
			Integer stock = book.getStock();
			book.setStock(stock + 1);
			bookRepo.save(book);
			userBook.setState("RETURNED");
			userBookRepo.save(userBook);
		}		
	}

}
