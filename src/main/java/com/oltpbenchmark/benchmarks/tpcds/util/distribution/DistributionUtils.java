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

import com.oltpbenchmark.benchmarks.tpcds.util.TpcdsException;
import com.oltpbenchmark.util.RowRandomInt;
import com.oltpbenchmark.util.StringUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static com.oltpbenchmark.benchmarks.tpcds.util.random.TPCDSRandomValueGenerator.generateUniformRandomInt;

public final class DistributionUtils
{
    private DistributionUtils() {}

    protected static final class WeightsBuilder
    {
        List<Integer> weights = new ArrayList<>();
        int previousWeight;

        public WeightsBuilder computeAndAddNextWeight(int weight)
        {
            int newWeight = previousWeight + weight;
            weights.add(newWeight);
            previousWeight = newWeight;
            return this;
        }

        public List<Integer> getWeights()
        {
            return List.copyOf(weights);
        }
    }

    protected static Iterator<List<String>> getDistributionIterator(String filename)
    {        
        String absPath = "/benchmarks/tpcds/distribution/" + filename;
        try (InputStream resource = DistributionUtils.class.getResourceAsStream(absPath)) {
            // get an iterator that iterates over lists of the colon separated values from the distribution files
            Stream<String> lines = new BufferedReader(new InputStreamReader(resource, StandardCharsets.ISO_8859_1)).lines();
            return lines.filter(line -> {
                line = line.trim();
                return !line.isEmpty() && !line.startsWith("--");
            }).map(line -> List.copyOf(StringUtil.splitToList(
                Pattern.compile("(?<!\\\\):"), line))).iterator();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected static List<String> getListFromCommaSeparatedValues(String toSplit)
    {
        List<String> values = StringUtil.splitToList(Pattern.compile("(?<!\\\\),"), toSplit);
        
        return values.stream().map(value -> value.replaceAll("\\\\", "")).collect(Collectors.toList());
    }

    protected static <T> T pickRandomValue(List<T> values, List<Integer> weights, RowRandomInt randomNumberStream)
    {
        int weight = generateUniformRandomInt(1, weights.get(weights.size() - 1), randomNumberStream);
        return getValueForWeight(weight, values, weights);
    }

    private static <T> T getValueForWeight(int weight, List<T> values, List<Integer> weights)
    {
        for (int index = 0; index < weights.size(); index++) {
            if (weight <= weights.get(index)) {
                return values.get(index);
            }
        }

        throw new TpcdsException("random weight was greater than max weight");
    }

    protected static <T> T getValueForIndexModSize(long index, List<T> values)
    {
        int size = values.size();
        int indexModSize = (int) (index % size);
        return values.get(indexModSize);
    }

    protected static int pickRandomIndex(List<Integer> weights, RowRandomInt randomNumberStream)
    {
        int weight = generateUniformRandomInt(1, weights.get(weights.size() - 1), randomNumberStream);
        return getIndexForWeight(weight, weights);
    }

    private static int getIndexForWeight(int weight, List<Integer> weights)
    {
        for (int index = 0; index < weights.size(); index++) {
            if (weight <= weights.get(index)) {
                return index;
            }
        }

        throw new TpcdsException("random weight was greater than max weight");
    }

    protected static int getWeightForIndex(int index, List<Integer> weights)
    {
        return index == 0 ? weights.get(index) : weights.get(index) - weights.get(index - 1);  // reverse the accumulation of weights.
    }
}
