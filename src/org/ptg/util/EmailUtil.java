/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.ptg.admin.WebSiteConstants;


public class EmailUtil {
    public void SendMailWithAttachment(String from, String to, String subject, String msg, Map<String, String> attachements) {
        try {

            MultiPartEmail email = new MultiPartEmail();
            email.setHostName(WebSiteConstants.EMAIL_SERVER);
            email.setSmtpPort(Integer.valueOf(WebSiteConstants.EMAIL_PORT));
            email.addTo(to, to);
            email.setFrom(from, from);
            email.setSubject(subject);
            email.setMsg(msg);
            addAttachement(email, attachements);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public void SendMailWithAttachmentEx(String from, String to, String subject, String msg, Map<String, String> attachements) {
        try {

            MultiPartEmail email = new MultiPartEmail();
            email.setHostName(WebSiteConstants.EMAIL_SERVER);
            email.setSmtpPort(Integer.valueOf(WebSiteConstants.EMAIL_PORT));
            String[] names = StringUtils.split(to, ";,");
            if (names.length > 1) {
                for (String nameStr : names) {
                    email.addTo(nameStr);
                }
            } else {
                email.addTo(to);
            }
            email.setFrom(from);
            email.setSubject(subject);
            email.setMsg(msg);
            addAttachement(email, attachements);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    private void addAttachement(MultiPartEmail mpMail, Map<String, String> attachments) {
    	if(attachments!=null){
        for (Map.Entry<String, String> en : attachments.entrySet()) {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(en.getValue());
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription(en.getKey());
            attachment.setName(en.getKey());
            try {
                mpMail.attach(attachment);
            } catch (EmailException e) {
                System.out.println("Failed to add attachement adding another " + en.getValue());
                e.printStackTrace();
            }
        }
    	}

    }


}
