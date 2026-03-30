package com.queenstouch.queenstouchbackend.service.docx;

import com.queenstouch.queenstouchbackend.model.CoverLetter;
import com.queenstouch.queenstouchbackend.model.User;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class DocxCoverLetterGenerator {

    private static final String BRAND_COLOR_HEX = "0A4D68";

    public byte[] generate(CoverLetter cl, User user) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Header
            XWPFParagraph namePara = doc.createParagraph();
            namePara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun nameRun = namePara.createRun();
            nameRun.setText(user.getFirstName() + " " + user.getLastName());
            nameRun.setFontFamily("Calibri");
            nameRun.setFontSize(20);
            nameRun.setBold(true);
            nameRun.setColor(BRAND_COLOR_HEX);

            XWPFParagraph emailPara = doc.createParagraph();
            XWPFRun emailRun = emailPara.createRun();
            emailRun.setText(user.getEmail());
            emailRun.setFontFamily("Calibri");
            emailRun.setFontSize(10);
            emailRun.setColor("505050");
            emailPara.setSpacingAfter(400); // Add some space below header

            // Body
            String content = cl.getGeneratedContent() != null ? cl.getGeneratedContent() : "";
            String[] paragraphs = content.split("\\n\\n|\\r\\n\\r\\n");

            for (String pText : paragraphs) {
                if (!pText.trim().isEmpty()) {
                    XWPFParagraph p = doc.createParagraph();
                    XWPFRun run = p.createRun();
                    run.setText(pText.trim());
                    run.setFontFamily("Calibri");
                    run.setFontSize(11);
                    p.setSpacingAfter(200);
                }
            }

            doc.write(out);
            return out.toByteArray();
        }
    }
}
