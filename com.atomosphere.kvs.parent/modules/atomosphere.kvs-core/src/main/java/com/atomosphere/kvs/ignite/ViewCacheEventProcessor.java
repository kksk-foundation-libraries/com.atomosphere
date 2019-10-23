package com.atomosphere.kvs.ignite;

import org.apache.ignite.Ignite;

import com.atomosphere.kvs.model.PutEvent;
import com.atomosphere.kvs.model.RemoveEvent;

public class ViewCacheEventProcessor {
	private final ViewCacheAccessor viewCacheAccessor;

	public ViewCacheEventProcessor(Ignite ignite, String cacheName) {
		viewCacheAccessor = new ViewCacheAccessor(ignite, cacheName);
	}

	public void onPut(PutEvent putEvent) {
		if (putEvent.getBusinessKeyOld() == null) {
			viewCacheAccessor.add(putEvent.getBusinessKeyNew(), putEvent.getPrimaryKey(), putEvent.getStart(), putEvent.getEnd());
		} else {
			viewCacheAccessor.modify(putEvent.getBusinessKeyOld(), putEvent.getBusinessKeyNew(), putEvent.getPrimaryKey(), putEvent.getStart(), putEvent.getEnd());
		}
	}

	public void onRemove(RemoveEvent removeEvent) {
		if (removeEvent.getBusinessKey() != null) {
			viewCacheAccessor.remove(removeEvent.getBusinessKey(), removeEvent.getPrimaryKey());
		}
	}
}
