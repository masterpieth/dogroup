package com.dogroup.dto;

import java.util.Date;
import java.util.List;

/**
 * 과제 관련 DTO
 * @author Chanmin Sung
 *
 */
public class HomeworkDTO {
    private int studyId; 	        //스터디 ID
    private String userEmail;       //회원 ID
    private Date studySubmitDt; 	//제출 날짜
	
    public HomeworkDTO() {
		super();
	}

	public HomeworkDTO(int studyId, String userEmail, Date studySubmitDt) {
		this.studyId = studyId;
		this.userEmail = userEmail;
		this.studySubmitDt = studySubmitDt;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Date getStudySubmitDt() {
		return studySubmitDt;
	}

	public void setStudySubmitDt(Date studySubmitDt) {
		this.studySubmitDt = studySubmitDt;
	}
}
