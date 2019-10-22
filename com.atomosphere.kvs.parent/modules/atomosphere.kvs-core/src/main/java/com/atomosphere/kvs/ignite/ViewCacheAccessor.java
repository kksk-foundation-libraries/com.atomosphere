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
	private final AddEntryProcessor addEntryProcessor = new AddEntryProcessor();
	private final ModifyEntryProcessor modifyEntryProcessor = new ModifyEntryProcessor();
	private final RemoveEntryProcessor removeEntryProcessor = new RemoveEntryProcessor();

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
		cache.invoke(new BusinessKey(key), addEntryProcessor, new HistoricalIndex(begin, end, index));
	}

	private void _modify(byte[] key, byte[] index, long begin, long end) {
		cache.invoke(new BusinessKey(key), modifyEntryProcessor, new HistoricalIndex(begin, end, index));
	}

	private void _remove(byte[] key, byte[] index) {
		cache.invoke(new BusinessKey(key), removeEntryProcessor, index);
	}

	private static final class GetEntryProcessor implements EntryProcessor<BusinessKey, HistoricalIndexValue, byte[]> {
		@Override
		public byte[] process(MutableEntry<BusinessKey, HistoricalIndexValue> entry, Object... arguments) throws EntryProcessorException {
			Long current = (Long) arguments[0];
			for (HistoricalIndex historicalIndex : entry.getValue().getHistoricalIndexs()) {
				if (historicalIndex.getBegin() <= current.longValue() && current.longValue() < historicalIndex.getEnd()) {
					return historicalIndex.getIndex();
				}
			}
			return null;
		}
	}

	private static final class AddEntryProcessor implements EntryProcessor<BusinessKey, HistoricalIndexValue, Void> {
		@Override
		public Void process(MutableEntry<BusinessKey, HistoricalIndexValue> entry, Object... arguments) throws EntryProcessorException {
			HistoricalIndex historicalIndex = (HistoricalIndex) arguments[0];
			HistoricalIndex[] historicalIndexs = null;
			if (entry.getValue() == null) {
				entry.setValue(new HistoricalIndexValue(new HistoricalIndex[] { historicalIndex }));
			} else {
				historicalIndexs = new HistoricalIndex[entry.getValue().getHistoricalIndexs().length + 1];
				historicalIndexs[0] = historicalIndex;
				System.arraycopy(entry.getValue().getHistoricalIndexs(), 0, historicalIndexs, 1, entry.getValue().getHistoricalIndexs().length);
				entry.getValue().setHistoricalIndexs(historicalIndexs);
			}
			return null;
		}
	}

	private static final class ModifyEntryProcessor implements EntryProcessor<BusinessKey, HistoricalIndexValue, Void> {
		@Override
		public Void process(MutableEntry<BusinessKey, HistoricalIndexValue> entry, Object... arguments) throws EntryProcessorException {
			HistoricalIndex historicalIndex = (HistoricalIndex) arguments[0];
			HistoricalIndex[] historicalIndexs = entry.getValue().getHistoricalIndexs();
			for (int i = 0; i < historicalIndexs.length; i++) {
				if (Arrays.equals(historicalIndexs[i].getIndex(), historicalIndex.getIndex())) {
					historicalIndexs[i] = historicalIndex;
				}
			}
			entry.getValue().setHistoricalIndexs(historicalIndexs);
			return null;
		}
	}

	private static final class RemoveEntryProcessor implements EntryProcessor<BusinessKey, HistoricalIndexValue, Void> {
		@Override
		public Void process(MutableEntry<BusinessKey, HistoricalIndexValue> entry, Object... arguments) throws EntryProcessorException {
			HistoricalIndex historicalIndex = (HistoricalIndex) arguments[0];
			HistoricalIndex[] historicalIndexs = entry.getValue().getHistoricalIndexs();
			if (historicalIndexs.length == 1) {
				if (Arrays.equals(historicalIndexs[0].getIndex(), historicalIndex.getIndex())) {
					entry.remove();
				}
			} else {
				int idx = 0;
				HistoricalIndex[] newHistoricalIndexs = Arrays.copyOf(historicalIndexs, historicalIndexs.length);
				for (int i = 0; i < historicalIndexs.length; i++) {
					if (!Arrays.equals(historicalIndexs[i].getIndex(), historicalIndex.getIndex())) {
						newHistoricalIndexs[idx] = historicalIndexs[i];
					}
				}
				if (historicalIndexs.length != idx + 1) {
					historicalIndexs = Arrays.copyOf(newHistoricalIndexs, idx + 1);
					entry.getValue().setHistoricalIndexs(historicalIndexs);
				}
			}
			return null;
		}
	}
}
