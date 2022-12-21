package com.dogroup.control;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dogroup.dto.PageBean;
import com.dogroup.dto.StudyDTO;
import com.dogroup.dto.StudyUserDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;
import com.dogroup.exception.RemoveException;
import com.dogroup.service.StudyService;

/**
 * 스터디 관련 REST 컨트롤러
 * @author NYK
 *
 */
@RestController
@RequestMapping("study/*")
public class StudyRestController {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StudyService studyService;
	
	/**
	 * 스터디 상세 조회한다.
	 * @param studyId
	 * @return	StudyDTO
	 * @throws FindException
	 */
	@GetMapping("{studyId}")
	public ResponseEntity<?> searchStudyInfo(@PathVariable int studyId) throws FindException {
		log.info("searchStudyInfo(컨트롤러) 시작: studyId: " + studyId);
		
		StudyDTO study = studyService.searchStudyInfo(studyId);
		
		log.info("searchStudyInfo(컨트롤러) 끝");
		return new ResponseEntity<>(study, HttpStatus.OK);
	}
	
	/**
	 * 스터디에 가입한다.
	 * @param studyId
	 * @param study
	 * @param session
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("join/{studyId}")
	public ResponseEntity<?> joinStudy(@PathVariable int studyId, StudyDTO study, HttpSession session) throws Exception {
		log.info("joinStudy(컨트롤러) 시작: studyId: " + studyId);
		
		String email = (String) session.getAttribute("loginedId");
		studyService.joinStudy(email, study);
		
		log.info("joinStudy(컨트롤러) 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 스터디 참여를 취소한다.
	 * @param studyId
	 * @param study
	 * @param session
	 * @return
	 * @throws RemoveException
	 */
	@DeleteMapping("join/{studyId}")
	public ResponseEntity<?> leaveStudy(@PathVariable int studyId, StudyDTO study, HttpSession session) throws RemoveException {
		log.info("leaveStudy(컨트롤러) 시작: studyId: " + studyId);
		
		String email = (String) session.getAttribute("loginedId");
		studyService.leaveStudy(study, email);
		
		log.info("leaveStudy(컨트롤러) 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 스터디를 개설한다.
	 * @param study
	 * @return
	 * @throws Exception 
	 */
	@PostMapping
	public ResponseEntity<?> openStudy(StudyDTO study) throws Exception {
		log.info("openStudy(컨트롤러) 시작");
		
		studyService.openStudy(study);
		
		log.info("openStudy(컨트롤러) 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 스터디 내용을 수정한다.
	 * @param studyId
	 * @param study
	 * @return
	 * @throws ModifyException
	 */
	@PutMapping("{studyId}")
	public ResponseEntity<?> modifyStudy(@PathVariable int studyId, StudyDTO study) throws ModifyException {
		log.info("modifyStudy(컨트롤러) 시작");
		
		studyService.modifyStudy(study);
		
		log.info("modifyStudy(컨트롤러) 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 스터디를 삭제한다.
	 * @param studyId
	 * @return
	 * @throws RemoveException
	 */
	@DeleteMapping("{studyId}")
	public ResponseEntity<?> deleteStudy(@PathVariable int studyId) throws RemoveException {
		log.info("deleteStudy(컨트롤러) 시작");
		
		studyService.deleteStudy(studyId);
		
		log.info("deleteStudy(컨트롤러) 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 스터디원의 상세 정보를 조회한다.
	 * @param studyId
	 * @param email
	 * @return
	 * @throws FindException 
	 */
	@GetMapping("users/{studyId}")
	public ResponseEntity<?> searchStudyUserInfo(@PathVariable int studyId, String email) throws FindException {
		log.info("searchStudyUserInfo(컨트롤러) 시작: studyId: " + studyId + " /email: " + email);
		
		StudyUserDTO studyUser = studyService.searchMyStudyUserInfo(email, studyId);
		
		log.info("searchStudyUserInfo(컨트롤러) 끝");
		return new ResponseEntity<>(studyUser, HttpStatus.OK);
	}
	
	/**
	 * 출석형 과제를 제출한다.
	 * @param studyId
	 * @param session
	 * @return
	 * @throws AddException
	 */
	@PostMapping("homework/{studyId}")
	public ResponseEntity<?> homework(@PathVariable int studyId, HttpSession session) throws AddException {
		log.info("homework(컨트롤러) 시작: studyId: " + studyId);
		
		String email = (String) session.getAttribute("loginedId");
		studyService.checkTodayHomework(email, studyId);
		
		log.info("homework(컨트롤러) 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * Github형 과제를 제출한다.
	 * @param studyId
	 * @param session
	 * @return
	 * @throws AddException
	 */
	@PostMapping("githomework/{studyId}")
	public ResponseEntity<?> gitHomework(@PathVariable int studyId, HttpSession session) throws AddException {
		log.info("githomework(컨트롤러) 시작: studyId: " + studyId);
		
		String email = (String) session.getAttribute("loginedId");
		studyService.checkMyGithubCommit(email, studyId);
		
		log.info("githomework(컨트롤러) 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 스터디 목록을 조회한다.
	 * @param currentPage
	 * @param studyOption
	 * @return
	 * @throws FindException
	 */
	@GetMapping("list/{currentPage}")
	public ResponseEntity<?> searchStudy(@PathVariable int currentPage, @RequestBody StudyDTO studyOption) throws FindException {
		log.info("searchStudy(컨트롤러) 시작: currentPage: " + currentPage);
		
		PageBean<StudyDTO> studyList = studyService.getPageBeanAll(currentPage, studyOption);
		
		log.info("searchStudy(컨트롤러) 끝");
		return new ResponseEntity<>(studyList, HttpStatus.OK);
	}
	
	/**
	 * 진행중인 스터디를 검색한다.
	 * @param currentPage
	 * @param studyOption
	 * @param session
	 * @return
	 * @throws FindException
	 */
	@GetMapping("my/list/{currentPage}")
	public ResponseEntity<?> searchMyStudy(@PathVariable int currentPage, @RequestBody StudyDTO studyOption, HttpSession session) throws FindException {
		log.info("searchMyStudy(컨트롤러) 시작: currentPage: " + currentPage);
		
		String email = (String) session.getAttribute("loginedId");
		PageBean<StudyDTO> studyList = studyService.getPageBeanMy(currentPage, studyOption, email);
		
		log.info("searchMyStudy(컨트롤러) 끝");
		return new ResponseEntity<>(studyList, HttpStatus.OK);
	}
}
