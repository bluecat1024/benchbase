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
import com.oltpbenchmark.benchmarks.tpcds.util.row.ShipModeRow;

import static com.oltpbenchmark.benchmarks.tpcds.util.BusinessKeyGenerator.makeBusinessKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.SHIP_MODE;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.ShipModeDistributions.SHIP_MODE_TYPE_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.ShipModeDistributions.getShipModeCarrierAtIndex;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.ShipModeDistributions.getShipModeCodeForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.ShipModeDistributions.getShipModeTypeForIndexModSize;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.ShipModeGeneratorColumn.SM_CONTRACT;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.ShipModeGeneratorColumn.SM_NULLS;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.ALPHA_NUMERIC;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateRandomCharset;

public class ShipModeRowGenerator
        extends AbstractRowGenerator
{
    public ShipModeRowGenerator()
    {
        super(SHIP_MODE);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long nullBitMap = createNullBitMap(SHIP_MODE, getRandomNumberStream(SM_NULLS));
        long smShipModeSk = rowNumber;
        String smShipModeId = makeBusinessKey(rowNumber);

        long index = rowNumber;

        String smType = getShipModeTypeForIndexModSize(rowNumber);
        index /= SHIP_MODE_TYPE_DISTRIBUTION.getSize();

        String smCode = getShipModeCodeForIndexModSize(index);

        String smCarrier = getShipModeCarrierAtIndex((int) (rowNumber) - 1);

        String smContract = generateRandomCharset(ALPHA_NUMERIC, 1, 20, getRandomNumberStream(SM_CONTRACT));

        return new RowGeneratorResult(new ShipModeRow(nullBitMap, smShipModeSk, smShipModeId, smType, smCode, smCarrier, smContract));
    }
}
