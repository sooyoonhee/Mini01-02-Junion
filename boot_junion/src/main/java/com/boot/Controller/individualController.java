package com.boot.Controller;

import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.boot.DAO.recentNoticeDAO;
import com.boot.DTO.BoardDTO;
import com.boot.DTO.ComNoticeDTO;
import com.boot.DTO.ComScrapDTO;
import com.boot.DTO.CompanyInfoDTO;
import com.boot.DTO.Criteria2;
import com.boot.DTO.JaewonCriteria;
import com.boot.DTO.JoinDTO;
import com.boot.DTO.JoinManagementPageDTO;
import com.boot.DTO.LoginDTO;
import com.boot.DTO.NoticeDTO;
import com.boot.DTO.NoticeScrapDTO;
import com.boot.DTO.PageDTO;
import com.boot.DTO.ResumeDTO;
import com.boot.DTO.ResumeUploadDTO;
import com.boot.DTO.UserDTO;
import com.boot.DTO.UserJobDTO;
import com.boot.DTO.UserStackDTO;
import com.boot.Service.ComNoticeService;
import com.boot.Service.CompanyInfo;
import com.boot.Service.IndividualService;
import com.boot.Service.JoinService;
import com.boot.Service.LoginServiceImpl;
import com.boot.Service.PageService;
import com.boot.Service.ResumeService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class individualController {
	
	@Autowired
	private IndividualService service;
	
	@Autowired
	private JoinService joinService;
	
	@Autowired
	private CompanyInfo comService;
	
	@Autowired
	private PageService pageService;
		
	
	@RequestMapping("/individualMain")//개인 마이페이지 메인으로
	public String individualMain(Model model) {
		log.info("@# individualMain");		
		
		return "individualMain";
	}
	
	@RequestMapping("/jobOffer")//받은 제안
	public String individualJobOffer(Model model){
		log.info("@# individualMain");		
		
		return "individualJobOffer";
	}
		
	
	@RequestMapping("/individualNoticeScrap") //스크랩 공고 목록
//	public String individualNoticeScrap(HttpServletRequest request, Model model, Criteria2 cri2, @RequestParam(value = "keyword", required = false) String keyword) {
	public String individualNoticeScrap(HttpServletRequest request, Model model, Criteria2 cri2, 
			@RequestParam(value = "keyword", required = false) String keyword , @RequestParam(required = false) String orderBy ) {
		log.info("@# individualNoticeScrap");	
		
		HttpSession session = request.getSession();
		
		if (orderBy == null) {
            orderBy = (String) session.getAttribute("orderBy");
            if (orderBy == null) {
                orderBy = "desc";
            }
        }
		
		
		String user_email = (String)session.getAttribute("login_email");
		log.info("@# individualNoticeScrap  user_email=>"+user_email);	
		
//		cri2.setFilter1(filter1);
		ArrayList<ComNoticeDTO> list = new ArrayList<>();
//		if (cri2.getFilter1() == null || cri2.getFilter1().equals("desc")) {
		if (orderBy == null || orderBy.equals("desc")) {
			// 사용자정보의 스크랩 채용공고 목록 가져오기(기본 최신순)
			list = pageService.noticelistWithPaging(cri2, request);
//			log.info("@# individualNoticeScrap getNoticeScrapList=>"+list);		
//			model.addAttribute("noticeList", list);
			
//		}else if (cri2.getFilter1().equals("asc") ){
		}else if (orderBy.equals("asc") ){
			
			// 사용자정보의 스크랩 채용공고 목록(오래된 순으로) 가져오기
			list = pageService.noticelistCreateAsc(cri2, request);
		} else {
	        // 기본 최신순으로 설정
	        list = pageService.noticelistWithPaging(cri2, request);
	    }
				
		log.info("@# individualNoticeScrap getNoticeScrapList=>"+list);		
		model.addAttribute("noticeList", list);
		int total = pageService.getNoticeTotalCount(user_email, keyword);
		log.info("@# individualNoticeScrap total=>"+total);
//		model.addAttribute("total",total);
					
		model.addAttribute("pageMaker", new PageDTO(total,cri2));			
			
		
//		rttr.addAttribute("noticeList",list);
//		rttr.addAttribute("pageMaker",new PageDTO(total,cri2));	
		
		return "individualNoticeScrap";
	}
	
	

	@RequestMapping("/noticeScrapWithStatus") //스크랩 공고 목록
//	public String noticeScrapWithStatus(HttpServletRequest request, Model model, Criteria2 cri2, @RequestParam(value = "keyword", required = false) String keyword) {
	public String noticeScrapWithStatus(HttpServletRequest request, Model model, Criteria2 cri2,
			@RequestParam(value = "keyword", required = false) String keyword , @RequestParam(value = "filter1", required = false) String filter1 ) {
		log.info("@# noticeScrapWithStatus");	
		
		HttpSession session = request.getSession();
		String user_email = (String)session.getAttribute("login_email");
		log.info("@# individualNoticeScrap  user_email=>"+user_email);	
		
		cri2.setFilter1(filter1);
		// 사용자정보의 스크랩 채용공고 목록 가져오기
		ArrayList<ComNoticeDTO> list = pageService.noticelistWithPaging(cri2, request);
		log.info("@# individualNoticeScrap getNoticeScrapList=>"+list);		
		model.addAttribute("noticeList", list);
				
		int total = pageService.getNoticeTotalCount(user_email, keyword);
		log.info("@# individualNoticeScrap total=>"+total);
//		model.addAttribute("total",total);
					
		model.addAttribute("pageMaker", new PageDTO(total,cri2));			
						
		
		return "redirect:/individualNoticeScrap";
	}
	

	@RequestMapping("/noticeScrapDelete")//스크랩 공고 삭제
	public String comScrapDelete(HashMap<String, String> param, HttpServletRequest request) {
	
		log.info("@# noticeScrapDelete");		
		HttpSession session = request.getSession();
		String user_email = (String)session.getAttribute("login_email");
		
		String [] arrStr = request.getParameterValues("arrStr");//notice_num배열
        int size = arrStr.length;
        NoticeScrapDTO dto = new NoticeScrapDTO();
        log.info("@# arrStr notice_num배열====>" + Arrays.toString(arrStr));
        for(int i=0; i<size; i++) {
        	dto.setNotice_num(Integer.parseInt(arrStr[i]));//배열값이 string으로 넘어오기땨뮨에
        	dto.setUser_email(user_email);
        	
        	log.info("@# notice_num,  user_email넣은 dto====>" + dto);
        	
        	service.noticeScrapDelete(dto);
        }
		
		return "redirect:/individualNoticeScrap";
	}
	
	
	
	
	
	@RequestMapping("/interComlist")//관심기업
	public String individualInterComlist(HttpServletRequest request, Criteria2 cri, @RequestParam(value = "keyword", required = false) String keyword, Model model) {
		log.info("@# individualInterComlist");		
		
		HttpSession session = request.getSession();
		String user_email = (String)session.getAttribute("login_email");
	
		ArrayList<CompanyInfoDTO> list = pageService.comlistWithPaging(cri,request);		
		int total = pageService.getComTotalCount(user_email, keyword);
		model.addAttribute("comList", list);
//		model.addAttribute("total",total);	
		model.addAttribute("pageMaker", new PageDTO(total, cri));	
		
		return "interComlist";
	}
	
	@RequestMapping("/comScrapDelete")
//	@ResponseBody
//	public String comScrapDelete(@RequestParam("arrStr[]") String[] arrStr, HashMap<String, String> param, HttpServletRequest request,RedirectAttributes rttr) 
	public String comScrapDelete(HashMap<String, String> param, HttpServletRequest request, RedirectAttributes rttr) 
//	public String comScrapDelete(HashMap<String, String> param, HttpServletRequest request) 
	{
		log.info("@# comScrapDelete");		
		HttpSession session = request.getSession();
		String user_email = (String)session.getAttribute("login_email");
		
		String[] arrStr = request.getParameterValues("arrStr");
        int size = arrStr.length;
        ComScrapDTO dto = new ComScrapDTO();
        log.info("@# arrStr====>" + Arrays.toString(arrStr));
        log.info("@# arrStr====>" + Arrays.toString(arrStr));
        for(int i=0; i<size; i++) {
        	dto.setCom_email(arrStr[i]);
        	dto.setUser_email(user_email);
        	
        	
        	service.comScrapDelete(dto);
        }
		

//        rttr.addAttribute("pageNum",param.get("pageNum"));
//		rttr.addAttribute("amount",param.get("amount"));	
		
		
//		return 1;
		return "redirect:/interComlist";
	}
	
	
	
	
	@RequestMapping("/userInfo")//회원정보수정 메뉴 > 회원정보 확인
	public String individualUserInfo(@RequestParam HashMap<String, String> param, HttpServletRequest request, Model model) {
	
		log.info("@# individualUserInfo");
		HttpSession session = request.getSession();
		String user_email = (String)session.getAttribute("login_email");
		
		UserDTO dto = service.getUserInfo(user_email);
		log.info("@# individualUserInfo dto=>"+dto);
		model.addAttribute("userInfo", dto);
		
		List<UserJobDTO> jobdtos = service.getUserJob(user_email);
		log.info("@# individualUserInfo jobdto=>"+jobdtos);
		model.addAttribute("jobInfo", jobdtos);
		
		List<UserStackDTO> stackdtos = service.getUserStack(user_email);
		log.info("@# individualUserInfo stackdto=>"+stackdtos);
		model.addAttribute("stackInfo", stackdtos);
		
		
		return "individualUserInfo";
	}
	@RequestMapping("/modifyPage")//회원정보수정 페이지
	public String individualModifyInfo(@RequestParam HashMap<String, String> param,HttpServletRequest request, Model model) {
		
		log.info("@# individualModifyInfo");
		HttpSession session = request.getSession();
		String user_email = (String)session.getAttribute("login_email");
		
		UserDTO dto = service.getUserInfo(user_email);
		log.info("@# individualModifyInfo dto=>"+dto);
		model.addAttribute("userInfo", dto);
		
		List<UserJobDTO> jobdtos = service.getUserJob(user_email);
		// UserJobDTO의 job 필드의 값들을 가져와 ,로 구분된 문자열로 반환
	    String jobStr = jobdtos.stream()
					    		.map(UserJobDTO::getJob_name)
				                .collect(Collectors.joining(","));
	    log.info("@# individualModifyInfo jobStr => "+jobStr);
		model.addAttribute("user_job", jobStr);
		
		List<UserStackDTO> stackdtos = service.getUserStack(user_email);
		// 리스트를 쉼표로 구분된 문자열로 변환 > 쿼리를 통해 저장하려면 JSON형식이나 문자열로 반환해서 보내줘야 함
		 String stackStr = stackdtos.stream()
	                .map(UserStackDTO::getStack_name)
	                .collect(Collectors.joining(","));
	    log.info("@# individualModifyInfo stackStr => "+stackStr);
		model.addAttribute("user_stack", stackStr);
		
		return "individualUserInfoModify";
	}

	@RequestMapping("/userInfoModifyPop")
	public String userInfoModifyPop(@RequestParam HashMap<String, String> param, Model model) 
	{
		log.info("@# userInfoModifyPop 희망직무");	
		
		
		List<JoinDTO> job = joinService.job();		
		model.addAttribute("job_name", job);
		
		List<JoinDTO> job2 = joinService.job2();		
		model.addAttribute("job_name2", job2);
		
		List<JoinDTO> job3 = joinService.job3();		
		model.addAttribute("job_name3", job3);
		
		
		
		return "userInfoModifyPop";
	}
	
	@RequestMapping("/userInfoModifyPop2")
	public String userInfoModifyPop2(@RequestParam HashMap<String, String> param, Model model) 
	{
		log.info("@# userInfoModifyPop2 주요기술");	
		
		List<JoinDTO> stack =  joinService.stack();		
		model.addAttribute("stack_name", stack);
		
		List<JoinDTO> stack2 = joinService.stack2();		
		model.addAttribute("stack_name2", stack2);
		
		List<JoinDTO> stack3 = joinService.stack3();				
		model.addAttribute("stack_name3", stack3);
		
		
		return "userInfoModifyPop2";
	}
	
	
	
	
	@RequestMapping("/modifyInfo")//회원정보수정하기
	public String individualUserInfoModify(@RequestParam HashMap<String, String> param, HttpServletRequest request, Model model){ 
	
		log.info("@# individualUserInfoModify");	
		log.info("@# individualUserInfoModify param =>" +param);	

		String input_pw = param.get("input_pw");
        String session_pw = param.get("session_pw");
        
		log.info("@# individualUserInfoModify session_pw =>"+input_pw);//세션 pw화인
		log.info("@# individualUserInfoModify input_pw =>"+session_pw);//사용자가 입력한 pw화인
		
		
		if (input_pw.equals(session_pw)) {//입력한 비번이 세션의 비번과 같으면 
			
			service.modify(param); //회원정보 update	
			
			UserDTO newDTO = service.getUserInfo(param.get("user_email"));	
			log.info("@# individualUserInfoModify newDTO=>"+newDTO);
			model.addAttribute("userInfo", newDTO);//업데이트 한 새 정보를 다시 모델에 담아서 넘겨줌
			
			String user_email = param.get("user_email");
			log.info("@# individualUserInfoModify user_email==>" + param.get("user_email"));
			log.info("@# individualUserInfoModify user_stack==>" + param.get("user_stack"));
			log.info("@# individualUserInfoModify user_job==>" + param.get("user_job"));
			
			service.deleteJob(user_email);//수정전 데이터 삭제
			UserJobDTO dto = new UserJobDTO();
			String arr [] = param.get("user_job").split(",");
			for(int i = 0; i<arr.length; i++) 
			{
				dto.setJob_name(arr[i]);
				dto.setUser_email(user_email);
				service.insertJob(dto); 
			}
			
			service.deleteStack(user_email);//수정전 데이터 삭제
			UserStackDTO dto2 = new UserStackDTO();
			String arr2 [] = param.get("user_stack").split(",");
			for(int i = 0; i<arr2.length; i++) 
			{
				dto2.setUser_email(user_email);
				dto2.setStack_name(arr2[i]);
				service.insertStack(dto2); 
			}	
			
			
			return "individualUserInfo";
		}else {//입력한 비번이 세션의 비번과 다르면 다시 회원정보 수정페이지로
			log.info("@# 실패 ㅠㅠ");
			
			return "redirect:individualUserInfo";
		}
	}
		
//	@RequestMapping("/insertJobStack")
//	public String insertJobStack(@RequestParam String user_email, @RequestParam String user_stack, @RequestParam String user_job, 
//							Model model, HttpServletRequest request) 
//	{		
//		log.info("@# insertJobStack로 넘어옴");		
//		log.info("@# insertJobStack user_email==>" + user_email);
//		log.info("@# insertJobStack user_stack==>" + user_stack);
//		log.info("@# insertJobStack user_job==>" + user_job);
//		
//		String arr [] = user_job.split(",");
//		for(int i = 0; i<arr.length; i++) 
//		{
//			service.insertJob(user_email, arr[i]); 
//		}
//		
//		String arr2 [] = user_stack.split(",");
//		for(int i = 0; i<arr2.length; i++) 
//		{
//			service.insertStack(user_email, arr2[i]); 
//		}		     
//		
//		return "redirect:login";
//	}
	
	
	
	
	
	
}
