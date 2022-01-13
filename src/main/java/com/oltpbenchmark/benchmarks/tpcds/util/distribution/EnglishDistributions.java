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

public final class EnglishDistributions
{
    private static final StringValuesDistribution ADJECTIVES_DISTRIBUTION = buildStringValuesDistribution("adjectives.dst", 1, 1);
    private static final StringValuesDistribution ADVERBS_DISTRIBUTION = buildStringValuesDistribution("adverbs.dst", 1, 1);
    private static final StringValuesDistribution ARTICLES_DISTRIBUTION = buildStringValuesDistribution("articles.dst", 1, 1);
    private static final StringValuesDistribution AUXILIARIES_DISTRIBUTION = buildStringValuesDistribution("auxiliaries.dst", 1, 1);
    private static final StringValuesDistribution PREPOSITIONS_DISTRIBUTION = buildStringValuesDistribution("prepositions.dst", 1, 1);
    private static final StringValuesDistribution NOUNS_DISTRIBUTION = buildStringValuesDistribution("nouns.dst", 1, 1);
    private static final StringValuesDistribution SENTENCES_DISTRIBUTION = buildStringValuesDistribution("sentences.dst", 1, 1);
    public static final StringValuesDistribution SYLLABLES_DISTRIBUTION = buildStringValuesDistribution("syllables.dst", 1, 1);
    private static final StringValuesDistribution TERMINATORS_DISTRIBUTION = buildStringValuesDistribution("terminators.dst", 1, 1);
    private static final StringValuesDistribution VERBS_DISTRIBUTION = buildStringValuesDistribution("verbs.dst", 1, 1);

    private EnglishDistributions() {}

    public static String pickRandomAdjective(RowRandomInt stream)
    {
        return ADJECTIVES_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomAdverb(RowRandomInt stream)
    {
        return ADVERBS_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomArticle(RowRandomInt stream)
    {
        return ARTICLES_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomAuxiliary(RowRandomInt stream)
    {
        return AUXILIARIES_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomNoun(RowRandomInt stream)
    {
        return NOUNS_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomPreposition(RowRandomInt stream)
    {
        return PREPOSITIONS_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomSentence(RowRandomInt stream)
    {
        return SENTENCES_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomTerminator(RowRandomInt stream)
    {
        return TERMINATORS_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }

    public static String pickRandomVerb(RowRandomInt stream)
    {
        return VERBS_DISTRIBUTION.pickRandomValue(0, 0, stream);
    }
}
