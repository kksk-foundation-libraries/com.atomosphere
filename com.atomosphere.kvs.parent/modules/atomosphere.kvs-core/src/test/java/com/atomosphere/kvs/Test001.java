package com.atomosphere.kvs;

import java.time.Instant;

import javax.cache.CacheManager;
import javax.cache.Caching;

import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.junit.Test;

public class Test001 {

	@Test
	public void test() {
		CacheManager manager = Caching.getCachingProviders().iterator().next().getCacheManager();
		CacheConfiguration<Binary, Binary> cfg001 = new CacheConfiguration<Binary, Binary>("Sample001") //
				.setCacheMode(CacheMode.LOCAL) //
		;
		manager.createCache("Sample001", cfg001);
		CacheConfiguration<Binary, Binary> cfg002 = new CacheConfiguration<Binary, Binary>("Sample002") //
				.setCacheMode(CacheMode.LOCAL) //
		;
		manager.createCache("Sample002", cfg002);
		BusinessCache cache = new BusinessCache(manager, this::toKey, "Sample001", "Sample002");
		String v1 = "1234,001,567890";
		String v2 = "1234,002,567891";
		String v3 = "1234,003,567892";
		String df1 = "2019-10-18T00:00:00.00Z";
		String dt1 = "2019-10-20T00:00:00.00Z";
		String df2 = "2019-10-20T00:00:00.00Z";
		String dt2 = "2019-10-22T00:00:00.00Z";
		String df3 = "2019-10-22T00:00:00.00Z";
		String dt3 = "2019-10-24T00:00:00.00Z";
		String cond1 = "2019-10-19T00:00:00.00Z";
		String cond2 = "2019-10-21T00:00:00.00Z";
		String cond3 = "2019-10-23T00:00:00.00Z";

		cache.put(toKey(v1.getBytes()), toIndex(v1.getBytes()), v1.getBytes(), Instant.parse(df1).toEpochMilli(), Instant.parse(dt1).toEpochMilli());
		byte[] data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		System.out.format("data1:%s\n", data1 == null ? null : new String(data1));

		cache.put(toKey(v2.getBytes()), toIndex(v2.getBytes()), v2.getBytes(), Instant.parse(df2).toEpochMilli(), Instant.parse(dt2).toEpochMilli());
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		System.out.format("data1:%s\n", data1 == null ? null : new String(data1));
		byte[] data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		System.out.format("data2:%s\n", data2 == null ? null : new String(data2));

		cache.put(toKey(v3.getBytes()), toIndex(v3.getBytes()), v3.getBytes(), Instant.parse(df3).toEpochMilli(), Instant.parse(dt3).toEpochMilli());

		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		System.out.format("data2:%s\n", data2 == null ? null : new String(data2));

		byte[] data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		System.out.format("data3:%s\n", data3 == null ? null : new String(data3));

		cache.remove(toIndex(v1.getBytes()));
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		System.out.format("data1:%s\n", data1 == null ? null : new String(data1));

		cache.remove(toIndex(v2.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		System.out.format("data2:%s\n", data2 == null ? null : new String(data2));

		cache.remove(toIndex(v3.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		System.out.format("data3:%s\n", data3 == null ? null : new String(data3));

		manager.close();
		//		fail("Not yet implemented");
	}

	public byte[] toKey(byte[] arr) {
		String s = new String(arr);
		String k = s.split(",")[0];
		return k.getBytes();
	}

	public byte[] toIndex(byte[] arr) {
		String s = new String(arr);
		String i = s.split(",")[1];
		return i.getBytes();
	}
}
