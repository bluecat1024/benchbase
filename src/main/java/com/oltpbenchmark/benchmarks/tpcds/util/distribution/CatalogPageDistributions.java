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

package com.oltpbenchmark.benchmarks.tpcds.util.distribution;

import com.oltpbenchmark.util.RowRandomInt;

import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.StringValuesDistribution.buildStringValuesDistribution;

public final class CatalogPageDistributions
{
    private static final StringValuesDistribution CATALOG_PAGE_TYPES_DISTRIBUTION = buildStringValuesDistribution("catalog_page_types.dst", 1, 2);

    private CatalogPageDistributions() {}

    public static String pickRandomCatalogPageType(RowRandomInt stream)
    {
        // only the second set of weights is ever used for random picking
        return CATALOG_PAGE_TYPES_DISTRIBUTION.pickRandomValue(0, 1, stream);
    }
}
