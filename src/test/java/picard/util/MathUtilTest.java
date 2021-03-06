package picard.util;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static picard.util.MathUtil.divide;

/**
 * @author mccowan
 */
public class MathUtilTest {
    @Test
    public void logMathTest() {
        Assert.assertEquals(MathUtil.LOG_10_MATH.getLogValue(10), 1d, 0.00001d);
        Assert.assertEquals(MathUtil.LOG_10_MATH.getNonLogValue(1), 10d, 0.00001d);
        Assert.assertEquals(MathUtil.LOG_10_MATH.mean(5, 5, 5), 5d, 0.00001d);
        // http://www.wolframalpha.com/input/?i=log10%2810%5E1.23%2B10%5E4.56%2B10%5E99999%29
        Assert.assertEquals(MathUtil.LOG_10_MATH.sum(1.23, 4.56, 2), 4.5613970317323586660874152202433434022756298235604568d, 0.00001d);
        // http://www.wolframalpha.com/input/?i=log10%2810%5E1.23+*+10%5E4.56+*+10%5E2%29
        Assert.assertEquals(MathUtil.LOG_10_MATH.product(1.23, 4.56, 2), 7.7899999999999999999999999999999999999999999999999999d, 0.00001d);

        TestNGUtil.assertEqualDoubleArrays(MathUtil.LOG_10_MATH.getLogValue(new double[]{0.1, 1.0, 10.0}), new double[]{-1.0, 0.0, 1.0}, 0.00001d);
        TestNGUtil.assertEqualDoubleArrays(MathUtil.LOG_2_MATH.getLogValue(new double[]{0.5, 1.0, 2.0}), new double[]{-1.0, 0.0, 1.0}, 0.00001d);
    }

    @DataProvider
    public Object[][] seqMethodTestCases() {
        return new Object[][]{
                new Object[]{0d, 5d, 1d, new double[]{0, 1, 2, 3, 4, 5}},
                new Object[]{0d, 0.5d, 0.1d, new double[]{0, 0.1, 0.2, 0.3, 0.4, 0.5}},
                new Object[]{0d, 0.5d, 0.11d, new double[]{0, 0.11, 0.22, 0.33, 0.44}},
                new Object[]{50d, 55d, 1.25d, new double[]{50, 51.25, 52.5, 53.75, 55}},
                new Object[]{10d, 0d, 02d, new double[]{}},
                new Object[]{10d, 0d, -2d, new double[]{10, 8, 6, 4, 2, 0}},
        };
    }

    @Test(dataProvider = "seqMethodTestCases")
    public void testSeqGeneration(final double from, final double to, final double by, final double[] expected) {
        final double[] actual = MathUtil.seq(from, to, by);
        Assert.assertEquals(actual.length, expected.length);

        for (int i = 0; i < expected.length; ++i) {
            Assert.assertTrue(Math.abs(actual[i] - expected[i]) < 0.0000001);
        }
    }

    @DataProvider
    public Object[][] divideMethodTestCases() {
        return new Object[][]{
                new Object[]{new double[]{1, 2, 3, 4}, new double[]{2, 3, 4, -5}, new double[]{.5, 2.0 / 3, 3.0 / 4, -4.0 / 5}},
                new Object[]{new double[]{100}, new double[]{200}, new double[]{.5}},
                new Object[]{new double[]{0, 4, -3, 2}, new double[]{200, 30, 32, 12}, new double[]{0, 4.0 / 30, -3.0 / 32, 2.0 / 12}},
                new Object[]{new double[]{}, new double[]{}, new double[]{}},

        };
    }

    @Test(dataProvider = "divideMethodTestCases")
    public void testDivide(final double[] numerators, final double[] denominators, final double[] expected) {
        assertEquals(divide(numerators, denominators), expected);
    }

    @DataProvider
    public Object[][] divideMethodFailTestCases() {
        return new Object[][]{
                new Object[]{new double[]{1, 2, 3, 4}, new double[]{2, 3, 4}},
                new Object[]{new double[]{100}, new double[]{}},
        };
    }

