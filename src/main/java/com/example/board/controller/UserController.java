package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// import java.util.List;

import javax.servlet.http.HttpSession;

import com.example.board.model.Board;
import com.example.board.model.User;
// import com.example.board.repository.BoardRepository;
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
    public String signinPost(@ModelAttribute User user, Model model) {
        List<User> dbUsers = userRepository.findByEmail(user.getEmail());

        if (dbUsers != null && !dbUsers.isEmpty()) {
            User dbUser = dbUsers.get(0); // 첫 번째 사용자를 선택

            String encodedPwd = dbUser.getPwd();
            String userPwd = user.getPwd();
            boolean isMatch = passwordEncoder.matches(userPwd, encodedPwd);

            if (isMatch) {
                session.setAttribute("user_id", dbUser.getId());
                session.setAttribute("user_email", dbUser.getEmail());

                return "redirect:/"; // 로그인 성공 시 홈 페이지로 리다이렉트
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "signin";
            }
        } else {
            model.addAttribute("error", "User not found");
            return "signin";
        }
    }
	@GetMapping("/usercheck")
	public String usercheck() {
		return "usercheck";
	}
	
	@PostMapping("/usercheck")
    public String usercheck(@ModelAttribute User user, Model model) {
		System.out.println(user);
		String userEmail =(String)session.getAttribute("user_email");
        if (userEmail!=null|| user.getPwd() != null) {
			List<User> dbUser = userRepository.findByEmail(userEmail);
            		System.out.println(dbUser);


            String encodedPwd = dbUser.get(0).getPwd();
			;
            String userPwd = user.getPwd();
            boolean isMatch = passwordEncoder.matches(userPwd, encodedPwd);

            if (isMatch) {
                
				System.out.println(1);
                return "redirect:/myPage"; 
            } else {
                model.addAttribute("error", "Invalid email or password");
                System.out.println(2);
				return "usercheck";
            }
        } else {
            model.addAttribute("error", "User not found");
			System.out.println(3);
			return "usercheck";
        }
    }
	@GetMapping("/signout")
	public String signout() {
		session.invalidate();
		return "redirect:/";
	}

	
	@GetMapping("/signup") 
public String signup(Model model) {
    model.addAttribute("user", new User()); // User 객체를 모델에 추가
    return "signup";
}

@PostMapping("/signup")
@Transactional
public String signupPost(@ModelAttribute User user, BindingResult bindingResult) {
    // 이메일 중복 검사
    List<User> existingUsers = userRepository.findByEmail(user.getEmail());
    if (!existingUsers.isEmpty()) {
        bindingResult.rejectValue("email", "error.user", "이미 가입된 이메일입니다");
        return "signup";
    }

    // 이메일 검사
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
    if (!user.getEmail().matches(emailPattern)) {
        bindingResult.rejectValue("email", "error.user", "Invalid email");
        return "signup";
    }

    // 비밀번호 검사
    String pwdPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
    if (!user.getPwd().matches(pwdPattern)) {
        bindingResult.rejectValue("pwd", "error.user", "영문 숫자 특수기호 조합 8자리 이상 입력해 주세요");
        return "signup";
    }

    // 비밀번호 암호화
    String userPwd = user.getPwd();
    String encodedPwd = passwordEncoder.encode(userPwd); // 비밀번호 암호화
    user.setPwd(encodedPwd); // 암호화된 비밀번호를 설정

    userRepository.save(user);

    return "redirect:/";
}
	@GetMapping("/myPage")
public String myPage(Model model) {
    Long userId = (Long) session.getAttribute("user_id");
    if (userId == null) {
        // User is not logged in, redirect to signin
        return "redirect:/signin";
    }

    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
        // User not found, handle as needed (e.g., show an error page)
        return "error-page";
    }

    model.addAttribute("user", user);
    return "myPage";
}

@PostMapping("/myPage")
@Transactional
public String myPagePost(@ModelAttribute User user, BindingResult bindingResult) {
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
	String pwdPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";

    if (!user.getEmail().matches(emailPattern)) {
        bindingResult.rejectValue("email", "error.user", "Invalid email");
        return "myPage";
    }

    if (user.getPwd() != null && !user.getPwd().isEmpty()) {
			if (!user.getPwd().matches(pwdPattern)) {
				bindingResult.rejectValue("pwd", "error.user", "Invalid password");
				return "myPage";
			}
	
			// Check if the password has been provided and is not empty
			String encodedPwd = passwordEncoder.encode(user.getPwd());
			user.setPwd(encodedPwd);
		}
	
		userRepository.save(user);

    return "redirect:/myPage";
}
}