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

package com.oltpbenchmark.benchmarks.tpcds.util.row;

import java.util.List;

import static com.oltpbenchmark.benchmarks.tpcds.util.generator.IncomeBandGeneratorColumn.IB_INCOME_BAND_ID;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.IncomeBandGeneratorColumn.IB_LOWER_BOUND;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.IncomeBandGeneratorColumn.IB_UPPER_BOUND;

public class IncomeBandRow
        extends TableRowWithNulls
{
    private final int ibIncomeBandId;
    private final int ibLowerBound;
    private final int ibUpperBound;

    public IncomeBandRow(long nullBitMap, int ibIncomeBandId, int ibLowerBound, int ibUpperBound)
    {
        super(nullBitMap, IB_INCOME_BAND_ID);
        this.ibIncomeBandId = ibIncomeBandId;
        this.ibLowerBound = ibLowerBound;
        this.ibUpperBound = ibUpperBound;
    }

    @Override
    public List<String> getValues()
    {
        return List.of(getStringOrNull(ibIncomeBandId, IB_INCOME_BAND_ID),
                getStringOrNull(ibLowerBound, IB_LOWER_BOUND),
                getStringOrNull(ibUpperBound, IB_UPPER_BOUND));
    }
}
