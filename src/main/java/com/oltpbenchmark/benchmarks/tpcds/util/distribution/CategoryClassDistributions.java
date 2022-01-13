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

import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.CategoryClassDistributions.CategoryClassDistribution.buildCategoryClassDistribution;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getDistributionIterator;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.DistributionUtils.getListFromCommaSeparatedValues;
import static java.lang.Integer.parseInt;

public final class CategoryClassDistributions
{
    private static final List<CategoryClassDistribution> CATEGORY_CLASS_DISTRIBUTIONS =
            List.of(buildCategoryClassDistribution("women_class.dst"),
                    buildCategoryClassDistribution("men_class.dst"),
                    buildCategoryClassDistribution("children_class.dst"),
                    buildCategoryClassDistribution("shoe_class.dst"),
                    buildCategoryClassDistribution("music_class.dst"),
                    buildCategoryClassDistribution("jewelry_class.dst"),
                    buildCategoryClassDistribution("home_class.dst"),
                    buildCategoryClassDistribution("sport_class.dst"),
                    buildCategoryClassDistribution("book_class.dst"),
                    buildCategoryClassDistribution("electronic_class.dst"));

    public static CategoryClass pickRandomCategoryClass(int categoryId, RowRandomInt stream)
    {
        return CATEGORY_CLASS_DISTRIBUTIONS.get(categoryId).pickRandomCategoryClass(stream);
    }

    private CategoryClassDistributions() {}

    public static class CategoryClassDistribution
    {
        private final List<String> names;
        private final List<Integer> brandCounts;
        private final List<Integer> weights;

        public CategoryClassDistribution(List<String> names, List<Integer> brandCounts, List<Integer> weights)
        {
            this.names = names;
            this.brandCounts = brandCounts;
            this.weights = weights;
        }

        public static CategoryClassDistribution buildCategoryClassDistribution(String filename)
        {
            List<String> namesBuilder = new ArrayList<>();
            List<Integer> brandCountsBuilder = new ArrayList<>();
            WeightsBuilder weightsBuilder = new WeightsBuilder();

            Iterator<List<String>> iterator = getDistributionIterator(filename);
            while (iterator.hasNext()) {
                List<String> fields = iterator.next();

                List<String> values = getListFromCommaSeparatedValues(fields.get(0));

                namesBuilder.add(values.get(0));
                brandCountsBuilder.add(parseInt(values.get(1)));

                List<String> weights = getListFromCommaSeparatedValues(fields.get(1));
                weightsBuilder.computeAndAddNextWeight(parseInt(weights.get(0)));
            }

            return new CategoryClassDistribution(namesBuilder,
                    brandCountsBuilder,
                    weightsBuilder.getWeights());
        }

        public CategoryClass pickRandomCategoryClass(RowRandomInt stream)
        {
            int index = DistributionUtils.pickRandomIndex(weights, stream);
            return new CategoryClass(index + 1, names.get(index), brandCounts.get(index));
        }
    }

    public static class CategoryClass
    {
        private final int id;
        private final String name;
        private final int brandCount;

        public CategoryClass(int id, String name, int brandCount)
        {
            this.id = id;
            this.name = name;
            this.brandCount = brandCount;
        }

        public int getBrandCount()
        {
            return brandCount;
        }

        public String getName()
        {
            return name;
        }

        public long getId()
        {
            return id;
        }
    }
}
