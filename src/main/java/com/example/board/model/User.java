package com.example.board.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String email;
	private String pwd;
	private String name;

	@OneToMany(mappedBy = "user")
    private List<Board> boards;
	public List<Board> getBoards() {
        return boards;
    }
	public void setBoards(List<Board> boards) {
        this.boards = boards;
    }
	// private String userId;
	// public User orElse(Object object) {
	// 	return null;
	// }
}