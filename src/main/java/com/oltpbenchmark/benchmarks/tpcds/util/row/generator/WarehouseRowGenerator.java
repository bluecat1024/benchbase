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
import com.oltpbenchmark.benchmarks.tpcds.util.row.WarehouseRow;
import com.oltpbenchmark.benchmarks.tpcds.util.type.Address;

import static com.oltpbenchmark.benchmarks.tpcds.util.BusinessKeyGenerator.makeBusinessKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.WAREHOUSE;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WarehouseGeneratorColumn.W_NULLS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WarehouseGeneratorColumn.W_WAREHOUSE_ADDRESS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WarehouseGeneratorColumn.W_WAREHOUSE_NAME;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WarehouseGeneratorColumn.W_WAREHOUSE_SQ_FT;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateRandomText;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateUniformRandomInt;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Address.makeAddressForColumn;

public class WarehouseRowGenerator
        extends AbstractRowGenerator
{
    public WarehouseRowGenerator()
    {
        super(WAREHOUSE);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long nullBitMap = createNullBitMap(WAREHOUSE, getRandomNumberStream(W_NULLS));
        long wWarehouseSk = rowNumber;
        String wWarehouseId = makeBusinessKey(rowNumber);
        String wWarehouseName = generateRandomText(10, 20, getRandomNumberStream(W_WAREHOUSE_NAME));
        int wWarehouseSqFt = generateUniformRandomInt(50000, 1000000, getRandomNumberStream(W_WAREHOUSE_SQ_FT));
        Address wAddress = makeAddressForColumn(WAREHOUSE, getRandomNumberStream(W_WAREHOUSE_ADDRESS), session.getScaling());

        return new RowGeneratorResult(new WarehouseRow(nullBitMap, wWarehouseSk, wWarehouseId, wWarehouseName, wWarehouseSqFt, wAddress));
    }
}
