package com.atomosphere.kvs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HistoricalIndex {
	private long begin;
	private long end;
	private byte[] index;
}
