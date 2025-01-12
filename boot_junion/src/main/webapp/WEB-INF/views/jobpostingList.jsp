<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title> 공고관리 목록페이지 </title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/default.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/jobpostingList.css">
<!--<link rel="stylesheet" href="src/main/resources/static/css/style.css">-->
<!-- import font-awesome, line-awesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/line-awesome/1.3.0/line-awesome/css/line-awesome.min.css">
<!-- import pretendard font -->
<link rel="stylesheet" as="style" crossorigin href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.8/dist/web/variable/pretendardvariable.css"/>
<!-- import js -->
<script src="https://code.jquery.com/jquery-3.6.3.min.js" integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous"></script>

</head>
<body>
	
	
<!------------------------------------------------ 헤더 시작 ------------------------>


<div class="container">
	<%@ include file="nav_company.jsp" %>
	<%@ include file="quickMenu.jsp" %>
    
    <div class="mainContent">
        <header>
			<%@ include file="dropdown.jsp" %>
		</header>  
		<!--------------------------------------------- 헤더 끝 ------------------------>  
		  <main>
            <div class="containe">
                <div class="toptitle">
                    <h3 class = "toptitlehh">채용 공고</h3>
                </div>
            
				<div class="tabWrap">
                    <div class="tabCon All" onclick="showTab('all')">
                        <h3 class="tabTitle">
                            <a href="#">전체</a>
                        </h3>
                        <h3 class="tabNum">
                            <a href="#">${totalCount.totalCount}</a>
                        </h3>
                    </div>
                    <div class="tabCon proposal" onclick="showTab('proposal')">
                        <h3 class="tabTitle">
                            <a href="#">진행중인 공고</a>
                        </h3>
                        <h3 class="tabNum">
                            <a href="#">${totalCount.onCount}</a>
                        </h3>
                    </div>                    
                    <div class="tabCon post" onclick="showTab('post')">
                        <h3 class="tabTitle">
                            <a href="#">마감된 공고</a>
                        </h3>
                        <h3 class="tabNum">
                            <a href="#">${totalCount.offCount}</a>
                        </h3>
                    </div>
                </div>
				
				
				
				
					

				<!-- 전체 공고 -->
				<c:if test="${totalCount.totalCount > 0}">
				    <div id="all" class="job-section">
				        <c:forEach items="${jobpostingList}" var="dto">
				            <div class="box">
				                <div class="left">
				                    <div class="job">
				                        <div class="jobjob">
				                            <h5 class="carh5">${dto.notice_career}</h5>
				                        </div>
				                    </div>
									
									
									
				                    <div>
				                        <div class="job-title">
			                                <a href="jobPostDetail?notice_num=${dto.notice_num}"><h5  class="titlebb">${dto.notice_title}</h5></a>
											<!--"${pageContext.request.contextPath}/jobpostingSupport?notice_num=${dto.notice_num}&resume_num=${dto.resume_num}"-->
				                        </div>
										<div class="datesbox">
					                        <div class="dates">${dto.notice_startDate} ~ ${dto.notice_endDate} </div>
					                        <div class="end">공고 : ${dto.notice_final}</div>
										</div>
				                    </div>
				                </div>
				                <div class="right">
				                    <div class="deletejobp">
										<a href="comRegistModify?notice_num=${dto.notice_num}" class="jobjob">
										    <h5 class="carh5" >공고수정</h5>
										</a>
				                    </div>
				                    <div class="deletejobp">
				                        <a href="#" onclick="deleteJobPosting('${dto.notice_num}')" class="jobjob">
				                            <h5 class="carh5">삭제</h5>
				                        </a>
				                    </div>
									<div class="applicant-count" >
									    <a class="jobjob" href="jobpostingSupport?notice_num=${dto.notice_num}">
									        <h5 class="carh6">지원자 ${dto.t_count}</h5>
									    </a>
									</div>
				                </div>
				            </div>
				        </c:forEach>
				    </div>
				</c:if>

				<!-- 진행중인 공고 -->
				<c:if test="${totalCount.onCount > 0}">
				    <div id="proposal" class="job-section" style="display: none;">
				        <c:forEach items="${jobpostingList}" var="dto">
				            <c:if test="${dto.notice_final == '진행중'}">
				                <div class="box">
				                    <div class="left">
				                        <div class="job">
				                            <div class="jobjob">
				                                <h5 class="carh5">${dto.notice_career}</h5>
				                            </div>
				                        </div>
				                        <div>
				                            <div class="job-title">
				                                <a href="jobPostDetail?notice_num=${dto.notice_num}"><h5  class="titlebb">${dto.notice_title}</h5></a>
				                            </div>
											<div class="datesbox">
											    <div class="dates">${dto.notice_startDate} ~ ${dto.notice_endDate} </div>
											    <div class="end">공고 : ${dto.notice_final}</div>
											</div>
				                        </div>
				                    </div>
				                    <div class="right">
				                        <div class="deletejobp">
											<a href="#" class="jobjob">
											    <h5 class="carh5" >공고수정</h5>
											</a>
				                        </div>
				                        <div class="deletejobp">
				                            <a href="#" onclick="deleteJobPosting('${dto.notice_num}')" class="jobjob">
				                                <h5 class="carh5">삭제</h5>
				                            </a>
				                        </div>
										<a class="applicant-count" href="jobpostingSupport?notice_num=${dto.notice_num}">
										    <div class="jobjob">
										        <h5 class="carh6">지원자 ${dto.t_count}</h5>
										    </div>
										</a>
				                    </div>
				                </div>
				            </c:if>
				        </c:forEach>
				    </div>
				</c:if>

				<!-- 마감된 공고 -->
				<c:if test="${totalCount.offCount > 0}">
				    <div id="post" class="job-section" style="display: none;">
				        <c:forEach items="${jobpostingList}" var="dto">
				            <c:if test="${dto.notice_final == '마감'}">
				                <div class="box">
				                    <div class="left">
				                        <div class="job">
				                            <div class="jobjob">
				                                <h5 class="carh5">${dto.notice_career}</h5>
				                            </div>
				                        </div>
				                        <div>
				                            <div class="job-title">
				                                <a href="jobPostDetail?notice_num=${dto.notice_num}"><h5  class="titlebb">${dto.notice_title}</h5></a>
				                            </div>
											<div class="datesbox">
											    <div class="dates">${dto.notice_startDate} ~ ${dto.notice_endDate} </div>
											    <div class="end">공고 : ${dto.notice_final}</div>
											</div>
				                        </div>
				                    </div>
				                    <div class="right">
				                        <div class="deletejobp">
											<a href="#" class="jobjob">
											    <h5 class="carh5" >공고수정</h5>
											</a>
				                        </div>
				                        <div class="deletejobp">
				                            <a href="#" onclick="deleteJobPosting('${dto.notice_num}')" class="jobjob">
				                                <h5 class="carh5">삭제</h5>
				                            </a>
				                        </div>
				                        <a class="applicant-count" href="jobpostingSupport?notice_num=${dto.notice_num}">
				                            <div class="jobjob">
				                                <h5 class="carh6">지원자 ${dto.t_count}</h5>
				                            </div>
				                        </a>
				                    </div>
				                </div>
				            </c:if>
				        </c:forEach>
				    </div>
				</c:if>
				

            </div> <!-- container끝 -->
        </main>
    </div> <!-- //main-content -->
