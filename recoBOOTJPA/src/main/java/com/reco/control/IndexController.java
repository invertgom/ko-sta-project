package com.reco.control;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.reco.dto.PageDTO;
import com.reco.exception.FindException;
import com.reco.notice.service.NoticeService;
import com.reco.notice.vo.Notice;

@Controller
public class IndexController {

	
		@Autowired
		private NoticeService service;	
	
		@GetMapping("/")
		public String index() {
			return "index.jsp";
		}
		
//		@GetMapping("/noticewritepage")
//		public String noticewrite() {
//			return "noticewrite.jsp";
//		}
		
		@GetMapping("/noticemodifypage")
		public String noticemodify(int ntcIdx,Model model) {
			String saveDirectory = "C:\\230\\msa_boot_project\\recoBOOTJPA\\src\\main\\resources\\static\\images\\noticeimages";
			File dir = new File(saveDirectory);
			//첨부파일 저장소에서 images이름 가져와서 returnMap에 넣기
			String[] imageFiles = dir.list(new FilenameFilter() {		
				@Override
				public boolean accept(File dir, String name) {
					return name.contains("reco_notice_"+ntcIdx+"_image_");
				}
			});
			
			if(imageFiles.length>0) {
				model.addAttribute("image", imageFiles[0]);
			}
			return "noticemodify.jsp";
		}
		
		@GetMapping("/boardwritepage")
		public String boardwite() {
			return "boardwrite.jsp";
		}
		
		@GetMapping("/boardmodifypage")
		public String boardmodify(int brdIdx,Model model) {
			String saveDirectory = "C:\\230\\msa_boot_project\\recoBOOTJPA\\src\\main\\resources\\static\\images\\boardimages";
			File dir = new File(saveDirectory);
			//첨부파일 저장소에서 images이름 가져와서 returnMap에 넣기
			String[] imageFiles = dir.list(new FilenameFilter() {		
				@Override
				public boolean accept(File dir, String name) {
					return name.contains("reco_board_"+brdIdx+"_image_");
				}
			});
			if(imageFiles.length>0) {
				model.addAttribute("image", imageFiles[0]);
			}
			return "boardmodify.jsp";
		}
		
		@GetMapping("/pwdcheck")
		public String pwdcheck(HttpSession session) {
			if(session.getAttribute("myPage") == null) {
				return "pwdcheck.jsp";
			}else {
				return "mycallist.jsp";
			}
		}
		
		@GetMapping("/mycallist")
		public String mycallist(HttpSession session) {
			session.setAttribute("myPage", session);
			return "mycallist.jsp";
		}		
		
		/*
		 * @GetMapping("/mycommunity") public String mycommunity() {
		 *  return "mycommunity.jsp"; 
		 *  }
		 */
		
		//나의 공지사항 글 보는 컨트롤러
		@GetMapping(value = {"mycommunity/{uNickname}", "myntc/{uNickname}/{currentPage}"})
		public Object myNtc(@PathVariable String uNickname, @PathVariable Optional<Integer> currentPage ,Model model){
			ModelAndView mnv = new ModelAndView();
			PageDTO<Notice> pageDTO;

			try {
				int cp = 1;
				if(currentPage.isPresent()) { //currentPage
					cp = currentPage.get();
				}
				pageDTO = service.findNtcByNickname(uNickname, cp, PageDTO.CNT_PER_PAGE);
				mnv.addObject("noticePageDTO", pageDTO);
				mnv.setViewName("mycommunity.jsp");
			} catch (FindException e) {
				e.printStackTrace();
				mnv.addObject("msg", e.getMessage());
				mnv.setViewName("mycommunity.jsp");
			}
			return mnv;
		}
		
		@GetMapping("/myprivate")
		public String myprivate() {
			return "myprivate.jsp";
		}
		@GetMapping("/findEmailPage")
		public String findEmailPage() {
			return "findEmail.jsp";
		}
		
}
