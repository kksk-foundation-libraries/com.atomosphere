package com.atomosphere.kvs.ignite;

import java.time.Instant;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.junit.Test;

import com.atomosphere.kvs.model.Binary;

public class Test001 {

	@Test
	public void test() {
		Ignite ignite = Ignition.start();
		CacheConfiguration<Binary, Binary> cfg001 = new CacheConfiguration<Binary, Binary>("Sample001") //
				.setCacheMode(CacheMode.LOCAL) //
		;
		ignite.createCache(cfg001);
		CacheConfiguration<Binary, Binary> cfg002 = new CacheConfiguration<Binary, Binary>("Sample002") //
				.setCacheMode(CacheMode.LOCAL) //
		;
		ignite.createCache(cfg002);
		BusinessCache cache = new BusinessCache(ignite, "Sample001", "Sample002");
		String v1 = "1234,001,567890";
		String v2 = "1234,002,567891";
		String v22 = "1235,002,567891x";
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

		byte[] data1, data2, data3, data22;

		System.out.println("add v1");
		cache.put(toKey(v1.getBytes()), toIndex(v1.getBytes()), v1.getBytes(), Instant.parse(df1).toEpochMilli(), Instant.parse(dt1).toEpochMilli());
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		System.out.println("add v2");
		cache.put(toKey(v2.getBytes()), toIndex(v2.getBytes()), v2.getBytes(), Instant.parse(df2).toEpochMilli(), Instant.parse(dt2).toEpochMilli());
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		System.out.println("add v3");
		cache.put(toKey(v3.getBytes()), toIndex(v3.getBytes()), v3.getBytes(), Instant.parse(df3).toEpochMilli(), Instant.parse(dt3).toEpochMilli());
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		System.out.println("put v22");
		cache.put(toKey(v22.getBytes()), toIndex(v22.getBytes()), v22.getBytes(), Instant.parse(df2).toEpochMilli(), Instant.parse(dt2).toEpochMilli());
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		System.out.println("remove v1");
		cache.remove(toIndex(v1.getBytes()));
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		System.out.println("remove v2");
		cache.remove(toIndex(v2.getBytes()));
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		System.out.println("remove v22");
		cache.remove(toIndex(v22.getBytes()));
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		System.out.println("remove v3");
		cache.remove(toIndex(v3.getBytes()));
		data1 = cache.get(Instant.parse(cond1).toEpochMilli(), toKey(v1.getBytes()));
		data2 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v2.getBytes()));
		data22 = cache.get(Instant.parse(cond2).toEpochMilli(), toKey(v22.getBytes()));
		data3 = cache.get(Instant.parse(cond3).toEpochMilli(), toKey(v3.getBytes()));
		log(data1, data2, data22, data3);

		ignite.close();
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

	private void log(byte[]... data) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (byte[] _data : data) {
			if (!first)
				sb.append(",");
			sb.append(_data == null ? null : "[" +  new String(_data) + "]");
			first = false;
		}
		System.out.println(sb.toString());
	}
}
