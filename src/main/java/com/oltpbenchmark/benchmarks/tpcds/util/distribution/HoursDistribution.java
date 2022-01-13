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
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.pickRandomValue;
import static java.lang.Integer.parseInt;

public class HoursDistribution
{
    private static final int NUM_WEIGHT_FIELDS = Weights.values().length;
    private static final String VALUES_AND_WEIGHTS_FILENAME = "hours.dst";
    private static final HoursDistribution HOURS_DISTRIBUTION = buildHoursDistribution();

    private final List<Integer> hours;
    private final List<String> amPm;
    private final List<String> shifts;
    private final List<String> subShifts;
    private final List<String> meals;
    private final List<List<Integer>> weightsLists;

    public HoursDistribution(List<Integer> hours, List<String> amPm, List<String> shifts, List<String> subShifts, List<String> meals, List<List<Integer>> weightsLists)
    {
        this.hours = hours;
        this.amPm = amPm;
        this.shifts = shifts;
        this.subShifts = subShifts;
        this.meals = meals;
        this.weightsLists = weightsLists;
    }

    private static HoursDistribution buildHoursDistribution()
    {
        List<Integer> hoursBuilder = new ArrayList<>();
        List<String> amPmBuilder = new ArrayList<>();
        List<String> shiftsBuilder = new ArrayList<>();
        List<String> subShiftsBuilder = new ArrayList<>();
        List<String> mealsBuilder = new ArrayList<>();

        List<WeightsBuilder> weightsBuilders = new ArrayList<>(NUM_WEIGHT_FIELDS);
        for (int i = 0; i < NUM_WEIGHT_FIELDS; i++) {
            weightsBuilders.add(new WeightsBuilder());
        }

        Iterator<List<String>> iterator = getDistributionIterator(VALUES_AND_WEIGHTS_FILENAME);
        while (iterator.hasNext()) {
            List<String> fields = iterator.next();

            List<String> values = getListFromCommaSeparatedValues(fields.get(0));
            hoursBuilder.add(parseInt(values.get(0)));
            amPmBuilder.add(values.get(1));
            shiftsBuilder.add(values.get(2));
            subShiftsBuilder.add(values.get(3));
            mealsBuilder.add(values.get(4));

            List<String> weights = getListFromCommaSeparatedValues(fields.get(1));
            for (int i = 0; i < weights.size(); i++) {
                weightsBuilders.get(i).computeAndAddNextWeight(Integer.valueOf(weights.get(i)));
            }
        }

        List<List<Integer>> weightsListBuilder = new ArrayList<>();
        for (WeightsBuilder weightsBuilder : weightsBuilders) {
            weightsListBuilder.add(weightsBuilder.getWeights());
        }

        return new HoursDistribution(hoursBuilder, amPmBuilder, shiftsBuilder, subShiftsBuilder, mealsBuilder, weightsListBuilder);
    }

    public static int pickRandomHour(Weights weights, RowRandomInt randomNumberStream)
    {
        return pickRandomValue(HOURS_DISTRIBUTION.hours, HOURS_DISTRIBUTION.weightsLists.get(weights.ordinal()), randomNumberStream);
    }

    public static HourInfo getHourInfoForHour(int hour)
    {
        return new HourInfo(HOURS_DISTRIBUTION.amPm.get(hour), HOURS_DISTRIBUTION.shifts.get(hour), HOURS_DISTRIBUTION.subShifts.get(hour), HOURS_DISTRIBUTION.meals.get(hour));
    }

    public static class HourInfo
    {
        private final String amPm;
        private final String shift;
        private final String subShift;
        private final String meal;

        public HourInfo(String amPm, String shift, String subShift, String meal)
        {
            this.amPm = amPm;
            this.shift = shift;
            this.subShift = subShift;
            this.meal = meal;
        }

        public String getAmPm()
        {
            return amPm;
        }

        public String getShift()
        {
            return shift;
        }

        public String getSubShift()
        {
            return subShift;
        }

        public String getMeal()
        {
            return meal;
        }
    }

    public enum Weights
    {
        UNIFORM,
        STORE,
        CATALOG_AND_WEB
    }
}