    @Test(dataProvider = "divideMethodFailTestCases", expectedExceptions = RuntimeException.class)
    public void testDivideFail(final double[] lhs, final double[] rhs) {
        divide(lhs, rhs);
    }

    //TestNG doesn't have a utility function with this signature....
    private void assertEquals(final double[] actual, final double[] expected) {
        Assert.assertEquals(actual.length, expected.length, "Arrays do not have equal lengths");

        for (int i = 0; i < actual.length; ++i) {
            Assert.assertEquals(actual[i], expected[i], "Array differ at position " + i);
        }
    }

    @Test
    public void testRandomSublist() {
        final Random random = new Random();
        final List<Integer> list = Arrays.asList(1, 2, 3);

        Assert.assertEquals(list, MathUtil.randomSublist(list, 3, random));
        Assert.assertEquals(list, MathUtil.randomSublist(list, 4, random));
        Assert.assertEquals(MathUtil.randomSublist(list, 2, random).size(), 2);
    }

    @Test(dataProvider = "divideDoubleTestCases")
    public void testDivideDouble(final double numerator, final double denominator, final double expected) {
        Assert.assertEquals(MathUtil.divide(numerator, denominator), expected);
    }

    @DataProvider
    public Object[][] divideDoubleTestCases() {
        return new Object[][]{
                new Object[]{15.0, 3.0, 5.0},
                new Object[]{15.0, 0.0, 0.0}
        };
    }

    @DataProvider
    public Object[][] getProbabilityFromLogTestData() {
        return new Object[][]{
                new Object[]{
                        new double[]{}, new double[]{}
                },
                new Object[]{
                        new double[]{.001, .01, .1, 1, 10, 100, 1000, 10000D}, new double[]{-3, -2, -1, 0, 1, 2, 3, 4}
                },
                new Object[]{
                        new double[]{1D}, new double[]{0D}
                },
                new Object[]{
                        new double[]{.1234D, .2345D, .3456D}, new double[]{Math.log10(.1234D), Math.log10(.2345D), Math.log10(.3456D)}
                },

        };
    }

    @Test(dataProvider = "getProbabilityFromLogTestData")
    public void getProbabilityFromLogTest(final double[] input, final double[] expected) {
        TestNGUtil.assertEqualDoubleArrays(MathUtil.getLogFromProbability(input), expected, 1e-8);
    }

    @DataProvider
    public Object[][] pNormalizeLogProbabilityTestData() {
        return new Object[][]{
                new Object[]{
                        new double[]{}, new double[]{}
                },
                new Object[]{
                        new double[]{0D}, new double[]{1D}
                },
                new Object[]{
                        new double[]{0D, 0D}, new double[]{0.5, 0.5}
                },
                new Object[]{
                        new double[]{0, -1, -2}, new double[]{1 / 1.11, .1 / 1.11, 0.01 / 1.11}
                },
                new Object[]{
                        new double[]{1000D, 1000D}, new double[]{0.5, 0.5}
                },
                new Object[]{
                        new double[]{1002D, 1001D, 1000D}, new double[]{1 / 1.11, .1 / 1.11, 0.01 / 1.11}
                },
        };
    }

    @Test(dataProvider = "pNormalizeLogProbabilityTestData")
    public void pNormalizeLogProbabilityTest(final double[] input, final double[] expected) {
        TestNGUtil.assertEqualDoubleArrays(MathUtil.pNormalizeLogProbability(input), expected, 1e-8);
    }

    @DataProvider
    public Object[][] testMaxWithArrayAndScalarData() {
        return new Object[][]{
                {new double[]{1, 2, 3}, 2D, new double[]{2, 2, 3}},
                {new double[]{2, 1, 3}, 2D, new double[]{2, 2, 3}},
                {new double[]{3, 2, 1}, 2D, new double[]{3, 2, 2}},
                {new double[]{2, 3, 1}, 2D, new double[]{2, 3, 2}},
                {new double[]{}, 2D, new double[]{}},
        };
    }

    @Test(dataProvider = "testMaxWithArrayAndScalarData")
    public void testMaxWithArrayAndScalar(final double[] array, final double scalar, final double[] expected) {
        Assert.assertEquals(MathUtil.capFromBelow(array, scalar), expected);
    }

