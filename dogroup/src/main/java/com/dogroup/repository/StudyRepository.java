package com.dogroup.repository;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.dogroup.dto.HomeworkDTO;
import com.dogroup.dto.StudyDTO;
import com.dogroup.dto.StudyUserDTO;
import com.dogroup.dto.SubjectDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;
import com.dogroup.exception.RemoveException;

public interface StudyRepository {

	/**
	 * 과제를 테이블에 추가한다. 오늘날짜의 과제가 없으면 AddException을 터뜨린다.
	 * 
	 * @param email      회원Email
	 * @param studyId    스터디Id
	 * @param created_at 이벤트생성일자
	 * @throws AddException 데이터를 넣을 수 없을 때 발생하는 예외
	 */
	void insertHomeworkByEmail(String email, int studyId, Date created_at) throws AddException;

	/**
	 * 유저의 email로 진행한 (검색 조건에 해당하는)스터디 정보를 반환한다.
	 * 
	 * @param currentPage 현재 페이지
	 * @param cntPerPage  페이당 보여줄 스터디 개수
	 * @param studyOption 검색 조건을 담고있는 객체
	 * @param email       유저의 이메일
	 * @return 검색 조건에 해당하는 스터디 리스트
	 * @throws FindException
	 */
	List<StudyDTO> selectStudyByEmail(int currentPage, int cntPerPage, StudyDTO studyDTO, String email)
			throws FindException;

	/**
	 * 유저의 email로 진행한 (검색 조건에 해당하는)스터디의 갯수를 반환한다.
	 * 
	 * @param studyOption 검색 조건을 담고있는 객체
	 * @param email       유저의 이메일
	 * @return 검색 조건에 해당하는 스터디의 갯수
	 * @throws FindException
	 */
	int myStudyCount(StudyDTO studyDTO, String email) throws FindException;

	/**
	 * 스터디원의 email로 스터디원의 List<HomeworkDTO> 리스트 반환한다.
	 * 
	 * @param email
	 * @param sstudyId
	 * @return 스터디원의 과제 리스트
	 * @throws FindException 상세 조회중 오류시 발생
	 */
	List<HomeworkDTO> selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException;

	/**
	 * 스터디의 정보를 반환한다.
	 * 
	 * @param studyId
	 * @return 스터디의 기본 정보를 반환한다. 스터디의 현재 참여자수 + 스터디장의 성실도 + 스터디의 과목 정보
	 * @throws FindException 상세 조회중 오류시 발생
	 */
	StudyDTO selectStudyByStudyId(int studyId) throws FindException;

	/**
	 * 스터디에 해당하는 제출한 과제를 반환한다.
	 * 
	 * @param studyId 스터디아이디
	 * @return
	 * @throws FindException
	 */
	List<HomeworkDTO> selectHomeworkByStudyId(int studyId) throws FindException;

	/**
	 * 스터디를 Insert 한다
	 * 
	 * @param study 추가할 스터디 내용
	 * @throws AddException 추가 중 오류시 발생한다.
	 */
	void insertStudy(StudyDTO study) throws AddException;

	/**
	 * 스터디를 update한다
	 * 
	 * @param study 업데이트할 스터디 내용
	 * @throws ModiftException 업데이트 중 오류시 발생한다.
	 */
	void updateStudy(StudyDTO study) throws ModifyException;

	/**
	 * 스터디원을 Insert 한다 - 지갑 관련 추가-삭제 등을 담당하는 프로시저를 호출한다. flag 1: 스터디원 추가 flag 0:
	 * 스터디원 삭제
	 */
	void insertStudyUser(StudyDTO study, String email) throws AddException;

	/**
	 * 스터디장을 insert 한다 flag 1: 스터디원 추가 flag 0: 스터디원 삭제
	 */
	void insertStudyUserLeader(StudyDTO study, SqlSession session) throws AddException;

	/**
	 * 스터디에서 탈퇴한다 - StudyUser 테이블에서 정보를 제거한다.
	 * 
	 * @param study 스터디정보
	 * @param email 사용자이메일
	 * @throws RemoveException
	 */
	void deleteStudyUser(StudyDTO study, String email) throws RemoveException;

	/**
	 * 스터디 과목을 insert 한다
	 * 
	 * @param study    스터디 정보
	 * @param subjects 스터디과목들
	 * @param conn     insertStudy시의 connection
	 * @throws AddException 실패시 발생시킬 예외
	 */
	void insertStudySubject(StudyDTO study, SqlSession session) throws AddException;

	/**
	 * 검색 조건에 맞는 스터디 리스트를 반환한다.
	 * 
	 * @param currentPage 현재 페이지
	 * @param cntPerPage  페이지당 보여줄 스터디 갯수
	 * @param studyOption 검색 조건을 담고있는 객체
	 * @return 검색 조건에 맞는 스터디 리스트
	 * @throws FindException
	 */
	List<StudyDTO> selectStudy(int currentPage, int cntPerPage, StudyDTO studyOption) throws FindException;

	/**
	 * 검색 조건에 맞는 스터디의 갯수를 반환한다.
	 * 
	 * @param studyOption 검색 조건을 담고있는 객체
	 * @return 검색 조건에 맞는 스터디의 갯수
	 * @throws FindException
	 */
	int studyCount(StudyDTO studyOption) throws FindException;

	/**
	 * 회원의 성실도를 반환한다.
	 * 
	 * @param email
	 * @return 회원의 성실도
	 * @throws FindException
	 */
	int searchUserDeligence(String email) throws FindException;

	/**
	 * 회원의 성실도를 반영한다. (스터디 종료 성실도 결과 반영)
	 * 
	 * @param studyUser
	 * @throws ModifyException
	 */
	void setUserDeligence(StudyUserDTO studyUser) throws ModifyException;

	/**
	 * 회원에게 스터디 종료에 따른 금액을 환급한다.
	 * 
	 * @param email
	 * @param i
	 */
	void refundToUser(int studyId, String email, int prize) throws ModifyException;

	/**
	 * 스터디원 목록과 스터디원의 회원 정보를 반환한다.
	 * 
	 * @author kangb
	 * @param studyId
	 * @throws FindException
	 */
	List<StudyUserDTO> selectStudyUsersByStudyId(int studyId) throws FindException;

	/**
	 * 스터디ID에 해당하는 과목들을 삭제한다.
	 * 
	 * @param studyDTO 스터디 ID
	 * @throws RemoveException
	 */
	void deleteStudySubject(SqlSession session, int studyId) throws RemoveException;

	/**
	 * 스터디를 삭제한다. 스터디원, 스터디 과목 삭제 및 환불처리도 진행된다.
	 */
	void deleteStudy(int studyId) throws RemoveException;

	/**
	 * 회원의 이메일로 현재 진행중인 깃 방식 스터디를 조회한다.
	 * 
	 * @param email
	 * @throws FindException 스터디가 존재하면 FindException을 터뜨린다.
	 */
	void selectCurrentlyStudyByEmail(String email) throws FindException;

	/**
	 * 과목들을 모두 가져온다.
	 * 
	 * @return
	 * @throws FindException
	 */
	List<SubjectDTO> selectSubject() throws FindException;

	/**
	 * 스터디장이 지금까지 참여했던 스터디의 총 개수를 int타입으로 반환한다.
	 * 
	 * @return int(개수)
	 * @throws FindException
	 */
	int selectStudyLeaderFinishStudy(String email) throws FindException;
	
	/**
	 * 정산 작업 완료 후 스터디 paid 정보를 1로 변경한다.
	 * @param studyId
	 */
	void updateStudyPaid(int studyId);
}
