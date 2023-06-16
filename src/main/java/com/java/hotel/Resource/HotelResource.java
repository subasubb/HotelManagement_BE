package com.java.hotel.Resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.hotel.Model.BookingDetails;
import com.java.hotel.Model.RoomsDetails;
import com.java.hotel.service.HotelServiceImpl;

@Validated
@RestController
public class HotelResource {

	@Autowired
	HotelServiceImpl hotelServiceImpl;

	@GetMapping("/api/v1/get/rooms")
	public List<RoomsDetails> getAvailableRooms(
			@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate,
			@RequestParam(value = "location") String location) {
		return hotelServiceImpl.getAvailableRooms(fromDate, toDate, location);
	}

	@PostMapping("/api/v1/post/confirmBooking")
	public BookingDetails getValues(
			@RequestBody BookingDetails bookingDetails) {
		return hotelServiceImpl.confirmBooking(bookingDetails);
	}

	@PostMapping("/api/v1/post/generatepdf")
	public byte[] generatePdf(@RequestBody BookingDetails bookingDetails) {
		return hotelServiceImpl.generatePdf(bookingDetails);
	}

}