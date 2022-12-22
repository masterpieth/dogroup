package com.dogroup.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 과제 관련 DTO
 * @author Chanmin Sung
 *
 */
public class HomeworkDTO {
    private int studyId; 	        //스터디 ID
    private String email;       //회원 ID
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date studySubmitDt; 	//제출 날짜
	
    public HomeworkDTO() {
		super();
	}

	public HomeworkDTO(int studyId, String email, Date studySubmitDt) {
		this.studyId = studyId;
		this.email = email;
		this.studySubmitDt = studySubmitDt;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getStudySubmitDt() {
		return studySubmitDt;
	}

	public void setStudySubmitDt(Date studySubmitDt) {
		this.studySubmitDt = studySubmitDt;
	}

}
