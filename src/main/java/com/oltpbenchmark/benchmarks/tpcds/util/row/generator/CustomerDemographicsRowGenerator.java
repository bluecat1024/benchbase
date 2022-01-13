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

import com.oltpbenchmark.benchmarks.tpcds.util.Session;
import com.oltpbenchmark.benchmarks.tpcds.util.row.CustomerDemographicsRow;

import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER_DEMOGRAPHICS;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.CREDIT_RATING_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.EDUCATION_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.GENDER_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.MARITAL_STATUS_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.PURCHASE_BAND_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getCreditRatingForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getEducationForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getGenderForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getMaritalStatusForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getPurchaseBandForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CustomerDemographicsGeneratorColumn.CD_NULLS;

public class CustomerDemographicsRowGenerator
        extends AbstractRowGenerator
{
    private static final int MAX_CHILDREN = 7;
    private static final int MAX_EMPLOYED = 7;
    private static final int MAX_COLLEGE = 7;

    public CustomerDemographicsRowGenerator()
    {
        super(CUSTOMER_DEMOGRAPHICS);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long nullBitMap = createNullBitMap(CUSTOMER_DEMOGRAPHICS, getRandomNumberStream(CD_NULLS));
        long cDemoSk = rowNumber;
        long index = cDemoSk - 1;

        String cdGender = getGenderForIndexModSize(index);
        index = index / GENDER_DISTRIBUTION.getSize();
        String cdMaritalStatus = getMaritalStatusForIndexModSize(index);

        index = index / MARITAL_STATUS_DISTRIBUTION.getSize();
        String cdEducationStatus = getEducationForIndexModSize(index);

        index = index / EDUCATION_DISTRIBUTION.getSize();
        int cdPurchaseEstimate = getPurchaseBandForIndexModSize(index);

        index = index / PURCHASE_BAND_DISTRIBUTION.getSize();
        String cdCreditRating = getCreditRatingForIndexModSize(index);

        index = index / CREDIT_RATING_DISTRIBUTION.getSize();
        int cdDepCount = (int) (index % (long) MAX_CHILDREN);

        index /= (long) MAX_CHILDREN;
        int cdEmployedCount = (int) (index % (long) MAX_EMPLOYED);

        index /= (long) MAX_EMPLOYED;
        int cdDepCollegeCount = (int) (index % (long) MAX_COLLEGE);

        return new RowGeneratorResult(new CustomerDemographicsRow(nullBitMap,
                cDemoSk,
                cdGender,
                cdMaritalStatus,
                cdEducationStatus,
                cdPurchaseEstimate,
                cdCreditRating,
                cdDepCount,
                cdEmployedCount,
                cdDepCollegeCount));
    }
}
