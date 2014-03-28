package com.chrylis.codec.base58;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;

/**
 * Convenience wrapper for working with UUIDs in Base58. Since the Base58 operations are fairly expensive computationally, this
 * class is annotated with Spring's {@link Cacheable} using the cache named {@literal base58uuid}. This annotation is contained in
 * the {@literal spring-context} artifact, and in its absence at runtime, the classloader will simply ignore it.
 * 
 * @author Christopher Smith
 * 
 */
@Cacheable("base58uuid")
public class Base58UUID {
	public String encode(UUID uuid) {
		// 50-50 chance that the UUID's high {@code long} value will be negative, so just preemptively
		// pad the byte buffer we'll be encoding from
		ByteBuffer bb = ByteBuffer.allocate(17);
		bb.put((byte) 0);
		bb.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).flip();
		return Base58Codec.doEncode(bb.array());
	}

	public UUID decode(String base58) {
		ByteBuffer bb = ByteBuffer.wrap(Base58Codec.doDecode(base58, 16));
		return new UUID(bb.getLong(), bb.getLong());
	}

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Convenience wrapper for converting a {@code String} to a name-based UUID and returning the Base58-encoded value. The
	 * {@code String}'s characters are converted to bytes according to UTF-8.
	 * 
	 * @param name the name from which to construct a UUID
	 * @return the name converted to a UUID and Base58-encoded
	 */
	public String encodeUuidFromName(String name) {
		return encode(UUID.nameUUIDFromBytes(name.getBytes(UTF_8)));
	}
}
