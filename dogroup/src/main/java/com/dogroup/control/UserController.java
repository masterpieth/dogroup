package com.dogroup.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dogroup.dto.UserDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;
import com.dogroup.service.UserService;
import com.dogroup.util.GithubLoginUtil;

/**
 * User관련 컨트롤러
 * 로그인시 Redirect를 해줘야할때가 있어 RestController가 될 수 없습니다.
 * @author NYK
 *
 */
@Controller
@RequestMapping("user/*")
public class UserController {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserService userService;
	
	private String frontURL = "http://192.168.2.46:5500/src/main/webapp/html/";		//개발시 본인의 IP로 바꿔 쓰세요
	
	/**
	 * github 로그인 페이지로 보낸다. github에 로그인이 되어있는 경우에는 바로 githubAuthCallback으로 이동된다.
	 * @return					github 로그인 페이지
	 * @throws Exception
	 */
	@GetMapping("login")
	public ResponseEntity<?> login() throws Exception {
		log.info("login 시작");
		
		String clientId = GithubLoginUtil.getApiClientId();
		Map<String, Object> map = new HashMap<>();
		map.put("authorize_url", GithubLoginUtil.AUTHORIZE_URL);
		map.put("redirect_url", GithubLoginUtil.REDIRECT_URL);
		map.put("client_id", clientId);
		
		log.info("login 끝");
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
	
	/**
	 * 로그아웃한다.
	 * @param session
	 * @return
	 */
	@GetMapping("logout")
	public ResponseEntity<?> logout(HttpSession session) {
		log.info("logout 시작");
		session.invalidate();
		log.info("logout 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 인증 후 사용자의 email을 받아온다.
	 * @param code				github api에서 발급받은 1회용 인증 코드
	 * @param session			HttpSession
	 * @return					아이디가 없으면 회원가입 페이지로, 없으면 index 페이지로 redirect됨
	 * @throws Exception
	 */
	@GetMapping("auth/github/callback")
	public String githubAuthCallback(String code, HttpSession session) throws Exception {
		log.info("github auth callback 시작 - 1회용 code: " + code);
		
		String accessToken = GithubLoginUtil.getAccessToken(code);
		String email = GithubLoginUtil.getUserEmail(accessToken);
		
		String redirectURL = "";
		if(userService.idDuplicateCheck(email)) {
			redirectURL = frontURL + "user_signup.html?email=" + email;
		} else {
			UserDTO user = userService.searchUserInfo(email);
			if(user.getStatus() == 0) {
				redirectURL = frontURL + "index.html?status=0";
			} else {
				session.setAttribute("loginedId", email);
				redirectURL = frontURL + "index.html?email=" + email;
			}
		}
		log.info("github auth callback 끝 redirectURL: " + redirectURL);
		return "redirect:" + redirectURL;
	}
	
	/**
	 * 회원가입한다.
	 * @param user				email, name, password 정보를 가지고 있다.
	 * @return
	 * @throws AddException
	 */
	@PostMapping("signup")
	@ResponseBody
	public ResponseEntity<?> signup(@RequestBody UserDTO user) throws AddException {
		log.info("signup 시작: " + user.getEmail() + "/" + user.getName() + "/" + user.getPassword());
		
		userService.signUp(user);
		
		log.info("signup 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 회원탈퇴한다.
	 * @param session
	 * @return
	 * @throws ModifyException
	 */
	@DeleteMapping
	@ResponseBody
	public ResponseEntity<?> withdraw(HttpSession session) throws ModifyException {
		log.info("withdraw 시작");
		
		String email = (String) session.getAttribute("loginedId");
		userService.withdraw(email);
		session.invalidate();
		
		log.info("withdraw 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 사용자 정보를 반환한다.
	 * @return
	 * @throws FindException 
	 */
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> userInfo(HttpSession session) throws FindException {
		log.info("userInfo 시작");
		
		String email = (String)session.getAttribute("loginedId");
		UserDTO user = userService.searchUserInfo(email);
		
		log.info("userInfo 끝");
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	/**
	 * 사용자 정보를 수정한다.
	 * @param user
	 * @return
	 * @throws ModifyException
	 */
	@PutMapping
	@ResponseBody
	public ResponseEntity<?> updateUser(@RequestBody UserDTO user) throws ModifyException {
		log.info("updateUser(컨트롤러) 시작");
		
		userService.modifyUser(user);
		
		log.info("updateUser(컨트롤러) 끝");
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}
