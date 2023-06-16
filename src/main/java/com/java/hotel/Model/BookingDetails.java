package com.java.hotel.Model;

import lombok.Data;

@Data
public class BookingDetails {

	String bookingId;

	String name;

	String emailId;

	String phoneNumber;

	String dob;

	String age;

	String idType;

	String idNumber;

	String location;

	String fromDate;

	String toDate;

	String adultCount;

	String childCount;

	String totalAmount;

	String roomsBooked;

	String classId = "200";
}
