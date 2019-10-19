package com.atomosphere.kvs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessValue {
	private byte[] key;
	private byte[] value;
}
