package com.sprk.service.user.util.email;

import com.sprk.commons.exception.*;
import com.sprk.service.user.util.TextHelper;
import com.sprk.service.user.util.logger.LoggerManager;
import com.sprk.service.user.util.logger.LoggerModel;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.mail.internet.MimeMessage;
import java.io.File;





@Component
public class EmailUtils {

    private final JavaMailSender javaMailSender;
    private final TextHelper textHelper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public EmailUtils(
            JavaMailSender javaMailSender,
            TextHelper textHelper
    ) {
        this.javaMailSender = javaMailSender;
        this.textHelper = textHelper;
    }

    @Value("${spring.mail.username}")
    private String senderMailAddress;

    @Value("${app.email.verify.endpoint}")
    private String mailVerificationEndpoint;

    @Value("${app.email.verify.key}")
    private String mailVerificationKey;




    public boolean sendMail(String recipient) {
        return mailSender(recipient, "no-reply", null, false, null);
    }
    public boolean sendMail(String recipient, String subject) {
        return mailSender(recipient, subject, null, false, null);
    }
    public boolean sendMail(String recipient, String subject, String messageBody) {
        return mailSender(recipient, subject, messageBody, false, null);
    }
    public boolean sendMail(String recipient, String subject, String messageBody, boolean isHtml) {
        return mailSender(recipient, subject, messageBody, isHtml, null);
    }
    public boolean sendMail(String recipient, String subject, String messageBody, String attachmentPath) {
        return mailSender(recipient, subject, messageBody, false, attachmentPath);
    }
    public boolean sendMail(String recipient, String subject, String messageBody, boolean isHtml, String attachmentPath) {
        return mailSender(recipient, subject, messageBody, isHtml, attachmentPath);
    }



