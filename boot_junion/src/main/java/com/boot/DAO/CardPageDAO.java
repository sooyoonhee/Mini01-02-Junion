package com.boot.DAO;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.boot.DTO.ComNoticeAttachDTO;
import com.boot.DTO.ComNoticeDTO;
import com.boot.DTO.Criteria4;
import com.boot.DTO.Standard;

@Mapper
public interface CardPageDAO {
//	public ArrayList<ComNoticeDAO> cardPageList(String orderType, Standard std);// 페이징을 위한 메소드
	public ArrayList<ComNoticeDTO> cardPageList(@Param("orderType") String orderType, @Param("std") Standard std);// 페이징 + 필터링
//	public ArrayList<ComNoticeDAO> cardPageList(CardPageDAO dao);// 페이징을 위한 메소드
//	public int getTotalCount();//전체 공고 수 구하기
	public int getTotalCount(Standard std);//전체 공고 수 구하기
//	public int getTotalCount(@Param("std") Standard std);//전체 공고 수 구하기
	
	public ArrayList<String> getCareerList(); // 경력 리스트
	public ArrayList<String> getStackList(); // 기술 스킬 리스트
	public ArrayList<String> getLocationList(); // 지역 리스트
	
	public List<ComNoticeAttachDTO> cardPageFileList(int notice_num); // 2024-08-01 지수 (공고 목록 사진 들고오기)
}
