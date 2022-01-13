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

import java.util.Objects;
import java.util.Optional;

// This class was derived from the TpchColumnType class in the following repo
// https://github.com/airlift/tpch. The license for that class can be found here
// https://www.apache.org/licenses/LICENSE-2.0.
public class ColumnType
{
    public enum Base
    {
        INTEGER,
        IDENTIFIER,
        DATE,
        DECIMAL,
        VARCHAR,
        CHAR,
        TIME
    }

    private final Base base;
    private final Optional<Integer> precision;
    private final Optional<Integer> scale;

    private ColumnType(Base base, Optional<Integer> precision, Optional<Integer> scale)
    {
        this.base = base;
        this.precision = precision;
        this.scale = scale;
    }

    ColumnType(Base base, int precision, int scale)
    {
        this(base, Optional.of(precision), Optional.of(scale));
    }

    ColumnType(Base base, int precision)
    {
        this(base, Optional.of(precision), Optional.empty());
    }

    ColumnType(Base base)
    {
        this(base, Optional.empty(), Optional.empty());
    }

    public Base getBase()
    {
        return base;
    }

    public Optional<Integer> getPrecision()
    {
        return precision;
    }

    public Optional<Integer> getScale()
    {
        return scale;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColumnType that = (ColumnType) o;
        return Objects.equals(base, that.base) &&
                Objects.equals(precision, that.precision) &&
                Objects.equals(scale, that.scale);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(base, precision, scale);
    }
}
