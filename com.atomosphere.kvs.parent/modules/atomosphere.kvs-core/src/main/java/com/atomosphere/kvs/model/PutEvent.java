package com.atomosphere.kvs.model;


// Code generated by colf(1); DO NOT EDIT.


import static java.lang.String.format;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.InputMismatchException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;


/**
 * Data bean with built-in serialization support.

 * @author generated by colf(1)
 * @see <a href="https://github.com/pascaldekloe/colfer">Colfer's home</a>
 */
@javax.annotation.Generated(value="colf(1)", comments="Colfer from schema file model.colf")
public class PutEvent extends com.atomosphere.kvs.ColferObject implements Serializable {

	/** The upper limit for serial byte sizes. */
	public static int colferSizeMax = 16 * 1024 * 1024;




	public PrimaryKey primaryKey;

	public TimestampData start;

	public TimestampData end;

	public BusinessKey businessKeyOld;

	public BusinessKey businessKeyNew;

	public byte[] others;


	/** Default constructor */
	public PutEvent() {
		init();
	}

	private static final byte[] _zeroBytes = new byte[0];

	/** Colfer zero values. */
	private void init() {
		others = _zeroBytes;
	}

	/**
	 * {@link #reset(InputStream) Reusable} deserialization of Colfer streams.
	 */
	public static class Unmarshaller {

		/** The data source. */
		protected InputStream in;

		/** The read buffer. */
		public byte[] buf;

		/** The {@link #buf buffer}'s data start index, inclusive. */
		protected int offset;

		/** The {@link #buf buffer}'s data end index, exclusive. */
		protected int i;


		/**
		 * @param in the data source or {@code null}.
		 * @param buf the initial buffer or {@code null}.
		 */
		public Unmarshaller(InputStream in, byte[] buf) {
			// TODO: better size estimation
			if (buf == null || buf.length == 0)
				buf = new byte[Math.min(PutEvent.colferSizeMax, 2048)];
			this.buf = buf;
			reset(in);
		}

		/**
		 * Reuses the marshaller.
		 * @param in the data source or {@code null}.
		 * @throws IllegalStateException on pending data.
		 */
		public void reset(InputStream in) {
			if (this.i != this.offset) throw new IllegalStateException("colfer: pending data");
			this.in = in;
			this.offset = 0;
			this.i = 0;
		}

		/**
		 * Deserializes the following object.
		 * @return the result or {@code null} when EOF.
		 * @throws IOException from the input stream.
		 * @throws SecurityException on an upper limit breach defined by {@link #colferSizeMax}.
		 * @throws InputMismatchException when the data does not match this object's schema.
		 */
		public PutEvent next() throws IOException {
			if (in == null) return null;

			while (true) {
				if (this.i > this.offset) {
					try {
						PutEvent o = new PutEvent();
						this.offset = o.unmarshal(this.buf, this.offset, this.i);
						return o;
					} catch (BufferUnderflowException e) {
					}
				}
				// not enough data

				if (this.i <= this.offset) {
					this.offset = 0;
					this.i = 0;
				} else if (i == buf.length) {
					byte[] src = this.buf;
					// TODO: better size estimation
					if (offset == 0) this.buf = new byte[Math.min(PutEvent.colferSizeMax, this.buf.length * 4)];
					System.arraycopy(src, this.offset, this.buf, 0, this.i - this.offset);
					this.i -= this.offset;
					this.offset = 0;
				}
				assert this.i < this.buf.length;

				int n = in.read(buf, i, buf.length - i);
				if (n < 0) {
					if (this.i > this.offset)
						throw new InputMismatchException("colfer: pending data with EOF");
					return null;
				}
				assert n > 0;
				i += n;
			}
		}

	}


