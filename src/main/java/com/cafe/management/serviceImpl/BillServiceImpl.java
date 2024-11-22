package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.BillDao;
import com.cafe.management.JWT.JwtFilter;
import com.cafe.management.POJO.Analysis.Analysis1;
import com.cafe.management.POJO.Analysis.Analysis2;
import com.cafe.management.POJO.Bill;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.service.BillService;
import com.cafe.management.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Data
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    BillDao billDao;



    @Override
    public ResponseEntity<String> generateBill(Map<String, Object> requestMap) {
        log.info("Inside generateBill");
        try {
            String fileName;
            if (validateRequestMap(requestMap)) {
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("uu id");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }

                String data = "Name:" + requestMap.get("name") + "\n" + "contactNumber:" + requestMap.get("contactNumber") +
                        "\n" + "Email:" + requestMap.get("email") + "\n" + ("PaymentMethod:") + requestMap.get("paymentMethod")+
                        "\n" + ("DeliveryAddress:") + requestMap.get("deliveryAddress");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));
                document.open();

                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = new JSONArray((String) requestMap.get("productDetails"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    addRows(table, CafeUtils.getMapFromJson(jsonObject.toString()));
                }

                document.add(table);
                Paragraph footer = new Paragraph("Total:" + requestMap.get("total") + "\n"
                        + "Thank You for Coming!", getFont("Data"));
                document.add(footer);
                document.close();

                autoPrintPdf(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf");
                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("Data Not Found", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));


    }

    private void addTableHeader(PdfPTable table) {

        log.info("Inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(ColumnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(ColumnTitle));
                    header.setBackgroundColor(BaseColor.CYAN);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);


                });
    }

    private Font getFont(String type) {

        log.info("Inside getFont");
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rec = new Rectangle(577, 825, 18, 15);
        rec.enableBorderSide(1);
        rec.enableBorderSide(2);
        rec.enableBorderSide(4);
        rec.enableBorderSide(8);
        rec.setBorderColor(BaseColor.BLACK);
        rec.setBorderWidth(1);
        document.add(rec);
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setDeliveryAddress((String) requestMap.get("deliveryAddress"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("total")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean validateRequestMap(Map<String, Object> requestMap) {

        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("total");


    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> list = new ArrayList<>();
        if (jwtFilter.isAdmin()) {
            list = billDao.getAllBills();
        } else {
            list = billDao.getBillByUserName(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf : requestMap", requestMap);
        try {
            byte[] byteArray = new byte[0];
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            String filePath = CafeConstants.STORE_LOCATION + "\\" + (String) requestMap.get("uuid") + ".pdf";
            if (CafeUtils.isFileExist(filePath)){
                byteArray= getByteArray (filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }
            else {
                requestMap.put("isGenerate", false);
                generateBill(requestMap);
                byteArray= getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }



    private byte[] getByteArray(String filePath) throws  Exception{
        File initialFile = new File(filePath);
        InputStream targetStream= new FileInputStream(initialFile);
        byte[] byteArray= IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;

    }
    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional optional = billDao.findById(id);
            if (!optional.isEmpty()){
                billDao.deleteById(id);
                return CafeUtils.getResponseEntity("Bill deleted Successfully.", HttpStatus.OK);

            }else {
                return  CafeUtils.getResponseEntity("Bill ID does not exist", HttpStatus.OK);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Analysis1>> getAllOrderCountByUser() {
        try {
            if (jwtFilter.isAdmin()) {
                List<Analysis1> result = billDao.getAllOrderCountByUser();
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Analysis1>> getSpentMoneyTotalAll() {
        try {
            if (jwtFilter.isAdmin()) {
                List<Analysis1> result = billDao.getSpentMoneyTotalAll();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<Analysis2>> getProductSaleAll() {
        try {
            if (jwtFilter.isAdmin()) {
                List<Analysis2> result = billDao.getProductSaleAllAsAnalysis2();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<Analysis2>> getCategorySaleAll() {
        try {
            if (jwtFilter.isAdmin()) {
                List<Analysis2> result = billDao.getCategorySaleAllAsAnalysis2();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> autoPrintPdf(String filePath) {
        try {
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            pras.add(new Copies(1)); // Number of copies to print

            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            DocPrintJob printJob = PrintServiceLookup.lookupDefaultPrintService().createPrintJob();

            FileInputStream fis = new FileInputStream(filePath);
            Doc doc = new SimpleDoc(fis, flavor, null);

            printJob.print(doc, pras);

            fis.close();

            return new ResponseEntity<>("PDF printed successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Failed to print PDF", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}




