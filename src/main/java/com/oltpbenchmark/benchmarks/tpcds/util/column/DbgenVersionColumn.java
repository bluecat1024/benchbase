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

import static com.oltpbenchmark.benchmarks.tpcds.util.Table.DBGEN_VERSION;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.DATE;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.TIME;
import static com.oltpbenchmark.benchmarks.tpcds.util.column.ColumnTypes.varchar;

public enum DbgenVersionColumn
        implements Column
{
    DV_VERSION(varchar(16)),
    DV_CREATE_DATE(DATE),
    DV_CREATE_TIME(TIME),
    DV_CMDLINE_ARGS(varchar(200));

    private final ColumnType type;

    DbgenVersionColumn(ColumnType type)
    {
        this.type = type;
    }

    @Override
    public Table getTable()
    {
        return DBGEN_VERSION;
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
