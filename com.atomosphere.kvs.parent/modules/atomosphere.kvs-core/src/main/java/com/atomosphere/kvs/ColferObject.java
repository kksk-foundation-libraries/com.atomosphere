package com.atomosphere.kvs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;

public abstract class ColferObject implements Binarylizable {
	public abstract byte[] marshal(OutputStream out, byte[] buf) throws IOException;

	public abstract int unmarshal(byte[] buf, int offset);

	public final byte[] marshal() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			return marshal(out, null);
		} catch (IOException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final <T extends ColferObject> T unmarshal(byte[] buf) {
		unmarshal(buf, 0);
		return (T) this;
	}

	@Override
	public void readBinary(BinaryReader reader) throws BinaryObjectException {
		unmarshal(reader.readByteArray("_data"));
	}

	@Override
	public void writeBinary(BinaryWriter writer) throws BinaryObjectException {
		writer.writeByteArray("_data", marshal());
	}
}