	/**
	 * Serializes the object.
	 * @param out the data destination.
	 * @param buf the initial buffer or {@code null}.
	 * @return the final buffer. When the serial fits into {@code buf} then the return is {@code buf}.
	 *  Otherwise the return is a new buffer, large enough to hold the whole serial.
	 * @throws IOException from {@code out}.
	 * @throws IllegalStateException on an upper limit breach defined by {@link #colferSizeMax}.
	 */
	public byte[] marshal(OutputStream out, byte[] buf) throws IOException {
		// TODO: better size estimation
		if (buf == null || buf.length == 0)
			buf = new byte[Math.min(PutEvent.colferSizeMax, 2048)];

		while (true) {
			int i;
			try {
				i = marshal(buf, 0);
			} catch (BufferOverflowException e) {
				buf = new byte[Math.min(PutEvent.colferSizeMax, buf.length * 4)];
				continue;
			}

			out.write(buf, 0, i);
			return buf;
		}
	}

	/**
	 * Serializes the object.
	 * @param buf the data destination.
	 * @param offset the initial index for {@code buf}, inclusive.
	 * @return the final index for {@code buf}, exclusive.
	 * @throws BufferOverflowException when {@code buf} is too small.
	 * @throws IllegalStateException on an upper limit breach defined by {@link #colferSizeMax}.
	 */
	public int marshal(byte[] buf, int offset) {
		int i = offset;

		try {
			if (this.primaryKey != null) {
				buf[i++] = (byte) 0;
				i = this.primaryKey.marshal(buf, i);
			}

			if (this.start != null) {
				buf[i++] = (byte) 1;
				i = this.start.marshal(buf, i);
			}

			if (this.end != null) {
				buf[i++] = (byte) 2;
				i = this.end.marshal(buf, i);
			}

			if (this.businessKeyOld != null) {
				buf[i++] = (byte) 3;
				i = this.businessKeyOld.marshal(buf, i);
			}

			if (this.businessKeyNew != null) {
				buf[i++] = (byte) 4;
				i = this.businessKeyNew.marshal(buf, i);
			}

			if (this.others.length != 0) {
				buf[i++] = (byte) 5;

				int size = this.others.length;
				if (size > PutEvent.colferSizeMax)
					throw new IllegalStateException(format("colfer: com/atomosphere/kvs/model.PutEvent.others size %d exceeds %d bytes", size, PutEvent.colferSizeMax));

				int x = size;
				while (x > 0x7f) {
					buf[i++] = (byte) (x | 0x80);
					x >>>= 7;
				}
				buf[i++] = (byte) x;

				int start = i;
				i += size;
				System.arraycopy(this.others, 0, buf, start, size);
			}

			buf[i++] = (byte) 0x7f;
			return i;
		} catch (ArrayIndexOutOfBoundsException e) {
			if (i - offset > PutEvent.colferSizeMax)
				throw new IllegalStateException(format("colfer: com/atomosphere/kvs/model.PutEvent exceeds %d bytes", PutEvent.colferSizeMax));
			if (i > buf.length) throw new BufferOverflowException();
			throw e;
		}
	}

	/**
	 * Deserializes the object.
	 * @param buf the data source.
	 * @param offset the initial index for {@code buf}, inclusive.
	 * @return the final index for {@code buf}, exclusive.
	 * @throws BufferUnderflowException when {@code buf} is incomplete. (EOF)
	 * @throws SecurityException on an upper limit breach defined by {@link #colferSizeMax}.
	 * @throws InputMismatchException when the data does not match this object's schema.
	 */
	public int unmarshal(byte[] buf, int offset) {
		return unmarshal(buf, offset, buf.length);
	}

