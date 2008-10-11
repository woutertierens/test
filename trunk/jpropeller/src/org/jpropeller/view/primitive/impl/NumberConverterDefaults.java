package org.jpropeller.view.primitive.impl;

/**
 * Implementations of {@link NumberConverter} for common {@link Number} classes
 * @author bwebster
 */
public class NumberConverterDefaults {

	private final static NumberConverter<Float> floatConverter = new FloatConverter();
	private final static NumberConverter<Double> doubleConverter = new DoubleConverter();
	private final static NumberConverter<Byte> byteConverter = new ByteConverter();
	private final static NumberConverter<Short> shortConverter = new ShortConverter();
	private final static NumberConverter<Integer> integerConverter = new IntegerConverter();
	private final static NumberConverter<Long> longConverter = new LongConverter();
	
	//Not sure big decimal support is a good idea - for the other types we are probably quite
	//confident of conversions, but perhaps not for this
	//private final static NumberConverter<BigDecimal> bigDecimalConverter = new BigDecimalConverter();

	/**
	 * Get a {@link NumberConverter} for {@link Float}
	 * @return
	 * 		Number converter
	 */
	public static NumberConverter<Float> getFloatConverter() {
		return floatConverter;
	}
	/**
	 * Get a {@link NumberConverter} for {@link Double}
	 * @return
	 * 		Number converter
	 */
	public static NumberConverter<Double> getDoubleConverter() {
		return doubleConverter;
	}
	/**
	 * Get a {@link NumberConverter} for {@link Byte}
	 * @return
	 * 		Number converter
	 */
	public static NumberConverter<Byte> getByteConverter() {
		return byteConverter;
	}
	/**
	 * Get a {@link NumberConverter} for {@link Short}
	 * @return
	 * 		Number converter
	 */
	public static NumberConverter<Short> getShortConverter() {
		return shortConverter;
	}
	/**
	 * Get a {@link NumberConverter} for {@link Integer}
	 * @return
	 * 		Number converter
	 */
	public static NumberConverter<Integer> getIntegerConverter() {
		return integerConverter;
	}
	/**
	 * Get a {@link NumberConverter} for {@link Long}
	 * @return
	 * 		Number converter
	 */
	public static NumberConverter<Long> getLongConverter() {
		return longConverter;
	}
//	/**
//	 * Get a {@link NumberConverter} for {@link BigDecimal}
//	 * @return
//	 * 		Number converter
//	 */
//	public static NumberConverter<BigDecimal> getBigDecimalConverter() {
//		return bigDecimalConverter;
//	}
	
	private static class FloatConverter implements NumberConverter<Float> {
		@Override
		public Number toNumber(Float t) {
			return t;
		}
		@Override
		public Float toT(Number n) {
			return new Float(n.floatValue());
		}
	}

	private static class DoubleConverter implements NumberConverter<Double> {
		@Override
		public Number toNumber(Double t) {
			return t;
		}
		@Override
		public Double toT(Number n) {
			return new Double(n.doubleValue());
		}
	}
	
	private static class ByteConverter implements NumberConverter<Byte> {
		@Override
		public Number toNumber(Byte t) {
			return t;
		}
		@Override
		public Byte toT(Number n) {
			return new Byte(n.byteValue());
		}
	}

	private static class ShortConverter implements NumberConverter<Short> {
		@Override
		public Number toNumber(Short t) {
			return t;
		}
		@Override
		public Short toT(Number n) {
			return new Short(n.shortValue());
		}
	}

	private static class IntegerConverter implements NumberConverter<Integer> {
		@Override
		public Number toNumber(Integer t) {
			return t;
		}
		@Override
		public Integer toT(Number n) {
			return new Integer(n.intValue());
		}
	}
	private static class LongConverter implements NumberConverter<Long> {
		@Override
		public Number toNumber(Long t) {
			return t;
		}
		@Override
		public Long toT(Number n) {
			return new Long(n.longValue());
		}
	}

//	private static class BigDecimalConverter implements NumberConverter<BigDecimal> {
//		@Override
//		public Number toNumber(BigDecimal t) {
//			return t;
//		}
//		@Override
//		public BigDecimal toT(Number n) {
//			return new BigDecimal(n.doubleValue());
//		}
//	}

}
