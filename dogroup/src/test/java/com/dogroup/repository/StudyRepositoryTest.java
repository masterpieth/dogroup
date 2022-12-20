package com.dogroup.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import com.dogroup.DogroupApplication;
import com.dogroup.config.MyApplicationContext;
import com.dogroup.dto.StudyDTO;
import com.dogroup.dto.StudySubjectDTO;
import com.dogroup.dto.StudyUserDTO;
import com.dogroup.dto.SubjectDTO;
import com.dogroup.dto.UserDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;

@SpringBootTest
@ContextConfiguration(classes = {DogroupApplication.class, MyApplicationContext.class})
class StudyRepositoryTest {

	@Autowired
	StudyRepository studyRepository;
	
	@Autowired
	UserRepository userRepository;
	
//	@Test
	void 금액환급() throws ModifyException, FindException {
		int studyId = 100;
		String email = "user1@gmail.com";
		int prize = 100;
		
		assertThrows(ModifyException.class, () -> studyRepository.refundToUser(studyId, email, prize));
		
		int studyId2 = 52;
		int prize2 = 1000;
		
		studyRepository.refundToUser(studyId2, email, prize2);
		UserDTO user = userRepository.selectUserByEmail(email);
		assertEquals(1003000, user.getUserBalance());
	}
	
	/**
	 * StudyRepo의 setUserDeligence 동작 테스트
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
	 * @author NYK
	 * @throws FindException 
	 */
//	@Test
//	void 스터디장넣기() {
//		String email = "user1@gmail.com";
//		StudyDTO study = new StudyDTO();
//		study.setStudyId(120);
//		study.setStudyFee(213);
//		
//		try {
//			studyRepository.insertStudyUserLeader(study);
//		} catch (AddException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
	
//	@Test
	void 스터디정보가져오기() throws FindException {
		int studyId = 52;
		StudyDTO study = studyRepository.selectStudyByStudyId(studyId);
		assertNotNull(study);
	}
}
