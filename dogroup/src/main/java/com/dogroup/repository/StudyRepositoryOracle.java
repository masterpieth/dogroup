package com.dogroup.repository;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dogroup.dto.HomeworkDTO;
import com.dogroup.dto.StudyDTO;
import com.dogroup.dto.StudySubjectDTO;
import com.dogroup.dto.StudyUserDTO;
import com.dogroup.dto.SubjectDTO;
import com.dogroup.dto.UserDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;
import com.dogroup.exception.RemoveException;
import com.my.sql.MyConnection;

@Repository("studyRepository")
public class StudyRepositoryOracle implements StudyRepository {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * 회원의 이메일로 진행된 (검색 조건에 맞는)스터디 정보를 반환한다.
	 */
	@Override
	public List<StudyDTO> selectStudyByEmail(int currentPage, int cntPerPage, StudyDTO studyOption, String email) throws FindException {
	Connection conn = null;
	PreparedStatement preStmt = null;
	ResultSet rs = null;
	List<StudyDTO> list = new ArrayList<>();
	String studyStudySQL = null;
	int startRow = currentPage * cntPerPage - cntPerPage + 1;
	int endRow = currentPage * cntPerPage;
	try {
		conn = MyConnection.getConnection();
		studyStudySQL = "SELECT st.*, s.subject_code, s.subject_name, s.subject_parent_code,(SELECT user_diligence FROM users WHERE user_email = st.user_email) diligence FROM "
						+ "(SELECT * FROM "
						+ "(SELECT rownum rn, a.* FROM "
						+ "(SELECT * FROM study "
						+ "WHERE study_title Like ? "
						+ "AND user_email Like ? "
						+ "AND study_size = ? "
						+ "AND study_diligence_cutline <= ? "
						+ "AND study_fee <= ? "
						+ "AND study_start_date >= ? "
						+ "AND study_end_date <= ? "
						+ "AND study_id in (select study_id from study_users WHERE user_email = ?) "
						+ "order by study_id) a "
						+ ")WHERE rn BETWEEN ? AND ?) st "
						+ "JOIN study_subject ss ON st.study_id = ss.study_id "
						+ "JOIN subject s ON  ss.subject_code = s.subject_code";
		preStmt = conn.prepareStatement(studyStudySQL);
		preStmt.setString(1, "%" + studyOption.getStudyTitle()+ "%");
		preStmt.setString(2, "%" + studyOption.getStudyLeader().getEmail() + "%");
		preStmt.setInt(3, studyOption.getStudySize());
		preStmt.setDouble(4, studyOption.getStudyDiligenceCutline());
		preStmt.setInt(5, studyOption.getStudyFee());
		preStmt.setDate(6, new java.sql.Date(studyOption.getStudyStartDate().getTime()));
		preStmt.setDate(7, new java.sql.Date(studyOption.getStudyEndDate().getTime()));
		preStmt.setString(8, email);
		preStmt.setInt(9, startRow);
		preStmt.setInt(10, endRow);
		
		int oldRN = 0;
		int newRN = 0;
		StudyDTO study = null;
		StudySubjectDTO studySubject = null;
		List<StudySubjectDTO> studySubjectList = null;
		SubjectDTO subject = null;
		SubjectDTO parentSubject = null;
		
		rs = preStmt.executeQuery();
		while (rs.next()) {
			newRN = rs.getInt("rn");	
			if(oldRN != newRN) { //같은 스터디를 가르키는지 확인(과목 정보만 바뀐 같은 스터디인가?)
				study = new StudyDTO();
				//스터디의 기본정보				
				study.setStudyId(rs.getInt("study_id"));
				study.setStudyTitle(rs.getString("study_title"));
				study.setStudyCertification(rs.getInt("study_certification"));
				study.setStudySize(rs.getInt("study_size"));
				study.setStudyFee(rs.getInt("study_fee"));
				study.setStudyDiligenceCutline(rs.getInt("study_diligence_cutline"));
				study.setStudyHomeworkPerWeek(rs.getInt("study_homework_per_week"));
				study.setStudyPostDate(rs.getDate("study_post_date"));
				study.setStudyStartDate(rs.getDate("study_start_date"));
				study.setStudyEndDate(rs.getDate("study_end_date"));
				study.setStudyContent(rs.getClob("study_content"));
				study.setStudyPaid(rs.getInt("study_paid"));
				study.setStudyGatheredSize(rs.getInt("study_gathered_size"));
				//스터디장의 정보
				UserDTO u = new UserDTO();
				u.setDiligence(rs.getInt("diligence"));
				u.setEmail(rs.getString("user_email"));
				study.setStudyLeader(u);
				//스터디의 과목정보
				studySubjectList = new ArrayList<>();
				studySubject = new StudySubjectDTO(study.getStudyId(), null);
				subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
				parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
				subject.setSubjectParent(parentSubject);
				studySubject.setSubject(subject);
				studySubjectList.add(studySubject);
				study.setSubjects(studySubjectList);
				//리스트에 추가
				list.add(study);
				oldRN = newRN;
			} else {
				//스터디의 과목정보만 추가
				studySubject = new StudySubjectDTO(study.getStudyId(), null);
				subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
				parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
				subject.setSubjectParent(parentSubject);
				study.getSubjects().add(studySubject);
			}
		}
		return list;
	} catch (Exception e) {
		e.printStackTrace();
		throw new FindException(e.getMessage());
	} finally {
		MyConnection.close(rs, preStmt, conn);
	}
}
	
