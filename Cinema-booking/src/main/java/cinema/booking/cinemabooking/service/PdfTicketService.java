package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.exception.FileStorageException;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Service responsible for generating PDF documents with tickets.
 * Uses OpenPDF (lowagie) for PDF creation and ZXing for QR code generation.
 */
@Slf4j
@Service
public class PdfTicketService {

    // Define colors and styles
    private static final Color PRIMARY_COLOR = new Color(76, 29, 149);
    private static final Color BORDER_COLOR = new Color(139, 69, 19);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Generates a PDF file containing tickets for a given reservation.
     *
     * @param reservation The reservation containing the tickets.
     * @return ByteArrayInputStream stream containing the generated PDF data.
     * @throws FileStorageException if PDF generation fails.
     */
    public ByteArrayInputStream generateReservationPdf(Reservation reservation) {
        log.info("Starting PDF generation for reservation code: {}", reservation.getReservationCode()); //

        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.addTitle("Ticket - Reservation " + reservation.getReservationCode());
            document.addAuthor("CinemaBooking System");

            for (int i = 0; i < reservation.getTickets().size(); i++) {
                if (i > 0) {
                    document.newPage();
                }
                Ticket ticket = reservation.getTickets().get(i);
                log.debug("Processing ticket [{}] for seance: {}", ticket.getTicketCode(), ticket.getSeance().getMovie().getTitle()); //
                addTicketToDocument(document, ticket);
            }

            document.close();
            log.info("PDF generation completed successfully. Size: {} bytes", out.size());

        } catch (Exception e) {
            log.error("Critical error during PDF generation for reservation: {}", reservation.getReservationCode(), e);
            throw new FileStorageException("Failed to generate PDF file", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Adds a single ticket layout to the PDF document.
     *
     * @param document The target PDF document.
     * @param ticket   The ticket entity to visualize.
     * @throws Exception If PDF construction fails.
     */
    private void addTicketToDocument(Document document, Ticket ticket) throws Exception {
        PdfPTable mainTable = new PdfPTable(1);
        mainTable.setWidthPercentage(95);
        mainTable.setSpacingBefore(10);
        mainTable.setSpacingAfter(10);

        // Header Section
        PdfPCell header = new PdfPCell(new Phrase("🎬 Cinema Ticket",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.WHITE)));
        header.setBackgroundColor(PRIMARY_COLOR);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPadding(15);
        header.setBorder(Rectangle.BOX);
        header.setBorderWidth(3);
        header.setBorderColor(BORDER_COLOR);
        mainTable.addCell(header);

        // Body Section
        PdfPCell body = new PdfPCell();
        body.setPadding(20);
        body.setBorder(Rectangle.BOX);
        body.setBorderWidth(2);
        body.setBorderColor(PRIMARY_COLOR);

        // Movie Title
        Paragraph movieTitle = new Paragraph(ticket.getSeance().getMovie().getTitle(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY_COLOR));
        movieTitle.setAlignment(Element.ALIGN_CENTER);
        movieTitle.setSpacingAfter(15);
        body.addElement(movieTitle);

        addSeparator(body, 2);

        // Info Table (Details)
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(20);
        infoTable.setWidths(new float[]{30, 70});

        infoTable.addCell(createLabelCell("DATE:"));
        infoTable.addCell(createValueCell(ticket.getSeance().getStartTime().format(DATE_FORMATTER)));

        infoTable.addCell(createLabelCell("ROOM:"));
        infoTable.addCell(createValueCell(ticket.getSeance().getCinemaRoom().getName()));

        infoTable.addCell(createLabelCell("ROW/SEAT:"));
        infoTable.addCell(createValueCell(
                String.format("ROW %d | SEAT %d", ticket.getSeat().getRowNumber(), ticket.getSeat().getSeatNumber())
        ));

        infoTable.addCell(createLabelCell("TICKET TYPE:"));
        String ticketType = "REGULAR".equals(ticket.getTicketType().name()) ? "Regular" : "Reduced";
        infoTable.addCell(createValueCell(ticketType));

        infoTable.addCell(createLabelCell("PRICE:"));
        infoTable.addCell(createValueCell(String.format("%.2f zł", ticket.getPrice())));

        body.addElement(infoTable);

        addSeparator(body, 1);

        // QR Code & Ticket Code Section
        PdfPTable qrCodeTable = new PdfPTable(2);
        qrCodeTable.setWidthPercentage(100);
        qrCodeTable.setWidths(new float[]{50, 50});
        qrCodeTable.setSpacingAfter(10);

        // QR Image
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setPadding(10);

        // Generate QR Code
        log.trace("Generating QR code for ticket: {}", ticket.getTicketCode());
        Image qrImage = generateQRCode(ticket.getTicketCode());
        qrImage.scaleToFit(150, 150);
        qrCell.addElement(qrImage);
        qrCodeTable.addCell(qrCell);

        // Text Code
        PdfPCell codeTextCell = new PdfPCell();
        codeTextCell.setBorder(Rectangle.NO_BORDER);
        codeTextCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeTextCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph codeLabel = new Paragraph("TICKET CODE:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY));
        codeLabel.setAlignment(Element.ALIGN_CENTER);
        codeTextCell.addElement(codeLabel);

        Paragraph code = new Paragraph(ticket.getTicketCode(),
                FontFactory.getFont(FontFactory.COURIER_BOLD, 10, PRIMARY_COLOR));
        code.setAlignment(Element.ALIGN_CENTER);
        codeTextCell.addElement(code);

        qrCodeTable.addCell(codeTextCell);
        body.addElement(qrCodeTable);

        mainTable.addCell(body);

        // Footer
        PdfPCell footer = new PdfPCell(new Phrase(
                "Keep this ticket safe. Valid only for the specified seance. Enjoy your movie!",
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY)
        ));
        footer.setHorizontalAlignment(Element.ALIGN_CENTER);
        footer.setPadding(10);
        footer.setBorder(Rectangle.NO_BORDER);
        mainTable.addCell(footer);

        document.add(mainTable);
    }

    /**
     * Helper to create a styled label cell for the info table.
     */
    private PdfPCell createLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        cell.setBackgroundColor(BACKGROUND_COLOR);
        return cell;
    }

    /**
     * Helper to create a styled value cell for the info table.
     */
    private PdfPCell createValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(50, 50, 50))));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        return cell;
    }

    /**
     * Helper to add a separator line.
     */
    private void addSeparator(PdfPCell container, float width) {
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingAfter(15);
        PdfPCell sepCell = new PdfPCell();
        sepCell.setBorder(Rectangle.TOP);
        sepCell.setBorderWidth(width);
        sepCell.setBorderColor(PRIMARY_COLOR);
        sepCell.setPadding(0);
        separator.addCell(sepCell);
        container.addElement(separator);
    }

    /**
     * Generates a QR Code image from a text string.
     */
    private Image generateQRCode(String text) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", baos);

        return Image.getInstance(baos.toByteArray());
    }
}