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

import static tec.units.ri.unit.MetricPrefix.*;
import static tec.units.ri.unit.Units.LITRE;

import java.io.IOException;
import java.lang.CharSequence;
import java.util.HashMap;
import java.util.Map;

import tec.units.ri.AbstractUnit;
import tec.units.ri.function.AddConverter;
import tec.units.ri.function.MultiplyConverter;
import tec.units.ri.function.RationalConverter;
import tec.units.ri.internal.format.l10n.FieldPosition;
import tec.units.ri.internal.format.l10n.ParsePosition;
import tec.units.ri.unit.AlternateUnit;
import tec.units.ri.unit.BaseUnit;
import tec.units.ri.unit.ProductUnit;
import tec.units.ri.unit.TransformedUnit;
import tec.units.ri.unit.Units;
import tec.units.ri.unit.MetricPrefix;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.Quantity;
import javax.measure.format.ParserException;
import javax.measure.format.UnitFormat;

/**
 * <p>
 * This class implements the {@link UnitFormat} interface for formatting and parsing {@link Unit
 * units}.
 * </p>
 * 
 * <p>
 * For SI units, the 20 SI prefixes used to form decimal multiples
 * and sub-multiples of SI units are recognized. {@link Units} are
 * directly recognized. For example:<br>
 * <code>
 *        AbstractUnit.parse("m°C").equals(MetricPrefix.MILLI(Units.CELSIUS))
 *        AbstractUnit.parse("kW").equals(MetricPrefix.KILO(Units.WATT))
 *        AbstractUnit.parse("ft").equals(Units.METRE.multiply(0.3048))</code>
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Eric Russell
 * @version 0.7, Dec 30, 2015
 */
public abstract class SimpleUnitFormat extends AbstractUnitFormat {
	/**
	 * 
	 */
//	private static final long serialVersionUID = 4149424034841739785L;

	/**
	 * Flavor of this format
	 * 
	 * @author Werner
	 *
	 */
	public static enum Flavor {
		Default, ASCII
	}

	/**
	 * Holds the standard unit format.
	 */
	private static final DefaultFormat DEFAULT = new DefaultFormat();

	/**
	 * Holds the ASCIIFormat unit format.
	 */
	private static final ASCIIFormat ASCII = new ASCIIFormat();

	/**
	 * Returns the unit format for the default locale (format used by
	 * {@link AbstractUnit#parse(CharSequence) AbstractUnit.parse(CharSequence)} and
	 * {@link Unit#toString() Unit.toString()}).
	 * 
	 * @return the default unit format (locale sensitive).
	 */
	public static SimpleUnitFormat getInstance() {
		return getInstance(Flavor.Default);
	}

	/**
	 * Returns the {@link SimpleUnitFormat} in the desired {@link Flavor}
	 * 
	 * @return the instance for the given {@link Flavor}.
	 */
	public static SimpleUnitFormat getInstance(Flavor flavor) {
		switch (flavor) {
		case ASCII:
			return SimpleUnitFormat.ASCII;
		default:
			return DEFAULT;
		}
	}

	/**
	 * Base constructor.
	 */
	protected SimpleUnitFormat() {
	}

	/**
	 * Formats the specified unit.
	 *
	 * @param unit
	 *            the unit to format.
	 * @param appendable
	 *            the appendable destination.
	 * @throws IOException
	 *             if an error occurs.
	 */
	public abstract Appendable format(Unit<?> unit, Appendable appendable)
			throws IOException;

	/**
	 * Parses a sequence of character to produce a unit or a rational product of
	 * unit.
	 *
	 * @param csq
	 *            the <code>CharSequence</code> to parse.
	 * @param pos
	 *            an object holding the parsing index and error position.
	 * @return an {@link Unit} parsed from the character sequence.
	 * @throws IllegalArgumentException
	 *             if the character sequence contains an illegal syntax.
	 */
	public abstract Unit<? extends Quantity> parseProductUnit(CharSequence csq,
			ParsePosition pos) throws ParserException;

