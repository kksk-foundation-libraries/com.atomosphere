package com.atomosphere.annotation;

import java.time.Instant;

public enum ColferType {
	BOOL("bool", boolean.class), //
	UINT8("uint8", byte.class), //
	UINT16("uint16", short.class), //
	UINT32("uint32", int.class), //
	UINT64("uint64", long.class), //
	INT32("int32", int.class), //
	INT64("int64", long.class), //
	FLOAT32("float32", float.class), //
	FLOAT64("float64", double.class), //
	TIMESTAMP("timestamp", Instant.class), //
	TEXT("text", String.class), //
	BINARY("binary", byte[].class), //
	OTHER(Schema.UNDEFINED, Object.class), //
	;

	public final String colferType;
	public final Class<?> javaType;

	private ColferType(String colferType, Class<?> javaType) {
		this.colferType = colferType;
		this.javaType = javaType;
	}

}
