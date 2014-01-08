package com.chrylis.codec.base58;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;

public class Base58Codec {

	public static final Charset CHARSET_ASCII = Charset.forName("US-ASCII");

	// A whole block in Base58 consists of 7424 bits, the least common multiple
	// of 58 and 256. This is equivalent to 29 bytes or 128 Base58 digits.

	public static final BigInteger BASE = BigInteger.valueOf(58);

	/**
	 * The number of whole unencoded bytes in a block of 128 Base58 digits.
	 */
	public static final int BLOCK_LENGTH_BYTES = 29;

	/**
	 * The number of whole encoded Base58 digits resulting from a block of 29 bytes.
	 */
	public static final int BLOCK_LENGTH_DIGITS = 128;

	/**
	 * Unfortunately, the good folks at Flickr didn't think to make the alphabet ASCIIbetical, so we can't binary-search the
	 * {@code byte[]}.
	 */
	public static final char ALPHABET[] = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();

	public String encode(byte[] source) {
		if (source.length == 0)
			return "";

		BigInteger dividend = new BigInteger(source);
		if (dividend.equals(BigInteger.ZERO))
			return "1";

		BigInteger qr[]; // quotient and remainder

		StringBuilder sb = new StringBuilder();

		while (dividend.compareTo(BigInteger.ZERO) > 0) {
			qr = dividend.divideAndRemainder(BASE);
			int base58DigitValue = qr[1].intValue();

			// this tacks each successive digit on at the end, so it's LSD first
			sb.append(ALPHABET[base58DigitValue]);

			dividend = qr[0];
		}

		// so we reverse the string before returning it
		return sb.reverse().toString();
	}

	public String encode(final long value) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(value);
		bb.flip();
		return encode(bb.array());
	}

	public byte[] decode(final String source) {
		BigInteger value = BigInteger.ZERO;

		Iterator<Character> it = stringIterator(source);
		while (it.hasNext()) {
			value = value.add(BigInteger.valueOf(ArrayUtils.indexOf(ALPHABET, it.next())));
			if (it.hasNext())
				value = value.multiply(BASE);
		}

		return value.toByteArray();
	}

	public byte[] decode(final String source, final int numBytes) {
		return padToSize(decode(source), numBytes);
	}

	/**
	 * http://stackoverflow.com/questions/3925130/java-how-to-get-iteratorcharacter-from-string
	 * 
	 * @param string
	 * @return
	 */
	public static Iterator<Character> stringIterator(final String string) {
		// Ensure the error is found as soon as possible.
		if (string == null)
			throw new NullPointerException();

		return new Iterator<Character>() {
			private int index = 0;

			public boolean hasNext() {
				return index < string.length();
			}

			public Character next() {
				/*
				 * Throw NoSuchElementException as defined by the Iterator contract, not IndexOutOfBoundsException.
				 */
				if (!hasNext())
					throw new NoSuchElementException();
				return string.charAt(index++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static byte[] padToSize(final byte[] array, final int size) {
		if (size < array.length)
			throw new IllegalArgumentException("requested size " + size + " is shorter than existing length " + array.length);

		byte target[] = new byte[size];
		System.arraycopy(array, 0, target, size - array.length, array.length);
		return target;
	}
}
