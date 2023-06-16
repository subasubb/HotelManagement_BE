package com.java.hotel.configurations;

import java.time.Duration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

@Configuration
public class CouchbaseConfig {

	@Autowired
	Cluster cluster;

	@Autowired
	Environment environment;

	@PostConstruct
	public void init() {
		cluster.waitUntilReady(Duration.ofSeconds(environment.getProperty(
				"spring.couchbase.wait-until-ready", Long.class, 30L)));
	}

	@Bean
	public Bucket couchbaseBucket() {
		Bucket bucket = cluster.bucket(environment
				.getRequiredProperty("spring.couchbase.bucket-name"));
		bucket.waitUntilReady(Duration.ofSeconds(environment.getProperty(
				"spring.couchbase.wait-until-ready", Long.class, 30L)));
		return bucket;
	}

}