</div>    
    
<%@ include file="footer.jsp" %>
</body>
</html>



<!-- ------------------------------------------- 스크립트 시작 ------------------------------->
<script>
	
	function sendNoticeNum(notice_num, resume_num) {
	    $.ajax({
	        url: '${pageContext.request.contextPath}/jobpostingSupport', // 서버의 컨트롤러 URL
	        type: 'POST',
	        data: { 
	            notice_num: notice_num,
	            resume_num: resume_num
	        },
	        success: function(response) {
	            // 성공 시 처리할 로직 (예: 페이지 이동, 메시지 표시 등)
	            window.location.href = response.redirectUrl;
	        },
	        error: function(xhr, status, error) {
	            // 에러 시 처리할 로직
	            console.error("전송 중 오류 발생: ", error);
	            alert("전송 중 오류가 발생했습니다.");
	        }
	    });
	}
	
	
	
	function deleteJobPosting(notice_num) {
	           if (confirm("정말로 삭제하시겠습니까?")) {
	               $.ajax({
	                   url: '${pageContext.request.contextPath}/jobpostingDeleteWithRelatedData',
	                   type: 'POST',
	                   data: { notice_num: notice_num },
	                   success: function(response) {
	                       if (response === "success") {
	                           alert("삭제되었습니다.");
	                           location.reload();
	                       } else {
	                           alert("삭제 중 오류가 발생했습니다.");
	                       }
	                   },
	                   error: function(xhr, status, error) {
	                       console.error("삭제 중 오류 발생: ", error);
	                       alert("삭제 중 오류가 발생했습니다.");
	                   }
	               });
	           }
	       }
	
	
	// 탭 메뉴 클릭 시 해당 섹션 보여주는 함수
	   function showTab(tabName) {
	       // 모든 섹션 숨기기
	       document.querySelectorAll('.job-section').forEach(function(section) {
	           section.style.display = 'none';
	       });

	       // 클릭한 탭의 섹션만 보이기
	       document.getElementById(tabName).style.display = 'block';
	   }

	   // 페이지 로드 시 전체 탭을 기본으로 선택
	   document.addEventListener('DOMContentLoaded', function() {
	       showTab('all');
	   });
	


	   // CSS JS ---------------------------------------------------
	   // 24-07-09 이재원
	   // 체크박스 삭제 기능
	   // 전체삭제 / 부분삭제 둘다 가능	
	   $(document).ready(function()
	   {
	       /*
	           2024-06-25 이재원 
	           메뉴 눌렀을 때 메뉴 활성화 : active
	       */
	       $('.navMenu ul li').click(function(){
	           $(this).addClass('active');
	           $('.navMenu ul li').not(this).removeClass('active');
	       });

	       
	       /*
	           2024-06-25 이재원 
	           글자수 제한 + 넘는건 ...처리
	       */
	       $('.cardConBottom > .title').each(function()
	       {
	           var length = 21; //표시할 글자 수 정하기
	       
	           $(this).each(function()
	           {

	               if($(this).text().length >= length)
	               {
	                   $(this).text($(this).text().substr(0, length) + '...');	//지정한 글자수 이후 표시할 텍스트 '...'
	               }
	           });

	       });

	       /*
	           2024-06-25 이재원 
	           탭 메뉴 : 전체 , 포지션 제안, 관심기업
	       */

	       $('.tabCon.All').click(function(){
	           $('.cardConWrap').css({"display":"none"});
	           $('.cardConWrap.All').css({"display":"flex"});
	       });

	       $('.tabCon.proposal').click(function(){
	           $('.cardConWrap').css({"display":"none"});
	           $('.cardConWrap.proposal').css({"display":"flex"});
	       });

	       $('.tabCon.post').click(function(){
	           $('.cardConWrap').css({"display":"none"});
	           $('.cardConWrap.post').css({"display":"flex"});
	       });

	       


	   });
	   // CSS JS ---------------------------------------------------

</script>
