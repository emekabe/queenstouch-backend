package com.queenstouch.queenstouchbackend.service.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.queenstouch.queenstouchbackend.model.CoverLetter;
import com.queenstouch.queenstouchbackend.model.User;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfCoverLetterGenerator {

    private static final DeviceRgb BRAND_BLUE = new DeviceRgb(10, 77, 104);

    public byte[] generate(CoverLetter cl, User user) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(50, 50, 50, 50);

        PdfFont bold   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Header
        String name = user.getFirstName() + " " + user.getLastName();
        doc.add(new Paragraph(name).setFont(bold).setFontSize(22).setFontColor(BRAND_BLUE).setMarginBottom(3));
        doc.add(new Paragraph(user.getEmail()).setFont(regular).setFontSize(10).setFontColor(new DeviceRgb(80, 80, 80)).setMarginBottom(8));
        doc.add(new Paragraph("").setBorderBottom(new SolidBorder(BRAND_BLUE, 1.5f)).setMarginBottom(20));

        // Body Content
        String content = cl.getGeneratedContent() != null ? cl.getGeneratedContent() : "";
        String[] paragraphs = content.split("\\n\\n|\\r\\n\\r\\n");

        for (String p : paragraphs) {
             if (!p.trim().isEmpty()) {
                 doc.add(new Paragraph(p.trim()).setFont(regular).setFontSize(11).setMultipliedLeading(1.5f).setMarginBottom(10));
             }
        }

        doc.close();
        return out.toByteArray();
    }
}
