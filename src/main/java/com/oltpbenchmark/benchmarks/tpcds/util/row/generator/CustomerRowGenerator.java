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

package com.oltpbenchmark.benchmarks.tpcds.util.row.generator;

import com.oltpbenchmark.benchmarks.tpcds.util.Scaling;
import com.oltpbenchmark.benchmarks.tpcds.util.Session;
import com.oltpbenchmark.benchmarks.tpcds.util.row.CustomerRow;
import com.oltpbenchmark.benchmarks.tpcds.util.type.Date;

import static com.oltpbenchmark.benchmarks.tpcds.util.BusinessKeyGenerator.makeBusinessKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.JoinKeyUtils.generateJoinKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER_ADDRESS;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER_DEMOGRAPHICS;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.HOUSEHOLD_DEMOGRAPHICS;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.AddressDistributions.pickRandomCountry;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.FirstNamesWeights.FEMALE_FREQUENCY;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.FirstNamesWeights.GENERAL_FREQUENCY;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.SalutationsWeights.FEMALE;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.SalutationsWeights.MALE;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.getFirstNameFromIndex;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.getWeightForIndex;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.pickRandomIndex;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.pickRandomLastName;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.NamesDistributions.pickRandomSalutation;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_BIRTH_COUNTRY;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_BIRTH_DAY;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_CURRENT_ADDR_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_CURRENT_CDEMO_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_CURRENT_HDEMO_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_EMAIL_ADDRESS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_FIRST_NAME;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_FIRST_SALES_DATE_ID;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_LAST_NAME;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_LAST_REVIEW_DATE;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_NULLS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_PREFERRED_CUST_FLAG;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerGeneratorColumn.C_SALUTATION;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateRandomEmail;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateUniformRandomDate;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateUniformRandomInt;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.JULIAN_TODAYS_DATE;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.fromJulianDays;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.toJulianDays;

public class CustomerRowGenerator
        extends AbstractRowGenerator
{
    public CustomerRowGenerator()
    {
        super(CUSTOMER);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long cCustomerSk = rowNumber;
        String cCustomerId = makeBusinessKey(rowNumber);
        int randomInt = generateUniformRandomInt(1, 100, getRandomNumberStream(C_PREFERRED_CUST_FLAG));
        int cPreferredPercent = 50;
        boolean cPreferredCustFlag = randomInt < cPreferredPercent;

        Scaling scaling = session.getScaling();
        long cCurrentHdemoSk = generateJoinKey(C_CURRENT_HDEMO_SK, getRandomNumberStream(C_CURRENT_HDEMO_SK), HOUSEHOLD_DEMOGRAPHICS, 1, scaling);
        long cCurrentCdemoSk = generateJoinKey(C_CURRENT_CDEMO_SK, getRandomNumberStream(C_CURRENT_CDEMO_SK), CUSTOMER_DEMOGRAPHICS, 1, scaling);
        long cCurrentAddrSk = generateJoinKey(C_CURRENT_ADDR_SK, getRandomNumberStream(C_CURRENT_ADDR_SK), CUSTOMER_ADDRESS, cCustomerSk, scaling);

        int nameIndex = pickRandomIndex(GENERAL_FREQUENCY, getRandomNumberStream(C_FIRST_NAME));
        String cFirstName = getFirstNameFromIndex(nameIndex);
        String cLastName = pickRandomLastName(getRandomNumberStream(C_LAST_NAME));
        int femaleNameWeight = getWeightForIndex(nameIndex, FEMALE_FREQUENCY);
        String cSalutation = pickRandomSalutation(femaleNameWeight == 0 ? MALE : FEMALE, getRandomNumberStream(C_SALUTATION));

        Date maxBirthday = new Date(1992, 12, 31);
        Date minBirthday = new Date(1924, 1, 1);
        Date oneYearAgo = fromJulianDays(JULIAN_TODAYS_DATE - 365);
        Date tenYearsAgo = fromJulianDays(JULIAN_TODAYS_DATE - 3650);
        Date today = fromJulianDays(JULIAN_TODAYS_DATE);
        Date birthday = generateUniformRandomDate(minBirthday, maxBirthday, getRandomNumberStream(C_BIRTH_DAY));
        int cBirthDay = birthday.getDay();
        int cBirthMonth = birthday.getMonth();
        int cBirthYear = birthday.getYear();

        String cEmailAddress = generateRandomEmail(cFirstName, cLastName, getRandomNumberStream(C_EMAIL_ADDRESS));
        Date lastReviewDate = generateUniformRandomDate(oneYearAgo, today, getRandomNumberStream(C_LAST_REVIEW_DATE));
        int cLastReviewDate = toJulianDays(lastReviewDate);
        Date firstSalesDate = generateUniformRandomDate(tenYearsAgo, today, getRandomNumberStream(C_FIRST_SALES_DATE_ID));
        int cFirstSalesDateId = toJulianDays(firstSalesDate);
        int cFirstShiptoDateId = cFirstSalesDateId + 30;

        String cBirthCountry = pickRandomCountry(getRandomNumberStream(C_BIRTH_COUNTRY));

        return new RowGeneratorResult(new CustomerRow(cCustomerSk,
                cCustomerId,
                cCurrentCdemoSk,
                cCurrentHdemoSk,
                cCurrentAddrSk,
                cFirstShiptoDateId,
                cFirstSalesDateId,
                cSalutation,
                cFirstName,
                cLastName,
                cPreferredCustFlag,
                cBirthDay,
                cBirthMonth,
                cBirthYear,
                cBirthCountry,
                cEmailAddress,
                cLastReviewDate,
                createNullBitMap(CUSTOMER, getRandomNumberStream(C_NULLS))));
    }
}
