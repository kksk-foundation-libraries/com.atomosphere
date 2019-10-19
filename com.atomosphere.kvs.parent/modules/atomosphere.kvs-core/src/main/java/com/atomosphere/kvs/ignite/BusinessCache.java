package com.atomosphere.kvs.ignite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.transactions.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atomosphere.kvs.model.Binary;
import com.atomosphere.kvs.model.BusinessKey;
import com.atomosphere.kvs.model.BusinessValue;
import com.atomosphere.kvs.model.HistoricalIndex;
import com.atomosphere.kvs.model.HistoricalIndexValue;

public class BusinessCache {
	private static final Logger LOG = LoggerFactory.getLogger(BusinessCache.class);

	private final IgniteCache<BusinessKey, HistoricalIndexValue> viewCache;
	private final IgniteCache<Binary, BusinessValue> masterCache;
	private final IgniteTransactions transactions;

	public BusinessCache(Ignite cacheManager, String viewCacheName, String masterCacheName) {
		this(cacheManager, cacheManager, viewCacheName, masterCacheName);
	}

	public BusinessCache(Ignite viewCacheManager, Ignite masterCacheManager, String viewCacheName, String masterCacheName) {
		viewCache = viewCacheManager.cache(viewCacheName);
		masterCache = masterCacheManager.cache(masterCacheName);
		transactions = viewCacheManager.transactions();
	}

	public byte[] get(long current, byte[] key) {
		try (Transaction txn = transactions.txStart()) {
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
			return businessValue.getValue();
		}
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
		BusinessValue businessValue = new BusinessValue(key, value);
		HistoricalIndex historicalIndex = new HistoricalIndex(begin, end, index);
		Binary _index = new Binary(index);
		try (Transaction txn = transactions.txStart()) {
			BusinessValue oldBusinessValue = masterCache.getAndPut(_index, businessValue);
			int result = 0;
			if (oldBusinessValue != null) {
				result = viewCache.invoke(new BusinessKey(oldBusinessValue.getKey()), removeEntryProcessor, index);
				if (LOG.isDebugEnabled()) {
					LOG.debug("remove(put) result:[{}]", result);
				}
			}
			result = viewCache.invoke(businessKey, putEntryProcessor, historicalIndex);
			if (LOG.isDebugEnabled()) {
				LOG.debug("put result:[{}]", result);
			}
			txn.commit();
		}
	}

	public void remove(byte[] index) {
		Binary _index = new Binary(index);
		BusinessValue businessValue = masterCache.get(_index);
		if (businessValue == null) {
			return;
		}
		BusinessKey businessKey = new BusinessKey(businessValue.getKey());
		if (businessKey.getData() == null) {
			LOG.warn("could not extract business key from business value:[{}]", new String(businessValue.getValue()));
			return;
		}
		try (Transaction txn = transactions.txStart()) {
			int result = viewCache.invoke(businessKey, removeEntryProcessor, index);
			if (LOG.isDebugEnabled()) {
				LOG.debug("remove result:[{}]", result);
			}
			masterCache.remove(_index);
			txn.commit();
		}
	}
}