	/**
	 * Deserializes the object.
	 * @param buf the data source.
	 * @param offset the initial index for {@code buf}, inclusive.
	 * @param end the index limit for {@code buf}, exclusive.
	 * @return the final index for {@code buf}, exclusive.
	 * @throws BufferUnderflowException when {@code buf} is incomplete. (EOF)
	 * @throws SecurityException on an upper limit breach defined by {@link #colferSizeMax}.
	 * @throws InputMismatchException when the data does not match this object's schema.
	 */
	public int unmarshal(byte[] buf, int offset, int end) {
		if (end > buf.length) end = buf.length;
		int i = offset;

		try {
			byte header = buf[i++];

			if (header == (byte) 0) {
				this.primaryKey = new PrimaryKey();
				i = this.primaryKey.unmarshal(buf, i, end);
				header = buf[i++];
			}

			if (header == (byte) 1) {
				this.start = new TimestampData();
				i = this.start.unmarshal(buf, i, end);
				header = buf[i++];
			}

			if (header == (byte) 2) {
				this.end = new TimestampData();
				i = this.end.unmarshal(buf, i, end);
				header = buf[i++];
			}

			if (header == (byte) 3) {
				this.businessKeyOld = new BusinessKey();
				i = this.businessKeyOld.unmarshal(buf, i, end);
				header = buf[i++];
			}

			if (header == (byte) 4) {
				this.businessKeyNew = new BusinessKey();
				i = this.businessKeyNew.unmarshal(buf, i, end);
				header = buf[i++];
			}

			if (header == (byte) 5) {
				int size = 0;
				for (int shift = 0; true; shift += 7) {
					byte b = buf[i++];
					size |= (b & 0x7f) << shift;
					if (shift == 28 || b >= 0) break;
				}
				if (size < 0 || size > PutEvent.colferSizeMax)
					throw new SecurityException(format("colfer: com/atomosphere/kvs/model.PutEvent.others size %d exceeds %d bytes", size, PutEvent.colferSizeMax));

				this.others = new byte[size];
				int start = i;
				i += size;
				System.arraycopy(buf, start, this.others, 0, size);

				header = buf[i++];
			}

			if (header != (byte) 0x7f)
				throw new InputMismatchException(format("colfer: unknown header at byte %d", i - 1));
		} finally {
			if (i > end && end - offset < PutEvent.colferSizeMax) throw new BufferUnderflowException();
			if (i < 0 || i - offset > PutEvent.colferSizeMax)
				throw new SecurityException(format("colfer: com/atomosphere/kvs/model.PutEvent exceeds %d bytes", PutEvent.colferSizeMax));
			if (i > end) throw new BufferUnderflowException();
		}

		return i;
	}

	// {@link Serializable} version number.
	private static final long serialVersionUID = 6L;

	// {@link Serializable} Colfer extension.
	private void writeObject(ObjectOutputStream out) throws IOException {
		// TODO: better size estimation
		byte[] buf = new byte[1024];
		int n;
		while (true) try {
			n = marshal(buf, 0);
			break;
		} catch (BufferUnderflowException e) {
			buf = new byte[4 * buf.length];
		}

		out.writeInt(n);
		out.write(buf, 0, n);
	}

	// {@link Serializable} Colfer extension.
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		init();

