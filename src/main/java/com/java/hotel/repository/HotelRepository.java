package com.java.hotel.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutationResult;
import com.java.hotel.Model.BookingDetails;
import com.java.hotel.Model.RoomsDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Scope("prototype")
public class HotelRepository {

	private final Cluster cluster;
	private final Collection collection;

	@Autowired
	public HotelRepository(Cluster cluster, Bucket bucket) {
		this.cluster = cluster;
		this.collection = bucket.defaultCollection();
	}

	public List<RoomsDetails> select() {
		String query = "SELECT `hotel`.* FROM `hotel` where classId = 100";
		return cluster.query(query).rowsAs(RoomsDetails.class);
	}

	public List<BookingDetails> getBookedDetails(String location) {
		String query;
		if (location != null) {
			query = "SELECT `hotel`.* FROM `hotel` where classId = '200' AND location = "
					+ "\"" + location + "\"";
		} else {
			query = "SELECT `hotel`.* FROM `hotel` where classId = '200'";
		}
		return cluster.query(query).rowsAs(BookingDetails.class);
	}

	public BookingDetails confirmBooking(String documentName,
			BookingDetails bookingDetails) {
		log.debug("Insert document {}", bookingDetails);
		try {
			collection.insert(documentName, bookingDetails);
		} catch (DocumentExistsException e) {
			throw e;
		}
		return bookingDetails;
	}

	public RoomsDetails addRooms(String documentId, RoomsDetails roomsDetails) {
		log.debug("Insert document {}", roomsDetails);
		try {
			collection.insert("RoomDetails_" + documentId, roomsDetails);
		} catch (DocumentExistsException e) {
			throw e;
		}
		return roomsDetails;
	}

	public List<RoomsDetails> selectUsingId(String accountId) {
		String query = "SELECT * FROM `info` where id = " + accountId;
		return cluster.query(query).rowsAs(RoomsDetails.class);
	}

}