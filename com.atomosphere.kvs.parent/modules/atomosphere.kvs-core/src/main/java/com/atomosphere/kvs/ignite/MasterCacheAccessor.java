package com.atomosphere.kvs.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;

import com.atomosphere.kvs.model.Binary;
import com.atomosphere.kvs.model.BusinessValue;

public class MasterCacheAccessor {
	private final IgniteCache<Binary, BusinessValue> cache;

	public MasterCacheAccessor(Ignite ignite, String cacheName) {
		cache = ignite.cache(cacheName);
	}

	public byte[] get(byte[] index) {
		return cache.get(new Binary(index)).getValue();
	}

	public boolean add(byte[] key, byte[] index, byte[] value, long begein, long end) {
		return cache.putIfAbsent(new Binary(index), new BusinessValue(key, value));
	}

	public void modify(byte[] key, byte[] index, byte[] value, long begein, long end) {
		cache.put(new Binary(index), new BusinessValue(key, value));
	}

	public void remove(byte[] index) {
		cache.remove(new Binary(index));
	}
}
