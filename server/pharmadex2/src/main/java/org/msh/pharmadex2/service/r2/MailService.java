package org.msh.pharmadex2.service.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.dto.AboutDTO;
import org.msh.pharmadex2.dto.AskForPass;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
/**
 * create and send email
 * @author khomenska
 *
 */
@Service
public class MailService {
	private static final Logger logger = LoggerFactory.getLogger(MailService.class);

	@Autowired
	private Messages messages;
	@Autowired
    private JavaMailSender emailSender;

	/**
	 * create Attention Letter
	 * @param link 
	 */
	public String createAttentionMail(UserDetailsDTO user, String sendTo, String applName, 
			String curActivity, String nextActivity, String textInMail, String link)  {
		String res = "";
		if(user.getEmail().endsWith("@gmail.com")) {
			JavaMailSenderImpl impl = (JavaMailSenderImpl) emailSender;
			if(impl.getUsername() != null) {
				SimpleMailMessage mailMess = new SimpleMailMessage(); 
				mailMess.setFrom(impl.getUsername());
		        mailMess.setTo(sendTo);
		        mailMess.setSubject(messages.get("mailAttentionSubj"));
		        String text = "";
		        if(textInMail != null && textInMail.length() > 0) {
		        	text = textInMail;
		        }else {//Application ****. The process is complete ????. Submitted to process &&&&.
		        	text = messages.get("mailAttentionFull");
				    text = text.replace("****", applName);
				    text = text.replace("????", curActivity);
				    text = text.replace("&&&&", nextActivity);
				    text += " " + messages.get("mailAttentionText");
		        }
		        text+="\n"+link;
		        mailMess.setText(text);
		        try {
		        	emailSender.send(mailMess);
		        	res = messages.get("mailSentApplicant");
		        }catch(Exception e) {
		        	logger.info(e.getMessage() + " "+sendTo);
		        	res=messages.get("errorMailServiceSettings");
		        }
			}else {
				logger.info(messages.get("errorPropertyMail"));
				res=messages.get("errorPropertyMail");
			}
		}
		return res;
	}
	/**
	 * Send test eMail to supervisor's address
	 * @param user 
	 * @param message
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public AboutDTO testMail(UserDetailsDTO user, AboutDTO message) throws ObjectNotFoundException {
		message.clearErrors();
		if(user.getEmail().endsWith("@gmail.com")) {
			SimpleMailMessage mailMess = new SimpleMailMessage(); 
			JavaMailSenderImpl impl = (JavaMailSenderImpl) emailSender;
			mailMess.setFrom(impl.getUsername());
	        mailMess.setTo(user.getEmail());
	        mailMess.setSubject(messages.get("Test"));
	        mailMess.setText(messages.get("Test"));
	        try {
	        	emailSender.send(mailMess);
	        	message.setValid(true);
	        	message.setIdentifier(messages.get("send_success")+" "+user.getEmail());
	        }catch(Exception e) {
	        	message.setValid(false);
				message.setIdentifier(e.getMessage()+" "+user.getEmail());
	        }
		}else {
			message.setValid(false);
			message.setIdentifier(messages.get("invalid_email")+" "+user.getEmail());
		}
		return message;
	}
	/**
	 * Send a password
	 * @param data
	 * @return
	 */
	public AskForPass temporaryPasswordSend(AskForPass data) {
		SimpleMailMessage mailMess = new SimpleMailMessage(); 
		JavaMailSenderImpl impl = (JavaMailSenderImpl) emailSender;
		mailMess.setFrom(impl.getUsername());
        mailMess.setTo(data.getEmail());
        mailMess.setSubject(messages.get("temp_password"));
        mailMess.setText(data.getTp());
        try {
        	emailSender.send(mailMess);
        	data.setIdentifier(messages.get("send_password_success"));
        }catch(Exception e) {
        	data.addError(e.getMessage());
        }
		return data;
	}
	

}
