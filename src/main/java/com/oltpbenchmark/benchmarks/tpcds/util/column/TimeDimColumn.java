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

import static com.oltpbenchmark.benchmarks.tpcds.util.Table.TIME_DIM;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.IDENTIFIER;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.INTEGER;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.character;

public enum TimeDimColumn
        implements Column
{
    T_TIME_SK(IDENTIFIER),
    T_TIME_ID(character(16)),
    T_TIME(INTEGER),
    T_HOUR(INTEGER),
    T_MINUTE(INTEGER),
    T_SECOND(INTEGER),
    T_AM_PM(character(2)),
    T_SHIFT(character(20)),
    T_SUB_SHIFT(character(20)),
    T_MEAL_TIME(character(20));

    private final ColumnType type;

    TimeDimColumn(ColumnType type)
    {
        this.type = type;
    }

    @Override
    public Table getTable()
    {
        return TIME_DIM;
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