	/**
	 * 회원의 이메일로 진행된 (검색 조건에 맞는)스터디의 갯수를 반환한다.
	 */
	@Override
	public int myStudyCount(StudyDTO studyOption, String email) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String studyStudyCountSQL = null;
		try {
			conn = MyConnection.getConnection();
			studyStudyCountSQL = "SELECT count(*) FROM study "
								+ "WHERE study_title Like ? "
								+ "AND user_email Like ? "
								+ "AND study_size = ? "
								+ "AND study_diligence_cutline <= ? "
								+ "AND study_fee <= ? "
								+ "AND study_start_date >= ? "
								+ "AND study_end_date <= ? "
								+ "AND study_id in (select study_id from study_users WHERE user_email = ?)";
			preStmt = conn.prepareStatement(studyStudyCountSQL);
			preStmt.setString(1, "%" + studyOption.getStudyTitle()+ "%");
			preStmt.setString(2, "%" + studyOption.getStudyLeader().getEmail() + "%");
			preStmt.setInt(3, studyOption.getStudySize());
			preStmt.setDouble(4, studyOption.getStudyDiligenceCutline());
			preStmt.setInt(5, studyOption.getStudyFee());
			preStmt.setDate(6, new java.sql.Date(studyOption.getStudyStartDate().getTime()));
			preStmt.setDate(7, new java.sql.Date(studyOption.getStudyEndDate().getTime()));
			preStmt.setString(8, email);
			rs = preStmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
	
	/**
	 * 스터디의 현재 정보를 반환한다.
	 */
	@Override
	public StudyDTO selectStudyByStudyId(int studyId) throws FindException {
		log.info("selectStudyByStudyId 시작: studyId: " + studyId);
		SqlSession session = null;
		StudyDTO study = null;
		try {
			session = sqlSessionFactory.openSession();
			study = session.selectOne("com.dogroup.mybatis.StudyMapper.selectStudyByStudyId", studyId);
			if(study == null) {
				throw new FindException("정보 조회에 실패했습니다.");
			}
			return study;

		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("selectStudyByStudyId 끝");
		}
	}

	/**
	 * 스터디원 목록과 스터디원의 회원 정보를 반환한다.
	 * 
	 * @author kangb MyBatis로 converting완료
	 */
	@Override
	public List<StudyUserDTO> selectStudyUsersByStudyId(int studyId) throws FindException {
		log.info("studyUsers 시작 : studyId: " + studyId);
		List<StudyUserDTO> studyUserList = new ArrayList<>();
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			studyUserList = session.selectList("com.dogroup.mybatis.StudyMapper.studyUsers", studyId);
			return studyUserList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("selectStudyByStudyId 끝");
		}

	}

