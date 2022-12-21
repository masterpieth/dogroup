package com.dogroup.repository;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dogroup.dto.UserDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;

@Repository("userRepository")
public class UserRepositoryOracle implements UserRepository {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * 회원을 저장소에 추가한다.
	 * @param inputUser 회원의 가입 정보
	 * @throws AddException 회원 정보 추가 실패시 발생
	 */
	@Override
	public void insertUser(UserDTO inputUser) throws AddException {
		log.info("insertUser 시작: " + inputUser.getEmail() + "/" + inputUser.getName() + "/" + inputUser.getPassword());
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.insert("com.dogroup.mybatis.UserMapper.insertUser", inputUser);
		} catch(Exception e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("insertUser 끝");
		}
	}
	
	/**
	 * 이메일로 회원아이디에 해당하는 고객을 반환한다
	 * @param email 아이디
	 * @return 고객
	 * @throws FindException 아이디에 해당하는 고객이 없으면 FindException발생한다
	 * @throws SQLException
	 * @throws Exception
	 */
	@Override
	public UserDTO selectUserByEmail(String email) throws FindException {
		log.info("selectUserByEmail 시작 - email: " + email);
		SqlSession session = null;
		UserDTO result = null;
		try {
			session = sqlSessionFactory.openSession();
			result = session.selectOne("com.dogroup.mybatis.UserMapper.selectUserByEmail", email);
			if(result == null) {
				throw new FindException("결과를 찾지 못했습니다.");
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("selectUserByEmail 종료");
		}
	}
	
	/**
	 * 회원 탈퇴처리한다.
	 * @param email
	 * @throws ModifyException
	 */
	@Override
	public void updateUserStatus(String email) throws ModifyException {
		log.info("updateUserStatus 시작: email: " + email);
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.dogroup.mybatis.UserMapper.updateUserStatus", email);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("updateUserStatus 끝");
		}
	}
}
