package com.dogroup.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.dogroup.DogroupApplication;
import com.dogroup.config.MyApplicationContext;
import com.dogroup.dto.StudyDTO;
import com.dogroup.dto.StudySubjectDTO;
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

//	@Test

	// @Test
	/**
	 * @author NYK
	 * @throws ModifyException
	 * @throws FindException
	 */

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
//	@Test
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
	 * StudyRepo의 insertStudySubject 테스트
	 * 
	 * @author NYK
	 * @throws AddException
	 */
//	@Test
//	void 스터디과목넣기() {
//		List<StudySubjectDTO> list = new ArrayList<>();
//		for(int i=1; i<=3; i++) {
//			StudySubjectDTO dto = new StudySubjectDTO(52, new SubjectDTO("D000" + i, null, null));
//			list.add(dto);
//		}
//		try {
//			studyRepository.insertStudySubject(list, null);
//		} catch (AddException e) {
//			e.printStackTrace();
//			fail();
//		} 
//	}

	/**
	 * insertStudyUserLeader 테스트
	 * 
	 * @author NYK
	 * @throws FindException
	 */
//	@Test
//	void 스터디장넣기() {
//		String email = "user1@gmail.com";
//		StudyDTO study = new StudyDTO();
//		study.setStudyId(120);
//		study.setStudyFee(213);
		
//		try {
//			studyRepository.insertStudyUserLeader(study);
//		} catch (AddException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}


	/**
	 * selectStudyByStudyId Test
	 * @author NYK
	 * @throws FindException
	 */
//	@Test

	
	//@Test

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




	//@Test
	/**Homework를 insert하는 Test Case

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
	
	/**
	 * @author kangb
	 * 스터디ID에 해당하는 과목들을 삭제하는 Test Case
	 */
	/*
	@Test
	 void deleteStudySubject() throws RemoveException{
		SqlSession session = SqlSessionFactory
		studyRepository.deleteStudySubject(session, 80);		
	}
	*/
	
	//@Test
	void 스터디삭제() {
		try {
			studyRepository.deleteStudy(79);
		} catch (RemoveException e) {
			e.printStackTrace();
			fail();
		}
	}

	
}
