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
import com.itextpdf.text.pdf.*;
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
        try {
            String fileName;
            if (!validateRequestMap(requestMap)) {
                return CafeUtils.getResponseEntity("Data Not Found", HttpStatus.BAD_REQUEST);
            }

            if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                fileName = (String) requestMap.getOrDefault("uuid", requestMap.get("uu id"));
            } else {
                fileName = CafeUtils.getUUID();
                requestMap.put("uuid", fileName);
                insertBill(requestMap);
            }

            String data = "Name: " + requestMap.get("name") + "\n"
                    + "ContactNumber: " + requestMap.get("contactNumber") + "\n"
                    + "Email: " + requestMap.get("email") + "\n"
                    + "PaymentMethod: " + requestMap.get("paymentMethod") + "\n"
                    + "DeliveryAddress: " + requestMap.get("deliveryAddress");

            Document document = new Document(PageSize.A4, 36, 36, 120, 120);
            String outPath = CafeConstants.STORE_LOCATION + File.separator + fileName + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outPath));

            // Add page event for header/footer
            writer.setPageEvent(new HeaderFooterPageEvent(
                    CafeConstants.HEADER_IMAGE_PATH,
                    CafeConstants.FOOTER_IMAGE_PATH,
                    "Mest Tech",  // company name
                    fileName       // UUID for QR code
            ));

            document.open();

            setRectangleInPdf(document);

            // Optional: Title below the header image
            Paragraph title = new Paragraph("Invoice", getFont("Header"));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

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

            Paragraph footer = new Paragraph(
                    "Total: " + requestMap.get("total") + "\nThank You for Coming!",
                    getFont("Data")
            );
            footer.setSpacingBefore(10f);
            document.add(footer);

            document.close();

            autoPrintPdf(outPath);
            return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            if (CafeUtils.isFileExist(filePath)) {
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            } else {
                requestMap.put("isGenerate", false);
                generateBill(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getByteArray(String filePath) throws Exception {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;

    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
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
        switch (type) {
            case "Header":
                return FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
            case "Data":
                return FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
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

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional optional = billDao.findById(id);
            if (!optional.isEmpty()) {
                billDao.deleteById(id);
                return CafeUtils.getResponseEntity("Bill deleted Successfully.", HttpStatus.OK);

            } else {
                return CafeUtils.getResponseEntity("Bill ID does not exist", HttpStatus.OK);
            }
        } catch (Exception ex) {
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


    // Inner class for header/footer
    static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private final Image headerImg;
        private final Image footerImg;
        private final String companyName;
        private final Image qrImg;

        HeaderFooterPageEvent(String headerImgPath, String footerImgPath, String companyName, String qrContent) throws Exception {
            this.companyName = companyName;

            // header image
            Image h = null;
            if (headerImgPath != null && !headerImgPath.trim().isEmpty()) {
                h = Image.getInstance(headerImgPath);
                h.setInterpolation(true);
            }
            this.headerImg = h;

            // footer image
            Image f = null;
            if (footerImgPath != null && !footerImgPath.trim().isEmpty()) {
                f = Image.getInstance(footerImgPath);
                f.setInterpolation(true);
            }
            this.footerImg = f;

            // QR code
            BarcodeQRCode qr = new BarcodeQRCode(qrContent, 150, 150, null);
            this.qrImg = qr.getImage();
            this.qrImg.setInterpolation(true);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte cb = writer.getDirectContent();

                // ---- HEADER (image centered at top) ----
                if (headerImg != null) {
                    Image img = Image.getInstance(headerImg);
                    float maxWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                    img.scaleToFit(maxWidth, 90);
                    float x = (document.getPageSize().getWidth() - img.getScaledWidth()) / 2f;
                    float y = document.getPageSize().getTop() - 40; // draws within top margin
                    img.setAbsolutePosition(x, y - img.getScaledHeight());
                    cb.addImage(img);
                }

                // ---- FOOTER ----
                if (footerImg != null) {
                    // place footer image centered at the bottom margin
                    Image img = Image.getInstance(footerImg);
                    float maxWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                    img.scaleToFit(maxWidth, 80);
                    float x = (document.getPageSize().getWidth() - img.getScaledWidth()) / 2f;
                    float y = document.bottomMargin() - 10;
                    img.setAbsolutePosition(x, y);
                    cb.addImage(img);
                } else {
                    // fallback: company name + QR
                    PdfPTable footerTable = new PdfPTable(2);
                    footerTable.setWidths(new float[]{3f, 1.2f});
                    footerTable.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());

                    PdfPCell left = new PdfPCell();
                    left.setBorder(Rectangle.TOP);
                    left.setHorizontalAlignment(Element.ALIGN_LEFT);
                    left.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    left.setPadding(8f);
                    Phrase company = new Phrase(companyName + "\nScan QR for details", new Font(Font.FontFamily.HELVETICA, 10));
                    left.addElement(company);

                    PdfPCell right = new PdfPCell();
                    right.setBorder(Rectangle.TOP);
                    right.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    right.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    right.setPadding(6f);
                    Image qrClone = Image.getInstance(qrImg);
                    qrClone.scaleToFit(70, 70);
                    right.addElement(qrClone);

                    footerTable.addCell(left);
                    footerTable.addCell(right);

                    float footerY = document.bottomMargin() - 5;
                    footerTable.writeSelectedRows(0, -1, document.leftMargin(), footerY, cb);
                }

            } catch (Exception ignore) {
                // swallow to prevent crash on footer/header issue
            }
        }
    }
}
