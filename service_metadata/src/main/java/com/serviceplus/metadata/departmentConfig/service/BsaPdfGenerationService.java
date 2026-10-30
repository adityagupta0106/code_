package com.serviceplus.metadata.departmentConfig.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.serviceplus.metadata.departmentConfig.config.BsaRegistrationProperties;
import com.serviceplus.metadata.departmentConfig.entity.BsaConfiguration;
import com.serviceplus.metadata.departmentConfig.util.BsaCryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BsaPdfGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(BsaPdfGenerationService.class);

    private final BsaRegistrationProperties properties;

    public BsaPdfGenerationService(BsaRegistrationProperties properties) {
        this.properties = properties;
    }

    public byte[] generateConfigurationPdf(BsaConfiguration config,
                                          String requestId,
                                          String adminEmail,
                                          String plainPassword,
                                          String plainKey,
                                          String plainIv) {
        logger.info("Generating BSA Integration Certificate PDF for department Id={}, requestId={}",
                config.getEntityId(), requestId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(24, 76, 120));
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(40, 40, 40));
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font monoValueFont = FontFactory.getFont(FontFactory.COURIER, 9, Color.BLACK);
            Font warningFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, new Color(180, 40, 40));

            // Header Banner
            Paragraph header = new Paragraph("SERVICEPLUS 2.0 - GOVERNMENT PLATFORM", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("Beneficiary Satyapan App (BSA) - Department Integration Certificate", subTitleFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            subHeader.setSpacingAfter(10f);
            document.add(subHeader);

            // Divider Line
            LineSeparator separator = new LineSeparator(1.5f, 100, new Color(24, 76, 120), Element.ALIGN_CENTER, -2);
            document.add(separator);

            // Document Meta Table
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setSpacingBefore(10f);
            metaTable.setSpacingAfter(15f);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
            String generatedDate = sdf.format(new Date());

            addMetaCell(metaTable, "Document Ref ID:", requestId != null ? requestId : "REF-" + config.getId(), labelFont, valueFont);
            addMetaCell(metaTable, "Generation Timestamp:", generatedDate, labelFont, valueFont);
            addMetaCell(metaTable, "Registration Status:", "REGISTERED", labelFont, valueFont);
            addMetaCell(metaTable, "Service Code:", config.getServiceCode() != null ? config.getServiceCode() : "N/A", labelFont, valueFont);

            document.add(metaTable);

            // Section 1: Department Information
            addSectionHeader(document, "1. DEPARTMENT ONBOARDING DETAILS", sectionFont);
            PdfPTable deptTable = new PdfPTable(new float[]{30f, 70f});
            deptTable.setWidthPercentage(100);
            deptTable.setSpacingAfter(15f);

            addRow(deptTable, "Department ID", String.valueOf(config.getEntityId()), labelFont, valueFont);
            addRow(deptTable, "Department Name", config.getSchemeName() != null ? config.getSchemeName() : "N/A", labelFont, valueFont);
            addRow(deptTable, "Scheme Code", config.getSchemeCode() != null ? config.getSchemeCode() : "N/A", labelFont, valueFont);
            int sCode = config.getsCode() != null ? config.getsCode() : 99;
            addRow(deptTable, "State Code", String.valueOf(sCode) + (sCode == 99 ? " (Non-State / Central)" : ""), labelFont, valueFont);
            addRow(deptTable, "Department Admin Email", adminEmail != null ? adminEmail : "N/A", labelFont, valueFont);
            document.add(deptTable);

            // Section 2: Integration Configuration
            addSectionHeader(document, "2. BSA INTEGRATION PARAMETERS", sectionFont);
            PdfPTable configTable = new PdfPTable(new float[]{30f, 70f});
            configTable.setWidthPercentage(100);
            configTable.setSpacingAfter(15f);

            addRow(configTable, "Assigned Service Code", config.getServiceCode() != null ? config.getServiceCode() : "N/A", labelFont, valueFont);
            addRow(configTable, "BSA User ID (Production)", config.getUserName(), labelFont, valueFont);
            addRow(configTable, "Staging URL", config.getStagingUrl() != null ? config.getStagingUrl() : properties.getRegistration().getStagingUrl(), labelFont, valueFont);
            addRow(configTable, "Production URL", config.getProdUrl() != null ? config.getProdUrl() : properties.getRegistration().getProdUrl(), labelFont, valueFont);
            addRow(configTable, "BSA Support Desk", properties.getSupportDesk().getEmail(), labelFont, valueFont);
            document.add(configTable);

            // Section 3: Secure Credentials
            addSectionHeader(document, "3. SECURITY CREDENTIALS (RESTRICTED ACCESS)", sectionFont);
            PdfPTable credTable = new PdfPTable(new float[]{30f, 70f});
            credTable.setWidthPercentage(100);
            credTable.setSpacingAfter(10f);

            String rawPass = config.getPassword();
            String rawKey = properties.getEncryptionKey();
            String rawIv = properties.getEncryptionIv();
            String rawKeyStaging = config.getKeyStaging();
            String rawIvStaging = config.getIvStaging();

            boolean mask = properties.getPdf().isMaskCredentials();
            String displayPassword = mask ? BsaCryptoUtil.maskPassword(rawPass) : rawPass;
            String displayKey = mask ? BsaCryptoUtil.maskSecret(rawKey) : rawKey;
            String displayIv = mask ? BsaCryptoUtil.maskSecret(rawIv) : rawIv;
            String displayKeyStaging = mask ? BsaCryptoUtil.maskSecret(rawKeyStaging) : (rawKeyStaging != null ? rawKeyStaging : "N/A");
            String displayIvStaging = mask ? BsaCryptoUtil.maskSecret(rawIvStaging) : (rawIvStaging != null ? rawIvStaging : "N/A");

            addRow(credTable, "BSA Password", displayPassword, labelFont, monoValueFont);
            addRow(credTable, "Production AES-256 Key", displayKey, labelFont, monoValueFont);
            addRow(credTable, "Production IV", displayIv, labelFont, monoValueFont);
            addRow(credTable, "Staging Encryption Key", displayKeyStaging, labelFont, monoValueFont);
            addRow(credTable, "Staging IV", displayIvStaging, labelFont, monoValueFont);
            document.add(credTable);

            // Warning Notice
            Paragraph warning = new Paragraph(
                    "CONFIDENTIALITY NOTICE: The information contained in this document, including encryption keys and authentication credentials, " +
                    "is strictly confidential and intended solely for the designated Department Administrator. " +
                    "These credentials must be imported into a secure hardware security module (HSM) or keystore. " +
                    "Do NOT store or transmit these credentials in plain text over insecure channels.",
                    warningFont
            );
            warning.setSpacingAfter(15f);
            document.add(warning);

            // Footer / Sign-off
            Paragraph footer = new Paragraph("Generated automatically by ServicePlus 2.0 Security Subsystem. No physical signature required.", valueFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            logger.info("BSA Integration Certificate PDF generated successfully. Size: {} bytes", baos.size());
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to generate BSA Integration PDF for Department Id={}", config.getEntityId(), e);
            throw new RuntimeException("Failed to generate BSA configuration PDF: " + e.getMessage(), e);
        }
    }

    private void addSectionHeader(Document document, String title, Font font) throws DocumentException {
        Paragraph p = new Paragraph(title, font);
        p.setSpacingBefore(5f);
        p.setSpacingAfter(5f);
        document.add(p);
    }

    private void addMetaCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3f);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value, valueFont));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        cell1.setBackgroundColor(new Color(245, 247, 250));
        cell1.setPadding(6f);
        cell1.setBorderColor(new Color(220, 224, 230));

        PdfPCell cell2 = new PdfPCell(new Phrase(value != null ? value : "", valueFont));
        cell2.setPadding(6f);
        cell2.setBorderColor(new Color(220, 224, 230));

        table.addCell(cell1);
        table.addCell(cell2);
    }
}
