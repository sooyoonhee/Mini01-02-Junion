package com.boot.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.boot.DTO.ResumeUploadDTO;

//실행시 매퍼파일을 읽어 들이도록 지정
@Mapper
public interface ResumeUploadDAO {  // 파일추가, 열람, 삭제
//	파일업로드는 파라미터를 DTO 사용
	public void resumeInsertFile(ResumeUploadDTO vo);  // 파라미터가 boardattachDTO / 파일추가
	public List<ResumeUploadDTO> resumeGetFileList(int resume_num); // int타입 boardNO파라미터를 list타입 <boardattachDTO> getFileList로 사용하려함 / 파일열람
	public void resumeDeleteFile(String resume_num);  // boardNO 맞춰서 파일삭제로직 / 파일삭제
	public void insertResumeImage(ResumeUploadDTO vo);
	public void deleteResumeImage(int resume_num);//이미지파일 하나만 삭제
}
















