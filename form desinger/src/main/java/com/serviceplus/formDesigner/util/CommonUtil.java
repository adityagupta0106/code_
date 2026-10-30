package com.serviceplus.formDesigner.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.entity.ExcelCell;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtil {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwx0123456789";

    public static UserSessionDTO getUserSessionDetails(HttpServletRequest request) {
        String user = request.getHeader("USER-DETAILS");
        if (isEmpty(user))
            return null;

        return (UserSessionDTO) stringToEntity(user, UserSessionDTO.class);
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static Object stringToEntity(String data, Class<?> classs) {
        return new Gson().fromJson(data, classs);
    }

    public static String entityToString(Object data) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create().toJson(data);
    }

    public static String createExcel(String path, String fileName, String sheetName, List<String> headerList, List<List<ExcelCell>> body) {

        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet(sheetName);
        String completeFilePath = "";
        try {

            Row row = null;
            Cell cell = null;

            XSSFCellStyle cellStyle = workBook.createCellStyle();
            cellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);

            Font hSSFFont1 = (Font) workBook.createFont();
            hSSFFont1.setFontHeightInPoints((short) 12);
            hSSFFont1.setBoldweight(Font.BOLDWEIGHT_BOLD);
            hSSFFont1.setColor(HSSFColor.BLACK.index);
            cellStyle.setFont(hSSFFont1);

            Integer rowIndex = 0;
            Integer colIndex = 0;
            Integer maxRow = 0;
            Integer maxCol = 0;

            Row headerRow = sheet.createRow(rowIndex);
            for (String headerVal : headerList) {
                cell = headerRow.createCell(colIndex);
                cell.setCellValue(headerVal);
                cell.setCellStyle(cellStyle);
                ++colIndex;
            }
            sheet.autoSizeColumn(rowIndex);

            for (List<ExcelCell> bodyCells : body) {
                colIndex = 0;
                if (maxRow > rowIndex) {
                    rowIndex = maxRow;
                }

                row = sheet.createRow(++rowIndex);
                for (ExcelCell cellVal : bodyCells) {

                    if (cellVal.getInputType() == ApplicationConstants.ATTR_DYNAMIC_ROW) {
                        //maxRow = rowIndex;

                        List<Map<String, Object>> mapData = (List<Map<String, Object>>) cellVal.getValue();
                        if (maxCol > colIndex) {
                            colIndex = maxCol;
                        }
                        Integer tabRow = rowIndex;

                        for (Map<String, Object> map : mapData) {
                            Integer tabCol = colIndex;
                            if (tabRow != rowIndex) {
                                row = sheet.createRow(tabRow);
                            }
                            for (Object value : map.values()) {
                                cell = (Cell) row.createCell(tabCol);
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                String val = value != null ? value.toString() : "";
                                cell.setCellValue(val);
                                ++tabCol;
                                if (tabCol > maxCol) {
                                    maxCol = tabCol;
                                }
                            }
                            sheet.autoSizeColumn(tabRow);
                            ++tabRow;
                            if (tabRow > maxRow) {
                                maxRow = tabRow;
                            }
                        }
                    } else {
                        cell = (Cell) row.createCell(colIndex);
                        cell.setCellType(cellVal.getCellType());
                        String val = cellVal.getValue() != null ? cellVal.getValue().toString() : "";
                        cell.setCellValue(val);
                        maxCol = ++colIndex;
                    }
                }
                sheet.autoSizeColumn(rowIndex);

            }

            if (body.isEmpty()) {
                colIndex = 0;
                row = sheet.createRow(++rowIndex);

                cell = (Cell) row.createCell(colIndex);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(MessageConstants.RECORD_NOT_FOUND);
                sheet.autoSizeColumn(rowIndex);
            }

            completeFilePath = path + "\\" + fileName + ".xlsx";

            File file = new File(completeFilePath);

            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }


            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                workBook.write(out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return completeFilePath;
    }

    public static String saveXlsxFile(XSSFWorkbook xssfWorkbook, String path, String file, String fileExtension) {
        final String filePath = path + "/" + file + fileExtension;
        File directory = new File(filePath);
        FileOutputStream out = null;
        try {
            if (!directory.exists()) {
                directory.createNewFile();
            }
            out = new FileOutputStream(directory);
            xssfWorkbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }


    public static String randomAlphaNumeric(int count) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(ALPHA_NUMERIC_STRING.charAt(random.nextInt(ALPHA_NUMERIC_STRING.length())));
        }
        return sb.toString();
    }

    public static String generateKeyAndIV16Char() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        String stringKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        return stringKey + "." + randomAlphaNumeric(16);
    }

    public static Object decryptClientData(String encryptedStr, String key, String iv) throws Exception {

        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedStr);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

//	    String decryptedData = new String(decryptedBytes);
//	    return decryptedData;

        ObjectMapper objectMapper = new ObjectMapper();
        Object object = objectMapper.readValue(decryptedBytes, Object.class);
        return object;
    }

    public static String encryptClientData(Object data, String key, String iv) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] jsonBytes;
        if (data instanceof JSONObject || data instanceof JSONArray) {
            jsonBytes = data.toString().getBytes(StandardCharsets.UTF_8);
        } else {
            jsonBytes = objectMapper.writeValueAsBytes(data);
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(jsonBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String encrypt(String content, String authKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            SecretKeySpec skeySpec = new SecretKeySpec(authKey.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
            byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
            String finalString = org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);

            return finalString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String content, String authKey) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(authKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
            byte[] original = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(content));

            return new String(original);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PublicKey loadPublicKey(Resource resource) throws Exception {

        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        key = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return factory.generatePublic(spec);
    }

    public static boolean hasRole(UserSessionDTO user, Integer roleId) {

        List<UserSessionDTO.Roles> roles = user.getRoles();
        return roles != null && roles.stream().anyMatch(r -> roleId.equals(r.getRoleId()));
    }

    public static int getCacheTimeoutWithJitter(int baseTimeoutSeconds, int jitterSeconds) {
        return baseTimeoutSeconds + ThreadLocalRandom.current().nextInt(-jitterSeconds, jitterSeconds + 1);
    }
}