	/**
	 * Parses a sequence of character to produce a single unit.
	 *
	 * @param csq
	 *            the <code>CharSequence</code> to parse.
	 * @param pos
	 *            an object holding the parsing index and error position.
	 * @return an {@link Unit} parsed from the character sequence.
	 * @throws IllegalArgumentException
	 *             if the character sequence does not contain a valid unit
	 *             identifier.
	 */
	public abstract Unit<? extends Quantity> parseSingleUnit(CharSequence csq,
			ParsePosition pos) throws ParserException;

	/**
	 * Attaches a system-wide label to the specified unit. For example:<br><code>
	 * SimpleUnitFormat.getInstance().label(DAY.multiply(365), "year");
	 * SimpleUnitFormat.getInstance().label(METRE.multiply(0.3048), "ft"); </code><br>If
	 * the specified label is already associated to an unit the previous
	 * association is discarded or ignored.
	 * 
	 * @param unit
	 *            the unit being labeled.
	 * @param label
	 *            the new label for this unit.
	 * @throws IllegalArgumentException
	 *             if the label is not a
	 *             {@link SimpleUnitFormat#isValidIdentifier(String)} valid
	 *             identifier.
	 */
	public abstract void label(Unit<?> unit, String label);

	/**
	 * Attaches a system-wide alias to this unit. Multiple aliases may be
	 * attached to the same unit. Aliases are used during parsing to recognize
	 * different variants of the same unit. For example: [code]
	 * SimpleUnitformat.getInstance().alias(METER.multiply(0.3048), "foot");
	 * SimpleUnitFormat.getInstance().alias(METER.multiply(0.3048), "feet");
	 * SimpleUnitFormat.getInstance().alias(METER, "meter");
	 * SimpleUnitFormat.getInstance().alias(METER, "metre"); [/code] If the
	 * specified label is already associated to an unit the previous association
	 * is discarded or ignored.
	 *
	 * @param unit
	 *            the unit being aliased.
	 * @param alias
	 *            the alias attached to this unit.
	 * @throws IllegalArgumentException
	 *             if the label is not a
	 *             {@link SimpleUnitFormat#isValidIdentifier(String)} valid
	 *             identifier.
	 */
	public abstract void alias(Unit<?> unit, String alias);

	/**
	 * Indicates if the specified name can be used as unit identifier.
	 *
	 * @param name
	 *            the identifier to be tested.
	 * @return <code>true</code> if the name specified can be used as label or
	 *         alias for this format;<code>false</code> otherwise.
	 */
	public abstract boolean isValidIdentifier(String name);

	/**
	 * Formats an unit and appends the resulting text to a given string buffer.
	 *
	 * @param unit
	 *            the unit to format.
	 * @param toAppendTo
	 *            where the text is to be appended
	 * @param pos
	 *            the field position (not used).
	 * @return <code>toAppendTo</code>
	 */
	public final StringBuffer format(Object unit,
			final StringBuffer toAppendTo, FieldPosition pos) {
		try {
			Object dest = toAppendTo;
			if (dest instanceof Appendable) {
				format((Unit<?>) unit, (Appendable) dest);
			} else { // When retroweaver is used to produce 1.4 binaries.
				format((Unit<?>) unit, new Appendable() {

					public Appendable append(char arg0) throws IOException {
						toAppendTo.append(arg0);
						return null;
					}

					public Appendable append(CharSequence arg0)
							throws IOException {
						toAppendTo.append(arg0);
						return null;
					}

					public Appendable append(CharSequence arg0, int arg1,
							int arg2) throws IOException {
						toAppendTo.append(arg0.subSequence(arg1, arg2));
						return null;
					}
				});
			}
			return toAppendTo;
		} catch (IOException e) {
			throw new Error(e); // Should never happen.
		}
	}

	/**
	 * Parses the text from a string to produce an object.
	 * 
	 * @param source
	 *            the string source, part of which should be parsed.
	 * @param pos
	 *            the cursor position.
	 * @return the corresponding unit or <code>null</code> if the string cannot
	 *         be parsed.
	 */
	public final Unit<?> parseObject(String source, ParsePosition pos) throws ParserException {
		//int start = pos.getIndex();
		return parseProductUnit(source, pos);
	}

	/**
	 * This class represents an exponent with both a power (numerator) and a
	 * root (denominator).
	 */
	private static class Exponent {
		public final int pow;
		public final int root;

