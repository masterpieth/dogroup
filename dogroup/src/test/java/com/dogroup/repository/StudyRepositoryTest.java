package com.dogroup.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.dogroup.DogroupApplication;
import com.dogroup.config.MyApplicationContext;
import com.dogroup.dto.HomeworkDTO;
import com.dogroup.dto.StudyDTO;
import com.dogroup.dto.StudyUserDTO;
import com.dogroup.dto.UserDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;
import com.dogroup.exception.RemoveException;

@SpringBootTest
@ContextConfiguration(classes = { DogroupApplication.class, MyApplicationContext.class })
class StudyRepositoryTest {

	@Autowired
	StudyRepository studyRepository;

	@Autowired
	UserRepository userRepository;

	
	/**
	 * @author NYK
	 * @throws ModifyException
	 * @throws FindException
	 */
	// @Test
	void 금액환급() throws ModifyException, FindException {
		int studyId = 100;
		String email = "user1@gmail.com";
		int prize = 100;

		assertThrows(ModifyException.class, () -> studyRepository.refundToUser(studyId, email, prize));

		int studyId2 = 52;
		int prize2 = 1000;

		studyRepository.refundToUser(studyId2, email, prize2);

		studyRepository.refundToUser(studyId, email, prize);

		UserDTO user = userRepository.selectUserByEmail(email);
		assertEquals(1003000, user.getUserBalance());
	}

	
	/**
	 * StudyRepo의 setUserDeligence 동작 테스트
	 * 
	 * @author NYK
	 * @throws FindException
	 * @throws ModifyException
	 */
	//@Test
	void 성실도수정() throws FindException, ModifyException {
		String email = "user1@gmail.com";

		StudyUserDTO su = new StudyUserDTO();
		su.setDiligence(70);
		su.setEmail(email);

		studyRepository.setUserDeligence(su);

		UserDTO user2 = userRepository.selectUserByEmail(email);
		assertEquals(70, user2.getDiligence());
	}

	/**
	 * StudyRepo의 searchUserDeligence 테스트
	 * 
	 * @author NYK
	 * @throws FindException
	 */
//	@Test
	void 성실도조회() throws FindException {
		String email = "user1@gmail.com";
		int diligence = studyRepository.searchUserDeligence(email);
		assertEquals(70, diligence);
	}

	
	/**
	 * selectStudyByStudyId Test
	 * @author NYK
	 * @throws FindException
	 */
//	@Test
	void 스터디정보가져오기() throws FindException {
		int studyId = 55;
		StudyDTO study = studyRepository.selectStudyByStudyId(studyId);
		assertNotNull(study);
	}

	
	/**
	 * @author kangb
	 * @return 스터디원목록과 스터디원의 회원정보를 반환하는 Test Case
	 * @throws FindException
	 */
//	 @Test
	void studyUsersTest() throws FindException {
		int studyId = 52;
		List<StudyUserDTO> list = studyRepository.selectStudyUsersByStudyId(studyId);
		assertNotNull(list);
		assertEquals(1, list.size());

	}

//	@Test
	
	/**
	 * @author kangb
	 * @throws AddException
	 */
	void insertHomeworkByEmail() throws AddException {
		String email = "user5@gmail.com";
		int studyId = 54;
		Date created_at = new Date();
		studyRepository.insertHomeworkByEmail(email, studyId, created_at);
		assertNotNull(created_at);

	}

	
	//@Test
	void 스터디삭제() {
		try {
			studyRepository.deleteStudy(79);
		} catch (RemoveException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	/**
	 * @author KTH
	 * @throws FindException
	 * @throws ParseException
	 */
	//@Test
	void studyCount() throws FindException, ParseException {
		StudyDTO study = new StudyDTO();
		study.setStudyTitle("스");
		UserDTO leader = new UserDTO();
		study.setStudyLeader(leader);
		study.getStudyLeader().setEmail("@");
		study.setStudySize(7);
		study.setStudyDiligenceCutline(1000);
		study.setStudyFee(10000);
		String strDate = "20220101";
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
		Date formatDate = dtFormat.parse(strDate);
		study.setStudyStartDate(formatDate);
		strDate = "20231231";
		dtFormat = new SimpleDateFormat("yyyyMMdd");
		formatDate = dtFormat.parse(strDate);
		study.setStudyEndDate(formatDate);
		studyRepository.studyCount(study);
	}

	/**
	 * @author Chanmin
	 * @throws FindException
	 */
	//@Test
	void 스터디의모든과제가져오기() throws FindException {
		int studyId = 61;
		List<HomeworkDTO> list = studyRepository.selectHomeworkByStudyId(studyId);
		assertNotNull(list);
	}
	
	/**
	 * @author Chanmin
	 * @throws FindException
	 */
	//@Test
	void 검색조건과회원의이메일로스터디갯수조회하기() throws FindException, ParseException {
		String email = "user8@gmail.com";
		
		StudyDTO study = new StudyDTO();
		//study.setStudyTitle("스");
		UserDTO leader = new UserDTO();
		study.setStudyLeader(leader);
		study.getStudyLeader().setEmail("@");
		//study.setStudySize(7);
		//study.setStudyDiligenceCutline(1000);
		//study.setStudyFee(10000);
		
		String strDate = "20220101";
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
		Date formatDate = dtFormat.parse(strDate);
		study.setStudyStartDate(formatDate);
		
	//	strDate = "20231231";
	//	dtFormat = new SimpleDateFormat("yyyyMMdd");
	//	formatDate = dtFormat.parse(strDate);
	//	study.setStudyEndDate(formatDate);
		int Cnt = studyRepository.myStudyCount(study, email);
		assertNotNull(Cnt);
	}
	
	/**
	 * @author Chanmin
	 * @throws FindException
	 */
	@Test
	void 검색조건과회원의이메일로스터디목록조회하기() throws ParseException, FindException {
	String email = "user8@gmail.com";
		
	StudyDTO study = new StudyDTO();
	//	study.setStudyTitle("스");
	//	UserDTO leader = new UserDTO();
	//	study.setStudyLeader(leader);
	//	study.getStudyLeader().setEmail("@");
	//	study.setStudySize(8);
	//	study.setStudyDiligenceCutline(1000);
	//	study.setStudyFee(10000);
		
	//	String strDate = "20220101";
	//	SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
	//	Date formatDate = dtFormat.parse(strDate);
	//	study.setStudyStartDate(formatDate);
	//	
	//	strDate = "20231231";
	//	dtFormat = new SimpleDateFormat("yyyyMMdd");
	//	formatDate = dtFormat.parse(strDate);
	//	study.setStudyEndDate(formatDate);
		List<StudyDTO> list = studyRepository.selectStudyByEmail(1, 5, study, email);
		assertNotNull(list);
	}
}