package com.atomosphere.kvs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalIndexValue {
	private HistoricalIndex[] historycalIndexs;
}
