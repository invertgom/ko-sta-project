package com.reco.board.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.reco.board.service.BoardService;
import com.reco.board.vo.Board;
import com.reco.board.vo.Comment;
import com.reco.customer.vo.Customer;
import com.reco.dto.PageDTO;
import com.reco.dto.PageDTO2;
import com.reco.exception.AddException;
import com.reco.exception.FindException;
import com.reco.exception.ModifyException;
import com.reco.exception.RemoveException;
import com.reco.notice.service.NoticeService;
import com.reco.notice.vo.Notice;

import net.coobird.thumbnailator.Thumbnailator;
@Controller
public class BoardController {
	
	@Autowired
	private BoardService service;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Logger log = LoggerFactory.getLogger(BoardService.class.getName());
	
	@Autowired
	private NoticeService NoticeService;
	
	//??????????????? ????????? ??????
	@PostMapping("brdadd")
	public Object boardAdd( @RequestPart (required = false) MultipartFile letterFiles
							,@RequestPart (required = false) MultipartFile imageFile
							,int brdType,String brdTitle,String brdContent,String brdAttachment,HttpSession session, Model model) {
		Customer c = (Customer)session.getAttribute("loginInfo");
		
		String brdUNickName = c.getUNickName();
		Board b = new Board();
		b.setBrdType(brdType);
		b.setBrdTitle(brdTitle);
		b.setBrdContent(brdContent);
		b.setBrdUNickName(brdUNickName);
		if(letterFiles != null) {
			b.setBrdAttachment(letterFiles.getOriginalFilename());
		}else {
			b.setBrdAttachment(brdAttachment); //?
		}
		
		ModelAndView mnv = new ModelAndView();
		
		try{
			PageDTO2<Board> PageDTO2 = service.addBrd(b);
			Board board = PageDTO2.getBoard();
//			int exbrd = board.getBrdIdx();
//			System.out.println("?????????" + exbrd);
			mnv.addObject("PageDTO2", PageDTO2);
			logger.info("???????????? addbrd 1:" + board.getBrdIdx() + board.getBrdTitle() + board.getBrdContent());
			
			//????????? ????????? ????????? ????????? ?????????. ????????? ????????? ??????
			String saveDirectory = "C:\\reco\\boardimages";
			if ( ! new File(saveDirectory).exists()) {
				logger.info("????????? ??????????????????");
				new File(saveDirectory).mkdirs(); //??????????????? ????????? ??????
			}
			
			int wroteBoardNo = board.getBrdIdx();//????????? ?????????
					
			//??????????????? ??????
			File thumbnailFile = null;
			if(imageFile !=null) {
				long imageFileSize = imageFile.getSize();
					if(imageFileSize != 0) {
						String imageFileName = imageFile.getOriginalFilename(); //???????????? ????????? ????????? ???????????????
						logger.info("??????????????? ??????:" + imageFileName +" ??????????????? ????????? " + imageFile.getSize());
						
						//????????????  ????????? ????????? ????????? ????????????
						String fileName ="reco_board_"+wroteBoardNo + "_image_" + UUID.randomUUID() + "_" + imageFileName;
						//??????????????? ??????
						File file = new File(saveDirectory, fileName);
						
						
						try {
							FileCopyUtils.copy(imageFile.getBytes(), file);
							
							//?????????????????? ?????????????????? image??? ????????? ??????
							if(imageFile !=null) {
								String contentType = imageFile.getContentType();
								if(!contentType.contains("image/")) {  //???????????????????????? ?????? ??????
									mnv.setViewName("failresult.jsp");
								}
							}
						
					
						//?????????????????? ?????? ?????????????????? ??????
						String thumbnailName =  "reco_board_"+ wroteBoardNo+"_image_"+imageFileName; //????????? ???????????? reco_notice_?????????_image_????????????
						thumbnailFile = new File(saveDirectory,thumbnailName); //??????????????? ??????????????? ??????
						FileOutputStream thumbnailOS;
						thumbnailOS = new FileOutputStream(thumbnailFile);
						InputStream imageFileIS = imageFile.getInputStream();
						int width = 300;
						int height = 300;
						Thumbnailator.createThumbnail(imageFileIS, thumbnailOS, width, height);
						} catch (IOException e) {
							e.printStackTrace();
						}logger.info("??????????????? ??????:" + thumbnailFile.getAbsolutePath() + ", ??????????????? ??????::" + thumbnailFile.length());
					}
			  }
		
	    	//letterFiles??? ??????
			if(letterFiles != null) {
				long letterFileSize = letterFiles.getSize();
					if(letterFileSize > 0) {
						String letterOriginalFileName = letterFiles.getOriginalFilename();//letter?????? ???????????? ??????
						logger.info("?????? ????????????:" + letterFiles.getOriginalFilename()+" ????????????: " + letterFiles.getSize());
						//????????? ?????? ?????? ???????????? ex) reco_board_?????????_letter_XXXX_????????????
						String letterName = "reco_board_"+wroteBoardNo + "_letter_" + UUID.randomUUID() + "_" + letterOriginalFileName;
						//letter?????? ??????
						File file2 = new File(saveDirectory, letterName);
							try {
								FileCopyUtils.copy(letterFiles.getBytes(), file2);
							} catch (IOException e2) {
								e2.printStackTrace();		
								mnv.setViewName("failresult.jsp");
							}
					}//end if(letterFileSize > 0) 
				}//end for
			//end if(letterFiles != null)						
			logger.info("???????????? addbrd 2:" + board.getBrdIdx() + board.getBrdTitle() + board.getBrdContent());	
			File dir = new File(saveDirectory);
			if(b.getBrdAttachment() !=null) {
				//???????????? ??????????????? letters?????? ???????????? returnMap??? ??????
				String[] letterFileNames = dir.list(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.contains("reco_board_"+board.getBrdIdx()+"_letter_");//b.getBrdAttachment());
					}
				});
				if(letterFileNames.length>0) {
					mnv.addObject("letter", letterFileNames[0]);
				}
			}		
			