		public Exponent(int pow, int root) {
			this.pow = pow;
			this.root = root;
		}
	}

	/**
	 * This class represents the standard format.
	 */
	protected static class DefaultFormat extends SimpleUnitFormat {

		/**
		 * Holds the name to unit mapping.
		 */
		final HashMap<String, Unit<?>> _nameToUnit = new HashMap<String, Unit<?>>();

		/**
		 * Holds the unit to name mapping.
		 */
		final HashMap<Unit<?>, String> _unitToName = new HashMap<Unit<?>, String>();

		@Override
		public void label(Unit<?> unit, String label) {
			if (!isValidIdentifier(label))
				throw new IllegalArgumentException("Label: " + label
						+ " is not a valid identifier.");
			synchronized (this) {
				_nameToUnit.put(label, unit);
				_unitToName.put(unit, label);
			}
		}

		@Override
		public void alias(Unit<?> unit, String alias) {
			if (!isValidIdentifier(alias))
				throw new IllegalArgumentException("Alias: " + alias
						+ " is not a valid identifier.");
			synchronized (this) {
				_nameToUnit.put(alias, unit);
			}
		}

		@Override
		public boolean isValidIdentifier(String name) {
			if ((name == null) || (name.length() == 0))
				return false;
			/*for (int i = 0; i < name.length(); i++) {
				if (!isUnitIdentifierPart(name.charAt(i)))
					return false;
			} */
			if (!isUnitIdentifierPart(name.charAt(0))) // label must not begin with a digit or mathematical operator
					return false;
			return true;
		}
		
		static boolean isLetter(char c) {
		    return (c > 64 && c < 91) || (c > 96 && c < 123);
		}

		//Not necessary but included anyways
		static boolean isUpperCase(char c) {
		    return c > 64 && c < 91;
		}

		static boolean isSpace(char c) {
		    //Accounts for spaces and other "space-like" characters
		    return c == 32 || c == 12 || c == 13 || c == 14;
		}

		static boolean isUnitIdentifierPart(char ch) {
			return isLetter(ch)
					|| (!Character.isWhitespace(ch) && !Character.isDigit(ch)
							&& (ch != '·') && (ch != '*') && (ch != '/')
							&& (ch != '(') && (ch != ')') && (ch != '[')
							&& (ch != ']') && (ch != '¹') && (ch != '²')
							&& (ch != '³') && (ch != '^') && (ch != '+') && (ch != '-'));
		}

		// Returns the name for the specified unit or null if product unit.
		public String nameFor(Unit<?> unit) {
			// Searches label database.
			String label = _unitToName.get(unit);
			if (label != null)
				return label;
			if (unit instanceof BaseUnit)
				return ((BaseUnit<?>) unit).getSymbol();
			if (unit instanceof AlternateUnit)
				return ((AlternateUnit<?>) unit).getSymbol();
			if (unit instanceof TransformedUnit) {
				TransformedUnit<?> tfmUnit = (TransformedUnit<?>) unit;
				Unit<?> baseUnits = tfmUnit.toSystemUnit();
				UnitConverter cvtr = tfmUnit.getSystemConverter();
				StringBuffer result = new StringBuffer();
				String baseUnitName = baseUnits.toString();
				if ((baseUnitName.indexOf('·') >= 0)
						|| (baseUnitName.indexOf('*') >= 0)
						|| (baseUnitName.indexOf('/') >= 0)) {
					// We could use parentheses whenever baseUnits is an
					// instanceof ProductUnit, but most ProductUnits have
					// aliases,
					// so we'd end up with a lot of unnecessary parentheses.
					result.append('(');
					result.append(baseUnitName);
					result.append(')');
				} else {
					result.append(baseUnitName);
				}
				if (cvtr instanceof AddConverter) {
					result.append('+');
					result.append(((AddConverter) cvtr).getOffset());
				} else if (cvtr instanceof RationalConverter) {
					double dividend = ((RationalConverter) cvtr).getDividend();
					if (dividend != 1) {
						result.append('*');
						result.append(dividend);
					}
					double divisor = ((RationalConverter) cvtr).getDivisor();
					if (divisor != 1) {
						result.append('/');
						result.append(divisor);
					}
					;
				} else if (cvtr instanceof MultiplyConverter) {
					result.append('*');
					result.append(((MultiplyConverter) cvtr).getFactor());
				} else { // Other converters.
					return "[" + baseUnits + "?]";
				}
				return result.toString();
			}
			// Compound unit.
			// if (unit instanceof CompoundUnit) {
			// CompoundUnit<?> cpdUnit = (CompoundUnit<?>) unit;
			// return nameFor(cpdUnit.getHigher()).toString() + ":"
			// + nameFor(cpdUnit.getLower());
			// }
			return null; // Product unit.
		}

