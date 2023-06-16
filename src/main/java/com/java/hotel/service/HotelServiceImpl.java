package com.java.hotel.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.java.hotel.Model.BookingDetails;
import com.java.hotel.Model.RoomsDetails;
import com.java.hotel.repository.CountersRepository;
import com.java.hotel.repository.HotelRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HotelServiceImpl {

	@Autowired
	HotelRepository hotelRepository;

	@Autowired
	private CountersRepository countersRepository;

	public List<RoomsDetails> getAvailableRooms(String fromDate, String toDate,
			String location) {
		List<BookingDetails> bookingDetails = hotelRepository
				.getBookedDetails(location);
		List<RoomsDetails> roomDetails = hotelRepository.select();
		AtomicInteger usedRooms = new AtomicInteger(0);
		bookingDetails.stream().forEach(book -> {
			if (DateRangeChecker(book.getFromDate(), book.getToDate(),
					fromDate)) {
				usedRooms.addAndGet(Integer.parseInt(book.getRoomsBooked()));
			}
		});
		List<RoomsDetails> filteredList = roomDetails.stream()
				.filter(room -> room.getLocation().equals(location))
				.collect(Collectors.toList());
		filteredList.stream().forEach(room -> {
			room.setAvailableRooms(usedRooms
					.updateAndGet(currentUsedRooms -> room.getAvailableRooms()
							- currentUsedRooms));
		});
		return filteredList;

	}

	private boolean DateRangeChecker(String fromDate, String toDate,
			String givenFromDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate from = LocalDate.parse(fromDate, formatter);
		LocalDate to = LocalDate.parse(toDate, formatter);
		LocalDate checkFrom = LocalDate.parse(givenFromDate, formatter);
		if ((checkFrom.isAfter(from) && checkFrom.isBefore(to))
				|| checkFrom.isEqual(from) || checkFrom.isEqual(to)) {
			return true;
		}
		return false;
	}

	public BookingDetails confirmBooking(BookingDetails bookingDetails) {
		String trimmedName = bookingDetails.getName().substring(0, 3)
				.toUpperCase();
		String locationCode = bookingDetails.getLocation().substring(0, 2)
				.toUpperCase();
		String datetime = bookingDetails.getFromDate().replace("-", "");

		String documentName = "BD_" + locationCode + "_" + trimmedName + "_"
				+ datetime + "_" + countersRepository.incCounter("0", 0L);
		bookingDetails.setBookingId(documentName);
		BookingDetails response = null;
		try {
			response = hotelRepository.confirmBooking(documentName,
					bookingDetails);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}

		return response;
	}

	public byte[] generatePdf(BookingDetails bookingDetails) {
		// Create a new Document
		Document document = new Document();
		try {
			// Create a ByteArrayOutputStream to hold the PDF content
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter writer = PdfWriter.getInstance(document, baos);

			PdfPageEventHelper eventHelper = new PdfPageEventHelper() {
				public void onEndPage(PdfWriter writer, Document document) {
					// Create a PdfContentByte for adding the watermark
					PdfContentByte content = writer.getDirectContentUnder();

					// Define the watermark text and font
					Font watermarkFont = FontFactory.getFont(
							FontFactory.HELVETICA, 60, Font.BOLD,
							BaseColor.LIGHT_GRAY);
					Phrase watermark = new Phrase("AADHYA Residency",
							watermarkFont);

					// Set the watermark's rotation
					PdfGState gs = new PdfGState();
					gs.setFillOpacity(0.2f);

					// Add the watermark to the page
					ColumnText.showTextAligned(content, Element.ALIGN_CENTER,
							watermark, PageSize.A4.getWidth() / 2,
							PageSize.A4.getHeight() / 2, 45);
				}
			};
			writer.setPageEvent(eventHelper);

			// Open the document
			document.open();

			document.newPage();

			Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,
					17);
			Paragraph heading = new Paragraph("AADHYA Residency", headingFont);
			heading.setAlignment(Element.ALIGN_CENTER);
			heading.setSpacingAfter(10f);
			document.add(heading);

			Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 13);
			Paragraph textContent = new Paragraph(
					"Thank you for booking rooms. Your Booking ID is "
							+ bookingDetails.getBookingId(),
					textFont);
			textContent.setAlignment(Element.ALIGN_LEFT);
			document.add(textContent);

			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);

			// Add the table header
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,
					13);
			PdfPCell headerCell = new PdfPCell(
					new Phrase("Booking Details", headerFont));
			headerCell.setColspan(2);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(headerCell);

			// Add the table rows
			Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
			addTableCell(table, "Name", bookingDetails.getName(), cellFont);
			addTableCell(table, "Email ID", bookingDetails.getEmailId(),
					cellFont);
			addTableCell(table, "Phone Number", bookingDetails.getPhoneNumber(),
					cellFont);
			addTableCell(table, "Room Location", bookingDetails.getLocation(),
					cellFont);
			addTableCell(table, "Check In", bookingDetails.getFromDate(),
					cellFont);
			addTableCell(table, "Check Out", bookingDetails.getToDate(),
					cellFont);
			addTableCell(table, "No. of Rooms Booked",
					bookingDetails.getRoomsBooked(), cellFont);
			addTableCell(table, "Total Amount",
					"₹" + bookingDetails.getTotalAmount(), cellFont);
			addTableCell(table, "Total Number of Person", String
					.valueOf(Integer.parseInt(bookingDetails.getAdultCount())
							+ Integer.parseInt(bookingDetails.getChildCount())),
					cellFont);
			addTableCell(table, "Age", bookingDetails.getAge(), cellFont);
			addTableCell(table, bookingDetails.getIdType(),
					bookingDetails.getIdNumber(), cellFont);

			// Add the table to the document
			document.add(table);

			Font textFont1 = FontFactory.getFont(FontFactory.HELVETICA, 9);
			Paragraph textContent1 = new Paragraph(
					"Kindly bring the copy of this document.\nBreakfast will be given as compliment.\n*If you need any modification in the booking kindly contact us.",
					textFont1);
			textContent1.setAlignment(Element.ALIGN_LEFT);
			document.add(textContent1);

			// Add the footer
			Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7);
			Paragraph footer = new Paragraph(
					"© 2023 AADHYA Residency. All rights reserved.",
					footerFont);
			footer.setAlignment(Element.ALIGN_CENTER);
			footer.setSpacingBefore(450f);
			document.add(footer);

			// Close the document
			document.close();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	private void addTableCell(PdfPTable table, String label, String value,
			Font font) {
		PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
		PdfPCell valueCell = new PdfPCell(new Phrase(value, font));

		table.addCell(labelCell);
		table.addCell(valueCell);
	}

}