			//???????????? ??????????????? images?????? ???????????? returnMap??? ??????
			String[] imageFiles = dir.list(new FilenameFilter() {		
				@Override
				public boolean accept(File dir, String name) {
					return name.contains("reco_board_"+board.getBrdIdx()+"_image_");
				}
			});
			
			if(imageFiles.length > 0) {
				mnv.addObject("image", imageFiles[0]);
			}
		
			mnv.setViewName("boarddetailresult.jsp");
		} catch(AddException e){
			e.getStackTrace();
			model.addAttribute("msg", e.getMessage());
			mnv.setViewName("failresult.jsp");
		} catch (FindException e) {
			e.printStackTrace();
			model.addAttribute("msg", e.getMessage());
			mnv.setViewName("failresult.jsp");
		}
		return mnv;
	}
	
	
	//??????????????? ??????????????? 
		@GetMapping(value={"brddetail", "brddetail/{currentPage}"}) //model??????
		public Object boardDetail(@PathVariable Optional<Integer> currentPage, 
								  int brdIdx) {
			
			ModelAndView mnv = new ModelAndView();
			try {
				PageDTO2<Board> pageDTO2;
				if(currentPage.isPresent()) {
					log.info("???????????? ???????????????"+currentPage);
					log.info("????????????brdIdx" + brdIdx);
					int cp = currentPage.get();
					pageDTO2 = service.findBrdByIdx(brdIdx,cp);
					log.info("???????????????"+pageDTO2.getComments()+"???????????????"+pageDTO2.getCurrentPage());
					
				}else {
					pageDTO2= service.findBrdByIdx(brdIdx);
					log.info("findBrdByIdx????????????pageDTO2" + pageDTO2);
				}
				mnv.addObject("PageDTO2", pageDTO2);
				String saveDirectory = "C:\\reco\\boardimages";
				File dir = new File(saveDirectory);
				Board b = pageDTO2.getBoard();
				if(b.getBrdAttachment() !=null) {
				//???????????? ??????????????? letters?????? ???????????? returnMap??? ??????
				String[] letterFileNames = dir.list(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.contains("reco_board_"+brdIdx+"_letter_");
					}
				});
				if(letterFileNames.length>0) {
					mnv.addObject("letter", letterFileNames[0]);
				}
			}
				
				//???????????? ??????????????? images?????? ???????????? returnMap??? ??????
				String[] imageFiles = dir.list(new FilenameFilter() {	
					
					@Override
					public boolean accept(File dir, String name) {
						return name.contains("reco_board_"+brdIdx+"_image_");
					}
				});
				
				if(imageFiles.length>0) {
					mnv.addObject("image", imageFiles[0]);
				}
				mnv.setViewName("boarddetailresult.jsp");
			} catch (FindException e) {
				mnv.addObject("msg",e.getMessage());
				mnv.setViewName("failresult.jsp");
			}
			return mnv;
		}
		
		
		//??????????????? ??????
		@GetMapping(value = {"brdlist", "brdlist/{currentPage}"})
		public Object boardList(@PathVariable Optional<Integer> currentPage) {
			ModelAndView mnv = new ModelAndView();
			try {
				PageDTO<Board> pageDTO;
				if(currentPage.isPresent()) {
					log.info("???????????? ???????????????"+currentPage);
					int cp = currentPage.get();
					pageDTO= service.findBrdAll(cp);
					log.info("?????????"+pageDTO.getList()+"???????????????"+pageDTO.getCurrentPage());
					
				}else {
					pageDTO= service.findBrdAll();
				}
				mnv.addObject("pageDTO", pageDTO);
				
			
				mnv.setViewName("boardlistresult.jsp");
			} catch (FindException e) {
				e.printStackTrace();
				mnv.addObject("msg", e.getMessage());
				mnv.setViewName("boardlistresult.jsp");
			}
			return mnv;
		}
	
		
		
		@PostMapping("brdmodify") //???????????? ?????? ??????
		public String boardModify(@RequestPart (required = false) MultipartFile letterFiles,
								int brdIdx,int brdType, String brdTitle, String brdContent, String brdAttachment, Model model) throws FindException {
			try {
				Board b = new Board();
				b.setBrdIdx(brdIdx);
				b.setBrdType(brdType);
				b.setBrdTitle(brdTitle);
				b.setBrdContent(brdContent);
				
				
				
				
				//?????? DB??? ????????? ???????????? ?????? ????????????
				PageDTO2<Board> getFileNameDTO = service.findBrdByIdx(brdIdx);
				String originAttachment = getFileNameDTO.getBoard().getBrdAttachment();
				logger.info("???????????? ???????????? ?????? ??????"+letterFiles.getOriginalFilename());
				
				if(letterFiles.getOriginalFilename() == "") { //attachment no 
					b.setBrdAttachment(originAttachment);
					logger.info("????????????"+originAttachment);
				}else { //attachment 
					b.setBrdAttachment(letterFiles.getOriginalFilename());
				}
					service.modifyBrd(b);
					PageDTO2<Board> PageDTO2 = service.findBrdByIdx(brdIdx);
					Board board = PageDTO2.getBoard();
					
					model.addAttribute("PageDTO2", PageDTO2);
					//????????????????????? ???????????? ???
					
					//??????????????? ????????? ????????????
					String saveDirectory = "C:\\reco\\boardimages";
					int wroteBoardNo = board.getBrdIdx();//????????? ?????????
					
					
					//letterFiles ??????
					if(letterFiles != null) {
						long letterFileSize = letterFiles.getSize();
						if(letterFileSize > 0) {
							String letterOriginalFileName = letterFiles.getOriginalFilename();//letter?????? ???????????? ??????
							logger.info("?????? ????????????:" + letterFiles.getOriginalFilename()+" ????????????: " + letterFiles.getSize());
							//????????? ?????? ?????? ???????????? ex) reco_notice_?????????_letter_XXXX_????????????
							String letterName = "reco_notice_"+wroteBoardNo + "_letter_" + UUID.randomUUID() + "_" + letterOriginalFileName;
							//letter?????? ??????
							File file2 = new File(saveDirectory, letterName);
								try {
									FileCopyUtils.copy(letterFiles.getBytes(), file2);
								} catch (IOException e2) {
									e2.printStackTrace();		
									return "failresult.jsp";
								}
						}//end if(letterFileSize > 0) 
					}//end if(letterFiles != null)	
					
					File dir = new File(saveDirectory);
					if(board.getBrdAttachment() !=null) {
						//???????????? ??????????????? letters?????? ???????????? returnMap??? ??????
						String[] letterFileNames = dir.list(new FilenameFilter() {
							
							@Override
							public boolean accept(File dir, String name) {
								return name.contains("reco_notice_"+board.getBrdIdx()+"_letter_");//b.getBrdAttachment());
							}
						});
						if(letterFileNames.length>0) {
							model.addAttribute("letter", letterFileNames[0]);
						}
					}
					//???????????? ??????????????? images?????? ???????????? returnMap??? ??????
					String[] imageFiles = dir.list(new FilenameFilter() {	
						
						@Override
						public boolean accept(File dir, String name) {
							return name.contains("reco_board_"+brdIdx+"_image_");
						}
					});
					
					if(imageFiles.length>0) {
						model.addAttribute("image", imageFiles[0]);
					}
					return "boarddetailresult.jsp";
				} catch (ModifyException e) {
					e.printStackTrace();
					return "failresult.jsp";
				} catch (Exception e) { //??? findException ???????????????
					e.printStackTrace();
					return "failresult.jsp";
				}
			}

		
		@GetMapping("brdremove")
		public String boardRemove(int brdIdx, Model model) {
			try {
				service.removeBrd(brdIdx);
				PageDTO<Board> pageDTO;
				pageDTO = service.findBrdAll();
				model.addAttribute("pageDTO", pageDTO);
				return "boardlistresult.jsp";
			} catch (RemoveException | FindException e) {
				System.out.println(e.getMessage());
				model.addAttribute("msg", e.getMessage());
				return "boardlistresult.jsp";
			}
		}
		
		
		@GetMapping({"boardfilter/{intBrdType}", "boardfilter/{intBrdType}/{currentPage}"})
		public String boardFilter(@PathVariable int intBrdType, @PathVariable Optional<Integer> currentPage, Model model) { //f??? brdType
			
			PageDTO<Board> pageDTO;
			if(intBrdType == 0) {
				try {
					int cp = 1;
					if(currentPage.isPresent()) {
						cp = currentPage.get();
					}
					pageDTO = service.findBrdByType(intBrdType,cp); 
					model.addAttribute("pageDTO",pageDTO);
					return "boardlistresult.jsp";
				} catch (FindException e) {
					e.printStackTrace();
					return "failresult.jsp";
				}
			}else if(intBrdType == 1) {
				try {
					int cp = 1;
					if(currentPage.isPresent()) {
						cp = currentPage.get();
					}
					pageDTO = service.findBrdByType(intBrdType,cp); 
					model.addAttribute("pageDTO",pageDTO);
					return "boardlistresult.jsp";
				} catch (FindException e) {
					e.printStackTrace();
					return "failresult.jsp";
				}
			}else if(intBrdType == 2) {
				try {
					int cp = 1;
					if(currentPage.isPresent()) {
						cp = currentPage.get();
					}
					pageDTO = service.findBrdByType(intBrdType,cp); 
					model.addAttribute("pageDTO",pageDTO);
					return "boardlistresult.jsp";
				} catch (FindException e) {
					e.printStackTrace();
					return "failresult.jsp";
				}
			}else {
				intBrdType = 3;
				try {
					int cp = 1;
					if(currentPage.isPresent()) {
						cp = currentPage.get();
					}
					pageDTO = service.findBrdAll(); 
					model.addAttribute("pageDTO",pageDTO);
					return "boardlistresult.jsp";
				} catch (FindException e) {
					e.printStackTrace();
					return "failresult.jsp";
				}
			}
		}
			
		
		//??????????????? ??????
		@GetMapping(value = {"brdsearch/{word}/{f}","brdsearch/{word}/{f}/{currentPage}"})
		public Object boardSearch(@PathVariable Optional<String> word, @PathVariable String f,@PathVariable Optional<Integer> currentPage ,Model model) {
			ModelAndView mnv = new ModelAndView();
			PageDTO<Board> pageDTO;
			logger.info("search???" + f);
			if(f.equals("brd_title")) {
				try {
					String w = "";
					if(word.isPresent()) { 
						w = word.get();
					}
					int cp = 1;
					if(currentPage.isPresent()) { //currentPage
						cp = currentPage.get();
					}
					pageDTO = service.findBrdByTitle(w,f,cp); 
					mnv.addObject("pageDTO", pageDTO);
					mnv.setViewName("boardlistresult.jsp");
				} catch (FindException e) {
					e.printStackTrace();
					mnv.addObject("msg", e.getMessage());
					mnv.setViewName("boardlistresult.jsp");
				}
				
			}else if(f.equals("brd_content")) {
				try {
					String w = "";
					if(word.isPresent()) { 
						w = word.get();
					}
					int cp = 1;
					if(currentPage.isPresent()) { //currentPage
						cp = currentPage.get();
					}
					pageDTO = service.findBrdByWord(w,f,cp); 
					mnv.addObject("pageDTO", pageDTO);
					mnv.setViewName("boardlistresult.jsp");
				} catch (FindException e) {
					e.printStackTrace();
					mnv.addObject("msg", e.getMessage());
					mnv.setViewName("boardlistresult.jsp");
				}
			}else {
				f = "brd_UNickName";
				try {
					String w = "";
					if(word.isPresent()) { 
						w = word.get();
					}
					int cp = 1;
					if(currentPage.isPresent()) { //currentPage
						cp = currentPage.get();
					}
					pageDTO = service.findBrdByUNickName(w,f,cp); 
					mnv.addObject("pageDTO", pageDTO);
					mnv.setViewName("boardlistresult.jsp");
				} catch (FindException e) {
					e.printStackTrace();
					mnv.addObject("msg", e.getMessage());
					mnv.setViewName("boardlistresult.jsp");
				}
			}
			return mnv;
		}
		
	
		
		
		@GetMapping("/boarddownload")
		public ResponseEntity<Resource>  download(String fileName) throws UnsupportedEncodingException {
			logger.info("???????????? ????????????");
			//?????? ????????????
			String saveDirectory = "C:\\reco\\boardimages";
			
			//HttpHeaders : ??????/??????????????? API
			HttpHeaders headers = new HttpHeaders();	
			headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream;charset=UTF-8");
			//??????????????? ???????????? ??????
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			//Resource : ??????(??????, URL)??? API
				//??????????????? ????????? ?????? ?????? ??????
				File f = new File(saveDirectory, fileName);		
				Resource resource = new FileSystemResource(f);
				try {
					logger.info("??????????????????: " + resource.getFilename());
					logger.info("????????????resource.contentLength()=" + resource.contentLength());
				} catch (IOException e) {
					e.printStackTrace();
				}
					
				ResponseEntity<Resource> responseEntity  =  
						new ResponseEntity<>(resource, headers, HttpStatus.OK);
				return responseEntity;
		}
		
		
		@GetMapping("/boarddownloadimage") 
		 public ResponseEntity<?> downloadImage(String imageFileName) throws UnsupportedEncodingException{
			 String saveDirectory = "C:\\reco\\boardimages";
			 File thumbnailFile = new File(saveDirectory,imageFileName);
			 HttpHeaders responseHeaders = new HttpHeaders();
			 try {
				 responseHeaders.set(HttpHeaders.CONTENT_LENGTH, thumbnailFile.length()+"");
				 responseHeaders.set(HttpHeaders.CONTENT_TYPE, Files.probeContentType(thumbnailFile.toPath()));
				 responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+URLEncoder.encode("a", "UTF-8"));
				 logger.info("??????????????? ????????????");
				 return new ResponseEntity<>(FileCopyUtils.copyToByteArray(thumbnailFile), responseHeaders, HttpStatus.OK);//success function ??????
			 }catch(IOException e) {
				 return new ResponseEntity<>("????????? ???????????? ??????",HttpStatus.INTERNAL_SERVER_ERROR);
			 }
		}
		
		
		@PostMapping("cmtadd")
		public String commentAdd(int brdIdx, String cmtContent, int cmtParentIdx, HttpSession session, Model model) {
			Customer c = (Customer)session.getAttribute("loginInfo");
			String cmtUNickName = c.getUNickName();
			Comment comment = new Comment();
			//comment.setBrdIdx(brdIdx);
			Board brd = new Board();
			brd.setBrdIdx(brdIdx);
			comment.setBrd(brd);
			comment.setCmtContent(cmtContent);	
			comment.setCmtParentIdx(cmtParentIdx);	
			comment.setCmtUNickName(cmtUNickName);
			
			try{
				PageDTO2<Board> PageDTO2 = service.addCmt(comment);
			    model.addAttribute("PageDTO2", PageDTO2);
			    return "boarddetailresult.jsp";
			}catch(AddException | FindException e){
				e.getStackTrace();
				return "failresult.jsp";
			}
		}
		
		@GetMapping("cmtmodify") //???????????? ?????? ??????
		public String commentModify(int brdIdx, int cmtIdx, String cmtContent, Model model) {
			Comment comment = new Comment();
			//comment.setBrdIdx(brdIdx);
			Board brd = new Board();
			brd.setBrdIdx(brdIdx);
			comment.setBrd(brd);
			comment.setCmtIdx(cmtIdx);
			comment.setCmtContent(cmtContent);
			
			try {
				service.modifyCmt(comment);
				PageDTO2<Board> PageDTO2 = service.findBrdByIdx(brdIdx);
				model.addAttribute("PageDTO2", PageDTO2);
				return "boarddetailresult.jsp";
			} catch (ModifyException e) {
				e.getStackTrace();
				return "failresult.jsp";
			} catch (FindException e) {
				e.getStackTrace();
				return "failresult.jsp";
			}			
		}
		
		
		@GetMapping("cmtremove") //???????????? ?????? ??????
		public String commentRemove(int brdIdx, int cmtIdx, Model model) {
			try {
				service.removeCmt(brdIdx, cmtIdx);
				PageDTO2<Board> PageDTO2 = service.findBrdByIdx(brdIdx);
				model.addAttribute("PageDTO2", PageDTO2);
				return "boarddetailresult.jsp";
			} catch (RemoveException e) {
				e.getStackTrace();
				return"failresult.jsp";
			} catch (FindException e) {
				e.getStackTrace();
				return"failresult.jsp";
			}
		}
		
		//????????????????????? ????????? ????????????????????? ???????????? ????????????
		@GetMapping("mybrdremove")
		public Object boardRemove(int brdIdx0, Optional<Integer> brdIdx1, Optional<Integer> brdIdx2, Optional<Integer> brdIdx3, Optional<Integer> brdIdx4, Model model) throws FindException {
			ModelAndView mnv = new ModelAndView();
		try {
			String uNickname = service.findBrdByIdx(brdIdx0).getBoard().getBrdUNickName();
		
			PageDTO<Notice> noticePageDTO;
			PageDTO<Board> boardPageDTO;
			PageDTO2<Board> commentPageDTO;
			int cp = 1;
			
					service.removeBrd(brdIdx0);
					if(brdIdx1.isPresent()) {
						service.removeBrd(brdIdx1.get());
					}
					if(brdIdx2.isPresent()) {
						service.removeBrd(brdIdx2.get());
					}
					if(brdIdx3.isPresent()) {
						service.removeBrd(brdIdx3.get());
					}
					if(brdIdx4.isPresent()) {
						service.removeBrd(brdIdx4.get());
					}
				noticePageDTO = NoticeService.findNtcByNickname(uNickname, cp, PageDTO.CNT_PER_PAGE);
				mnv.addObject("noticePageDTO", noticePageDTO);
				boardPageDTO = service.findBrdByUNickNameMy(uNickname, cp, PageDTO.CNT_PER_PAGE);				
				mnv.addObject("boardPageDTO",boardPageDTO);
				commentPageDTO = service.findCmtByUNickName(uNickname, cp, PageDTO2.CNT_PER_PAGE);
				mnv.addObject("commentPageDTO", commentPageDTO);
				mnv.setViewName("mycommunity.jsp");
			} catch (RemoveException e) {
				System.out.println(e.getMessage());
//				model.addAttribute("msg", e.getMessage());
				mnv.setViewName("mycommunity.jsp");
			}
			return mnv;
		}
		
		
		//??????????????? ?????????????????? ????????? ????????????
		@GetMapping("mybrd/{uNickname}/{currentPage}")
		public Object myBrd(@PathVariable String uNickname, @PathVariable int currentPage ,Model model){
			ModelAndView mnv = new ModelAndView();
			PageDTO<Notice> noticePageDTO;
			PageDTO<Board> boardPageDTO;
			PageDTO2<Board> commentPageDTO;
			int cp = 1;
			try {
				boardPageDTO = service.findBrdByUNickNameMy(uNickname, currentPage, PageDTO.CNT_PER_PAGE);
				mnv.addObject("boardPageDTO", boardPageDTO);
				
				noticePageDTO = NoticeService.findNtcByNickname(uNickname, cp, PageDTO.CNT_PER_PAGE);
				mnv.addObject("noticePageDTO", noticePageDTO);
								
				commentPageDTO = service.findCmtByUNickName(uNickname, cp, PageDTO2.CNT_PER_PAGE);
				mnv.addObject("commentPageDTO", commentPageDTO);
				
				mnv.setViewName("mycommunity.jsp");
			} catch (FindException e) {
				e.printStackTrace();
				mnv.addObject("msg", e.getMessage());
				mnv.setViewName("mycommunity.jsp");
			}
			return mnv;
		}
		
		//??????????????? ?????? ????????? ????????????
				@GetMapping("mycmt/{uNickname}/{currentPage}")
				public Object mycmt(@PathVariable String uNickname, @PathVariable int currentPage ,Model model){
					ModelAndView mnv = new ModelAndView();
					PageDTO<Notice> noticePageDTO;
					PageDTO<Board> boardPageDTO;
					PageDTO2<Board> commentPageDTO;	
					int cp = 1;

					try {
						boardPageDTO = service.findBrdByUNickNameMy(uNickname, cp, PageDTO.CNT_PER_PAGE);
						mnv.addObject("boardPageDTO", boardPageDTO);
						
						noticePageDTO = NoticeService.findNtcByNickname(uNickname, cp, PageDTO.CNT_PER_PAGE);
						mnv.addObject("noticePageDTO", noticePageDTO);
						
						commentPageDTO = service.findCmtByUNickName(uNickname, currentPage, PageDTO2.CNT_PER_PAGE);
						mnv.addObject("commentPageDTO", commentPageDTO);
						mnv.setViewName("mycommunity.jsp");
					} catch (FindException e) {
						e.printStackTrace();
						mnv.addObject("msg", e.getMessage());
						mnv.setViewName("mycommunity.jsp");
					}
					return mnv;
				}
				
				
				//????????????????????? ????????? ????????? ???????????? ????????????
				@GetMapping("mycmtremove/{uNickname}")
				public Object commentRemove(@PathVariable String uNickname,
											Optional<Integer> brdIdx0,  Optional<Integer> cmtIdx0, 
											Optional<Integer> brdIdx1,  Optional<Integer> cmtIdx1,
											Optional<Integer> brdIdx2,  Optional<Integer> cmtIdx2,
											Optional<Integer> brdIdx3,  Optional<Integer> cmtIdx3,
											Optional<Integer> brdIdx4,  Optional<Integer> cmtIdx4) {
					
//					
					ModelAndView mnv = new ModelAndView();
					PageDTO<Notice> noticePageDTO;
					PageDTO<Board> boardPageDTO;
					PageDTO2<Board> commentPageDTO;
					int cp = 1;
//					
					int brdIdxMy0 = 0;
					int cmtIdxMy0 = 0;
					
					int brdIdxMy1 = 0;
					int cmtIdxMy1 = 0;
					
					int brdIdxMy2 = 0;
					int cmtIdxMy2 = 0;
					
					int brdIdxMy3 = 0;
					int cmtIdxMy3 = 0;
					
					int brdIdxMy4 = 0;
					int cmtIdxMy4 = 0;
					
					try {			
						 	if(brdIdx0.isPresent() & cmtIdx0.isPresent()) {
						 		brdIdxMy0 = brdIdx0.get();
						 		cmtIdxMy0 = cmtIdx0.get();
								service.removeCmt(brdIdxMy0, cmtIdxMy0);
						 	}
							
							if(brdIdx1.isPresent() & cmtIdx1.isPresent()) {
						 		brdIdxMy1 = brdIdx1.get();
						 		cmtIdxMy1 = cmtIdx1.get();
								service.removeCmt(brdIdxMy1, cmtIdxMy1);
						 	}
							
							if(brdIdx2.isPresent() & cmtIdx2.isPresent()) {
						 		brdIdxMy2 = brdIdx2.get();
						 		cmtIdxMy2 = cmtIdx2.get();
								service.removeCmt(brdIdxMy2, cmtIdxMy2);
						 	}
							
							if(brdIdx3.isPresent() & cmtIdx3.isPresent()) {
						 		brdIdxMy3 = brdIdx3.get();
						 		cmtIdxMy3 = cmtIdx3.get();
								service.removeCmt(brdIdxMy3, cmtIdxMy3);
						 	}
							
							if(brdIdx4.isPresent() & cmtIdx4.isPresent()) {
						 		brdIdxMy4 = brdIdx4.get();
						 		cmtIdxMy4 = cmtIdx4.get();
								service.removeCmt(brdIdxMy4, cmtIdxMy4);
						 	}
							
							noticePageDTO = NoticeService.findNtcByNickname(uNickname, cp, PageDTO.CNT_PER_PAGE);
							mnv.addObject("noticePageDTO", noticePageDTO);
							
							boardPageDTO = service.findBrdByUNickNameMy(uNickname, cp, PageDTO.CNT_PER_PAGE);				
							mnv.addObject("boardPageDTO",boardPageDTO);
							
							commentPageDTO = service.findCmtByUNickName(uNickname, cp, PageDTO2.CNT_PER_PAGE);
							mnv.addObject("commentPageDTO", commentPageDTO);
							mnv.setViewName("mycommunity.jsp");
					} catch (RemoveException e) {
						System.out.println(e.getMessage());
						mnv.addObject("msg", e.getMessage());
						mnv.setViewName("mycommunity.jsp");
					} catch (FindException e) {
						e.printStackTrace();
						mnv.addObject("msg", e.getMessage());
						mnv.setViewName("mycommunity.jsp");
					}
					return mnv;
				}
					
}
