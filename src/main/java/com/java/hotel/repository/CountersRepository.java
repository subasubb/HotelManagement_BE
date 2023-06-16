package com.java.hotel.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.BinaryCollection;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.kv.CounterResult;
import com.couchbase.client.java.kv.IncrementOptions;

@Service
public class CountersRepository {

	private static final long INITIAL_COUNTER_VALUE = 0L;

	private final BinaryCollection collection;

	@Autowired
	public CountersRepository(Bucket bucket) {
		this.collection = bucket.defaultCollection().binary();
	}

	public long incCounter(final String counter) {
		CounterResult result = collection.increment(counter, IncrementOptions
				.incrementOptions().initial(INITIAL_COUNTER_VALUE));
		return result.content();
	}

	public long incCounter(final String counter, final long initial_value) {
		CounterResult result = collection.increment(counter,
				IncrementOptions.incrementOptions().initial(initial_value));
		return result.content();
	}
}