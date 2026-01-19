package com.reindecar.service.leasing;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.entity.contract.ContractTemplate;
import com.reindecar.entity.leasing.LeasingInvoice;
import com.reindecar.repository.contract.ContractTemplateRepository;
import com.reindecar.repository.leasing.LeasingInvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractPdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL);

    private final ContractTemplateRepository templateRepository;
    private final LeasingInvoiceRepository invoiceRepository;

    public byte[] generateContractPdf(Long templateId) {
        ContractTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Template not found: " + templateId));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph title = new Paragraph(template.getName(), TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph info = new Paragraph();
            info.add(new Chunk("Sözleşme Kodu: ", HEADER_FONT));
            info.add(new Chunk(template.getCode(), NORMAL_FONT));
            info.add(Chunk.NEWLINE);
            info.add(new Chunk("Kiralama Tipi: ", HEADER_FONT));
            info.add(new Chunk(template.getRentalType().name(), NORMAL_FONT));
            info.add(Chunk.NEWLINE);
            info.add(new Chunk("Versiyon: ", HEADER_FONT));
            info.add(new Chunk(String.valueOf(template.getTemplateVersion()), NORMAL_FONT));
            info.setSpacingAfter(20);
            document.add(info);

            if (template.getContent() != null) {
                Paragraph content = new Paragraph(template.getContent(), NORMAL_FONT);
                content.setSpacingAfter(20);
                document.add(content);
            }

            Paragraph footer = new Paragraph();
            footer.add(new Chunk("Oluşturulma Tarihi: " + DATE_FORMAT.format(template.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate()), SMALL_FONT));
            footer.add(Chunk.NEWLINE);
            footer.add(Chunk.NEWLINE);
            footer.add(new Chunk("Taraflar:", HEADER_FONT));
            footer.add(Chunk.NEWLINE);
            footer.add(Chunk.NEWLINE);
            footer.add(new Chunk("Kiralayan: _______________________          Kiracı: _______________________", NORMAL_FONT));
            document.add(footer);

            document.close();
            log.info("Contract PDF generated for template: {}", template.getCode());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate contract PDF", e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "PDF generation failed: " + e.getMessage());
        }
    }

    public byte[] generateInvoicePdf(Long invoiceId) {
        LeasingInvoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVOICE_NOT_FOUND, invoiceId.toString()));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph title = new Paragraph("FATURA", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph header = new Paragraph();
            header.add(new Chunk("Fatura No: ", HEADER_FONT));
            header.add(new Chunk(invoice.getInvoiceNumber(), NORMAL_FONT));
            header.add(Chunk.NEWLINE);
            header.add(new Chunk("Dönem: ", HEADER_FONT));
            header.add(new Chunk(DATE_FORMAT.format(invoice.getPeriodStart()) + " - " + DATE_FORMAT.format(invoice.getPeriodEnd()), NORMAL_FONT));
            header.add(Chunk.NEWLINE);
            header.add(new Chunk("Son Ödeme: ", HEADER_FONT));
            header.add(new Chunk(DATE_FORMAT.format(invoice.getDueDate()), NORMAL_FONT));
            header.setSpacingAfter(20);
            document.add(header);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2});

            addTableCell(table, "Açıklama", true);
            addTableCell(table, "Tutar", true);

            addTableCell(table, "Aylık Kira Bedeli", false);
            addTableCell(table, formatMoney(invoice.getMonthlyRent()), false);

            if (invoice.getExcessKm() > 0) {
                addTableCell(table, "Aşım KM Ücreti (" + invoice.getExcessKm() + " km)", false);
                addTableCell(table, formatMoney(invoice.getExcessKmCharge()), false);
            }

            if (invoice.getAdditionalCharges() != null && invoice.getAdditionalCharges().getAmount().signum() > 0) {
                String desc = invoice.getAdditionalChargesNote() != null ? "Ek Ücret: " + invoice.getAdditionalChargesNote() : "Ek Ücretler";
                addTableCell(table, desc, false);
                addTableCell(table, formatMoney(invoice.getAdditionalCharges()), false);
            }

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOPLAM", HEADER_FONT));
            totalLabelCell.setBackgroundColor(Color.LIGHT_GRAY);
            totalLabelCell.setPadding(8);
            table.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(formatMoney(invoice.getTotalAmount()), HEADER_FONT));
            totalValueCell.setBackgroundColor(Color.LIGHT_GRAY);
            totalValueCell.setPadding(8);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(totalValueCell);

            document.add(table);

            Paragraph status = new Paragraph();
            status.setSpacingBefore(20);
            status.add(new Chunk("Durum: ", HEADER_FONT));
            status.add(new Chunk(invoice.getStatus().name(), NORMAL_FONT));
            document.add(status);

            document.close();
            log.info("Invoice PDF generated: {}", invoice.getInvoiceNumber());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate invoice PDF", e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "PDF generation failed: " + e.getMessage());
        }
    }

    private void addTableCell(PdfPTable table, String text, boolean header) {
        Font font = header ? HEADER_FONT : NORMAL_FONT;
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        if (header) {
            cell.setBackgroundColor(Color.LIGHT_GRAY);
        }
        table.addCell(cell);
    }

    private String formatMoney(com.reindecar.common.valueobject.Money money) {
        if (money == null) return "0.00 TRY";
        return String.format("%.2f %s", money.getAmount(), money.getCurrency());
    }
}
