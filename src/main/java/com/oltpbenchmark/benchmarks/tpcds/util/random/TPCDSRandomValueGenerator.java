/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oltpbenchmark.benchmarks.tpcds.util.random;

import com.oltpbenchmark.util.RowRandomInt;
import com.oltpbenchmark.benchmarks.tpcds.util.distribution.CalendarDistribution;
import com.oltpbenchmark.benchmarks.tpcds.util.distribution.StringValuesDistribution;
import com.oltpbenchmark.benchmarks.tpcds.util.type.Date;
import com.oltpbenchmark.benchmarks.tpcds.util.type.Decimal;

import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.CalendarDistribution.getWeightForDayNumber;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomAdjective;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomAdverb;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomArticle;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomAuxiliary;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomNoun;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomPreposition;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomSentence;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomTerminator;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.pickRandomVerb;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.TopDomainsDistribution.pickRandomTopDomain;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.fromJulianDays;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.getDaysInYear;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.toJulianDays;
import static java.util.Objects.requireNonNull;

public final class TPCDSRandomValueGenerator
{
    public static final String ALPHA_NUMERIC = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789";
    public static final String DIGITS = "0123456789";

    private TPCDSRandomValueGenerator() {}

    public static int generateUniformRandomInt(int min, int max, RowRandomInt randomNumberStream)
    {
        int result = (int) randomNumberStream.nextRand(); // truncating long to int copies behavior of c code.
        result %= max - min + 1;
        result += min;
        return result;
    }

    public static long generateUniformRandomKey(long min, long max, RowRandomInt randomNumberStream)
    {
        int result = (int) randomNumberStream.nextRand(); // truncating long to int copies behavior of c code
        result %= (int) (max - min + 1);
        result += (int) min;
        return result;
    }

    public static Decimal generateUniformRandomDecimal(Decimal min, Decimal max, RowRandomInt randomNumberStream)
    {
        int precision = min.getPrecision() < max.getPrecision() ? min.getPrecision() : max.getPrecision();

        // compute number
        long number = randomNumberStream.nextRand();
        number %= max.getNumber() - min.getNumber() + 1;
        number += min.getNumber();

        return new Decimal(number, precision);
    }

    public static Date generateUniformRandomDate(Date min, Date max, RowRandomInt randomNumberStream)
    {
        int range = toJulianDays(max) - toJulianDays(min);
        int julianDays = toJulianDays(min) + generateUniformRandomInt(0, range, randomNumberStream);
        return fromJulianDays(julianDays);
    }

    public static Date generateSalesReturnsRandomDate(Date min, Date max, CalendarDistribution.Weights weights, RowRandomInt randomNumberStream)
    {
        // get random date based on distribution.
        // Copying behavior of dsdgen, but unclear what's going on there
        // Date.day represents the day of the month, but for some reason
        // it is interpreted in genrand_date as the day of the year.
        // That means we always start somewhere in January, I guess.
        int dayCount = min.getDay();
        int year = min.getYear();
        int totalWeight = 0;
        int range = toJulianDays(max) - toJulianDays(min);

        for (int i = 0; i < range; i++) {
            totalWeight += getWeightForDayNumber(dayCount, weights);
            if (dayCount == getDaysInYear(year)) {
                year += 1;
                dayCount = 1;
            }
            else {
                dayCount += 1;
            }
        }

        // Choose a random int up to totalWeight.
        // Then work backwards to get the first date where the chosen number is
        // greater than or equal to the sum of all weights from min to date.
        int tempWeightSum = generateUniformRandomInt(1, totalWeight, randomNumberStream);
        dayCount = min.getDay();
        int julianDays = Date.toJulianDays(min);
        year = min.getYear();
        while (tempWeightSum > 0) {
            tempWeightSum -= getWeightForDayNumber(dayCount, weights);
            dayCount += 1;
            julianDays += 1;
            if (dayCount > getDaysInYear(year)) {
                dayCount = 1;
                year += 1;
            }
        }

        return fromJulianDays(julianDays);
    }

    public static String generateRandomCharset(String set, int min, int max, RowRandomInt randomNumberStream)
    {
        requireNonNull(set, "set is null");

        int length = generateUniformRandomInt(min, max, randomNumberStream);
        StringBuilder builder = new StringBuilder();

        // It seems like it would make more sense to make length the loop condition.
        // For some reason dsdgen doesn't do that, and we want the RNG seeds to be the same
        // so we copy the behavior.
        for (int i = 0; i < max; i++) {
            int index = generateUniformRandomInt(0, set.length() - 1, randomNumberStream);
            if (i < length) {
                builder.append(set.charAt(index));
            }
        }
        return builder.toString();
    }

    public static String generateRandomEmail(String first, String last, RowRandomInt randomNumberStream)
    {
        String domain = pickRandomTopDomain(randomNumberStream);
        int companyLength = generateUniformRandomInt(10, 20, randomNumberStream);
        String company = generateRandomCharset(ALPHA_NUMERIC, 1, 20, randomNumberStream);
        company = company.length() < companyLength ? company : company.substring(0, companyLength);

        return String.format("%s.%s@%s.%s", first, last, company, domain);
    }

