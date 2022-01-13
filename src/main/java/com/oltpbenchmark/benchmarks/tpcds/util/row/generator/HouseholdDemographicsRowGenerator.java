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
import com.oltpbenchmark.benchmarks.tpcds.util.row.HouseholdDemographicsRow;

import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.HOUSEHOLD_DEMOGRAPHICS;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.BUY_POTENTIAL_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.DEP_COUNT_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.INCOME_BAND_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getBuyPotentialForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getDepCountForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getVehicleCountForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.HouseholdDemographicsGeneratorColumn.HD_NULLS;

public class HouseholdDemographicsRowGenerator
        extends AbstractRowGenerator
{
    public HouseholdDemographicsRowGenerator()
    {
        super(HOUSEHOLD_DEMOGRAPHICS);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long nullBitMap = createNullBitMap(HOUSEHOLD_DEMOGRAPHICS, getRandomNumberStream(HD_NULLS));
        long hdDemoSk = rowNumber;
        long index = hdDemoSk;
        long hdIncomeBandId = (index % INCOME_BAND_DISTRIBUTION.getSize()) + 1;

        index /= INCOME_BAND_DISTRIBUTION.getSize();
        String hdBuyPotential = getBuyPotentialForIndexModSize(index);

        index /= BUY_POTENTIAL_DISTRIBUTION.getSize();
        int hdDepCount = getDepCountForIndexModSize(index);

        index /= DEP_COUNT_DISTRIBUTION.getSize();
        int hdVehicleCount = getVehicleCountForIndexModSize(index);

        return new RowGeneratorResult(new HouseholdDemographicsRow(nullBitMap, hdDemoSk, hdIncomeBandId, hdBuyPotential, hdDepCount, hdVehicleCount));
    }
}
