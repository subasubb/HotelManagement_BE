package com.java.hotel.Model;

import lombok.Data;

@Data
public class RoomsDetails {

	String location;

	Integer availableRooms;

	Double price;

	Integer classId = 100;

}
