package com.fastbee.ai.support;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话附件正文抽取器。
 */
@Component
public class AiChatAttachmentTextExtractor {

    private static final int MAX_EXTRACT_CHARS = 24000;

    public ExtractedAttachment extract(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(message("ai.chat.attachment.file.required"));
        }
        String fileName = StringUtils.isBlank(file.getOriginalFilename()) ? "未命名文件" : file.getOriginalFilename();
        String suffix = resolveSuffix(fileName);
        try {
            byte[] fileBytes = file.getBytes();
            String text;
            if (".pdf".equals(suffix)) {
                text = extractPdf(fileBytes);
            } else if (".docx".equals(suffix)) {
                text = extractDocx(fileBytes);
            } else if (".xls".equals(suffix) || ".xlsx".equals(suffix)) {
                text = extractWorkbook(fileBytes);
            } else if (isPlainTextSuffix(suffix)) {
                text = decodeText(fileBytes);
            } else {
                throw new ServiceException(message("ai.chat.attachment.type.unsupported", suffix));
            }
            text = normalizeText(text);
            if (StringUtils.isBlank(text)) {
                throw new ServiceException(message("ai.chat.attachment.text.empty"));
            }
            return new ExtractedAttachment(fileName, file.getContentType(), trimToLimit(text), fileBytes);
        } catch (IOException ex) {
            throw new ServiceException(message("ai.chat.attachment.read.failed", ex.getMessage()));
        }
    }

    private String extractPdf(byte[] fileBytes) throws IOException {
        StringBuilder text = new StringBuilder();
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document);
                if (StringUtils.isBlank(pageText)) {
                    continue;
                }
                text.append("# PDF Page ").append(page).append('\n')
                        .append(pageText.trim()).append("\n\n");
            }
        }
        return text.toString();
    }

    private String extractDocx(byte[] fileBytes) throws IOException {
        String documentXml = "";
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileBytes))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName())) {
                    documentXml = new String(readAllBytes(zipInputStream), StandardCharsets.UTF_8);
                    break;
                }
            }
        }
        if (StringUtils.isBlank(documentXml)) {
            return "";
        }
        return documentXml
                .replaceAll("</w:p>", "\n")
                .replaceAll("</w:tr>", "\n")
                .replaceAll("<[^>]+>", "");
    }

    private String extractWorkbook(byte[] fileBytes) throws IOException {
        StringBuilder text = new StringBuilder();
        DataFormatter formatter = new DataFormatter(Locale.CHINA);
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileBytes))) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) {
                    continue;
                }
                text.append("# Sheet ").append(sheet.getSheetName()).append('\n');
                int rowLimit = Math.min(sheet.getLastRowNum(), 200);
                for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= rowLimit; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        if (cellIndex > 0) {
                            text.append('\t');
                        }
                        text.append(formatter.formatCellValue(row.getCell(cellIndex)));
                    }
                    text.append('\n');
                }
                text.append('\n');
            }
        }
        return text.toString();
    }

    private String decodeText(byte[] fileBytes) {
        String utf8 = new String(fileBytes, StandardCharsets.UTF_8);
        if (!utf8.contains("\uFFFD")) {
            return utf8;
        }
        return new String(fileBytes, Charset.forName("GB18030"));
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.replace("\uFEFF", "")
                .replaceAll("[\\t ]+\\n", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private String trimToLimit(String text) {
        if (text.length() <= MAX_EXTRACT_CHARS) {
            return text;
        }
        return text.substring(0, MAX_EXTRACT_CHARS) + "\n\n[附件正文过长，已截取前 " + MAX_EXTRACT_CHARS + " 字用于本轮会话]";
    }

    private boolean isPlainTextSuffix(String suffix) {
        return ".txt".equals(suffix) || ".md".equals(suffix) || ".csv".equals(suffix) || ".json".equals(suffix);
    }

    private String resolveSuffix(String fileName) {
        int index = fileName == null ? -1 : fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index).toLowerCase(Locale.ROOT) : "";
    }

    private byte[] readAllBytes(ZipInputStream zipInputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int len;
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        while ((len = zipInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    public record ExtractedAttachment(String fileName, String contentType, String text, byte[] fileBytes) {
    }
}
