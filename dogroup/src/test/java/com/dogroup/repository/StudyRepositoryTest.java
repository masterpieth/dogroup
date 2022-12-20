package com.dogroup.repository;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import com.dogroup.DogroupApplication;
import com.dogroup.config.MyApplicationContext;
import com.dogroup.dto.UserDTO;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;

@SpringBootTest
@ContextConfiguration(classes = {DogroupApplication.class, MyApplicationContext.class})
class StudyRepositoryTest {

	@Autowired
	StudyRepository studyRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Test
	void 금액환급() throws ModifyException, FindException {
		int studyId = 100;
		String email = "user1@gmail.com";
		int prize = 100;
		
		assertThrows(ModifyException.class, () -> studyRepository.refundToUser(studyId, email, prize));
		
		int studyId2 = 52;
		int prize2 = 1000;
		
		studyRepository.refundToUser(studyId, email, prize);
		UserDTO user = userRepository.selectUserByEmail(email);
		assertEquals(1001000, user.getUserBalance());
	}

}
