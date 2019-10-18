package com.atomosphere.kvs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessCache {
	private static final Logger LOG = LoggerFactory.getLogger(BusinessCache.class);

	private final Cache<BusinessKey, HistoricalIndexValue> viewCache;
	private final Cache<Binary, BusinessValue> masterCache;
	private final Function<byte[], byte[]> keyFromValue;

	public BusinessCache(CacheManager cacheManager, Function<byte[], byte[]> keyFromValue, String viewCacheName, String masterCacheName) {
		this(cacheManager, cacheManager, keyFromValue, viewCacheName, masterCacheName);
	}

	public BusinessCache(CacheManager viewCacheManager, CacheManager masterCacheManager, Function<byte[], byte[]> keyFromValue, String viewCacheName, String masterCacheName) {
		viewCache = viewCacheManager.getCache(viewCacheName);
		masterCache = masterCacheManager.getCache(masterCacheName);
		this.keyFromValue = keyFromValue;
	}

	public byte[] get(long current, byte[] key) {
		BusinessKey businessKey = new BusinessKey(key);
		HistoricalIndexValue historicalIndexValue = viewCache.get(businessKey);
		if (historicalIndexValue == null) {
			return null;
		}
		Optional<byte[]> opt_data = Arrays.asList(historicalIndexValue.getHistorycalIndexs()) //
				.stream() //
				.filter(historicalIndex -> historicalIndex.getBegin() <= current && historicalIndex.getEnd() >= current) //
				.map(historicalIndex -> historicalIndex.getIndex()) //
				.findFirst() //
		;
		if (!opt_data.isPresent()) {
			return null;
		}
		byte[] data = opt_data.get();
		if (data == null)
			return null;
		BusinessValue businessValue = masterCache.get(new Binary(data));
		if (businessValue == null) {
			return null;
		}
		return businessValue.getData();
	}

	private static final EntryProcessor<BusinessKey, HistoricalIndexValue, Integer> putEntryProcessor = new EntryProcessor<BusinessKey, HistoricalIndexValue, Integer>() {
		@Override
		public Integer process(MutableEntry<BusinessKey, HistoricalIndexValue> entry, Object... arguments) throws EntryProcessorException {
			int result = -1;
			HistoricalIndex historicalIndex = (HistoricalIndex) arguments[0];
			HistoricalIndexValue historicalIndexValue = entry.getValue();
			if (historicalIndexValue == null) {
				historicalIndexValue = new HistoricalIndexValue(new HistoricalIndex[] { historicalIndex });
				result = 1;
			} else {
				List<HistoricalIndex> list = new ArrayList<>(Arrays.asList(historicalIndexValue.getHistorycalIndexs()) //
						.stream() //
						.filter(_historicalIndex -> !Arrays.equals(_historicalIndex.getIndex(), historicalIndex.getIndex())) //
						.collect(Collectors.toList()) //
				);
				if (list.size() == historicalIndexValue.getHistorycalIndexs().length) {
					result = 2;
				} else {
					result = 3;
				}
				list.add(historicalIndex);
				historicalIndexValue.setHistorycalIndexs(list.toArray(new HistoricalIndex[list.size()]));
			}
			entry.setValue(historicalIndexValue);
			return result;
		}
	};

	private static final EntryProcessor<BusinessKey, HistoricalIndexValue, Integer> removeEntryProcessor = new EntryProcessor<BusinessKey, HistoricalIndexValue, Integer>() {
		@Override
		public Integer process(MutableEntry<BusinessKey, HistoricalIndexValue> entry, Object... arguments) throws EntryProcessorException {
			int result = -1;
			byte[] index = (byte[]) arguments[0];
			if (entry == null) {
				return result;
			}
			HistoricalIndexValue historicalIndexValue = entry.getValue();
			if (historicalIndexValue == null) {
				result = 1;
			} else {
				List<HistoricalIndex> list = new ArrayList<>(Arrays.asList(historicalIndexValue.getHistorycalIndexs()) //
						.stream() //
						.filter(_historicalIndex -> !Arrays.equals(_historicalIndex.getIndex(), index)) //
						.collect(Collectors.toList()) //
				);
				if (list.size() == 0) {
					entry.remove();
					result = 2;
				} else if (list.size() == historicalIndexValue.getHistorycalIndexs().length) {
					result = 1;
				} else {
					result = 3;
					historicalIndexValue.setHistorycalIndexs(list.toArray(new HistoricalIndex[list.size()]));
					entry.setValue(historicalIndexValue);
				}
			}
			return result;
		}
	};

	public void put(byte[] key, byte[] index, byte[] value, long begin, long end) {
		BusinessKey businessKey = new BusinessKey(key);
		BusinessValue businessValue = new BusinessValue(value);
		HistoricalIndex historicalIndex = new HistoricalIndex(begin, end, index);
		Binary _index = new Binary(index);
		masterCache.put(_index, businessValue);
		int result = viewCache.invoke(businessKey, putEntryProcessor, historicalIndex);
		if (LOG.isDebugEnabled()) {
			LOG.debug("put result:[{}]", result);
		}
	}

	public void remove(byte[] index) {
		Binary _index = new Binary(index);
		BusinessValue businessValue = masterCache.get(_index);
		if (businessValue == null) {
			return;
		}
		BusinessKey businessKey = new BusinessKey(keyFromValue.apply(businessValue.getData()));
		if (businessKey.getData() == null) {
			LOG.warn("could not extract business key from business value:[{}]", new String(businessValue.getData()));
			return;
		}
		int result = viewCache.invoke(businessKey, removeEntryProcessor, index);
		if (LOG.isDebugEnabled()) {
			LOG.debug("remove result:[{}]", result);
		}
		masterCache.remove(_index);
	}
}
