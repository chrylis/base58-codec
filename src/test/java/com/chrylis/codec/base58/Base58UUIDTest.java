package com.chrylis.codec.base58;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.util.UUID;

import org.junit.Test;

public class Base58UUIDTest {

	Base58UUID bu = new Base58UUID();

	@Test
	public void test() {
		assertEquals("1", bu.encode(new UUID(0, 0)));

		UUID negative = new UUID(-1, 0);
		assertEquals("xBuEXKpA6iqg2dTbApBGRw", bu.encode(negative));
		assertEquals(negative, bu.decode(bu.encode(negative)));

		UUID positive = UUID.nameUUIDFromBytes("test name".getBytes(Base58Codec.CHARSET_ASCII));
		assertEquals(positive, bu.decode(bu.encode(positive)));
	}

	@Test
	public void testEncodeName() {
		String NAME = "urn:abcd-efgh";
		
		UUID uuid = UUID.nameUUIDFromBytes(NAME.getBytes(Charset.forName("UTF-8")));
		String encoded = bu.encode(uuid);
		assertEquals("rLVV8s4pKg43DGDJLG1sEd", encoded);
		
		assertEquals(encoded, bu.encodeUuidFromName(NAME));
	}
}
