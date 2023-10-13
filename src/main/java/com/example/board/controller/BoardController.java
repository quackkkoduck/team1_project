package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.example.board.model.Board;
import com.example.board.model.User;
import com.example.board.repository.BoardRepository;

@Controller
public class BoardController {
	@Autowired
	BoardRepository boardRepository;

	@Autowired
	HttpSession session;

	@GetMapping("/board/delete/{id}")
	public String boardDeleteform(@PathVariable("id") long id) {

		Optional<Board> data = boardRepository.findById(id);
		if (data.isPresent()) {
			
			Board board = data.get();
			
	
			// 현재 사용자의 이메일
			String userEmail = (String) session.getAttribute("user_email");
	
			if (userEmail != null && userEmail.equals(board.getUser().getEmail())) {
				boardRepository.deleteById(id);
			}
		}
		return "redirect:/board/list";
	}

	@PostMapping("/board/delete/{id}")
public String boardDelete(@PathVariable("id") long id) {
	System.out.println(id);

    return "redirect:/board/list";
}
	@GetMapping("/board/update/{id}")
	public String boardUpdateForm(Model model, @PathVariable("id") long id) {
		Optional<Board> data = boardRepository.findById(id);
		Board board = data.orElse(null);
		if (board == null) {
			return "error-page"; // Handle the case where the board with the specified ID was not found
		} else {
			model.addAttribute("board", board);
			return "board/update";
		}
	}

	@PostMapping("/board/update/{id}")
	public String boardUpdate(
			@ModelAttribute Board updatedBoard, @PathVariable("id") long id) {
		Optional<Board> data = boardRepository.findById(id);
		if (data.isPresent()) {
			Board originalBoard = data.get();

			String userEmail = (String) session.getAttribute("user_email");
			if (userEmail != null && userEmail.equals(originalBoard.getUser().getEmail())) {
				originalBoard.setTitle(updatedBoard.getTitle());
				originalBoard.setContent(updatedBoard.getContent());
				boardRepository.save(originalBoard);
				return "redirect:/board/list";
			} else {
				return "redirect:/board/update/{id}"; // Handle the case where email doesn't match
			}
		} else {
			return "redirect:/board/update/{id}"; // Handle the case where the board with the specified ID was not found
		}
	}

	@GetMapping("/board/{id}")
	public String boardView(Model model, @PathVariable("id") long id) {
		Optional<Board> data = boardRepository.findById(id);
		Board board = data.get();
		model.addAttribute("board", board);
		return "board/view";
	}

	@GetMapping("/board/list")
	public String boardList(Model model) {
		Sort sort = Sort.by(Order.desc("id"));
		List<Board> list = boardRepository.findAll(sort);
		model.addAttribute("list", list);
		return "board/list";
	}

	@GetMapping("/board/write")
	public String boardWrite() {
		return "board/write";
	}

	@PostMapping("/board/write")
	public String boardWrite(@ModelAttribute Board board) {
		long userId = (long) session.getAttribute("user_id");
		String userEmail = (String) session.getAttribute("user_email");
		User user = new User();
		user.setId(userId);
		board.setUser(user);
		boardRepository.save(board);
		if (userEmail == null) {
			return "redirect:/signin";
		}
		return "redirect:/board/list";
	}
}
