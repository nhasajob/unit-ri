/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2016, Jean-Marie Dautelle, Werner Keil, V2COM.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-363 nor the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tec.units.ri.format;

import static org.junit.Assert.*;
import static tec.units.ri.unit.MetricPrefix.*;
import static tec.units.ri.unit.Units.GRAM;
import static tec.units.ri.unit.Units.HERTZ;
import static tec.units.ri.unit.Units.KILOGRAM;

import javax.measure.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Speed;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import tec.units.ri.format.SimpleUnitFormat;
import tec.units.ri.unit.Units;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 *
 */
public class BaseFormatTest {

	private SimpleUnitFormat fmt;

	@Before
	public void init() {
		fmt = SimpleUnitFormat.getInstance();
	}
	
	@Test
	@Ignore
	public void testFormat2() {
		Unit<Speed> kph = Units.KILOMETRES_PER_HOUR;
		String s = fmt.format(kph);
		assertEquals("kph", s);
	}
	
	@Test
	public void testKilo() {
		Unit<Mass> m = KILOGRAM;
		String s = fmt.format(m);
		assertEquals("kg", s);
	}
	
	@Test
	public void testKilo2() {
		Unit<Mass> m = KILO(GRAM);
		String s = fmt.format(m);
		assertEquals("kg", s);
	}
	
	@Test
	public void testMilli() {
		Unit<Mass> m = MILLI(GRAM);
		String s = fmt.format(m);
		assertEquals("mg", s);
	}
	
	@Test
	public void testNano() {
		Unit<Mass> m = NANO(GRAM);
		String s = fmt.format(m);
		assertEquals("ng", s);
	}
	
	@Test
	public void testFormatHz2() {
		Unit<Frequency> hz = MEGA(HERTZ);
		String s = fmt.format(hz);
		assertEquals("MHz", s);
	}
	
	@Test
	public void testFormatHz3() {
		Unit<Frequency> hz = KILO(HERTZ);
		String s = fmt.format(hz);
		assertEquals("kHz", s);
	}
}