    public static String generateRandomIpAddress(RowRandomInt randomNumberStream)
    {
        int[] ipSegments = new int[4];

        for (int i = 0; i < 4; i++) {
            ipSegments[i] = generateUniformRandomInt(1, 255, randomNumberStream);
        }

        return String.format("%d.%d.%d.%d", ipSegments[0], ipSegments[1], ipSegments[2], ipSegments[3]);
    }

    public static String generateRandomUrl(RowRandomInt randomNumberStream)
    {
        return "http://www.foo.com";  // This is what the c code does. No joke.
    }

    public static int generateExponentialRandomInt(int min, int max, RowRandomInt randomNumberStream)
    {
        int range = max - min + 1;
        double doubleResult = 0;
        for (int i = 0; i < 12; i++) {
            doubleResult += randomNumberStream.nextDouble() - 0.5;
        }
        return min + (int) (range * doubleResult);
    }

    public static long generateExponentialRandomKey(long min, long max, RowRandomInt randomNumberStream)
    {
        double doubleResult = 0;
        for (int i = 0; i < 12; i++) {
            doubleResult += ((double) randomNumberStream.nextRand() / Integer.MAX_VALUE) - 0.5;
        }
        return (int) min + (int) ((max - min + 1) * doubleResult); // truncating long to int copies behavior of c code
    }

    public static Decimal generateExponentialRandomDecimal(Decimal min, Decimal max, Decimal mean, RowRandomInt randomNumberStream)
    {
        // compute precision
        int precision = min.getPrecision() < max.getPrecision() ? min.getPrecision() : max.getPrecision();

        // compute number
        double doubleResult = 0;
        for (int i = 0; i < 12; i++) {
            doubleResult /= 2.0;
            doubleResult += (double) randomNumberStream.nextRand() / Integer.MAX_VALUE - 0.5;
        }

        long number = mean.getNumber() + (int) ((max.getNumber() - min.getNumber() + 1) * doubleResult);

        return new Decimal(number, precision);
    }

    public static Date generateExponentialRandomDate(Date min, Date max, RowRandomInt randomNumberStream)
    {
        int range = toJulianDays(max) - toJulianDays(min);
        int days = toJulianDays(min) + generateExponentialRandomInt(0, range, randomNumberStream);
        return fromJulianDays(days);
    }

    public static String generateRandomText(int minLength, int maxLength, RowRandomInt stream)
    {
        boolean isSentenceBeginning = true;
        StringBuilder text = new StringBuilder();
        int targetLength = generateUniformRandomInt(minLength, maxLength, stream);

        while (targetLength > 0) {
            String generated = generateRandomSentence(stream);
            if (isSentenceBeginning) {
                generated = generated.substring(0, 1).toUpperCase() + generated.substring(1);
            }

            int generatedLength = generated.length();
            isSentenceBeginning = (generated.charAt(generatedLength - 1) == '.');

            // truncate so as not to exceed target length
            if (targetLength < generatedLength) {
                generated = generated.substring(0, targetLength);
            }

            targetLength -= generatedLength;

            text.append(generated);
            if (targetLength > 0) {
                text.append(" ");
                targetLength -= 1;
            }
        }

        return text.toString();
    }

    private static String generateRandomSentence(RowRandomInt stream)
    {
        StringBuilder verbiage = new StringBuilder();
        String syntax = pickRandomSentence(stream);
        for (int i = 0; i < syntax.length(); i++) {
            switch (syntax.charAt(i)) {
                case 'N':
                    verbiage.append(pickRandomNoun(stream));
                    break;
                case 'V':
                    verbiage.append(pickRandomVerb(stream));
                    break;
                case 'J':
                    verbiage.append(pickRandomAdjective(stream));
                    break;
                case 'D':
                    verbiage.append(pickRandomAdverb(stream));
                    break;
                case 'X':
                    verbiage.append(pickRandomAuxiliary(stream));
                    break;
                case 'P':
                    verbiage.append(pickRandomPreposition(stream));
                    break;
                case 'A':
                    verbiage.append(pickRandomArticle(stream));
                    break;
                case 'T':
                    verbiage.append(pickRandomTerminator(stream));
                    break;
                default:
                    verbiage.append(syntax.charAt(i));  // this is for adding punctuation and white space.
                    break;
            }
        }
        return verbiage.toString();
    }

    public static String generateWord(long seed, int maxChars, StringValuesDistribution distribution)
    {
        long size = distribution.getSize();
        StringBuilder word = new StringBuilder();
        while (seed > 0) {
            String syllable = distribution.getValueAtIndex(0, (int) (seed % size));
            seed /= size;
            if ((word.length() + syllable.length()) <= maxChars) {
                word.append(syllable);
            }
            else {
                break;
            }
        }
        return word.toString();
    }
}
