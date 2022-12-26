package com.dogroup.repository;

import java.util.List;
import java.util.Map;

import com.dogroup.dto.WalletDTO;
import com.dogroup.exception.FindException;
import com.dogroup.exception.ModifyException;

public interface WalletRepository {
	/**
	 * 사용자의 지갑 목록을 반환한다.
	 * 
	 * @return 지갑 내역
	 * @throws FindException
	 */
	List<WalletDTO> selectWallet(int currentPage, int cntPerPage, String email) throws FindException;

	/**
	 * 
	 * @param email에 해당하는 사용자의 지갑에 돈을 충전함(새로운 거래내역생성, 현재날짜로그, 거래자분류, 카테고리분류, 금액 내역 생성)
	 * @param wallet User의 현재잔액을 업데이트함
	 * @throws Exception
	 */
	void updateUserBalance(WalletDTO wallet, int flag) throws ModifyException;

	/**
	 * 사용자의 지갑 총 갯수를 반환한다.
	 * @param email
	 * @return
	 * @throws FindException
	 */
	int selectWalletTotalCnt(String email) throws FindException;
	
	/**
	 * 사용자의 스터디 정산 보상 금액을 반환한다.
	 * @param studyId
	 * @param email
	 * @return
	 * @throws FindException 
	 */
	int selectWalletStudyEndPrize(int studyId, String email) throws FindException; 
}
