package com.focus.sv.ws.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.focus.sv.ws.dso.BookService;
import com.focus.sv.ws.dso.UserService;
import com.focus.sv.ws.dto.BookDto;
import com.focus.sv.ws.model.Book;
import com.focus.sv.ws.model.User;
import com.focus.sv.ws.model.UserBook;

@Controller
@RequestMapping("book")
public class BookController {

	private static final String STUDENT_ROLE = "STUDENT";
	private static final String STATE_REQUESTED = "REQUESTED";

	@Autowired
	BookService bookService;
	@Autowired
	UserService userService;
	
	@GetMapping("/find-all")
	public ResponseEntity<Object> getBooks(@RequestParam(name = "searchTerm", required = false) String searchTerm) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		try {
			List<BookDto> bookList  = new ArrayList<>();
			if(searchTerm == null) {
				bookList = bookService.findAllBooks();
			}else {
				bookList = bookService.filterAllBooks(searchTerm);
			}
			response.put("success", true);
			response.put("book_list", bookList);
				return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "It was an error");
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/save")
	public ResponseEntity<Object> save(@RequestBody Book book
			) {
			Map<String,Object> response = new HashMap<String, Object>();
		try {		
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			 String role = authentication.getAuthorities().stream()
                     .findFirst()
                     .map(GrantedAuthority::getAuthority)
                     .orElse(null);
			 if(STUDENT_ROLE.equals(role)) {
					response.put("success", false);
					response.put("message", "Unauthorized");
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			 }
			bookService.save(book);
			response.put("success", true);
			response.put("message", "Your book was added successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "There was an error. ".concat(e.getMessage()));
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/details")
	public ResponseEntity<Object> details(Long bookId) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		try {
			BookDto book = bookService.findById(bookId);
			response.put("success", true);
			response.put("book_details", book);
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "There was an error");
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/request")
	public ResponseEntity<Object> request(@RequestParam(required = true)Long bookId
			) {
			Map<String,Object> response = new HashMap<String, Object>();
		try {		
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();

			if (authentication == null && !authentication.isAuthenticated()) {
				response.put("success", true);
				response.put("message", "Unauthorized");
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
		    String username = authentication.getName(); 

				 User us = userService.findByUserName(username);
			 String role = authentication.getAuthorities().stream()
                     .findFirst()
                     .map(GrantedAuthority::getAuthority)
                     .orElse(null);
			 if(!STUDENT_ROLE.equals(role)) {
					response.put("success", true);
					response.put("message", "Unauthorized");
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			 }
			 BookDto bookDto = bookService.findById(bookId);
			 if(bookDto == null) {
					response.put("success", true);
					response.put("message", "Book not found");
					return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			 }
			bookService.requestBook(bookDto, us);
			response.put("success", true);
			response.put("message", "Your book was requested successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "There was an error. ".concat(e.getMessage()));
			e.printStackTrace();
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
	
	@GetMapping("/requested-books")
	public ResponseEntity<Object> getRequestedBooks() {
		HashMap<String, Object> response = new HashMap<String, Object>();
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
		    String username = authentication.getName(); 

			User us = userService.findByUserName(username);
			 String role = authentication.getAuthorities().stream()
                     .findFirst()
                     .map(GrantedAuthority::getAuthority)
                     .orElse(null);
			 if(!STUDENT_ROLE.equals(role)) {
					response.put("success", true);
					response.put("book_list", bookService.findByState(STATE_REQUESTED));
					return new ResponseEntity<>(response, HttpStatus.OK);
			 }
			response.put("success", true);
			response.put("book_list", bookService.findByUserId(us.getId()));
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "It was an error");
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	@PostMapping("/return")
	public ResponseEntity<Object> returned(@RequestParam(required = true)Long bookId
			) {
			Map<String,Object> response = new HashMap<String, Object>();
		try {		
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();

			if (authentication == null && !authentication.isAuthenticated()) {
				response.put("success", true);
				response.put("message", "Unauthorized");
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
		    String username = authentication.getName(); 

				 User us = userService.findByUserName(username);
			 String role = authentication.getAuthorities().stream()
                     .findFirst()
                     .map(GrantedAuthority::getAuthority)
                     .orElse(null);
			 if(STUDENT_ROLE.equals(role)) {
					response.put("success", true);
					response.put("message", "Unauthorized");
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			 }
		
			bookService.returnBook(bookId);
			response.put("success", true);
			response.put("message", "Your book was returned successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "There was an error. ".concat(e.getMessage()));
			e.printStackTrace();
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
}
