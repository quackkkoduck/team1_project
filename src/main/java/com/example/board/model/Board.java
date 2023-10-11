package com.example.board.model;

// import java.util.ArrayList;
// import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import lombok.Data;

@Entity
@Data
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String title;
	private String content;
	// private String userId;
	// @OneToMany(mappedBy = "board")
    // List<Comment> Comments =
    //  new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
	public User getUser() {
        return user;
    }
	public void setUser(User user) {
        this.user = user;
    }

}