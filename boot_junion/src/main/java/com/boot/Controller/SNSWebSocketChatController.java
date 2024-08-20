package com.boot.Controller;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.boot.DTO.SNSChat;
import com.boot.DTO.SNSRoom;
import com.boot.Service.SNSChatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SNSWebSocketChatController {
	
	private final SNSChatService chatService;
	
    public SNSWebSocketChatController(SNSChatService chatService) {
        this.chatService = chatService;
    }
    
    // 채팅 화면을 보여주는 메서드
    @GetMapping("/SNSChat")
    public String chatView(@RequestParam String receiver_id, Model model, HttpServletRequest request) {
    	log.info("@# chatView");
    	HttpSession session = request.getSession();
    	
    	if ((String)session.getAttribute("login_email") == null) {
    		return "redirect:login";
		} else {
	    	String login_email = (String)session.getAttribute("login_email");
	    	log.info("@# login_email=>"+login_email);
	    	
	    	model.addAttribute("receiver_id",receiver_id);
	    	
	    	String receiverName = chatService.getUserName(receiver_id);
	    	model.addAttribute("receiverName",receiverName);
	    	
	    	int roomcheck = chatService.checkRooms(login_email, receiver_id);
	    	
	    	if (roomcheck != 1) {
				SNSRoom room = new SNSRoom();
				room.setSenderId(login_email);
				room.setReceiverId(receiver_id);
				log.info("@# room=>"+room);
				
				chatService.createRoom(room);
				int roomNum = chatService.getRooms(login_email, receiver_id);
				model.addAttribute("roomNum",roomNum);
				
				log.info("@# room=>"+room);
			} else {
				int roomNum = chatService.getRooms(login_email, receiver_id);
				model.addAttribute("roomNum",roomNum);
			}
	    	
	    	return "SNSChat"; // JSP 파일명 (chat.jsp)
		}
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/sub/public")
//    public SNSChat sendMessage(SNSChat chat) {
    public SNSChat sendMessage(@Payload SNSChat chat) {
    	log.info("@# sendMessage");
        chat.setTimestamp(new Timestamp(System.currentTimeMillis()));
        chatService.addMessage(chat);
        log.info("@# chat=>"+chat);
        return chat;
    }
}
