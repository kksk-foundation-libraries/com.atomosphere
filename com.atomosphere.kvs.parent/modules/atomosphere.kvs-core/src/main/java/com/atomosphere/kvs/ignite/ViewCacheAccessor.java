package com.atomosphere.kvs.ignite;

import java.util.Arrays;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.transactions.Transaction;

import com.atomosphere.kvs.model.BusinessKey;
import com.atomosphere.kvs.model.HistoricalIndex;
import com.atomosphere.kvs.model.HistoricalIndexValue;

public class ViewCacheAccessor {
	private final IgniteCache<BusinessKey, HistoricalIndexValue> cache;
	private final IgniteTransactions transactions;

	public ViewCacheAccessor(Ignite ignite, String cacheName) {
		cache = ignite.cache(cacheName);
		transactions = ignite.transactions();
	}

	private final GetEntryProcessor getEntryProcessor = new GetEntryProcessor();

	public byte[] getIndex(long current, byte[] key) {
		return cache.invoke(new BusinessKey(key), getEntryProcessor, Long.valueOf(current));
	}

	public void add(byte[] key, byte[] index, long begin, long end) {
		try (Transaction txn = transactions.txStart()) {
			_add(key, index, begin, end);
		}
	}

	public void modify(byte[] key, byte[] index, long begin, long end) {
		try (Transaction txn = transactions.txStart()) {
			_modify(key, index, begin, end);
		}
	}

	public void modify(byte[] oldKey, byte[] newKey, byte[] index, long begin, long end) {
		try (Transaction txn = transactions.txStart()) {
			if (oldKey == newKey || Arrays.equals(oldKey, newKey)) {
				_modify(oldKey, index, begin, end);
			} else {
				_remove(oldKey, index);
				_add(newKey, index, begin, end);
			}
		}
	}

	public void remove(byte[] key, byte[] index) {
		try (Transaction txn = transactions.txStart()) {
			_remove(key, index);
		}
	}

	private void _add(byte[] key, byte[] index, long begin, long end) {
		// TODO
	}
	
	private void _modify(byte[] key, byte[] index, long begin, long end) {
		// TODO
	}
	
	private void _remove(byte[] key, byte[] index) {
		// TODO
	}

	private static final class GetEntryProcessor implements EntryProcessor<BusinessKey, HistoricalIndexValue, byte[]> {
		@Override
		public byte[] process(MutableEntry<BusinessKey, HistoricalIndexValue> entry, Object... arguments) throws EntryProcessorException {
			Long current = (Long) arguments[0];
			for (HistoricalIndex historicalIndex : entry.getValue().getHistorycalIndexs()) {
				if (historicalIndex.getBegin() <= current.longValue() && current.longValue() < historicalIndex.getEnd()) {
					return historicalIndex.getIndex();
				}
			}
			return null;
		}
	}
}