		// Returns the unit for the specified name.
		public Unit<?> unitFor(String name) {
			Unit<?> unit = _nameToUnit.get(name);
			if (unit != null)
				return unit;
			unit = SYMBOL_TO_UNIT.get(name);
			return unit;
		}

		// //////////////////////////
		// Parsing.

		@SuppressWarnings("unchecked")
		public Unit<? extends Quantity> parseSingleUnit(CharSequence csq,
				ParsePosition pos) throws ParserException {
			int startIndex = pos.getIndex();
			String name = readIdentifier(csq, pos);
			Unit unit = unitFor(name);
			check(unit != null, name + " not recognized", csq, startIndex);
			return unit;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Unit<? extends Quantity> parseProductUnit(CharSequence csq,
				ParsePosition pos) throws ParserException {
			Unit result = AbstractUnit.ONE;
			int token = nextToken(csq, pos);
			switch (token) {
			case IDENTIFIER:
				result = parseSingleUnit(csq, pos);
				break;
			case OPEN_PAREN:
				pos.setIndex(pos.getIndex() + 1);
				result = parseProductUnit(csq, pos);
				token = nextToken(csq, pos);
				check(token == CLOSE_PAREN, "')' expected", csq, pos.getIndex());
				pos.setIndex(pos.getIndex() + 1);
				break;
			}
			token = nextToken(csq, pos);
			while (true) {
				switch (token) {
				case EXPONENT:
					Exponent e = readExponent(csq, pos);
					if (e.pow != 1) {
						result = result.pow(e.pow);
					}
					if (e.root != 1) {
						result = result.root(e.root);
					}
					break;
				case MULTIPLY:
					pos.setIndex(pos.getIndex() + 1);
					token = nextToken(csq, pos);
					if (token == INTEGER) {
						long n = readLong(csq, pos);
						if (n != 1) {
							result = result.multiply(n);
						}
					} else if (token == FLOAT) {
						double d = readDouble(csq, pos);
						if (d != 1.0) {
							result = result.multiply(d);
						}
					} else {
						result = result.multiply(parseProductUnit(csq, pos));
					}
					break;
				case DIVIDE:
					pos.setIndex(pos.getIndex() + 1);
					token = nextToken(csq, pos);
					if (token == INTEGER) {
						long n = readLong(csq, pos);
						if (n != 1) {
							result = result.divide(n);
						}
					} else if (token == FLOAT) {
						double d = readDouble(csq, pos);
						if (d != 1.0) {
							result = result.divide(d);
						}
					} else {
						result = result.divide(parseProductUnit(csq, pos));
					}
					break;
				case PLUS:
					pos.setIndex(pos.getIndex() + 1);
					token = nextToken(csq, pos);
					if (token == INTEGER) {
						long n = readLong(csq, pos);
						if (n != 1) {
							result = result.shift(n);
						}
					} else if (token == FLOAT) {
						double d = readDouble(csq, pos);
						if (d != 1.0) {
							result = result.shift(d);
						}
					} else {
						throw new ParserException("not a number", pos.getIndex());
					}
					break;
				case EOF:
				case CLOSE_PAREN:
					return result;
				default:
					throw new ParserException("unexpected token " + token,
							pos.getIndex());
				}
				token = nextToken(csq, pos);
			}
		}

		private static final int EOF = 0;
		private static final int IDENTIFIER = 1;
		private static final int OPEN_PAREN = 2;
		private static final int CLOSE_PAREN = 3;
		private static final int EXPONENT = 4;
		private static final int MULTIPLY = 5;
		private static final int DIVIDE = 6;
		private static final int PLUS = 7;
		private static final int INTEGER = 8;
		private static final int FLOAT = 9;

		private int nextToken(CharSequence csq, ParsePosition pos) {
			final int length = csq.length();
			while (pos.getIndex() < length) {
				char c = csq.charAt(pos.getIndex());
				if (isUnitIdentifierPart(c)) {
					return IDENTIFIER;
				} else if (c == '(') {
					return OPEN_PAREN;
				} else if (c == ')') {
					return CLOSE_PAREN;
				} else if ((c == '^') || (c == '¹') || (c == '²') || (c == '³')) {
					return EXPONENT;
				} else if (c == '*') {
					char c2 = csq.charAt(pos.getIndex() + 1);
					if (c2 == '*') {
						return EXPONENT;
					} else {
						return MULTIPLY;
					}
				} else if (c == '·') {
					return MULTIPLY;
				} else if (c == '/') {
					return DIVIDE;
				} else if (c == '+') {
					return PLUS;
				} else if ((c == '-') || Character.isDigit(c)) {
					int index = pos.getIndex() + 1;
					while ((index < length)
							&& (Character.isDigit(c) || (c == '-')
									|| (c == '.') || (c == 'E'))) {
						c = csq.charAt(index++);
						if (c == '.') {
							return FLOAT;
						}
					}
					return INTEGER;
				}
				pos.setIndex(pos.getIndex() + 1);
			}
			return EOF;
		}

		private void check(boolean expr, String message, CharSequence csq,
				int index) throws ParserException {
			if (!expr) {
				throw new ParserException(message + " (in " + csq + " at index "
						+ index + ")", index);
			}
		}

		private Exponent readExponent(CharSequence csq, ParsePosition pos) {
			char c = csq.charAt(pos.getIndex());
			if (c == '^') {
				pos.setIndex(pos.getIndex() + 1);
			} else if (c == '*') {
				pos.setIndex(pos.getIndex() + 2);
			}
			final int length = csq.length();
			int pow = 0;
			boolean isPowNegative = false;
			int root = 0;
			boolean isRootNegative = false;
			boolean isRoot = false;
			while (pos.getIndex() < length) {
				c = csq.charAt(pos.getIndex());
				if (c == '¹') {
					if (isRoot) {
						root = root * 10 + 1;
					} else {
						pow = pow * 10 + 1;
					}
				} else if (c == '²') {
					if (isRoot) {
						root = root * 10 + 2;
					} else {
						pow = pow * 10 + 2;
					}
				} else if (c == '³') {
					if (isRoot) {
						root = root * 10 + 3;
					} else {
						pow = pow * 10 + 3;
					}
				} else if (c == '-') {
					if (isRoot) {
						isRootNegative = true;
					} else {
						isPowNegative = true;
					}
				} else if ((c >= '0') && (c <= '9')) {
					if (isRoot) {
						root = root * 10 + (c - '0');
					} else {
						pow = pow * 10 + (c - '0');
					}
				} else if (c == ':') {
					isRoot = true;
				} else {
					break;
				}
				pos.setIndex(pos.getIndex() + 1);
			}
			if (pow == 0)
				pow = 1;
			if (root == 0)
				root = 1;
			return new Exponent(isPowNegative ? -pow : pow,
					isRootNegative ? -root : root);
		}

		private long readLong(CharSequence csq, ParsePosition pos) {
			final int length = csq.length();
			int result = 0;
			boolean isNegative = false;
			while (pos.getIndex() < length) {
				char c = csq.charAt(pos.getIndex());
				if (c == '-') {
					isNegative = true;
				} else if ((c >= '0') && (c <= '9')) {
					result = result * 10 + (c - '0');
				} else {
					break;
				}
				pos.setIndex(pos.getIndex() + 1);
			}
			return isNegative ? -result : result;
		}

		//TODO change ParsePos to int arguments
		private double readDouble(CharSequence csq, ParsePosition pos) {
			final int length = csq.length();
			int start = pos.getIndex();
			int end = start + 1;
			while (end < length) {
				if ("0123456789+-.E".indexOf(csq.charAt(end)) < 0) {
					break;
				}
				end += 1;
			}
			pos.setIndex(end + 1);
			return Double.parseDouble(csq.subSequence(start, end).toString());
		}

		private String readIdentifier(CharSequence csq, ParsePosition pos) {
			final int length = csq.length();
			int start = pos.getIndex();
			int i = start;
			while ((++i < length) && isUnitIdentifierPart(csq.charAt(i))) {
			}
			pos.setIndex(i);
			return csq.subSequence(start, i).toString();
		}

		// //////////////////////////
		// Formatting.

		@Override
		public Appendable format(Unit<?> unit, Appendable appendable)
				throws IOException {
			String name = nameFor(unit);
			if (name != null)
				return appendable.append(name);
			if (!(unit instanceof ProductUnit))
				throw new IllegalArgumentException(
						"Cannot format given Object as a Unit");

			// Product unit.
			ProductUnit<?> productUnit = (ProductUnit<?>) unit;
			int invNbr = 0;

			// Write positive exponents first.
			boolean start = true;
			for (int i = 0; i < productUnit.getUnitCount(); i++) {
				int pow = productUnit.getUnitPow(i);
				if (pow >= 0) {
					if (!start) {
						appendable.append('·'); // Separator.
					}
					name = nameFor(productUnit.getUnit(i));
					int root = productUnit.getUnitRoot(i);
					append(appendable, name, pow, root);
					start = false;
				} else {
					invNbr++;
				}
			}

			// Write negative exponents.
			if (invNbr != 0) {
				if (start) {
					appendable.append('1'); // e.g. 1/s
				}
				appendable.append('/');
				if (invNbr > 1) {
					appendable.append('(');
				}
				start = true;
				for (int i = 0; i < productUnit.getUnitCount(); i++) {
					int pow = productUnit.getUnitPow(i);
					if (pow < 0) {
						name = nameFor(productUnit.getUnit(i));
						int root = productUnit.getUnitRoot(i);
						if (!start) {
							appendable.append('·'); // Separator.
						}
						append(appendable, name, -pow, root);
						start = false;
					}
				}
				if (invNbr > 1) {
					appendable.append(')');
				}
			}
			return appendable;
		}

		private void append(Appendable appendable, CharSequence symbol,
				int pow, int root) throws IOException {
			appendable.append(symbol);
			if ((pow != 1) || (root != 1)) {
				// Write exponent.
				if ((pow == 2) && (root == 1)) {
					appendable.append('²'); // Square
				} else if ((pow == 3) && (root == 1)) {
					appendable.append('³'); // Cubic
				} else {
					// Use general exponent form.
					appendable.append('^');
					appendable.append(String.valueOf(pow));
					if (root != 1) {
						appendable.append(':');
						appendable.append(String.valueOf(root));
					}
				}
			}
		}

//		private static final long serialVersionUID = 1L;

		public Unit<?> parse(CharSequence csq) throws ParserException {
			return parse(csq, 0);
		}

		@Override
		protected Unit<?> parse(CharSequence csq, int index)
				throws ParserException {
			return parseObject(csq.toString(), new ParsePosition(index));
		}
	}

