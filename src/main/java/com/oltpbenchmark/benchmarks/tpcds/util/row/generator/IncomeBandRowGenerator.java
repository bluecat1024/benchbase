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
import com.oltpbenchmark.benchmarks.tpcds.util.row.IncomeBandRow;

import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.INCOME_BAND;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getIncomeBandLowerBoundAtIndex;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DemographicsDistributions.getIncomeBandUpperBoundAtIndex;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.IncomeBandGeneratorColumn.IB_NULLS;

public class IncomeBandRowGenerator
        extends AbstractRowGenerator
{
    public IncomeBandRowGenerator()
    {
        super(INCOME_BAND);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long nullBitMap = createNullBitMap(INCOME_BAND, getRandomNumberStream(IB_NULLS));
        int ibIncomeBandId = (int) rowNumber;
        int ibLowerBound = getIncomeBandLowerBoundAtIndex((int) rowNumber - 1);
        int ibUpperBound = getIncomeBandUpperBoundAtIndex((int) rowNumber - 1);
        return new RowGeneratorResult(new IncomeBandRow(nullBitMap, ibIncomeBandId, ibLowerBound, ibUpperBound));
    }
}
