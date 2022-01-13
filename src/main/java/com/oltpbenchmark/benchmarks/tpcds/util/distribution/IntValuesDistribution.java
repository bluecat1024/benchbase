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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getDistributionIterator;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getListFromCommaSeparatedValues;
import static java.lang.Integer.parseInt;

public class IntValuesDistribution
{
    private final List<List<Integer>> valuesLists;
    private final List<List<Integer>> weightsLists;

    public IntValuesDistribution(List<List<Integer>> valuesLists, List<List<Integer>> weightsLists)
    {
        this.valuesLists = valuesLists;
        this.weightsLists = weightsLists;
    }

    public static IntValuesDistribution buildIntValuesDistribution(String valuesAndWeightsFilename, int numValueFields, int numWeightFields)
    {
        Iterator<List<String>> iterator = getDistributionIterator(valuesAndWeightsFilename);

        List<List<Integer>> valuesBuilders = new ArrayList<>(numValueFields);
        for (int i = 0; i < numValueFields; i++) {
            valuesBuilders.add(new ArrayList<Integer>());
        }

        List<WeightsBuilder> weightsBuilders = new ArrayList<>(numWeightFields);
        for (int i = 0; i < numWeightFields; i++) {
            weightsBuilders.add(new WeightsBuilder());
        }

        while (iterator.hasNext()) {
            List<String> fields = iterator.next();

            List<String> values = getListFromCommaSeparatedValues(fields.get(0));
            for (int i = 0; i < values.size(); i++) {
                valuesBuilders.get(i).add(parseInt(values.get(i)));
            }

            List<String> weights = getListFromCommaSeparatedValues(fields.get(1));
            for (int i = 0; i < weights.size(); i++) {
                weightsBuilders.get(i).computeAndAddNextWeight(parseInt(weights.get(i)));
            }
        }

        List<List<Integer>> valuesListsBuilder = new ArrayList<>();
        for (List<Integer> valuesBuilder : valuesBuilders) {
            valuesListsBuilder.add(valuesBuilder);
        }
        List<List<Integer>> valuesLists = valuesListsBuilder;

        List<List<Integer>> weightsListBuilder = new ArrayList<>();
        for (WeightsBuilder weightsBuilder : weightsBuilders) {
            weightsListBuilder.add(weightsBuilder.getWeights());
        }
        List<List<Integer>> weightsLists = weightsListBuilder;
        return new IntValuesDistribution(valuesLists, weightsLists);
    }

    public Integer getValueForIndexModSize(long index, int valueListIndex)
    {
        return DistributionUtils.getValueForIndexModSize(index, valuesLists.get(valueListIndex));
    }

    public Integer getValueAtIndex(int valueListIndex, int valueIndex)
    {
        return valuesLists.get(valueListIndex).get(valueIndex);
    }

    public int pickRandomIndex(int weightListIndex, RowRandomInt stream)
    {
        return DistributionUtils.pickRandomIndex(weightsLists.get(weightListIndex), stream);
    }

    public int getSize()
    {
        return valuesLists.get(0).size();
    }
}