	/**
	 * This class represents the ASCII format.
	 */
	protected static final class ASCIIFormat extends DefaultFormat {

		@Override
		public String nameFor(Unit<?> unit) {
			// First search if specific ASCII name should be used.
			String name = _unitToName.get(unit);
			if (name != null)
				return name;
			// Else returns default name.
			return DEFAULT.nameFor(unit);
		}

		@Override
		public Unit<?> unitFor(String name) {
			// First search if specific ASCII name.
			Unit<?> unit = _nameToUnit.get(name);
			if (unit != null)
				return unit;
			// Else returns default mapping.
			return DEFAULT.unitFor(name);
		}

		@Override
		public Appendable format(Unit<?> unit, Appendable appendable)
				throws IOException {
			String name = nameFor(unit);
			if (name != null)
				return appendable.append(name);
			if (!(unit instanceof ProductUnit))
				throw new IllegalArgumentException(
						"Cannot format given Object as a Unit");

			ProductUnit<?> productUnit = (ProductUnit<?>) unit;
			for (int i = 0; i < productUnit.getUnitCount(); i++) {
				if (i != 0) {
					appendable.append('*'); // Separator.
				}
				name = nameFor(productUnit.getUnit(i));
				int pow = productUnit.getUnitPow(i);
				int root = productUnit.getUnitRoot(i);
				appendable.append(name);
				if ((pow != 1) || (root != 1)) {
					// Use general exponent form.
					appendable.append('^');
					appendable.append(String.valueOf(pow));
					if (root != 1) {
						appendable.append(':');
						appendable.append(String.valueOf(root));
					}
				}
			}
			return appendable;
		}

