package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.example.board.model.Board;
import com.example.board.model.User;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.UserRepository;

import org.springframework.validation.BindingResult;
// import org.springframework.validation.annotation.Validated;

@Controller
public class UserController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping("/signin")
	public String signin() {
		return "signin";
	}
	
	@PostMapping("/signin")
	public String signinPost(@ModelAttribute User user) {
		User dbUser = userRepository.findByEmail(user.getEmail());
		String encodedPwd = dbUser.getPwd();
		String userPwd = user.getPwd();
		boolean isMatch = passwordEncoder.matches(userPwd, encodedPwd);
		if (isMatch) {
			session.setAttribute("user_id", dbUser.getId());
			session.setAttribute("user_email", dbUser.getEmail());

			return "redirect:/"; // 로그인 성공 시 홈 페이지로 리다이렉트
		} else {
			return "redirect:/signin"; // 로그인 실패 시 다시 로그인 페이지로 리다이렉트
		}
	}

	@GetMapping("/signout")
	public String signout() {
		session.invalidate();
		return "redirect:/";
	}
	@GetMapping("/myPage")
public String myPage(Model model) {
    User user = userRepository.findById((Long) session.getAttribute("user_id")).orElse(null);
    if (user == null) {
        // 사용자 정보를 찾을 수 없음
        return "redirect:/signin";
    }
    model.addAttribute("user", user);
    return "myPage";
}
@PostMapping("/myPage")
public String myPageUpdate(@ModelAttribute User user, BindingResult bindingResult) {
    // 현재 로그인한 사용자의 아이디를 가져옴
    Long userId = (Long) session.getAttribute("user_id");

    // 사용자 ID로 기존 사용자 정보를 찾음
    User existingUser = userRepository.findById(userId).orElse(null);

    if (existingUser == null) {
        // 사용자 정보를 찾을 수 없음
        return "redirect:/signin";
    }

    // 사용자 정보 업데이트
    existingUser.setEmail(user.getEmail());
    existingUser.setName(user.getName());

    // 비밀번호가 변경되었을 경우에만 업데이트
    if (user.getPwd() != null && !user.getPwd().isEmpty()) {
        String encodedPwd = passwordEncoder.encode(user.getPwd());
        existingUser.setPwd(encodedPwd);
    }

    userRepository.save(existingUser);

    return "redirect:/myPage";
}

	
	@GetMapping("/signup") 
	public String signup() {
		return "signup";
	}

	@PostMapping("/signup")
	@Transactional
	public String signupPost(@ModelAttribute User user, BindingResult bindingResult) {
		// 이메일 정규식 패턴
		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
		String pwdPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
	
		// 이메일 검사
		if (!user.getEmail().matches(emailPattern)) {
			bindingResult.rejectValue("email", "error.user", "Invalid email");
			System.out.println(5252);
			return "signup";
		}
		// 비번 검사
		if (!user.getPwd().matches(pwdPattern)) {
			bindingResult.rejectValue("pwd", "error.user", "Invalid pwd");
			return "signup";
		}
		String userPwd = user.getPwd();
		String encodedPwd=passwordEncoder.encode(userPwd);//비번 암호화해서 
		user.setPwd(encodedPwd); //다시 Pwd에 넣어준다
		userRepository.save(user);
		System.out.println(user); // 이메일 유효성 검사 실패 시 다시 회원가입 페이지로 이동
		return "redirect:/";
	}
}