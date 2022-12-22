package com.dogroup.control;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dogroup.dto.WalletDTO;
import com.dogroup.exception.FindException;
import com.dogroup.service.WalletService;

/**
 * 지갑 관련 REST 컨트롤러
 * @author NYK
 *
 */
@RestController
@RequestMapping("wallet/*")
public class WalletRestController {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WalletService walletService;
	
	/**
	 * 지갑에 있는 돈을 충전한다.
	 * @param userEmail
	 * @param wallet
	 * @return
	 * @throws Exception
	 */
	@PostMapping("deposit")
	public ResponseEntity<?> deposit(@RequestBody WalletDTO wallet) throws Exception {
		log.info("deposit 시작");
		
		walletService.deposit(wallet);
		
		log.info("deposit 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 지갑에 있는 돈을 출금한다.
	 * @param userEmail
	 * @param wallet
	 * @return
	 * @throws Exception
	 */
	@PostMapping("withdraw")
	public ResponseEntity<?> withdraw(@RequestBody WalletDTO wallet) throws Exception {
		log.info("withdraw 시작");
		
		walletService.withdraw(wallet);
		
		log.info("withdraw 끝");
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 거래내역을 조회한다.
	 * @param currentPage
	 * @param session
	 * @return
	 * @throws FindException
	 */
	@GetMapping("list/{currentPage}")
	public ResponseEntity<?> walletList(@PathVariable int currentPage, HttpSession session) throws FindException {
		log.info("walletList 시작: currentPage: " + currentPage);
		
		String email = (String) session.getAttribute("loginedId");
		List<WalletDTO> walletList = walletService.searchTransactionListAll(email);
		
		log.info("walletList 끝");
		return new ResponseEntity<>(walletList, HttpStatus.OK);
	}
}
