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
package com.oltpbenchmark.benchmarks.tpcds.util.column;

import com.oltpbenchmark.benchmarks.tpcds.util.Table;

import static com.oltpbenchmark.benchmarks.tpcds.util.Table.SHIP_MODE;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.IDENTIFIER;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.character;

public enum ShipModeColumn
        implements Column
{
    SM_SHIP_MODE_SK(IDENTIFIER),
    SM_SHIP_MODE_ID(character(16)),
    SM_TYPE(character(30)),
    SM_CODE(character(10)),
    SM_CARRIER(character(20)),
    SM_CONTRACT(character(20));

    private final ColumnType type;

    ShipModeColumn(ColumnType type)
    {
        this.type = type;
    }

    @Override
    public Table getTable()
    {
        return SHIP_MODE;
    }

    @Override
    public String getName()
    {
        return name().toLowerCase();
    }

    @Override
    public ColumnType getType()
    {
        return type;
    }

    @Override
    public int getPosition()
    {
        return ordinal();
    }
}
