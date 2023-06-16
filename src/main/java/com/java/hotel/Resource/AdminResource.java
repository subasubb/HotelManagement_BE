package com.java.hotel.Resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.java.hotel.Model.BookingDetails;
import com.java.hotel.Model.RoomsDetails;
import com.java.hotel.service.AdminServiceImpl;

@Validated
@RestController
public class AdminResource {

	@Autowired
	AdminServiceImpl adminServiceImpl;

	@PostMapping("/api/v1/add/rooms")
	public RoomsDetails addRooms(@RequestBody RoomsDetails roomsDetails) {
		return adminServiceImpl.addRooms(roomsDetails);
	}

	@GetMapping("/api/v1/get/roomsDetail")
	public List<RoomsDetails> getRoomsDetail() {
		return adminServiceImpl.getRoomsDetail();
	}

	@GetMapping("/api/v1/get/bookingDetail")
	public List<BookingDetails> getBookingDetail() {
		return adminServiceImpl.getBookingDetail();
	}

}