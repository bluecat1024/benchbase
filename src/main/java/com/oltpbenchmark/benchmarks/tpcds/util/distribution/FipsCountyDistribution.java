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

public class FipsCountyDistribution
{
    private static final FipsCountyDistribution FIPS_COUNTY_DISTRIBUTION = buildFipsCountyDistribution();
    private static final String VALUES_AND_WEIGHTS_FILENAME = "fips.dst";
    private static final int NUM_WEIGHT_FIELDS = 6;

    private final List<String> counties;
    private final List<String> stateAbbreviations;
    private final List<Integer> zipPrefixes;
    private final List<Integer> gmtOffsets;
    private final List<List<Integer>> weightsLists;

    public FipsCountyDistribution(List<String> counties,
            List<String> stateAbbreviations,
            List<Integer> zipPrefixes,
            List<Integer> gmtOffsets,
            List<List<Integer>> weightsLists)
    {
        this.counties = counties;
        this.stateAbbreviations = stateAbbreviations;
        this.zipPrefixes = zipPrefixes;
        this.gmtOffsets = gmtOffsets;
        this.weightsLists = weightsLists;
    }

    public static FipsCountyDistribution buildFipsCountyDistribution()
    {
        List<String> countiesBuilder = new ArrayList<>();
        List<String> stateAbbreviationsBuilder = new ArrayList<>();
        List<Integer> zipPrefixesBuilder = new ArrayList<>();
        List<Integer> gmtOffsetsBuilder = new ArrayList<>();

        List<WeightsBuilder> weightsBuilders = new ArrayList<>(NUM_WEIGHT_FIELDS);
        for (int i = 0; i < NUM_WEIGHT_FIELDS; i++) {
            weightsBuilders.add(new WeightsBuilder());
        }

        Iterator<List<String>> iterator = getDistributionIterator(VALUES_AND_WEIGHTS_FILENAME);
        while (iterator.hasNext()) {
            List<String> fields = iterator.next();

            List<String> values = getListFromCommaSeparatedValues(fields.get(0));

            // fips codes and state names are never used, so we leave them out
            countiesBuilder.add(values.get(1));
            stateAbbreviationsBuilder.add(values.get(2));
            zipPrefixesBuilder.add(Integer.parseInt(values.get(4)));
            gmtOffsetsBuilder.add(Integer.parseInt(values.get(5)));

            List<String> weights = getListFromCommaSeparatedValues(fields.get(1));
            for (int i = 0; i < weights.size(); i++) {
                weightsBuilders.get(i).computeAndAddNextWeight(Integer.valueOf(weights.get(i)));
            }
        }

        List<List<Integer>> weightsListBuilder = new ArrayList<>();
        for (WeightsBuilder weightsBuilder : weightsBuilders) {
            weightsListBuilder.add(weightsBuilder.getWeights());
        }

        return new FipsCountyDistribution(countiesBuilder,
                stateAbbreviationsBuilder,
                zipPrefixesBuilder,
                gmtOffsetsBuilder,
                weightsListBuilder);
    }

    public static int pickRandomIndex(FipsWeights weights, RowRandomInt stream)
    {
        return DistributionUtils.pickRandomIndex(FIPS_COUNTY_DISTRIBUTION.weightsLists.get(weights.ordinal()), stream);
    }

    public static String getCountyAtIndex(int index)
    {
        return FIPS_COUNTY_DISTRIBUTION.counties.get(index);
    }

    public static String getStateAbbreviationAtIndex(int index)
    {
        return FIPS_COUNTY_DISTRIBUTION.stateAbbreviations.get(index);
    }

    public static int getZipPrefixAtIndex(int index)
    {
        return FIPS_COUNTY_DISTRIBUTION.zipPrefixes.get(index);
    }

    public static int getGmtOffsetAtIndex(int index)
    {
        return FIPS_COUNTY_DISTRIBUTION.gmtOffsets.get(index);
    }

    public enum FipsWeights
    {
        UNIFORM,
        POPULATION,
        TIMEZONE,
        IN_ZONE_1,
        IN_ZONE_2,
        IN_ZONE_3
    }
}
