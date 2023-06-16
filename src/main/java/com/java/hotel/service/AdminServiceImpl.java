package com.java.hotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.java.hotel.Model.BookingDetails;
import com.java.hotel.Model.RoomsDetails;
import com.java.hotel.repository.CountersRepository;
import com.java.hotel.repository.HotelRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminServiceImpl {

	@Autowired
	HotelRepository hotelRepository;

	@Autowired
	private CountersRepository countersRepository;

	public RoomsDetails addRooms(RoomsDetails roomsDetails) {
		RoomsDetails response;
		try {
			response = hotelRepository.addRooms(
					String.valueOf(countersRepository.incCounter("0", 0L)),
					roomsDetails);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return response;
	}

	public List<RoomsDetails> getRoomsDetail() {
		List<RoomsDetails> roomDetailsList = hotelRepository.select();
		return roomDetailsList;
	}

	public List<BookingDetails> getBookingDetail() {
		List<BookingDetails> bookingDetailsList = hotelRepository
				.getBookedDetails(null);
		return bookingDetailsList;
	}

}