		@Override
		public boolean isValidIdentifier(String name) {
			if ((name == null) || (name.length() == 0))
				return false;
			if (!isUnitIdentifierPart(name.charAt(0))) // label must not begin with a digit or mathematical operator
					return false;
			return isAllASCII(name);
			/*
			for (int i = 0; i < name.length(); i++) {
				if (!isAsciiCharacter(name.charAt(i)))
				return false;
			}
			return true; */
		}
		
		//return if the given character is within the ASCII charset
		private static boolean isAsciiCharacter(char c) {
		  //return (c > 64 && c < 91) || (c > 96 && c < 123);
		  return (int)c<128;
		}
		
		// to check if a string only contains US-ASCII characters
		//
		private static boolean isAllASCII(String input) {
		    boolean isASCII = true;
		    for (int i = 0; i < input.length(); i++) {
		        int c = input.charAt(i);
		        if (c > 0x7F) {
		            isASCII = false;
		            break;
		        }
		    }
		    return isASCII;
		}
		
//		private static final long serialVersionUID = 1L;
	}

	/**
	 * Holds the unique symbols collection (base units or alternate units).
	 */
	private static final Map<String, Unit<?>> SYMBOL_TO_UNIT = new HashMap<String, Unit<?>>();

	// //////////////////////////////////////////////////////////////////////////
	// Initializes the standard unit database for SI units.