    public void sendEmployeeCreationMail(
            String recipientName,
            String recipientEmail,
            String link
    ) {
        String subject = "🔴 Employee Registration Form Submission Link.";
        String messageBody = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <title>" + subject + "</title>\n" +
                "  <style>\n" +
                "    table {\n" +
                "      border-collapse: collapse;\n" +
                "      margin: auto;\n" +
                "    }\n" +
                "    .mainTable\n" +
                "    {\n" +
                "      border: 3px solid  #06375b;\n" +
                "      width: 70%;\n" +
                "    }\n" +
                "    .letterTable{\n" +
                "      width: 90%;\n" +
                "    }\n" +
                "    td {\n" +
                "      padding: 10px;\n" +
                "      text-align: left;\n" +
                "      vertical-align: top;\n" +
                "    }\n" +
                "    .letterHeading {\n" +
                "      font-size: 24px;\n" +
                "      font-weight: bold;\n" +
                "      text-align: center;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .boldText {\n" +
                "      font-weight: bold;\n" +
                "    }\n" +
                "    .button {\n" +
                "      display: inline-block;\n" +
                "      padding: 10px 20px;\n" +
                "      background-color: #0074bd;\n" +
                "      color: #fff;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 4px;\n" +
                "    }\n" +
                "    .topHead {\n" +
                "      background-color: #06375b;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .logo {\n" +
                "      width: 200px;\n" +
                "      height: auto;\n" +
                "      display: block;\n" +
                "      margin: 20px auto;\n" +
                "    }\n" +
                "    .socialIcon {\n" +
                "      width: 20px;\n" +
                "      height: auto;\n" +
                "      vertical-align: middle;\n" +
                "      margin-right: 5px;\n" +
                "    }\n" +
                "    .footerText {\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    @media (max-width: 768px) {\n" +
                "     .mainTable {\n" +
                "     width: 100%;\n" +
                "      }\n" +
                "       .letterHeading {\n" +
                "       font-size: 14px;\n" +
                "      font-weight: normal;\n"+
                "      }\n" +
                "      p {\n"+
                "       font-size: 10px;\n" +
                "      }\n" +
                "  td {\n" +
                "   padding: 0px;\n" +
                "  }\n" +
                "   .button {\n" +
                "    display: inline-block;\n" +
                "    padding: 5px 10px;\n" +
                "    background-color: #0074bd;\n" +
                "  }\n" +
                "   .socialIcon {\n" +
                "   width: 12px;\n" +
                "   height: auto;\n" +
                "    }\n" +
                "   }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">\n" +
                "  <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" align=\"center\" class=\"mainTable\">\n" +
                "    <tr>\n" +
                "      <td class=\"topHead\">\n" +
                "        <img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518261/sprk_logo_registered__10_rxgocl.png\"      cloudName=\"dxlzzgbfw\"\n" +
                "        class=\"logo\" />\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td>\n" +
                "        <p class=\"letterHeading\">Employee Onboarding</p>\n" +
                "        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" class=\"letterTable\">\n" +
                "          <tr>\n" +
                "            <td>\n" +
                "              <p class=\"boldText\">Dear " + recipientName + ",</p>\n" +
                "              <p>\n" +
                "                We extend a warm welcome to you at SPRK Technologies. In order to\n" +
                "                facilitate a seamless onboarding process and provide you with access\n" +
                "                to our internal portal, we kindly request you to complete the Portal\n" +
                "                Access Form linked below:\n" +
                "              </p>\n" +
                "              <a href=\"" + link + "\" > <p class=\"button\">\n" +
                "                CLICK HERE</p></a>\n" +
                "              <p>\n" +
                "                Please note that the provided link will expire within 72 hours. To\n" +
                "                ensure a smooth transition, we encourage you to fill out the form at\n" +
                "                your earliest convenience.\n" +
                "              </p>\n" +
                "              <p>\n" +
                "                Should you require any assistance or support during this process,\n" +
                "                please do not hesitate to contact us via email at\n" +
                "                <a href=\"mailto:sprktechnologies.kharghar@gmail.com \">sprktechnologies.kharghar@gmail.com</a>\n" +
                "              </p>\n" +
                "              <p>\n" +
                "                We look forward to having you as a valuable member of our team.\n" +
                "              </p>\n" +
                "              <p class=\"boldText\">Best regards,</p>\n" +
                "              <p>SPRK Technologies</p>\n" +
                "              <p class=\"footerText\">\n" +
                "                <a href=\"https://sprktechnologies.in/home\">SPRK Website</a> | <a href=\"http://wa.me/919082572832?text=Hello \">Contact Us</a> | <a href=\"mailto:sprktechnologies.kharghar@gmail.com \">Support</a>\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" class=\"letterTable\">\n" +
                "          <tr>\n" +
                "            <td>\n" +
                "              <p class=\"boldText\">Social Handles</p>\n" +
                "              <p>Follow us on <a href=\"https://www.instagram.com/sprktech/?hl=en\"><img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/skill-icons_instagram_iee7mt.png\" class=\"socialIcon\" />@sprktech</a></p>\n" +
                "              <p>Contact us on <a href=\"https://in.linkedin.com/company/sprk-technologies\"><img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/devicon_linkedin_hfekat.png\" class=\"socialIcon\" />SPRK Technologies</a></p>\n" +
                "              <p class=\"footerText\">\n" +
                "                Address: SPRK Technologies, Plot No-11 Opposite:, Glomax Mall, Office:102-104,1st Floor, Royal Palace, Sector 2, Kharghar, Navi Mumbai, Maharashtra 410210 Contact us : 090825 72832/ 8425840175;\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr> \n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>";

        if (!sendMail(recipientEmail, subject, messageBody, true))
            throw new EmailDispatcherException("Failed to send email to the " + recipientEmail);
    }

