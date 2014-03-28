package com.chrylis.codec.base58;

import java.util.UUID;

import com.google.caliper.Benchmark;

public class Base58Benchmark extends Benchmark {

	public static final UUID UUID1 = UUID.fromString("ba24b2b0-ae2f-11e3-a5e2-0800200c9a66");

	private Base58UUID b58 = new Base58UUID();
	private int c;

	public void timeEncode(int reps) {
		for (int i = 0; i < reps; i++) {
			b58.encode(UUID1);
			c++;
		}
		System.out.println(c);
	}

	public static final int ONE_MILLION = 1000000;

	public static void main(String[] args) {
		Base58Benchmark b = new Base58Benchmark();

		b.timeEncode(ONE_MILLION);

		long start = System.nanoTime();
		for (int i = 0; i < 100; i++)
			b.timeEncode(ONE_MILLION);
		long end = System.nanoTime();
		long ms = (end - start) / 1000000;
		
		System.out.printf("start: %d, end: %d, elapsed: %dms, time for one million ops: %dms", start, end, ms, ms/100);
	}
}