	private static final Unit<?>[] SI_UNITS = { Units.AMPERE, Units.BECQUEREL,
			Units.CANDELA, Units.COULOMB, Units.FARAD, Units.GRAY, Units.HENRY,
			Units.HERTZ, Units.JOULE, Units.KATAL, Units.KELVIN, Units.LUMEN,
			Units.LUX, Units.METRE, Units.MOLE, Units.NEWTON, Units.OHM,
			Units.PASCAL, Units.RADIAN, Units.SECOND, Units.SIEMENS,
			Units.SIEVERT, Units.STERADIAN, Units.TESLA, Units.VOLT,
			Units.WATT, Units.WEBER };

	private static final String[] PREFIXES = {  YOTTA.getSymbol(), ZETTA.getSymbol(), 
			EXA.getSymbol(), PETA.getSymbol(), TERA.getSymbol(), GIGA.getSymbol(),
			MEGA.getSymbol(), KILO.getSymbol(), HECTO.getSymbol(), DEKA.getSymbol(),
			DECI.getSymbol(), CENTI.getSymbol(), MILLI.getSymbol(), MICRO.getSymbol(),
			NANO.getSymbol(), PICO.getSymbol(), FEMTO.getSymbol(), ATTO.getSymbol(),
			ZEPTO.getSymbol(), YOCTO.getSymbol()	 
		};

	// TODO we could try retrieving this dynamically in a static {} method from MetricPrefix if symbols above are also aligned
	private static final UnitConverter[] CONVERTERS = { YOTTA.getConverter(), ZETTA.getConverter(), 
			EXA.getConverter(), PETA.getConverter(), TERA.getConverter(), GIGA.getConverter(),
			MEGA.getConverter(), KILO.getConverter(), HECTO.getConverter(), DEKA.getConverter(),
			DECI.getConverter(), CENTI.getConverter(), MILLI.getConverter(), MICRO.getConverter(),
			NANO.getConverter(), PICO.getConverter(), FEMTO.getConverter(), ATTO.getConverter(),
			ZEPTO.getConverter(), YOCTO.getConverter()		
		};

	private static String asciiPrefix(String prefix) {
		return prefix == "µ" ? "micro" : prefix;
	}

