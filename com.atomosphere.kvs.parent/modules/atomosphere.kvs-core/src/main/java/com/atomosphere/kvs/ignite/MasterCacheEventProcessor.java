package com.atomosphere.kvs.ignite;

import org.apache.ignite.Ignite;

import com.atomosphere.kvs.model.PutEvent;
import com.atomosphere.kvs.model.Record;
import com.atomosphere.kvs.model.RemoveEvent;

public class MasterCacheEventProcessor {
	private final MasterCacheAccessor masterCacheAccessor;

	public MasterCacheEventProcessor(Ignite ignite, String cacheName) {
		masterCacheAccessor = new MasterCacheAccessor(ignite, cacheName);
	}

	public PutEvent onPut(PutEvent putEvent) {
		Record old = masterCacheAccessor.put( //
				putEvent.getPrimaryKey(), //
				new Record() //
						.withPrimaryKey(putEvent.getPrimaryKey())//
						.withBusinessKey(putEvent.getBusinessKeyNew()) //
						.withStart(putEvent.getStart()) //
						.withEnd(putEvent.getEnd()) //
						.withOthers(putEvent.getOthers()) //
		);
		if (old != null) {
			return putEvent.withBusinessKeyOld(old.getBusinessKey());
		} else {
			return putEvent;
		}
	}

	public RemoveEvent onRemove(RemoveEvent removeEvent) {
		Record old = masterCacheAccessor.remove(removeEvent.getPrimaryKey());
		if (old != null) {
			return removeEvent.withBusinessKey(old.getBusinessKey());
		}
		return removeEvent;
	}
}
