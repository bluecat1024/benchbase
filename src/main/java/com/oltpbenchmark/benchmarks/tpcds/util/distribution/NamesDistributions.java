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

public final class NamesDistributions
{
    private static final StringValuesDistribution FIRST_NAMES_DISTRIBUTION = buildStringValuesDistribution("first_names.dst", 1, 3);
    private static final StringValuesDistribution LAST_NAMES_DISTRIBUTION = buildStringValuesDistribution("last_names.dst", 1, 1);
    private static final StringValuesDistribution SALUTATIONS_DISTRIBUTION = buildStringValuesDistribution("salutations.dst", 1, 3);

    private NamesDistributions() {}

    public static String pickRandomFirstName(FirstNamesWeights firstNamesWeights, RowRandomInt stream)
    {
        return FIRST_NAMES_DISTRIBUTION.pickRandomValue(0, firstNamesWeights.ordinal(), stream);
    }

    public static int pickRandomIndex(FirstNamesWeights firstNamesWeights, RowRandomInt stream)
    {
        return FIRST_NAMES_DISTRIBUTION.pickRandomIndex(firstNamesWeights.ordinal(), stream);
    }

    public static String getFirstNameFromIndex(int index)
    {
        return FIRST_NAMES_DISTRIBUTION.getValueAtIndex(0, index);
    }

    public static int getWeightForIndex(int index, FirstNamesWeights firstNamesWeights)
    {
        return FIRST_NAMES_DISTRIBUTION.getWeightForIndex(index, firstNamesWeights.ordinal());
    }

    public static String pickRandomLastName(RowRandomInt stream)
    {
        return LAST_NAMES_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomSalutation(SalutationsWeights salutationsWeights, RowRandomInt stream)
    {
        return SALUTATIONS_DISTRIBUTION.pickRandomValue(0, salutationsWeights.ordinal(), stream);
    }

    public enum FirstNamesWeights
    {
        MALE_FREQUENCY,
        FEMALE_FREQUENCY,
        GENERAL_FREQUENCY
    }

    public enum SalutationsWeights
    {
        GENDER_NEUTRAL,
        MALE,
        FEMALE
    }
}