    @DataProvider
    public Object[][] testMinWithArrayAndScalarData() {
        return new Object[][]{
                {new double[]{1, 2, 3}, 2D, new double[]{1, 2, 2}},
                {new double[]{2, 1, 3}, 2D, new double[]{2, 1, 2}},
                {new double[]{3, 2, 1}, 2D, new double[]{2, 2, 1}},
                {new double[]{2, 3, 1}, 2D, new double[]{2, 2, 1}},
                {new double[]{}, 2D, new double[]{}},
        };
    }

    @Test(dataProvider = "testMinWithArrayAndScalarData")
    public void testMinWithArrayAndScalar(final double[] array, final double scalar, final double[] expected) {
        Assert.assertEquals(MathUtil.capFromAbove(array, scalar), expected);
    }

    @DataProvider
    public Object[][] testMinWithArrayData() {
        return new Object[][]{
                {new double[]{1, 2, 3}, 1D},
                {new double[]{2, 1, 3}, 1D},
                {new double[]{3, 2, 1}, 1D},
                {new double[]{2, 3, 1}, 1D},
        };
    }

    @Test(dataProvider = "testMinWithArrayData")
    public void testMinOfArray(final double[] array, final double expected) {
        Assert.assertEquals(MathUtil.min(array), expected);
    }

    @DataProvider
    public Object[][] testMaxWithArrayData() {
        return new Object[][]{
                {new double[]{1, 2, 3}, 3D},
                {new double[]{2, 1, 3}, 3D},
                {new double[]{3, 2, 1}, 3D},
                {new double[]{2, 3, 1}, 3D},
        };
    }

    @Test(dataProvider = "testMaxWithArrayData")
    public void testMaxOfArray(final double[] array, final double expected) {
        Assert.assertEquals(MathUtil.max(array), expected);
    }

    @DataProvider
    public Object[][] testMinWithArrayIntData() {
        return new Object[][]{
                {new int[]{1, 2, 3}, 1},
                {new int[]{2, 1, 3}, 1},
                {new int[]{3, 2, 1}, 1},
                {new int[]{2, 3, 1}, 1},
        };
    }

    @Test(dataProvider = "testMinWithArrayIntData")
    public void testMinOfIntArray(final int[] array, final int expected) {
        Assert.assertEquals(MathUtil.min(array), expected);
    }

    @DataProvider
    public Object[][] testMinWithArrayShortData() {
        return new Object[][]{
                {new short[]{1, 2, 3},(short) 1},
                {new short[]{2, 1, 3},(short) 1},
                {new short[]{3, 2, 1},(short) 1},
                {new short[]{2, 3, 1},(short) 1},
        };
    }

    @Test(dataProvider = "testMinWithArrayShortData")
    public void testMinOfShortArray(final short[] array, final short expected) {
        Assert.assertEquals(MathUtil.min(array), expected);
    }

    @DataProvider
    public Object[][] testMinWithArrayByteData() {
        return new Object[][]{
                {new byte[]{1, 2, 3}, (byte)1},
                {new byte[]{2, 1, 3}, (byte)1},
                {new byte[]{3, 2, 1}, (byte)1},
                {new byte[]{2, 3, 1}, (byte)1},
        };
    }

    @Test(dataProvider = "testMinWithArrayByteData")
    public void testMaxOfByteArray(final byte[] array, final byte expected) {
        Assert.assertEquals(MathUtil.min(array), expected);
    }

    @DataProvider
    public Object[][] testSubtractMaxData() {
        return new Object[][]{
                {new double[]{1, 2, 3}, new double[]{-2, -1, 0}},
                {new double[]{2, 1, 3}, new double[]{-1, -2, 0}},
                {new double[]{3, 2, 1}, new double[]{0, -1, -2}},
                {new double[]{2, 3, 1}, new double[]{-1, 0, -2}},
        };
    }

    @Test(dataProvider = "testSubtractMaxData")
    public void testSubtractMax(final double[] array, final double[] expected) {
        Assert.assertEquals(MathUtil.subtractMax(array), expected);
    }
}

