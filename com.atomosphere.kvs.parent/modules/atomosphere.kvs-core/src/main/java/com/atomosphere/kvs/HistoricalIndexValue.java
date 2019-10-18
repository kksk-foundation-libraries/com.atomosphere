package com.atomosphere.kvs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalIndexValue {
	private HistoricalIndex[] historycalIndexs;
}
