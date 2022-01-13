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

import com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.WeightsBuilder;
import com.oltpbenchmark.util.RowRandomInt;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getDistributionIterator;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getListFromCommaSeparatedValues;
import static java.lang.Integer.parseInt;

public class CategoriesDistribution
{
    private static final int NUM_WEIGHT_FIELDS = 1;
    private static final String VALUES_AND_WEIGHTS_FILENAME = "categories.dst";
    private static final CategoriesDistribution CATEGORIES_DISTRIBUTION = buildCategoriesDistribution();

    private final List<String> names;
    private final List<Integer> hasSizes;
    private final List<Integer> weights;

    private CategoriesDistribution(List<String> names, List<Integer> hasSizes, List<Integer> weights)
    {
        this.names = names;
        this.hasSizes = hasSizes;
        this.weights = weights;
    }

    private static CategoriesDistribution buildCategoriesDistribution()
    {
        List<String> namesBuilder = new ArrayList<>();
        List<Integer> hasSizesBuilder = new ArrayList<>();
        WeightsBuilder weightsBuilder = new WeightsBuilder();

        Iterator<List<String>> iterator = getDistributionIterator(VALUES_AND_WEIGHTS_FILENAME);
        while (iterator.hasNext()) {
            List<String> fields = iterator.next();

            List<String> values = getListFromCommaSeparatedValues(fields.get(0));

            namesBuilder.add(values.get(0));
            // we don't add the class distribution names because they are unused
            hasSizesBuilder.add(parseInt(values.get(2)));

            List<String> weights = getListFromCommaSeparatedValues(fields.get(1));
            weightsBuilder.computeAndAddNextWeight(parseInt(weights.get(0)));
        }

        return new CategoriesDistribution(namesBuilder,
                hasSizesBuilder,
                weightsBuilder.getWeights());
    }

    public static Integer pickRandomIndex(RowRandomInt stream)
    {
        return DistributionUtils.pickRandomIndex(CATEGORIES_DISTRIBUTION.weights, stream);
    }

    public static String getCategoryAtIndex(int index)
    {
        return CATEGORIES_DISTRIBUTION.names.get(index);
    }

    public static int getHasSizeAtIndex(int index)
    {
        return CATEGORIES_DISTRIBUTION.hasSizes.get(index);
    }
}
