package com.focus.sv.ws.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookDto {
	  private Long id;
		
	    private String title;

	    private String author;
	    
	    private Integer publishedYear;
	    
	    private Integer stock;
	    
	    private String genre;
}
