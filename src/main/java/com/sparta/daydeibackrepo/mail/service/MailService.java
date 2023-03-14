package com.sparta.daydeibackrepo.mail.service;

import com.sparta.daydeibackrepo.mail.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    private String sender;

    public void sendMail(MailDto mailDto) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailDto.getEmail());
        message.setFrom(sender);
        message.setSubject(String.format("%S님의 비밀번호가 재설정되었습니다.", mailDto.getName()));
        message.setText(mailDto.getMessage());

        mailSender.send(message);
    }
}
