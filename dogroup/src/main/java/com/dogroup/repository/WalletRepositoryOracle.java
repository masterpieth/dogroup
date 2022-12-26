package com.dogroup.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dogroup.dto.WalletDTO;
import com.dogroup.exception.AddException;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;

@Repository("walletRepository")
public class WalletRepositoryOracle implements WalletRepository {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	/**
	 * 사용자의 지갑 목록을 반환한다.
	 * @param		사용자 이메일
	 * @return		지갑 내역이 없으면 빈 list 객체, 오류가 나면 FindException
	 * @throws FindException
	 */
	@Override
	public List<WalletDTO> selectWallet(int currentPage, int cntPerPage, String email) throws FindException {
		log.info("selectWallet 시작");
		int startRow = currentPage * cntPerPage - cntPerPage + 1;
		int endRow = currentPage * cntPerPage;
		List<WalletDTO> list = null;
		SqlSession session = null;
		Map<String, Object> map = new HashMap<>();
		map.put("startRow", startRow);
		map.put("endRow", endRow);
		map.put("email", email);
		try {
			session = sqlSessionFactory.openSession();
			list = session.selectList("com.dogroup.mybatis.WalletMapper.selectWallet", map);
			if(list == null) {
				return new ArrayList<>();
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("selectWallet 끝");
		}
	}
	
	/**
	 * 
	 * @param email에  해당하는 사용자의 지갑에 돈을 충전하는 프로시저호출함
	 * @param balance 잔액을 업데이트함
	 * @throws Exception
	 */
	@Override
	public void updateUserBalance(WalletDTO wallet, int flag) throws ModifyException {
		
		log.info("updateUserBalance 시작: email" + wallet.toString() + "/ flag: " + flag);
		
		SqlSession session = null;
		Map<String, Object> map = new HashMap<>();
		map.put("flag", flag);
		map.put("email", wallet.getEmail());
		map.put("transactionUser", wallet.getTransactionUser());
		map.put("transactionCategory", 3);
		map.put("transactionMoney", wallet.getTransactionMoney());
		
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.dogroup.mybatis.WalletMapper.updateUserBalance", map);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("updateUserBalance 끝");
		}
	}

	@Override
	public int selectWalletTotalCnt(String email) throws FindException {
		log.info("selectWalletTotalCnt 시작: email" + email);
		
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.dogroup.mybatis.WalletMapper.selectWalletTotalCnt", email);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException("전체 개수 조회에 실패했습니다.");
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("selectWalletTotalCnt 끝");
		}
	}
	
	/**
	 * 사용자의 스터디 정산 보상 금액을 반환한다.
	 * @throws FindException 
	 */
	@Override
	public int selectWalletStudyEndPrize(int studyId, String email) throws FindException {
log.info("selectWalletStudyEndPrize 시작: email" + email);
		Map<String, Object> map = new HashMap<>();
		map.put("email", email);
		map.put("studyId", studyId);
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.dogroup.mybatis.WalletMapper.selectWalletStudyEndPrize", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException("정산 금액 조회에 실패했습니다.");
		} finally {
			if(session != null) {
				session.close();
			}
			log.info("selectWalletStudyEndPrize 끝");
		}
	}
}
