package com.sunmax.together.util;

import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Configuration
@Slf4j
public class EmailUtil {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private MailProperties mailProperties;

    /**
     * 发送带附件的邮件
     *
     * @param to        收件人邮箱
     * @param subject   主题
     * @param text      正文（支持HTML）
     * @param file 文件
     */
    public Boolean sendEmail(String to, String subject, String text, MultipartFile file) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true = 支持 HTML
            helper.setFrom(mailProperties.getUsername()); // 发件人（必须是你的企业邮箱）

            if (file != null && !file.isEmpty() && StringUtil.isNotEmpty(file.getOriginalFilename())) {
                // 添加附件
                helper.addAttachment(file.getOriginalFilename(), file);
            }

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            log.error("邮件发送失败", e);
            return false;
        }
    }

}
