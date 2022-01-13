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
import com.oltpbenchmark.benchmarks.tpcds.util.type.Decimal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getDistributionIterator;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getListFromCommaSeparatedValues;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Decimal.parseDecimal;
import static java.lang.Integer.parseInt;

public class ItemCurrentPriceDistribution
{
    private static final int NUM_WEIGHT_FIELDS = 4;
    private static final int NUM_VALUE_FIELDS = 3;
    private static final String VALUES_AND_WEIGHTS_FILENAME = "item_current_price.dst";
    private static final ItemCurrentPriceDistribution I_CURRENT_PRICE_DISTRIBUTION = buildICurrentPriceDistribution();

    private final List<Decimal> mins;
    private final List<Decimal> maxes;
    private final List<List<Integer>> weightLists;

    private ItemCurrentPriceDistribution(List<Decimal> mins, List<Decimal> maxes, List<List<Integer>> weightLists)
    {
        this.mins = mins;
        this.maxes = maxes;
        this.weightLists = weightLists;
    }

    private static ItemCurrentPriceDistribution buildICurrentPriceDistribution()
    {
        List<Decimal> minsBuilder = new ArrayList<>();
        List<Decimal> maxesBuilder = new ArrayList<>();

        List<WeightsBuilder> weightsBuilders = new ArrayList<>(NUM_WEIGHT_FIELDS);
        for (int i = 0; i < NUM_WEIGHT_FIELDS; i++) {
            weightsBuilders.add(new WeightsBuilder());
        }

        Iterator<List<String>> iterator = getDistributionIterator(VALUES_AND_WEIGHTS_FILENAME);
        while (iterator.hasNext()) {
            List<String> fields = iterator.next();

            List<String> values = getListFromCommaSeparatedValues(fields.get(0));

            // indices are never used
            minsBuilder.add(parseDecimal(values.get(1)));
            maxesBuilder.add(parseDecimal(values.get(2)));

            List<String> weights = getListFromCommaSeparatedValues(fields.get(1));
            for (int i = 0; i < weights.size(); i++) {
                weightsBuilders.get(i).computeAndAddNextWeight(parseInt(weights.get(i)));
            }
        }

        List<List<Integer>> weightsListBuilder = new ArrayList<>();
        for (WeightsBuilder weightsBuilder : weightsBuilders) {
            weightsListBuilder.add(weightsBuilder.getWeights());
        }

        return new ItemCurrentPriceDistribution(minsBuilder,
                maxesBuilder,
                weightsListBuilder);
    }

    public static List<Decimal> pickRandomCurrentPriceRange(RowRandomInt randomNumberStream)
    {
        int index = DistributionUtils.pickRandomIndex(I_CURRENT_PRICE_DISTRIBUTION.weightLists.get(0), randomNumberStream);
        return List.of(I_CURRENT_PRICE_DISTRIBUTION.mins.get(index), I_CURRENT_PRICE_DISTRIBUTION.maxes.get(index));
    }
}
