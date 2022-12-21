package com.dogroup.repository;

import java.sql.SQLException;

import com.dogroup.dto.UserDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;

public interface UserRepository {
	
	/**
	 * 회원을 저장소에 추가한다.
	 * @param inputUser 회원의 가입 정보
	 * @throws AddException 회원 정보 추가 실패시 발생
	 */
	void insertUser(UserDTO inputUser) throws AddException;

	/**
	 * 이메일로 회원아이디에 해당하는 고객을 반환한다
	 * @param email 아이디
	 * @return 고객
	 * @throws FindException 아이디에 해당하는 고객이 없으면 FindException발생한다
	 * @throws SQLException
	 * @throws Exception
	 */
	UserDTO selectUserByEmail(String email) throws FindException;
	
	/**
	 * 회원 탈퇴처리한다.
	 * @param email
	 * @throws ModifyException
	 */
	void updateUserStatus(String email) throws ModifyException;
	
	/**
	 * 회원 정보를 업데이트 한다(비밀번호 또는 이름)
	 * @param userDTO
	 * @throws ModifyException
	 */
	void updateUser(UserDTO userDTO) throws ModifyException;
}
