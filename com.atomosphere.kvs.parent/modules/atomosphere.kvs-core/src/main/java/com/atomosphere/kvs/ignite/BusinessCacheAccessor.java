package com.atomosphere.kvs.ignite;

import java.util.List;

import org.apache.ignite.Ignite;

import com.atomosphere.kvs.model.Record;

public class BusinessCacheAccessor {
	private final MasterCacheAccessor masterCacheAccessor;
	private final ViewCacheAccessor viewCacheAccessor;

	public BusinessCacheAccessor(Ignite ignite, String masterCacheName, String viewCacheName) {
		masterCacheAccessor = new MasterCacheAccessor(ignite, masterCacheName);
		viewCacheAccessor = new ViewCacheAccessor(ignite, viewCacheName);
	}

	public List<Record> get(long current, byte[] key) {
		return masterCacheAccessor.get(viewCacheAccessor.getPrimaryKeys(current, key));
	}
}
