package com.boot.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import com.boot.DAO.ComNoticeDAO;
import com.boot.DTO.CardPageDTO;
import com.boot.DTO.ComNoticeAttachDTO;
import com.boot.DTO.ComNoticeDTO;
import com.boot.DTO.JoinDTO;
import com.boot.DTO.RecentNoticeDTO;
import com.boot.DTO.ResumeDTO;

import com.boot.DTO.Standard;
import com.boot.DTO.SubmitDTO;
import com.boot.DTO.UserDTO;
import com.boot.Service.CardPageService;
import com.boot.Service.ComNoticeService;
import com.boot.Service.JoinService;
import com.boot.Service.ScrapService;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Slf4j
public class ComNoticeController {

	@Autowired
	private ComNoticeService postService;
	
	@Autowired
	private ScrapService scrapService;
	
	@Autowired
	private CardPageService cardPageService;
	
	@Autowired
	private JoinService joinService;
	

	@RequestMapping("/jobPostList")
//	@ResponseBody
//	public String jobPost(Model model, HttpSession session) {//대메뉴 -> 채용 클릭시 이동(페이징 처리를 위해 CardPageController 매핑됨)
//	public String jobPost(Model model) {//대메뉴 -> 채용 클릭시 이동(페이징 처리를 위해 CardPageController 매핑됨)
//		public String jobPost(Standard std, Model model, HttpSession session) {//대메뉴 -> 채용 클릭시 이동
		public String jobPost(@RequestParam(name = "orderType", required = false, defaultValue = "latest") String orderType,@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
							 @RequestParam(name = "com_email", required = false, defaultValue = "") String comEmail,				
							 Standard std, Model model, HttpSession session) {//대메뉴 -> 채용 클릭시 이동
//		public String jobPost(@RequestParam(name = "orderType", required = false, defaultValue = "latest") String orderType, Standard std, Model model, HttpSession session) {//대메뉴 -> 채용 클릭시 이동
		log.info("@# cardPage controller");
		log.info("@# cardPage controller std!!=>"+std);
		std.setComEmail(comEmail);
		
		model.addAttribute(orderType, orderType);
		
		 // com_email이 올바르게 설정되었는지 확인
	    log.info("com_email => " + std.getComEmail());
		
//		ArrayList<ComNoticeDAO> list = cardPageService.cardPageList(std);//현재 진행중인 공고를 가져옴
		ArrayList<ComNoticeDTO> list = cardPageService.cardPageList(orderType, std);//현재 진행중인 공고를 가져옴
//		int total = pageService.getTotalCount();
		int total = cardPageService.getTotalCount(std);
		log.info("@# cardPage controller total!!=>"+total);
		
		model.addAttribute("jobPost", list);//현재 진행중인 공고를 실어 보냄
		model.addAttribute("paging", new CardPageDTO(total, std));

		
		ArrayList <String> careerList = cardPageService.getCareerList();
		model.addAttribute("careerList", careerList);
		ArrayList <String> stackList = cardPageService.getStackList();
		model.addAttribute("stackList", stackList);
		ArrayList <String> locationList = cardPageService.getLocationList();
		model.addAttribute("locationList", locationList);
		
		 String user_email = (String) session.getAttribute("login_email");//세션에 저장된 사용자이메일 가져오기
//		 log.info("@# jobPost user_email => "+user_email);
		 
		 if(user_email != null) {// 세션에 이메일 값이 있다면 해당 사용자의 이메일을 기반으로 관심 공고 목록을 가져옴
			 ArrayList<Integer> noticeList = scrapService.getScrapNoticeNum(user_email);
			 
			 model.addAttribute("noticeList", noticeList);
			 }
		
		
		return "/recruitmentNotice/jobPostList";
	}

	
	// 2024-08-01 지수 (공고 목록 사진 들고오기)
//		이미지파일을 받아서 화면에 출력(byte 배열타입)
		@GetMapping("/cardPageDisplay")
		public ResponseEntity<byte[]> getFileAttach(String fileName) {
			log.info("@# display fileName=>"+fileName);
			
//			업로드 파일경로+이름
			File file = new File("C:\\devv\\upload\\"+fileName);
			log.info("@# file=>"+file);
			
			ResponseEntity<byte[]> result=null;
			HttpHeaders headers=new HttpHeaders();
			
			try {
//				파일타입을 헤더에 추가
				headers.add("Content-Type", Files.probeContentType(file.toPath()));
//				파일정보를 byte 배열로 복사+헤더정보+http상태 정상을 결과에 저장
				result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		@GetMapping(value = "/cardPageFileList")
		public ResponseEntity<List<ComNoticeAttachDTO>> cardPageFileList(@RequestParam HashMap<String, String> param) {
			log.info("@# cardPageFileList()");
			log.info("@# param=>"+param);
			log.info("@# param=>"+param.get("notice_num"));
			
			return new ResponseEntity<>(cardPageService.cardPageFileList(Integer.parseInt(param.get("notice_num"))), HttpStatus.OK);
		}

	
	
	
	
	
	
	
	
	@RequestMapping("/jobPostDetail")
	public String JobPost(HttpSession session, int notice_num, Model model) {//채용공고 목록 -> 채용공고 상세 이동
		log.info("jobPostDetail");
		log.info("notice_num!!!"+notice_num);
		
		
		// 24.07.30 연주 : 공고열람하면 최근본 공고 정보 저장하기==========================================================
		 
		// notice_num 값을 세션에 저장하므로 값이 있는지 확인
	    List<Integer> recentJobPosts = (List<Integer>) session.getAttribute("recentJobPost");
	    log.info("recentJobPosts!!!"+recentJobPosts);
	    
	    //정보가 없으면 리스트를 만들어줌
	    if (recentJobPosts == null) {
	        recentJobPosts = new ArrayList<>();
	    }
	    
	    // 이미 리스트에 있는지 확인하고, 없는 경우에만 추가
	    if (!recentJobPosts.contains(notice_num)) {
	        recentJobPosts.add(notice_num);
	    }
	    
	    log.info("@# jobPost recentJobPosts=> " + recentJobPosts);
	    
	    session.setAttribute("recentJobPost", recentJobPosts);//세션에 정보 저장
		
	    String user_email = (String) session.getAttribute("login_email");//세션에 저장된 사용자이메일 가져오기
	    log.info("@# jobPost user_email => "+user_email);
	    
        int size = recentJobPosts.size();
        RecentNoticeDTO recentNoticeDTO = new RecentNoticeDTO();//최근본 공고 저장할 DTO
//        log.info("@# recentJobPosts====>" + Arrays.toString(recentJobPosts));
//        log.info("@# arrStr====>" + Arrays.toString(arrStr));
        for(int i=0; i<size; i++) {
        	recentNoticeDTO.setNotice_num(recentJobPosts.get(i));
        	recentNoticeDTO.setUser_email(user_email);
        	
        	postService.updateRecentNotice(recentNoticeDTO);
        }
	   
     // 24.07.30 연주  끝================================================================================
        
     
	   		
		ComNoticeDTO dto = postService.JobPost(notice_num);
		postService.hitUP(notice_num);// 조회수를 증가
		model.addAttribute("company", dto);//공고 정보를 모델에 실어 보냄
		model.addAttribute("noticeNumber",notice_num);//공고 번호를 모델에 실어서 보냄
		
		ArrayList<ComNoticeDTO> list = postService.otherJobPost(notice_num);//해당 기업의 다른 공고를 검색해 옴
		int postNum = list.size();
		model.addAttribute("otherPost", list);
		model.addAttribute("postNum", postNum);
		
//		log.info("user_email");
		if(user_email != null) {//session의 이메일 정보를 얻어, 값이 있을 경우 로직 수행
			String com_email = scrapService.existingCompany(user_email, notice_num);// 관심 기업
			model.addAttribute("com_email", com_email);
			
			ArrayList<Integer> ScrapComList = scrapService.getScrapNoticeNum(user_email);// 스크랩 공고
			model.addAttribute("ScrapComList", ScrapComList);
		}
		
		return "/recruitmentNotice/jobPostDetail";
	}
	
	
	@RequestMapping("/profileInfo")
	public String profileInfo(HttpServletRequest request, Model model){//지원하기 외부 팝업 발생
		log.info("profileInfo");
		
		int notice_num = Integer.parseInt(request.getParameter("notice_num"));// 공고 번호를 얻음
		ComNoticeDTO dto =postService.getNoticeInfo(notice_num);//공고 번호를 기반으로 notice_tb의 정보를 가져옴
		model.addAttribute("notice", dto);
		
		HttpSession session = request.getSession();
		
		String login_email = (String) session.getAttribute("login_email");
		ArrayList<ResumeDTO> dtos = postService.getProfileList(login_email);
		log.info("profileInfo dtos==>"+dtos);
		model.addAttribute("userProfile", dtos);

		return "/recruitmentNotice/profileInfo";
	}
	
	@RequestMapping("/resumeUser")
	@ResponseBody
//	이력서 선택, 지원하기 버튼 클릭시 DB에 정보 추가
//	public void resumeUser(@RequestParam HashMap<String, String> param, HttpServletRequest request, Model moedel) {
	public boolean resumeUser(@RequestParam HashMap<String, String> param, HttpServletRequest request, Model moedel) {
		log.info("resumeUser!!!");
		log.info("resumeUser!!! param" +param);
		
		// 24.08.04 연주 : 지원하기 누르면 해당공고로 제안한 이력확인 하고 offer_agree=지원완료, resume_submitDate=현재날짜 offer테이블에 저장하기==========================================================
        
		//offer 테이블에서 notice_num과 user_email 값이 있는지 확인
		int notice_num = Integer.parseInt(param.get("notice_num"));
		String user_email = param.get("user_email");
		log.info("resumeUser!!! notice_num=" +notice_num+"user_email="+user_email);
		
		
		int offer_exist = postService.getOfferNum(notice_num,user_email);//offer 테이블 조회
		log.info("제안한적 있으면 1, 없으면 0 ->>"+offer_exist);
		
		if(offer_exist == 1) {
			log.info("제안한적 있어서 offer테이블 정보저장으로 분기 탐 ->>");
			postService.updateOfferStatus(notice_num, user_email);//offer 테이블에 정보저장
		}
		
		
	     // 24.08.04 연주  끝================================================================================
		
		
		boolean result = postService.updateSubmitData(param);//submit 테이블에 정보저장
		log.info("지원 결과 ->>"+result);
		
		return result;
	}
	
	
	@Autowired
    private ComNoticeService service;
	
	//기업 초기 메인 (공고 없을때)
	@RequestMapping("/comRegistMain")
	public String comRegistMain() {
		log.info("@# comRegistMain");
		
		return "comRegistMain";
	}
	
	//공고.지원자관리 목록
	@RequestMapping("/comRegistCheck")
	public String comRegistCheck() {
		log.info("@# comRegistCheck");
		
		return "comRegistCheck";
	}
	
	//채용공고 등록 페이지 
	@RequestMapping("/comRegistUpload")
	public String comRegistUpload(ComNoticeDTO comNoticeDTO, HttpServletRequest httpServletRequest, Model model) {
		log.info("@# comRegistUpload");
		
		HttpSession session = httpServletRequest.getSession();
		session.getAttribute("login_email");
		session.getAttribute("login_name");
		log.info("@# session  =>"+(String) session.getAttribute("login_email"));
		
		model.addAttribute("com_email",session.getAttribute("login_email"));
		model.addAttribute("com_name", session.getAttribute("login_name"));
		
		
		//24.08.14 하진 : 스택값 불일치 오류로 인한 스택값 수정
		List<JoinDTO> stack = joinService.stack();		
		model.addAttribute("stack_name", stack);
		
		List<JoinDTO> stack2 = joinService.stack2();		
		model.addAttribute("stack_name2", stack2);
		
		List<JoinDTO> stack3 = joinService.stack3();		
		model.addAttribute("stack_name3", stack3);
		return "comRegistUpload";
	}
	
	// 채용공고 등록
		@RequestMapping("/registerNotice")
		public String regiserNotice(ComNoticeDTO comNoticeDTO, HttpServletRequest httpServletRequest, Model model) {
			log.info("@# registerNotice");
			
			HttpSession session = httpServletRequest.getSession();
			session.getAttribute("login_email");
			session.getAttribute("login_name");
			log.info("@# session  =>"+(String) session.getAttribute("login_email"));
			
			model.addAttribute("com_email",session.getAttribute("login_email"));
			model.addAttribute("com_name", session.getAttribute("login_name"));
			
			log.info("@# comNoticeDTO=>"+comNoticeDTO);
			
			if (comNoticeDTO.getComNoticeAttachList() != null) {
				comNoticeDTO.getComNoticeAttachList().forEach(attach -> log.info("@# attach=>"+attach));
			}
			
			service.registerNotice(comNoticeDTO);
			
			service.noticeInsertStack(comNoticeDTO);
			service.noticeStauts(comNoticeDTO);
			
			httpServletRequest.setAttribute("msg", "공고를 등록하였습니다.");
			httpServletRequest.setAttribute("url", "/jobpostingList");
			return "/alert";
			
		}
		
		//2024-08-02 지수
		//채용공고 수정 페이지
		@RequestMapping("/comRegistModify")
		public String comRegistModify(int notice_num, HttpServletRequest httpServletRequest, Model model) {
		    log.info("@# comRegistModify");

		    HttpSession session = httpServletRequest.getSession();
		    session.getAttribute("login_email");
		    session.getAttribute("login_name");
		    log.info("@# session  =>" + (String) session.getAttribute("login_email"));

		    model.addAttribute("com_email", session.getAttribute("login_email"));
		    model.addAttribute("com_name", session.getAttribute("login_name"));

		    ComNoticeDTO dto = service.JobPost(notice_num);
		    
		    dto.setNotice_num(notice_num); // @@@@@@ 여기에 notice_num을 넣어야 getNoticeStack()에서 dto.notice_num 사용할 수 있음 -깡아지- @@@@@@
		    model.addAttribute("notice", dto);
		    model.addAttribute("noticeNumber", notice_num);
		    log.info("@#notice_num=>"+notice_num);

		    // 스택 리스트를 가져와서 모델에 추가
		    log.info("@# dto=>"+dto);
		    List<String> stackList = service.getNoticeStack(dto);
		    log.info("@# stackList=>"+stackList);
		    String stackListString = String.join(",", stackList);
		    log.info("@# stackListString=>"+stackListString);
		    model.addAttribute("stackListString", stackListString);

		    return "comRegistModify";
		}
		
		// 공고 수정 후 저장
		@PostMapping("/updateRegisterNotice")
		public String updateRegisterNotice(ComNoticeDTO comNoticeDTO, HttpServletRequest httpServletRequest) {
		    log.info("@# updateRegisterNotice");
		    
		    HttpSession session = httpServletRequest.getSession();
		    String loginEmail = (String) session.getAttribute("login_email");
		    log.info("@# session login_email => " + loginEmail);
		    
		    comNoticeDTO.setCom_email(loginEmail);
		    
		    service.updateRegisterNotice(comNoticeDTO); //공고 수정 update
		    service.noticeDeleteStack(comNoticeDTO.getNotice_num()); //스택 삭제 후
		    service.noticeInsertStack(comNoticeDTO); // 스택 저장
		    
		    httpServletRequest.setAttribute("msg", "공고를 수정하였습니다.");
			httpServletRequest.setAttribute("url", "/jobpostingList");
			
		    return "/alert";
		}


		
	
	@PostMapping("/registUploadAjaxAction")
//	public void uploadAjaxPost(MultipartFile[] uploadFile) {
//	ResponseEntity : 파일 정보를 넘기기위해서 사용
	public ResponseEntity<List<ComNoticeAttachDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {
		log.info("upload ajax post...");
		
		List<ComNoticeAttachDTO> list = new ArrayList<>();
		
		String uploadFolder = "C:\\devv\\upload";
		String uploadFolderPath = getFolder();
//		"D:\\dev\\upload"+년월일 폴더
		File uploadPath = new File(uploadFolder, uploadFolderPath);
		log.info("@# uploadPath=>"+uploadPath);
		
		if (uploadPath.exists() == false) {
			// make yyyy/MM/dd folder
			uploadPath.mkdirs();
		}
		
		for (MultipartFile multipartFile : uploadFile) {
			log.info("==============================");
//			getOriginalFilename : 업로드 되는 파일 이름
			log.info("@# 업로드 되는 파일 이름=>"+multipartFile.getOriginalFilename());
//			getSize : 업로드 되는 파일 크기
			log.info("@# 업로드 되는 파일 크기=>"+multipartFile.getSize());
			
			String uploadFileName = multipartFile.getOriginalFilename();
			
			UUID uuid = UUID.randomUUID();
			log.info("@# uuid=>"+uuid);
			
			ComNoticeAttachDTO comNoticeAttachDTO = new ComNoticeAttachDTO();
			comNoticeAttachDTO.setFileName(uploadFileName);
			comNoticeAttachDTO.setUuid(uuid.toString());
			comNoticeAttachDTO.setUploadPath(uploadFolderPath);
			log.info("@# comNoticeAttachDTO 01=>"+comNoticeAttachDTO);
			
			uploadFileName = uuid.toString() +"_"+uploadFileName;
			log.info("@# uuid uploadFileName=>"+uploadFileName);
			
//			saveFile : 경로하고 파일이름
			File saveFile = new File(uploadPath, uploadFileName);
			FileInputStream fis=null;
			
			try {
//				transferTo : saveFile 내용을 저장
				multipartFile.transferTo(saveFile);
				
//				참이면 이미지 파일
				if (checkImageType(saveFile)) {
					comNoticeAttachDTO.setImage(true);
					log.info("@# comNoticeAttachDTO 02=>"+comNoticeAttachDTO);
					
					fis = new FileInputStream(saveFile);
					
//					썸네일 파일은 s_ 를 앞에 추가
					FileOutputStream thumnail = new FileOutputStream(new File(uploadPath, "s_"+uploadFileName));
					
//					썸네일 파일 형식을 1200/1200 크기로 생성
					Thumbnailator.createThumbnail(fis, thumnail, 1200, 1200);
					
					thumnail.close();
				}
				
				list.add(comNoticeAttachDTO);
			} catch (Exception e) {
				log.error(e.getMessage());
			}finally {
				try {
					if (fis != null) fis.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}//end of for
		
//		파일정보들을 list 객체에 담고, http 상태값은 정상으로 리턴
		return new ResponseEntity<List<ComNoticeAttachDTO>>(list, HttpStatus.OK);
	}
	
//	날짜별 폴더 생성
	private String getFolder() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		String str = sdf.format(date);
		
		log.info("@# str=>"+str);
		log.info("@# separator=>"+File.separator);
		
		return str.replace("-", File.separator);
	}
	
//	이미지 여부 체크
	public boolean checkImageType(File file) {
		try {
//			이미지파일인지 체크하기 위한 타입(probeContentType)
			String contentType = Files.probeContentType(file.toPath());
			log.info("@# contentType=>"+contentType);
			
//			probeContentType 메소드 버그로 로직 추가
			if (contentType == null) {
				return false;
			}
			
			log.info("@# startsWith===>"+contentType.startsWith("image"));
			
//			startsWith : 파일종류 판단
			return contentType.startsWith("image");//참이면 이미지파일
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;//거짓이면 이미지파일이 아님
	}
	
//	이미지파일을 받아서 화면에 출력(byte 배열타입)
	@GetMapping("/registDisplay")
	public ResponseEntity<byte[]> getFile(String fileName) {
		log.info("@# display fileName=>"+fileName);
		
//		업로드 파일경로+이름
		File file = new File("C:\\devv\\upload\\"+fileName);
		log.info("@# file=>"+file);
		
		ResponseEntity<byte[]> result=null;
		HttpHeaders headers=new HttpHeaders();
		
		try {
//			파일타입을 헤더에 추가
			headers.add("Content-Type", Files.probeContentType(file.toPath()));
//			파일정보를 byte 배열로 복사+헤더정보+http상태 정상을 결과에 저장
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@PostMapping("/registDeleteFile")
	public ResponseEntity<String> deleteFile(String fileName, String type) {
		log.info("@# deleteFile fileName=>"+fileName);
		File file;
		
		try {
//			URLDecoder.decode : 서버에 올라간 파일을 삭제하기 위해서 디코딩
			file = new File("C:\\devv\\upload\\"+URLDecoder.decode(fileName, "UTF-8"));
			file.delete();
			
//			이미지 파일이면 썸네일도 삭제
			if (type.equals("image")) {
//				getAbsolutePath : 절대경로(full path)
				String largeFileName = file.getAbsolutePath().replace("s_", "");
				log.info("@# largeFileName=>"+largeFileName);
				
				file = new File(largeFileName);
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
//			예외 오류 발생시 not found 처리
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
//		deleted : success 의 result 로 전송
		return new ResponseEntity<String>("deleted",HttpStatus.OK);
	}
	
//	@GetMapping(value = "/getFileList", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
	@GetMapping(value = "/registGetFileList")
	public ResponseEntity<List<ComNoticeAttachDTO>> registGetFileList(@RequestParam HashMap<String, String> param) {
		log.info("@# registGetFileList()");
		log.info("@# param=>"+param);
		log.info("@# param=>"+param.get("notice_num"));
		
		return new ResponseEntity<>(service.registGetFileList(Integer.parseInt(param.get("notice_num"))), HttpStatus.OK);
	}
	
	
	
	//인재풀
	@RequestMapping("/comRegistApplicant")
	public String comRegistApplicant() {
		log.info("@# comRegistApplicant");
		
		return "comRegistApplicant";
	}


}