    public void sendEmployeeAccessApprovalMail(
            String recipientName,
            String recipientEmail,
            String employeeId,
            String strongPassword
    ) {
        String subject = "🎉 Employee Portal Access Approved.";
        String messageBody = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <title>" + subject + "</title>\n" +
                "    <style>\n" +
                "      table {\n" +
                "        border-collapse: collapse;\n" +
                "        margin: auto;\n" +
                "      }\n" +
                "      .mainTable\n" +
                "      {\n" +
                "        border: 3px solid  #06375b;\n" +
                "        width: 70%;\n" +
                "      }\n" +
                "      .letterTable{\n" +
                "        width: 90%;\n" +
                "      }\n" +
                "      td {\n" +
                "        padding: 10px;\n" +
                "        text-align: left;\n" +
                "        vertical-align: top;\n" +
                "      }\n" +
                "      .letterHeading {\n" +
                "        font-size: 24px;\n" +
                "        font-weight: bold;\n" +
                "        text-align: center;\n" +
                "        margin-bottom: 20px;\n" +
                "      }\n" +
                "      .boldText {\n" +
                "        font-weight: bold;\n" +
                "      }\n" +
                "      .button {\n" +
                "        display: inline-block;\n" +
                "        padding: 10px 20px;\n" +
                "        background-color: #0074bd;\n" +
                "        color: #fff;\n" +
                "        text-decoration: none;\n" +
                "        border-radius: 4px;\n" +
                "      }\n" +
                "      .topHead {\n" +
                "        background-color: #06375b;\n" +
                "        text-align: center;\n" +
                "      }\n" +
                "      .logo {\n" +
                "        width: 200px;\n" +
                "        height: auto;\n" +
                "        display: block;\n" +
                "        margin: 20px auto;\n" +
                "      }\n" +
                "      .socialIcon {\n" +
                "        width: 20px;\n" +
                "        height: auto;\n" +
                "        vertical-align: middle;\n" +
                "        margin-right: 5px;\n" +
                "      }\n" +
                "      .footerText {\n" +
                "        text-align: center;\n" +
                "      }\n" +
                "       @media (max-width: 768px) {\n"+
                "       .mainTable {\n"  +
                "       width: 90%;\n"   +
                "        }\n" +
                "         .letterHeading {\n" +
                "           font-size: 18px;\n" +
                "           font-weight: 300;\n" +
                "            }\n" +
                "            p {\n" +
                "            font-size: 10px;\n" +
                "             }\n" +
                "              }" +
                "    @media (max-width: 768px) {\n" +
                "     .mainTable {\n" +
                "     width: 100%;\n" +
                "      }\n" +
                "       .letterHeading {\n" +
                "       font-size: 14px;\n" +
                "      font-weight: normal;\n"+
                "      }\n" +
                "      p {\n"+
                "       font-size: 10px;\n" +
                "      }\n" +
                "  td {\n" +
                "   padding: 0px;\n" +
                "  }\n" +
                "   .button {\n" +
                "    display: inline-block;\n" +
                "    padding: 5px 10px;\n" +
                "    background-color: #0074bd;\n" +
                "  }\n" +
                "   .socialIcon {\n" +
                "   width: 12px;\n" +
                "   height: auto;\n" +
                "    }\n" +
                "   }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">\n" +
                "    <table\n" +
                "      cellspacing=\"0\"\n" +
                "      cellpadding=\"0\"\n" +
                "      border=\"0\"\n" +
                "      width=\"100%\"\n" +
                "      align=\"center\"\n" +
                "      class=\"mainTable\"\n" +
                "    >\n" +
                "      <tr>\n" +
                "        <td class=\"topHead\">\n" +
                "          <img\n" +
                "            src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518261/sprk_logo_registered__10_rxgocl.png\"\n" +
                "            cloudName=\"dxlzzgbfw\"\n" +
                "            class=\"logo\"\n" +
                "          />\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td>\n" +
                "          <p class=\"letterHeading\">Application Approved</p>\n" +
                "          <table\n" +
                "            cellspacing=\"0\"\n" +
                "            cellpadding=\"0\"\n" +
                "            border=\"0\"\n" +
                "            width=\"100%\"\n" +
                "            class=\"letterTable\"\n" +
                "          >\n" +
                "            <tr>\n" +
                "              <td>\n" +
                "                <p class=\"boldText\">Dear " + recipientName + ",</p>\n" +
                "                <p>\n" +
                "                  Congratulations ! Your application for employee portal access\n" +
                "                  has been approved . We are thrilled to welcome you to our\n" +
                "                  organization! We are excited to have someone of your talent\n" +
                "                  and expertise join our team, and we believe your contributions\n" +
                "                  will play a vital role in our continued success.\n" +
                "                </p>\n" +
                "                <p>\n" +
                "                  Your Login credentials are : <br />\n" +
                "                  EmployeeID: " + employeeId + " <br/>\n" +
                "                  Password: " + strongPassword + "\n" +
                "                </p>\n" +
                "                <p>\n" +
                "                  For security reasons, we recommend that you change your\n" +
                "                  password upon your first login.\n" +
                "                </p>\n" +
                "                <p>\n" +
                "                  If you have any queries , please feel free to reach out to us\n" +
                "                  at\n" +
                "                  <a href=\"mailto:sprktechnologies.kharghar@gmail.com \"\n" +
                "                    >sprktechnologies.kharghar@gmail.com</a\n" +
                "                  >\n" +
                "                  . We are here to assist you .\n" +
                "                </p>\n" +
                "                <p>\n" +
                "                  Once again, welcome to SPRK Technologies. We look forward to\n" +
                "                  working together and wish you the very best.\n" +
                "                </p>\n" +
                "                <p class=\"boldText\">Best regards,</p>\n" +
                "                <p>SPRK Technologies</p>\n" +
                "                <p class=\"footerText\">\n" +
                "                  <a href=\"https://sprktechnologies.in/home\">SPRK Website</a> |\n" +
                "                  <a href=\"http://wa.me/919082572832?text=Hello \">Contact Us</a>\n" +
                "                  |\n" +
                "                  <a href=\"mailto:sprktechnologies.kharghar@gmail.com \"\n" +
                "                    >Support</a\n" +
                "                  >\n" +
                "                </p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "          <table\n" +
                "            cellspacing=\"0\"\n" +
                "            cellpadding=\"0\"\n" +
                "            border=\"0\"\n" +
                "            width=\"100%\"\n" +
                "            class=\"letterTable\"\n" +
                "          >\n" +
                "            <tr>\n" +
                "              <td>\n" +
                "                <p class=\"boldText\">Social Handles</p>\n" +
                "                <p>\n" +
                "                  Follow us on\n" +
                "                  <a href=\"https://www.instagram.com/sprktech/?hl=en\"\n" +
                "                    ><img\n" +
                "                      src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/skill-icons_instagram_iee7mt.png\"\n" +
                "                      class=\"socialIcon\"\n" +
                "                    />@sprktech</a\n" +
                "                  >\n" +
                "                </p>\n" +
                "                <p>\n" +
                "                  Contact us on\n" +
                "                  <a href=\"https://in.linkedin.com/company/sprk-technologies\"\n" +
                "                    ><img\n" +
                "                      src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/devicon_linkedin_hfekat.png\"\n" +
                "                      class=\"socialIcon\"\n" +
                "                    />SPRK Technologies</a\n" +
                "                  >\n" +
                "                </p>\n" +
                "                <p class=\"footerText\">\n" +
                "                  Address: SPRK Technologies, Plot No-11 Opposite:, Glomax Mall,\n" +
                "                  Office:102-104,1st Floor, Royal Palace, Sector 2, Kharghar,\n" +
                "                  Navi Mumbai, Maharashtra 410210 Contact us : 090825 72832/\n" +
                "                  8425840175;\n" +
                "                </p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>";

        if (!sendMail(recipientEmail, subject, messageBody, true))
            throw new EmailDispatcherException("Failed to send email to the " + recipientEmail);
    }

