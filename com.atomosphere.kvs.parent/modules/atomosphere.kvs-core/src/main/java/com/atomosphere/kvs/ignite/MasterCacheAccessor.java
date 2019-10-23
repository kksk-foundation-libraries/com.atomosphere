package com.atomosphere.kvs.ignite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;

import com.atomosphere.kvs.model.PrimaryKey;
import com.atomosphere.kvs.model.Record;

class MasterCacheAccessor {
	private final IgniteCache<PrimaryKey, Record> cache;

	public MasterCacheAccessor(Ignite ignite, String cacheName) {
		cache = ignite.cache(cacheName);
	}

	public Record get(PrimaryKey primaryKey) {
		return cache.get(primaryKey);
	}

	public List<Record> get(List<PrimaryKey> primaryKeys) {
		return new ArrayList<>(primaryKeys.stream().map(primaryKey -> cache.get(primaryKey)).collect(Collectors.toList()));
	}

	public Record put(PrimaryKey primaryKey, Record record) {
		return cache.getAndPut(primaryKey, record);
	}

	public Record remove(PrimaryKey primaryKey) {
		return cache.getAndRemove(primaryKey);
	}
}