	/**
	 * 스터디의 모든 과제 제출 내역을 리스트로 반환한다. selectStudy스터디 목록조회(회원용) study, study_users,
	 * study_subject, subject테이블사용 현재 해당 스터디에 참가중인 회원수를 포함하여 보여줌 하나의 studyId로 검색했을때
	 * 서로다른 스터디과목명은 3개가 온다.
	 */
	@Override
	public List<HomeworkDTO> selectHomeworkByStudyId(int studyId) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		List<HomeworkDTO> homeworkList = new ArrayList<>();
		String homeworListSQL = "select * from homework where " + "study_id = ? order by user_email, study_submit_dt";
		try {
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(homeworListSQL);
			preStmt.setInt(1, studyId);
			rs = preStmt.executeQuery();

			while (rs.next()) {
				String userEmail = rs.getString("user_email");
				Date studySubmitDt = rs.getDate("study_submit_dt");
				HomeworkDTO homework = new HomeworkDTO();
				homework.setUserEmail(userEmail);
				homework.setStudySubmitDt(studySubmitDt);
				homeworkList.add(homework);
			}
			return homeworkList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}

	/**
	 * 스터디 유저의(개인) 모든 과제 제출 내역을 반환한다. 내역이 없으면 빈 리스트를 반환한다.
	 * 
	 * @parama userEmail 유저 email
	 * @param studyId 스터디 ID
	 * @return 과제 리스트
	 */
	@Override
	public List<HomeworkDTO> selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException {
		log.info("selectUserHomeworkByEmail 시작 userEmail: " + userEmail + " / studyId:" + studyId);
		SqlSession session = null;
		List<HomeworkDTO> list = null;

		Map<String, Object> map = new HashMap<>();
		map.put("email", userEmail);
		map.put("studyId", studyId);
		try {
			session = sqlSessionFactory.openSession();
			list = session.selectList("selectUserHomeworkByEmail", map);
			if (list == null) {
				return new ArrayList<>();
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("selectUserHomeworkByEmail 끝");
		}
	}

    /**
     * 스터디회원의 과제를 insert한다
     * @author kangb
     * MyBatis로 converting완료
     */
    @Override
    public void insertHomeworkByEmail(String email, int studyId, Date created_at) throws AddException {
        log.info("insertHomeworkByEmail 시작 email: " + email + "studyId: "+ studyId +"created_at: "+ created_at);
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("studyId", studyId);
            map.put("created_at", created_at);
            session.insert("com.dogroup.mybatis.StudyMapper.insertHomeworkByEmail", map);

        } catch (Exception e) {
            e.printStackTrace();
            throw new AddException("HomeWork Insert 실패");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
	/**
	 * 스터디를 Insert 한다.
	 * @throws AddException 
	 */
	@Override
	@Transactional(rollbackFor = AddException.class)
	public void insertStudy(StudyDTO study) throws AddException {
		log.info("insertStudy 시작");
		
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.insert("com.dogroup.mybatis.StudyMapper.insertStudy", study);
			
			insertStudyUserLeader(study, session);
			
			insertStudySubject(study, session);
		}catch (Exception e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("insertStudy 끝");
		}
	}
	
	/**
	 * 과목을 삭제한다.
	 * @param session
	 * @param study
	 * @throws RemoveException
	 */
	public void deleteStudySubject( SqlSession session, int studyId) throws RemoveException {
		session.delete("com.dogroup.mybatis.StudyMapper.deleteStudySubject", studyId);

	}

	/**
	 * 과목삭제와 수정을 동시에 트랜잭션처리한다. 스터디의 과목정보를 update한다.
	 * 
	 * @author kangb
	 */
	@Override
	@Transactional(rollbackFor = ModifyException.class)
	public void updateStudy(StudyDTO study) throws ModifyException {
		log.info("updateStudy 시작");
		SqlSession session = null;

		try {
			session = sqlSessionFactory.openSession();
			
			StudyDTO dto = new StudyDTO();
			dto.setStudyId();
			// 스터디과목 삭제하기
			deleteStudySubject(session, dto.getStudyId());
			
			//스터디과목 insert하기
			List<StudySubjectDTO> subjectList = new ArrayList<>();
			subjectList.addAll(study.getSubjects());

			for (StudySubjectDTO ssd : study.getSubjects()) {
				ssd.setStudyId(study.getStudyId());
			}
			session.insert("com.dogroup.mybatis.StudyMapper.insertStudySubject", study.getSubjects());
			insertStudySubject(study, session);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());

		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	/**
	 * 스터디장을 insert 한다 - 지갑 관련 추가-삭제 등을 담당하는 프로시저를 호출한다. flag 1: 스터디원 추가 flag 0:
	 * 스터디원 삭제
	 * 
	 * @param session insertStudy와 공유하는 session
	 */
	@Override
	public void insertStudyUserLeader(StudyDTO study, SqlSession session) throws AddException {
		log.info("insertStudyUserLeader 시작: studyId: " + study.getStudyId() + " / email: "
				+ study.getStudyLeader().getEmail());

		Map<String, Object> map = new HashMap<>();
		map.put("flag", 1);
		map.put("email", study.getStudyLeader().getEmail());
		map.put("studyId", study.getStudyId());
		map.put("transactionUser", null);
		map.put("transactionCategory", 2);
		map.put("transactionMoney", study.getStudyFee());
		try {
			session.update("com.dogroup.mybatis.StudyMapper.procWallet", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		}
		log.info("insertStudyUserLeader 끝");
	}

	/**
	 * 스터디원을 Insert 한다 - 지갑 관련 추가-삭제 등을 담당하는 프로시저를 호출한다. flag 1: 스터디원 추가 flag 0:스터디원
	 * 삭제
	 */
	@Override
	public void insertStudyUser(StudyDTO study, String email) throws AddException {
		log.info("insertStudyUser 시작: studyID: " + study.getStudyId() + "/ email: " + email);
		SqlSession session = null;
		Map<String, Object> map = new HashMap<>();
		map.put("flag", 1);
		map.put("email", email);
		map.put("studyId", study.getStudyId());
		map.put("transactionUser", null);
		map.put("transactionCategory", 2);
		map.put("transactionMoney", study.getStudyFee());
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.dogroup.mybatis.StudyMapper.procWallet", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("insertStudyUser 끝");
		}
	}

	/**
	 * 스터디에서 탈퇴한다 - StudyUser 테이블에서 정보를 제거한다. - 지갑 관련 추가-삭제 등을 담당하는 프로시저를 호출한다. flag
	 * 1: 스터디원 추가 flag 0:스터디원 삭제
	 */
	@Override
	public void deleteStudyUser(StudyDTO study, String email) throws RemoveException {
		log.info("deleteStudyUser 시작: studyID: " + study.getStudyId() + "/ email: " + email);
		SqlSession session = null;
		Map<String, Object> map = new HashMap<>();
		map.put("flag", 0);
		map.put("email", email);
		map.put("studyId", study.getStudyId());
		map.put("transactionUser", null);
		map.put("transactionCategory", 5);
		map.put("transactionMoney", study.getStudyFee());
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.dogroup.mybatis.StudyMapper.procWallet", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoveException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("deleteStudyUser 끝");
		}
	}

	/**
	 * 스터디 과목을 insert 한다
	 */
	@Override
	public void insertStudySubject(StudyDTO study, SqlSession session) throws AddException {
		log.info("insertStudySubject 시작");
		try {
			for (StudySubjectDTO ssd : study.getSubjects()) {
				ssd.setStudyId(study.getStudyId());
			}
			session.insert("com.dogroup.mybatis.StudyMapper.insertStudySubject", study.getSubjects());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("insertStudySubject 끝");
		}
	}

	/**
	 * 검색 조건에 맞는 스터디 리스트를 반환한다.
	 */
	@Override
	public List<StudyDTO> selectStudy(int currentPage, int cntPerPage, StudyDTO studyOption) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		List<StudyDTO> list = new ArrayList<>();
		String studyStudySQL = null;
		int startRow = currentPage * cntPerPage - cntPerPage + 1;
		int endRow = currentPage * cntPerPage;
		try {
			conn = MyConnection.getConnection();
			studyStudySQL = "SELECT st.*, s.subject_code, s.subject_name, s.subject_parent_code,"
							+ "(SELECT user_diligence FROM users WHERE user_email = st.user_email) diligence FROM "
							+ "(SELECT * FROM "
							+ "(SELECT rownum rn, a.* FROM "
							+ "(SELECT * FROM study WHERE "
							+ "study_title Like ? "
							+ "AND user_email Like ? "
							+ "AND study_size = ? "
							+ "AND study.STUDY_DILIGENCE_CUTLINE <= ? "
							+ "AND study_fee <= ?"
							+ "AND study_start_date >= ? "
							+ "AND study_end_date <= ? "
							+ "order by study_id) a "
							+ ")WHERE rn BETWEEN ? AND ?) st "
							+ "JOIN study_subject ss ON st.study_id = ss.study_id "
							+ "JOIN subject s ON  ss.subject_code = s.subject_code";
			preStmt = conn.prepareStatement(studyStudySQL);
			preStmt.setString(1, "%" + studyOption.getStudyTitle()+ "%");
			preStmt.setString(2, "%" + studyOption.getStudyLeader().getEmail() + "%");
			preStmt.setInt(3, studyOption.getStudySize());
			preStmt.setDouble(4, studyOption.getStudyDiligenceCutline());
			preStmt.setInt(5, studyOption.getStudyFee());
			preStmt.setDate(6, new java.sql.Date(studyOption.getStudyStartDate().getTime()));
			preStmt.setDate(7, new java.sql.Date(studyOption.getStudyEndDate().getTime()));
			preStmt.setInt(8, startRow);
			preStmt.setInt(9, endRow);
			
			int oldRN = 0;
			int newRN = 0;
			StudyDTO study = null;
			StudySubjectDTO studySubject = null;
			List<StudySubjectDTO> studySubjectList = null;
			SubjectDTO subject = null;
			SubjectDTO parentSubject = null;
			
			rs = preStmt.executeQuery();
			while (rs.next()) {
				newRN = rs.getInt("rn");	
				if(oldRN != newRN) { //같은 스터디를 가르키는지 확인(과목 정보만 바뀐 같은 스터디인가?)
					study = new StudyDTO();
					//스터디의 기본정보				
					study.setStudyId(rs.getInt("study_id"));
					study.setStudyTitle(rs.getString("study_title"));
					study.setStudyCertification(rs.getInt("study_certification"));
					study.setStudySize(rs.getInt("study_size"));
					study.setStudyFee(rs.getInt("study_fee"));
					study.setStudyDiligenceCutline(rs.getInt("study_diligence_cutline"));
					study.setStudyHomeworkPerWeek(rs.getInt("study_homework_per_week"));
					study.setStudyPostDate(rs.getDate("study_post_date"));
					study.setStudyStartDate(rs.getDate("study_start_date"));
					study.setStudyEndDate(rs.getDate("study_end_date"));
					study.setStudyContent(rs.getClob("study_content"));
					study.setStudyPaid(rs.getInt("study_paid"));
					study.setStudyGatheredSize(rs.getInt("study_gathered_size"));
					//스터디장의 정보
					UserDTO u = new UserDTO();
					u.setDiligence(rs.getInt("diligence"));
					u.setEmail(rs.getString("user_email"));
					study.setStudyLeader(u);
					//스터디의 과목정보
					studySubjectList = new ArrayList<>();
					studySubject = new StudySubjectDTO(study.getStudyId(), null);
					subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
					parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
					subject.setSubjectParent(parentSubject);
					studySubject.setSubject(subject);
					studySubjectList.add(studySubject);
					study.setSubjects(studySubjectList);
					//리스트에 추가
					list.add(study);
					oldRN = newRN;
				} else {
					//스터디의 과목정보만 추가
					studySubject = new StudySubjectDTO(study.getStudyId(), null);
					subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
					parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
					subject.setSubjectParent(parentSubject);
					study.getSubjects().add(studySubject);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}

	/**
	 * 검색 조건에 맞는 스터디 개수를 카운트하여 반환한다.
	 */
	@Override
	public int studyCount(StudyDTO studyDTO) throws FindException {
		log.info("studyCount  시작");
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			int count = session.selectOne("studyCount",studyDTO);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		log.info("studyCount 종료");
		}
	}

	/**
	 * 회원의 성실도를 반환한다.
	 * 
	 * @throws FindException
	 */
	@Override
	public int searchUserDeligence(String email) throws FindException {
		log.info("searchUserDeligence 시작 email: " + email);

		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			int diligence = session.selectOne("com.dogroup.mybatis.StudyMapper.searchUserDeligence", email);
			return diligence;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("searchUserDeligence 끝");
		}
	}

	/**
	 * 회원의 성실도를 반영한다. (스터디 종료 성실도 결과 반영)
	 * 
	 * @throws FindException
	 */
	@Override
	public void setUserDeligence(StudyUserDTO studyUser) throws ModifyException {
		log.info("setUserDeligence 시작 email: " + studyUser.getEmail());

		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.dogroup.mybatis.StudyMapper.setUserDeligence", studyUser);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("setUserDeligence 끝");
		}
	}

