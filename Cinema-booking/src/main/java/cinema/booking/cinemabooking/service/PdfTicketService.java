package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfTicketService {

    public ByteArrayInputStream generateReservationPdf(Reservation reservation) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.addTitle("Bilety - Rezerwacja " + reservation.getReservationCode());
            document.addAuthor("CinemaBooking System");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            for (int i = 0; i < reservation.getTickets().size(); i++) {
                if (i > 0) {
                    document.newPage();
                }
                addTicketToDocument(document, reservation.getTickets().get(i), formatter);
            }

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTicketToDocument(Document document, Ticket ticket, DateTimeFormatter formatter) throws Exception {
        PdfPTable mainTable = new PdfPTable(1);
        mainTable.setWidthPercentage(95);
        mainTable.setSpacingBefore(10);
        mainTable.setSpacingAfter(10);

        // Nagłówek
        PdfPCell header = new PdfPCell(new Phrase("🎬 BILET KINOWY",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.WHITE)));
        header.setBackgroundColor(new Color(76, 29, 149));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPadding(15);
        header.setBorder(Rectangle.BOX);
        header.setBorderWidth(3);
        header.setBorderColor(new Color(139, 69, 19));
        mainTable.addCell(header);

        // Główna zawartość
        PdfPCell body = new PdfPCell();
        body.setPadding(20);
        body.setBorder(Rectangle.BOX);
        body.setBorderWidth(2);
        body.setBorderColor(new Color(76, 29, 149));

        // Tytuł Filmu
        Paragraph movieTitle = new Paragraph(ticket.getSeance().getMovie().getTitle(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(76, 29, 149)));
        movieTitle.setAlignment(Element.ALIGN_CENTER);
        movieTitle.setSpacingAfter(15);
        body.addElement(movieTitle);

        // Linia separacyjna
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingAfter(15);
        PdfPCell sepCell = new PdfPCell();
        sepCell.setBorder(Rectangle.TOP);
        sepCell.setBorderWidth(2);
        sepCell.setBorderColor(new Color(76, 29, 149));
        sepCell.setPadding(0);
        separator.addCell(sepCell);
        body.addElement(separator);

        // Główna tabela informacji
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(20);
        infoTable.setWidths(new float[]{30, 70});

        // Informacje
        infoTable.addCell(createLabelCell("DATA:"));
        infoTable.addCell(createValueCell(ticket.getSeance().getStartTime().format(formatter)));

        infoTable.addCell(createLabelCell("SALA:"));
        infoTable.addCell(createValueCell(ticket.getSeance().getCinemaRoom().getName()));

        infoTable.addCell(createLabelCell("RZĄD/MIEJSCE:"));
        infoTable.addCell(createValueCell(
                "Rząd " + ticket.getSeat().getRowNumber() + " | Miejsce " + ticket.getSeat().getSeatNumber()
        ));

        infoTable.addCell(createLabelCell("TYP BILETU:"));
        String ticketType = ticket.getTicketType().name().equals("REGULAR") ? "Normalny" : "Ulgowy";
        infoTable.addCell(createValueCell(ticketType));

        infoTable.addCell(createLabelCell("CENA:"));
        infoTable.addCell(createValueCell(String.format("%.2f zł", ticket.getPrice())));

        body.addElement(infoTable);

        // Druga linia separacyjna
        PdfPTable separator2 = new PdfPTable(1);
        separator2.setWidthPercentage(100);
        separator2.setSpacingAfter(15);
        PdfPCell sepCell2 = new PdfPCell();
        sepCell2.setBorder(Rectangle.TOP);
        sepCell2.setBorderWidth(1);
        sepCell2.setBorderColor(new Color(76, 29, 149));
        sepCell2.setPadding(0);
        separator2.addCell(sepCell2);
        body.addElement(separator2);

        // Kod QR + Kod biletu
        PdfPTable qrCodeTable = new PdfPTable(2);
        qrCodeTable.setWidthPercentage(100);
        qrCodeTable.setWidths(new float[]{50, 50});
        qrCodeTable.setSpacingAfter(10);

        // Kod QR
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setPadding(10);

        Image qrImage = generateQRCode(ticket.getTicketCode());
        qrImage.scaleToFit(150, 150);
        qrCell.addElement(qrImage);
        qrCodeTable.addCell(qrCell);

        // Kod tekstowy
        PdfPCell codeTextCell = new PdfPCell();
        codeTextCell.setBorder(Rectangle.NO_BORDER);
        codeTextCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeTextCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph codeLabel = new Paragraph("KOD BILETU:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY));
        codeLabel.setAlignment(Element.ALIGN_CENTER);
        codeTextCell.addElement(codeLabel);

        Paragraph code = new Paragraph(ticket.getTicketCode(),
                FontFactory.getFont(FontFactory.COURIER_BOLD, 10, new Color(76, 29, 149)));
        code.setAlignment(Element.ALIGN_CENTER);
        codeTextCell.addElement(code);

        qrCodeTable.addCell(codeTextCell);
        body.addElement(qrCodeTable);

        mainTable.addCell(body);

        // Stopka
        PdfPCell footer = new PdfPCell(new Phrase(
                "Zachowaj ten bilet. Prezentuj go przy wejściu do kina.",
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY)
        ));
        footer.setHorizontalAlignment(Element.ALIGN_CENTER);
        footer.setPadding(10);
        footer.setBorder(Rectangle.NO_BORDER);
        mainTable.addCell(footer);

        document.add(mainTable);
    }

    private PdfPCell createLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        cell.setBackgroundColor(new Color(240, 240, 240));
        return cell;
    }

    private PdfPCell createValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(50, 50, 50))));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        return cell;
    }

    private Image generateQRCode(String text) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", baos);

        return Image.getInstance(baos.toByteArray());
    }
}
