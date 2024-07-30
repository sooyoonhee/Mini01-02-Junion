package com.boot.DTO;

import lombok.Data;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class Criteria2 {
	private int pageNum; // 페이지번호
	private int amount; // 페이지당 글 갯수	
	private String user_email;
	
	private String type;//어디서 검색할지 제목,내용..
	private String keyword;//검색에 넣은 단어
	
	private String filter1;//notice list 정렬할 때 select 박스에서 선택되는 값 DTO에 필드만 넣음 (연주0728)
	private String orderBy;//notice list 정렬할 때 select 박스에서 선택되는 값 DTO에 필드만 넣음 (연주0728)
//	private String filter2;//notice list 정렬할 때 select 박스에서 선택되는 값 DTO에 필드만 넣음 (연주0728)
//	private String filter3;//notice list 정렬할 때 select 박스에서 선택되는 값 DTO에 필드만 넣음 (연주0728)
	
	public Criteria2() 
	{
		this(1, 5);
	}
	
	public Criteria2(int pageNum, int amount) 
	{
		this.pageNum = pageNum;
		this.amount = amount;
	}
	
}