	/**
	 * 회원에게 스터디 종료에 따른 금액을 환급한다.
	 */
	@Override
	public void refundToUser(int studyId, String email, int prize) throws ModifyException {
		log.info("refundToUser 시작 studyId : " + studyId + "/ email: " + email + "/ prize: " + prize);

		SqlSession session = null;

		Map<String, Object> map = new HashMap<>();
		map.put("studyId", studyId);
		map.put("email", email);
		map.put("prize", prize);

		try {
			session = sqlSessionFactory.openSession();
			session.update("com.dogroup.mybatis.StudyMapper.refundToUser", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
			log.info("refundToUser 끝");
		}
	}

	/**
	 * 스터디를 삭제한다. 스터디원, 스터디 과목 삭제 및 환불처리도 진행된다.
	 */
	@Override
	@Transactional(rollbackFor = {RemoveException.class})
	public void deleteStudy(int studyId) throws RemoveException {
		log.info("deleteStudy 시작 studyId: " + studyId);
		
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			StudyDTO study = selectStudyByStudyId(studyId);
			List<StudyUserDTO> list = selectStudyUsersByStudyId(studyId);
			
			//스터디원탈퇴처리
			for(StudyUserDTO su : list) {
				deleteStudyUser(study, su.getEmail());
			}
			//과목삭제
			session.delete("com.dogroup.mybatis.StudyMapper.deleteStudySubject", studyId);
			//스터디삭제: 외래키때문에 delete가 불가함, 대신 size를 -1로 변경
			session.delete("com.dogroup.mybatis.StudyMapper.updateStudyStatus", studyId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoveException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("deleteStudy 끝");
		}
	}
}
