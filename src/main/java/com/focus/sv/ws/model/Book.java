package com.focus.sv.ws.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="books")
public class Book {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 25, nullable = false)
    private String title;

    @Column(length = 25, nullable = false)
    private String author;
    
    @Column(nullable = false)
    private Integer publishedYear;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(length = 25, nullable = false)
    private String genre;
    @JsonIgnore
    @ManyToMany(mappedBy = "usersBooks")
    private List<User> users = new ArrayList<>();
}
