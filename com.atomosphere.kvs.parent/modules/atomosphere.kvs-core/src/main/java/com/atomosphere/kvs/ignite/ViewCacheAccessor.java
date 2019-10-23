package com.atomosphere.kvs.ignite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.transactions.Transaction;

import com.atomosphere.kvs.model.BusinessKey;
import com.atomosphere.kvs.model.Historical;
import com.atomosphere.kvs.model.HistoricalArray;
import com.atomosphere.kvs.model.PrimaryKey;
import com.atomosphere.kvs.model.TimestampData;

class ViewCacheAccessor {
	private final IgniteCache<BusinessKey, HistoricalArray> cache;
	private final IgniteTransactions transactions;

	public ViewCacheAccessor(Ignite ignite, String cacheName) {
		cache = ignite.cache(cacheName);
		transactions = ignite.transactions();
	}

	private final GetEntryProcessor getEntryProcessor = new GetEntryProcessor();
	private final AddEntryProcessor addEntryProcessor = new AddEntryProcessor();
	private final ModifyEntryProcessor modifyEntryProcessor = new ModifyEntryProcessor();
	private final RemoveEntryProcessor removeEntryProcessor = new RemoveEntryProcessor();

	public List<PrimaryKey> getPrimaryKeys(long current, byte[] key) {
		return cache.invoke(new BusinessKey().withData(key), getEntryProcessor, new TimestampData().withData(current));
	}

	public void add(BusinessKey businessKey, PrimaryKey primaryKey, TimestampData start, TimestampData end) {
		_add(businessKey, primaryKey, start, end);
	}

	public void modify(BusinessKey businessKey, PrimaryKey primaryKey, TimestampData start, TimestampData end) {
		_modify(businessKey, primaryKey, start, end);
	}

	public void modify(BusinessKey businessKeyOld, BusinessKey businessKeyNew, PrimaryKey primaryKey, TimestampData start, TimestampData end) {
		if (businessKeyOld.equals(businessKeyNew)) {
			_modify(businessKeyOld, primaryKey, start, end);
		} else {
			try (Transaction txn = transactions.txStart()) {
				_remove(businessKeyOld, primaryKey);
				_add(businessKeyNew, primaryKey, start, end);
			}
		}
	}

	public void remove(BusinessKey businessKey, PrimaryKey primaryKey) {
		_remove(businessKey, primaryKey);
	}

	private void _add(BusinessKey businessKey, PrimaryKey primaryKey, TimestampData start, TimestampData end) {
		cache.invoke(businessKey, addEntryProcessor, new Historical().withPrimaryKey(primaryKey).withStart(start).withEnd(end));
	}

	private void _modify(BusinessKey businessKey, PrimaryKey primaryKey, TimestampData start, TimestampData end) {
		cache.invoke(businessKey, modifyEntryProcessor, new Historical().withPrimaryKey(primaryKey).withStart(start).withEnd(end));
	}

	private void _remove(BusinessKey businessKey, PrimaryKey primaryKey) {
		cache.invoke(businessKey, removeEntryProcessor, primaryKey);
	}

	private static final List<PrimaryKey> BLANK_LIST = new ArrayList<>();

	private static final class GetEntryProcessor implements EntryProcessor<BusinessKey, HistoricalArray, List<PrimaryKey>> {
		@Override
		public List<PrimaryKey> process(MutableEntry<BusinessKey, HistoricalArray> entry, Object... arguments) throws EntryProcessorException {
			long current = ((TimestampData) arguments[0]).getData();
			if (!entry.exists()) {
				return BLANK_LIST;
			}
			List<PrimaryKey> list = new ArrayList<>( //
					Arrays.asList(entry.getValue().getData()) //
							.stream() //
							.filter(historical -> historical.getStart().getData() <= current && current < historical.getEnd().getData()) //
							.map(historical -> historical.getPrimaryKey()) //
							.collect(Collectors.toList()) //
			);
			if (list.size() == 0)
				return BLANK_LIST;
			return list;
		}
	}

	private static final class AddEntryProcessor implements EntryProcessor<BusinessKey, HistoricalArray, Void> {
		@Override
		public Void process(MutableEntry<BusinessKey, HistoricalArray> entry, Object... arguments) throws EntryProcessorException {
			Historical historical = (Historical) arguments[0];
			if (!entry.exists()) {
				entry.setValue(new HistoricalArray().withData(new Historical[] { historical }));
			} else {
				List<Historical> list = new ArrayList<>();
				list.add(historical);
				list.addAll(Arrays.asList(entry.getValue().getData()));
				entry.getValue().setData(list.toArray(new Historical[list.size()]));
			}
			return null;
		}
	}

	private static final class ModifyEntryProcessor implements EntryProcessor<BusinessKey, HistoricalArray, Void> {
		@Override
		public Void process(MutableEntry<BusinessKey, HistoricalArray> entry, Object... arguments) throws EntryProcessorException {
			Historical historical = (Historical) arguments[0];
			List<Historical> list = new ArrayList<>( //
					Arrays.asList(entry.getValue().getData()) //
							.stream() //
							.map(_historical -> _historical.getPrimaryKey().equals(historical.getPrimaryKey()) ? historical : _historical) //
							.collect(Collectors.toList()) //
			);
			entry.getValue().setData(list.toArray(new Historical[list.size()]));
			return null;
		}
	}

	private static final class RemoveEntryProcessor implements EntryProcessor<BusinessKey, HistoricalArray, Void> {
		@Override
		public Void process(MutableEntry<BusinessKey, HistoricalArray> entry, Object... arguments) throws EntryProcessorException {
			PrimaryKey primaryKey = (PrimaryKey) arguments[0];
			List<Historical> list = new ArrayList<>( //
					Arrays.asList(entry.getValue().getData()) //
							.stream() //
							.filter(_historical -> _historical.getPrimaryKey().equals(primaryKey)) //
							.collect(Collectors.toList()) //
			);
			if (list.size() == 0) {
				entry.remove();
			} else {
				entry.getValue().setData(list.toArray(new Historical[list.size()]));
			}
			return null;
		}
	}
}