		int n = in.readInt();
		byte[] buf = new byte[n];
		in.readFully(buf);
		unmarshal(buf, 0);
	}

	// {@link Serializable} Colfer extension.
	private void readObjectNoData() throws ObjectStreamException {
		init();
	}

	/**
	 * Gets com/atomosphere/kvs/model.PutEvent.primaryKey.
	 * @return the value.
	 */
	public PrimaryKey getPrimaryKey() {
		return this.primaryKey;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.primaryKey.
	 * @param value the replacement.
	 */
	public void setPrimaryKey(PrimaryKey value) {
		this.primaryKey = value;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.primaryKey.
	 * @param value the replacement.
	 * @return {link this}.
	 */
	public PutEvent withPrimaryKey(PrimaryKey value) {
		this.primaryKey = value;
		return this;
	}

	/**
	 * Gets com/atomosphere/kvs/model.PutEvent.start.
	 * @return the value.
	 */
	public TimestampData getStart() {
		return this.start;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.start.
	 * @param value the replacement.
	 */
	public void setStart(TimestampData value) {
		this.start = value;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.start.
	 * @param value the replacement.
	 * @return {link this}.
	 */
	public PutEvent withStart(TimestampData value) {
		this.start = value;
		return this;
	}

	/**
	 * Gets com/atomosphere/kvs/model.PutEvent.end.
	 * @return the value.
	 */
	public TimestampData getEnd() {
		return this.end;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.end.
	 * @param value the replacement.
	 */
	public void setEnd(TimestampData value) {
		this.end = value;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.end.
	 * @param value the replacement.
	 * @return {link this}.
	 */
	public PutEvent withEnd(TimestampData value) {
		this.end = value;
		return this;
	}

	/**
	 * Gets com/atomosphere/kvs/model.PutEvent.businessKeyOld.
	 * @return the value.
	 */
	public BusinessKey getBusinessKeyOld() {
		return this.businessKeyOld;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.businessKeyOld.
	 * @param value the replacement.
	 */
	public void setBusinessKeyOld(BusinessKey value) {
		this.businessKeyOld = value;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.businessKeyOld.
	 * @param value the replacement.
	 * @return {link this}.
	 */
	public PutEvent withBusinessKeyOld(BusinessKey value) {
		this.businessKeyOld = value;
		return this;
	}

	/**
	 * Gets com/atomosphere/kvs/model.PutEvent.businessKeyNew.
	 * @return the value.
	 */
	public BusinessKey getBusinessKeyNew() {
		return this.businessKeyNew;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.businessKeyNew.
	 * @param value the replacement.
	 */
	public void setBusinessKeyNew(BusinessKey value) {
		this.businessKeyNew = value;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.businessKeyNew.
	 * @param value the replacement.
	 * @return {link this}.
	 */
	public PutEvent withBusinessKeyNew(BusinessKey value) {
		this.businessKeyNew = value;
		return this;
	}

	/**
	 * Gets com/atomosphere/kvs/model.PutEvent.others.
	 * @return the value.
	 */
	public byte[] getOthers() {
		return this.others;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.others.
	 * @param value the replacement.
	 */
	public void setOthers(byte[] value) {
		this.others = value;
	}

	/**
	 * Sets com/atomosphere/kvs/model.PutEvent.others.
	 * @param value the replacement.
	 * @return {link this}.
	 */
	public PutEvent withOthers(byte[] value) {
		this.others = value;
		return this;
	}

	@Override
	public final int hashCode() {
		int h = 1;
		if (this.primaryKey != null) h = 31 * h + this.primaryKey.hashCode();
		if (this.start != null) h = 31 * h + this.start.hashCode();
		if (this.end != null) h = 31 * h + this.end.hashCode();
		if (this.businessKeyOld != null) h = 31 * h + this.businessKeyOld.hashCode();
		if (this.businessKeyNew != null) h = 31 * h + this.businessKeyNew.hashCode();
		for (byte b : this.others) h = 31 * h + b;
		return h;
	}

	@Override
	public final boolean equals(Object o) {
		return o instanceof PutEvent && equals((PutEvent) o);
	}

	public final boolean equals(PutEvent o) {
		if (o == null) return false;
		if (o == this) return true;
		return o.getClass() == PutEvent.class
			&& (this.primaryKey == null ? o.primaryKey == null : this.primaryKey.equals(o.primaryKey))
			&& (this.start == null ? o.start == null : this.start.equals(o.start))
			&& (this.end == null ? o.end == null : this.end.equals(o.end))
			&& (this.businessKeyOld == null ? o.businessKeyOld == null : this.businessKeyOld.equals(o.businessKeyOld))
			&& (this.businessKeyNew == null ? o.businessKeyNew == null : this.businessKeyNew.equals(o.businessKeyNew))
			&& java.util.Arrays.equals(this.others, o.others);
	}

}
