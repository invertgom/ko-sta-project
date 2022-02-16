package com.reco.customer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.reco.customer.vo.Customer;
import com.reco.exception.AddException;
import com.reco.exception.FindException;
import com.reco.exception.ModifyException;


@Repository
public class CustomerDAOOracle implements CustomerDAOInterface {

	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void add(Customer customer) throws AddException {
		SqlSession session = null;
		
		try {
			logger.info("DAO로 전달된 customer"+customer.getUName()+customer.getUNickName()+customer.getUEmail()+customer.getUPwd());
			session = sqlSessionFactory.openSession(); //Connection : DB연결
			session.insert("com.reco.customer.CustomerMapper.add",customer);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		}finally {
			if(session !=null) {
				session.close();
			}
		}

	}

	@Override
	public Customer findByEmail(String uEmail) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Customer c= session.selectOne("com.reco.customer.CustomerMapper.findByEmail",uEmail);
			
			if(c != null) {				
				return c;
			}else {
				throw new FindException("이메일에 해당하는 고객이 없습니다");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session !=null) {
				session.close();
			}
		}
	}

	@Override
	public Customer findByNick(String uNickName) throws FindException {
		
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Customer c= session.selectOne("com.reco.customer.CustomerMapper.findByNick", uNickName);
			if(c != null) {				
				return c;
			}else {
				throw new FindException("닉네임에 해당하는 고객이 없습니다");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session !=null) {
				session.close();
			}
		}
	}

	@Override
	public void modifyStatus(int uIdx) throws ModifyException {
		
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.reco.customer.CustomerMapper.modifyStatus",uIdx);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		}finally {
			if(session !=null) {
				session.close();
			}
		}
	}
	
	@Override
	public void modifyPwd(int uIdx, String pwd) throws ModifyException {
		
		SqlSession session = null;
		try {
			Customer c = new Customer();
			c.setUIdx(uIdx);
			c.setUPwd(pwd);
			session = sqlSessionFactory.openSession();
			session.update("com.reco.customer.CustomerMapper.modifyPwd",c);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		}finally {
			if(session !=null) {
				session.close();
			}
		}
	}
	
	
	@Override
	public void findPwd(String email) throws ModifyException{
//		//네이버용
//		//발신자
//		String host = "smtp.naver.com"; // 네이버일 경우 네이버 계정, gmail경우 gmail 계정 
//		String user = email; 
//		String password = "password";      
//		
//		// SMTP 서버 정보를 설정한다. 
//		Properties props = new Properties(); 
//		props.put("mail.smtp.host", host); 
//		props.put("mail.smtp.port", 465); 
//		props.put("mail.smtp.auth", "true"); 
//		props.put("mail.smtp.ssl.enable", "true");
//		props.put("mail.smtp.ssl.trust", host);
//		Session session = Session.getInstance(props, new javax.mail.Authenticator() { 
//			protected PasswordAuthentication getPasswordAuthentication() { 
//				return new PasswordAuthentication(user, password); 
//				} 
//			}); 
//		
//		try { 
//			MimeMessage message = new MimeMessage(session); 
//			message.setFrom(new InternetAddress(user)); 
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email)); 
//			
//			// 메일 제목 
//			message.setSubject("테스트 이메일전송"); 
//			
//			// 메일 내용 
//			// 8자리의 대소문자,숫자가 합쳐진 난수발생
//			String newPwd= RandomStringUtils.randomAlphanumeric(8);
//			//생성된 난수로 비밀번호 변경
////			modifyPwd(findByEmail(email).getUIdx(), newPwd);
//			message.setText("테스트 이메일전송 성공"+newPwd); 
//			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
//			// send the message 
//			Transport.send(message); 
//			System.out.println("Success Message Send"); 
//		} 
//		catch (MessagingException e) { 
//			e.printStackTrace(); 
//		}//네이버용 이메일 보내기 end
////		catch (FindException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
		
		//지메일용 이메일전송 예제			
        String user = email; // 네이버일 경우 네이버 계정, gmail경우 gmail 계정
        String password = "password";   // 패스워드
        String host = "smtp.gmail.com";
        String sender = "schleech@gmail.com";
        // SMTP 서버 정보를 설정한다.
        Properties props = new Properties();
        props.put("mail.smtp.host", host); 
        props.put("mail.smtp.port", 587); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true"); 
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            //수신자메일주소
            message.setFrom(new InternetAddress(sender)); 
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email)); 

            // Subject
            message.setSubject("테스트 이메일전송"); //메일 제목을 입력

            // Text
			//8자리의 대소문자,숫자가 합쳐진 난수발생
            String newPwd= RandomStringUtils.randomAlphanumeric(8);
			//생성된 난수로 비밀번호 변경
//		  	modifyPwd(findByEmail(email).getUIdx(), newPwd);
            message.setText("테스트 이메일전송 성공"+newPwd);    //메일 내용을 입력

            // send the message
            Transport.send(message); ////전송
            System.out.println("message sent successfully...");
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } //지메일용 이메일 보내기 end
//        catch (FindException e) {
//			  e.printStackTrace();
//		}
	}

}