    public void sendEmployeeAccessDenialMail(
            String recipientName,
            String recipientEmail
    ) {
        String subject = "🔴 Employee Portal Access Application Status.";
        String messageBody = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <title>" + subject + "</title>\n" +
                "  <style>\n" +
                "    table {\n" +
                "      border-collapse: collapse;\n" +
                "      margin: auto;\n" +
                "    }\n" +
                "    .mainTable {\n" +
                "      border: 3px solid  #06375b;\n" +
                "      width: 70%;\n" +
                "    }\n" +
                "    .letterTable{\n" +
                "      width: 90%;\n" +
                "    }\n" +
                "    td {\n" +
                "      padding: 10px;\n" +
                "      text-align: left;\n" +
                "      vertical-align: top;\n" +
                "    }\n" +
                "    .letterHeading {\n" +
                "      font-size: 24px;\n" +
                "      font-weight: bold;\n" +
                "      text-align: center;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .boldText {\n" +
                "      font-weight: bold;\n" +
                "    }\n" +
                "    .button {\n" +
                "      display: inline-block;\n" +
                "      padding: 10px 20px;\n" +
                "      background-color: #0074bd;\n" +
                "      color: #fff;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 4px;\n" +
                "    }\n" +
                "    .topHead {\n" +
                "      background-color: #06375b;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .logo {\n" +
                "      width: 200px;\n" +
                "      height: auto;\n" +
                "      display: block;\n" +
                "      margin: 20px auto;\n" +
                "    }\n" +
                "    .socialIcon {\n" +
                "      width: 20px;\n" +
                "      height: auto;\n" +
                "      vertical-align: middle;\n" +
                "      margin-right: 5px;\n" +
                "    }\n" +
                "    .footerText {\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "       @media (max-width: 768px) {\n"+
                "       .mainTable {\n"  +
                "       width: 90%;\n"   +
                "        }\n" +
                "         .letterHeading {\n" +
                "           font-size: 18px;\n" +
                "           font-weight: 300;\n" +
                "            }\n" +
                "            p {\n" +
                "            font-size: 10px;\n" +
                "             }\n" +
                "              }" +
                "    @media (max-width: 768px) {\n" +
                "     .mainTable {\n" +
                "     width: 100%;\n" +
                "      }\n" +
                "       .letterHeading {\n" +
                "       font-size: 14px;\n" +
                "      font-weight: normal;\n"+
                "      }\n" +
                "      p {\n"+
                "       font-size: 10px;\n" +
                "      }\n" +
                "  td {\n" +
                "   padding: 0px;\n" +
                "  }\n" +
                "   .button {\n" +
                "    display: inline-block;\n" +
                "    padding: 5px 10px;\n" +
                "    background-color: #0074bd;\n" +
                "  }\n" +
                "   .socialIcon {\n" +
                "   width: 12px;\n" +
                "   height: auto;\n" +
                "    }\n" +
                "   }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">\n" +
                "  <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" align=\"center\" class=\"mainTable\">\n" +
                "    <tr>\n" +
                "      <td class=\"topHead\">\n" +
                "        <img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518261/sprk_logo_registered__10_rxgocl.png\"      cloudName=\"dxlzzgbfw\"\n" +
                "        class=\"logo\" />\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td>\n" +
                "        <p class=\"letterHeading\">Application Declined</p>\n" +
                "        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" class=\"letterTable\">\n" +
                "          <tr>\n" +
                "            <td>\n" +
                "              <p class=\"boldText\">Dear " + recipientName + ",</p>\n" +
                "              <p>\n" +
                "                We regret to inform you that your application for employee portal access  has been declined. Please contact SPRK Technologies to resolve it.</p>\n" +
                "              </p>\n" +
                "              <p class=\"letterText\">\n" +
                "                We appreciate your interest in accessing the SPRK Technologies employee portal and the time you invested in submitting your application. After careful review, we regret to inform you that your application for employee portal access has been declined.          </p>\n" +
                "              <p class=\"letterText\">\n" +
                "                If you have any queries , please feel free to reach out to us at <a href=\"mailto:sprktechnologies.kharghar@gmail.com \">sprktechnologies.kharghar@gmail.com</a> . We are here to assist you.\n" +
                "              </p>\n" +
                "              <p class=\"boldText\">Best regards,</p>\n" +
                "              <p>SPRK Technologies</p>\n" +
                "              <p class=\"footerText\">\n" +
                "                                <a href=\"https://sprktechnologies.in/home\">SPRK Website</a> | <a href=\"http://wa.me/919082572832?text=Hello \">Contact Us</a> | <a href=\"mailto:sprktechnologies.kharghar@gmail.com \">Support</a>\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" class=\"letterTable\">\n" +
                "          <tr>\n" +
                "            <td>\n" +
                "              <p class=\"boldText\">Social Handles</p>\n" +
                "              <p>Follow us on <a href=\"https://www.instagram.com/sprktech/?hl=en\"><img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/skill-icons_instagram_iee7mt.png\" class=\"socialIcon\" />@sprktech</a></p>\n" +
                "              <p>Contact us on <a href=\"https://in.linkedin.com/company/sprk-technologies\"><img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/devicon_linkedin_hfekat.png\" class=\"socialIcon\" />SPRK Technologies</a></p>\n" +
                "              <p class=\"footerText\">\n" +
                "                Address: SPRK Technologies, Plot No-11 Opposite:, Glomax Mall, Office:102-104,1st Floor, Royal Palace, Sector 2, Kharghar, Navi Mumbai, Maharashtra 410210 Contact us : 090825 72832/ 8425840175;\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr> \n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>";

        if (!sendMail(recipientEmail, subject, messageBody, true))
            throw new EmailDispatcherException("Failed to send email to the " + recipientEmail);
    }

    public void sendEmployeeRectificationLinkMail(
            String recipientName,
            String recipientEmail,
            String rectificationReason,
            String link
    ) {
        String subject = "🔴 Missing Information: Resend Your Application.";
        String messageBody = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <title>" + subject + "</title>\n" +
                "  <style>\n" +
                "    table {\n" +
                "      border-collapse: collapse;\n" +
                "      margin: auto;\n" +
                "    }\n" +
                "    .mainTable {\n" +
                "      border: 3px solid  #06375b;\n" +
                "      width: 70%;\n" +
                "    }\n" +
                "    .letterTable{\n" +
                "      width: 90%;\n" +
                "    }\n" +
                "    td {\n" +
                "      padding: 10px;\n" +
                "      text-align: left;\n" +
                "      vertical-align: top;\n" +
                "    }\n" +
                "    .letterHeading {\n" +
                "      font-size: 24px;\n" +
                "      font-weight: bold;\n" +
                "      text-align: center;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .boldText {\n" +
                "      font-weight: bold;\n" +
                "    }\n" +
                "    .button {\n" +
                "      display: inline-block;\n" +
                "      padding: 10px 20px;\n" +
                "      background-color: #0074bd;\n" +
                "      color: #fff;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 4px;\n" +
                "    }\n" +
                "    .topHead {\n" +
                "      background-color: #06375b;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .logo {\n" +
                "      width: 200px;\n" +
                "      height: auto;\n" +
                "      display: block;\n" +
                "      margin: 20px auto;\n" +
                "    }\n" +
                "    .socialIcon {\n" +
                "      width: 20px;\n" +
                "      height: auto;\n" +
                "      vertical-align: middle;\n" +
                "      margin-right: 5px;\n" +
                "    }\n" +
                "    .footerText {\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    @media (max-width: 768px) {\n" +
                "     .mainTable {\n" +
                "     width: 100%;\n" +
                "      }\n" +
                "       .letterHeading {\n" +
                "       font-size: 14px;\n" +
                "      font-weight: normal;\n"+
                "      }\n" +
                "      p {\n"+
                "       font-size: 10px;\n" +
                "      }\n" +
                "  td {\n" +
                "   padding: 0px;\n" +
                "  }\n" +
                "   .button {\n" +
                "    display: inline-block;\n" +
                "    padding: 5px 10px;\n" +
                "    background-color: #0074bd;\n" +
                "  }\n" +
                "   .socialIcon {\n" +
                "   width: 12px;\n" +
                "   height: auto;\n" +
                "    }\n" +
                "   }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">\n" +
                "  <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" align=\"center\" class=\"mainTable\">\n" +
                "    <tr>\n" +
                "      <td class=\"topHead\">\n" +
                "        <img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518261/sprk_logo_registered__10_rxgocl.png\"      cloudName=\"dxlzzgbfw\"\n" +
                "        class=\"logo\" />\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td>\n" +
                "        <p class=\"letterHeading\">Missing information</p>\n" +
                "        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" class=\"letterTable\">\n" +
                "          <tr>\n" +
                "            <td>\n" +
                "              <p class=\"boldText\">Dear " + recipientName + ",</p>\n" +
                "              <p>\n" +
                "                Thank you for initiating the onboarding process with SPRK Technologies. We appreciate your prompt attention to the Portal Access Form. Upon reviewing your submitted form, we noticed that there is some missing information " + rectificationReason + " required for the completion of your onboarding process. To ensure accurate and efficient processing, we kindly request you to revisit the form using the original link provided:\n" +
                "              </p>\n" +
                "              <a href=\"" + link + "\" > <p class=\"button\">\n" +
                "                CLICK HERE</p></a>\n" +
                "              <p>\n" +
                "                We understand that oversights can happen, and we appreciate your cooperation in providing the necessary details at your earliest convenience. This will enable us to expedite your onboarding and grant you timely access to our internal portal.\n" +
                "              </p>\n" +
                "              <p>\n" +
                "                If you encounter any difficulties or have questions regarding the missing information, please feel free to reach out to us at <a href=\"mailto:sprktechnologies.kharghar@gmail.com \">sprktechnologies.kharghar@gmail.com</a>. We are here to assist you throughout the process.\n" +
                "              </p>\n" +
                "              <p>\n" +
                "                Thank you for your understanding and cooperation.\n" +
                "            </p>\n" +
                "              <p class=\"boldText\">Best regards,</p>\n" +
                "              <p>SPRK Technologies</p>\n" +
                "              <p class=\"footerText\">\n" +
                "                                <a href=\"https://sprktechnologies.in/home\">SPRK Website</a> | <a href=\"http://wa.me/919082572832?text=Hello \">Contact Us</a> | <a href=\"mailto:sprktechnologies.kharghar@gmail.com \">Support</a>\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" class=\"letterTable\">\n" +
                "          <tr>\n" +
                "            <td>\n" +
                "              <p class=\"boldText\">Social Handles</p>\n" +
                "              <p>Follow us on <a href=\"https://www.instagram.com/sprktech/?hl=en\"><img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/skill-icons_instagram_iee7mt.png\" class=\"socialIcon\" />@sprktech</a></p>\n" +
                "              <p>Contact us on <a href=\"https://in.linkedin.com/company/sprk-technologies\"><img src=\"https://res.cloudinary.com/dxlzzgbfw/image/upload/v1701518175/devicon_linkedin_hfekat.png\" class=\"socialIcon\" />SPRK Technologies</a></p>\n" +
                "              <p class=\"footerText\">\n" +
                "                Address: SPRK Technologies, Plot No-11 Opposite:, Glomax Mall, Office:102-104,1st Floor, Royal Palace, Sector 2, Kharghar, Navi Mumbai, Maharashtra 410210 Contact us : 090825 72832/ 8425840175;\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr> \n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>";

        if (!sendMail(recipientEmail, subject, messageBody, true))
            throw new EmailDispatcherException("Failed to send email to the " + recipientEmail);
    }



    public void validateEmailOnWeb(String email) {
        final String finalMailVerificationKey = mailVerificationKey;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mailVerificationEndpoint)
                .queryParam("apikey", finalMailVerificationKey)
                .queryParam("email", email);
        String verificationUrl = builder.toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<?> response = restTemplate.exchange(
                verificationUrl,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                QuickEmailVerificationResponse.class
        );

        if (null != response.getBody()) {
            LoggerManager.log(
                    EmailUtils.class,
                    LoggerModel.builder()
                            .message(response.getBody().toString())
                            .build(),
                    LogLevel.INFO
            );
            if (response.getBody() instanceof QuickEmailVerificationResponse) {
                QuickEmailVerificationResponse responseBody = (QuickEmailVerificationResponse) response.getBody();
                if (Boolean.TRUE.equals(responseBody.getDisposable()))
                    throw new DisposableEmailException(email);
                if (!Boolean.TRUE.equals(responseBody.getSuccess()) || "invalid".equalsIgnoreCase(responseBody.getResult()))
                    throw new EmailDispatcherException(email);
            }
        }
    }



    private boolean mailSender(
            String recipient,
            String subject,
            String messageBody,
            boolean isHtml,
            String attachmentPath
    ) {
        if (textHelper.isBlank(senderMailAddress) || textHelper.isBlank(recipient) || textHelper.isBlank(subject) || textHelper.isBlank(messageBody))
            return false;

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderMailAddress);
            mimeMessageHelper.setTo(recipient);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(messageBody, isHtml);

            if (!textHelper.isBlank(attachmentPath)) {
                File file = new File(attachmentPath);
                FileSystemResource fileSystemResource = new FileSystemResource(file);
                String attachmentFilename = file.getName();
                mimeMessageHelper.addAttachment(attachmentFilename, fileSystemResource);
            }

            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception exception) {
            throw new EmailDispatcherException(exception.getMessage());
        }

    }

}