	static {
		for (int i = 0; i < SI_UNITS.length; i++) {
			for (int j = 0; j < PREFIXES.length; j++) {
				Unit<?> si = SI_UNITS[i];
				Unit<?> u = si.transform(CONVERTERS[j]);
				String symbol = (si instanceof BaseUnit) ? ((BaseUnit<?>) si)
						.getSymbol() : ((AlternateUnit<?>) si).getSymbol();
				DEFAULT.label(u, PREFIXES[j] + symbol);
				if (PREFIXES[j] == "µ") {
					ASCII.label(u, "micro"); // + symbol);
				}
			}
		}
		// Special case for KILOGRAM.
		DEFAULT.label(Units.GRAM, "g");
		for (int i = 0; i < PREFIXES.length; i++) {
			if (CONVERTERS[i] == KILO.getConverter()) // TODO should it better be equals()?
				continue; // kg is already defined.
			DEFAULT.label(
					Units.KILOGRAM.transform(CONVERTERS[i].concatenate(MILLI.getConverter())),
					PREFIXES[i] + "g");
			if (PREFIXES[i] == "µ") {
				ASCII.label(Units.KILOGRAM.transform(CONVERTERS[i]
						.concatenate(MILLI.getConverter())), "microg");
			}
		}

		// Alias and ASCIIFormat for Ohm
		DEFAULT.alias(Units.OHM, "Ohm");
		ASCII.label(Units.OHM, "Ohm");
		for (int i = 0; i < PREFIXES.length; i++) {
			DEFAULT.alias(Units.OHM.transform(CONVERTERS[i]), PREFIXES[i]
					+ "Ohm");
			ASCII.label(Units.OHM.transform(CONVERTERS[i]),
					asciiPrefix(PREFIXES[i]) + "Ohm");
		}

		// Special case for DEGREE_CElSIUS.
		//DEFAULT.label(Units.CELSIUS, "°C");
		DEFAULT.label(Units.CELSIUS, "\u00b0C");
		DEFAULT.alias(Units.CELSIUS, "℃");
		//DEFAULT.alias(Units.CELSIUS, "°C");
		ASCII.label(Units.CELSIUS, "Celsius");
		for (int i = 0; i < PREFIXES.length; i++) {
			DEFAULT.label(Units.CELSIUS.transform(CONVERTERS[i]), PREFIXES[i]
					+ "℃");
			DEFAULT.alias(Units.CELSIUS.transform(CONVERTERS[i]), PREFIXES[i]
					+ "°C");
			ASCII.label(Units.CELSIUS.transform(CONVERTERS[i]),
					asciiPrefix(PREFIXES[i]) + "Celsius");
		}

		DEFAULT.label(Units.PERCENT, "%");
		DEFAULT.label(Units.KILOGRAM, "kg");
		DEFAULT.label(Units.METRE, "m");
		DEFAULT.label(Units.SECOND, "s");
		DEFAULT.label(Units.MINUTE, "min");
		DEFAULT.label(Units.HOUR, "h");
		DEFAULT.label(Units.DAY, "day");
		DEFAULT.label(Units.WEEK, "week");
		DEFAULT.label(Units.YEAR, "year");
		DEFAULT.label(Units.KILOMETRES_PER_HOUR, "km/h");
		DEFAULT.label(Units.CUBIC_METRE, "\u33A5");
		ASCII.label(Units.CUBIC_METRE, "m3");
		ASCII.label(LITRE, "l");
		DEFAULT.label(LITRE, "l");
		DEFAULT.label(MetricPrefix.NANO(LITRE), "nl");
		ASCII.label(MetricPrefix.NANO(LITRE), "nl");
		DEFAULT.label(MetricPrefix.MICRO(LITRE), "µl");
		ASCII.label(MetricPrefix.MICRO(LITRE), "microL");
		ASCII.label(MetricPrefix.MILLI(LITRE), "mL");
		DEFAULT.label(MetricPrefix.MILLI(LITRE), "ml");
		ASCII.label(MetricPrefix.CENTI(LITRE), "cL");
		DEFAULT.label(MetricPrefix.CENTI(LITRE), "cl");
		ASCII.label(MetricPrefix.DECI(LITRE), "dL");
		DEFAULT.label(MetricPrefix.DECI(LITRE), "dl");
	}
}
