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
package tec.units.ri.internal.format.l10n;

import tec.uom.lib.common.function.ValueSupplier;

/*
 * Portions  Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

/**
 * An Annotation object is used as a wrapper for a text attribute value if the
 * attribute has annotation characteristics. These characteristics are:
 * <ul>
 * <li>The text range that the attribute is applied to is critical to the
 * semantics of the range. That means, the attribute cannot be applied to
 * subranges of the text range that it applies to, and, if two adjacent text
 * ranges have the same value for this attribute, the attribute still cannot be
 * applied to the combined range as a whole with this value.
 * <li>The attribute or its value usually do no longer apply if the underlying
 * text is changed.
 * </ul>
 *
 * An example is grammatical information attached to a sentence: For the
 * previous sentence, you can say that "an example" is the subject, but you
 * cannot say the same about "an", "example", or "exam". When the text is
 * changed, the grammatical information typically becomes invalid. Another
 * example is Japanese reading information (yomi).
 *
 * <p>
 * Wrapping the attribute value into an Annotation object guarantees that
 * adjacent text runs don't get merged even if the attribute values are equal,
 * and indicates to text containers that the attribute should be discarded if
 * the underlying text is modified.
 *
 * @see AttributedCharacterIterator
 * @see ValueSupplier
 */

class Annotation implements ValueSupplier<Object> {

	/**
	 * Constructs an annotation record with the given value, which may be null.
	 * 
	 * @param value
	 *            The value of the attribute
	 */
	Annotation(Object value) {
		this.value = value;
	}

	/**
	 * Returns the value of the attribute, which may be null.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the String representation of this Annotation.
	 */
	public String toString() {
		return getClass().getName() + "[value=" + value + "]";
	}

	private final Object value;
};
