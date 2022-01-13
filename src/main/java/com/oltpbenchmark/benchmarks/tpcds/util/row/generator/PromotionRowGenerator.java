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

package com.oltpbenchmark.benchmarks.tpcds.util.row.generator;

import com.oltpbenchmark.benchmarks.tpcds.util.Session;
import com.oltpbenchmark.benchmarks.tpcds.util.Table;
import com.oltpbenchmark.benchmarks.tpcds.util.row.PromotionRow;
import com.oltpbenchmark.benchmarks.tpcds.util.type.Decimal;

import static com.oltpbenchmark.benchmarks.tpcds.util.BusinessKeyGenerator.makeBusinessKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.JoinKeyUtils.generateJoinKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.PROMOTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.EnglishDistributions.SYLLABLES_DISTRIBUTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.PromotionGeneratorColumn.P_CHANNEL_DETAILS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.PromotionGeneratorColumn.P_CHANNEL_DMAIL;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.PromotionGeneratorColumn.P_END_DATE_ID;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.PromotionGeneratorColumn.P_ITEM_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.PromotionGeneratorColumn.P_NULLS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.PromotionGeneratorColumn.P_START_DATE_ID;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateRandomText;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateUniformRandomInt;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateWord;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.JULIAN_DATE_MINIMUM;

public class PromotionRowGenerator
        extends AbstractRowGenerator
{
    private static final int PROMO_START_MIN = -720;
    private static final int PROMO_START_MAX = 100;
    private static final int PROMO_LENGTH_MIN = 1;
    private static final int PROMO_LENGTH_MAX = 60;
    private static final int PROMO_NAME_LENGTH = 5;
    private static final int PROMO_DETAIL_LENGTH_MIN = 20;
    private static final int PROMO_DETAIL_LENGTH_MAX = 60;

    public PromotionRowGenerator()
    {
        super(PROMOTION);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long nullBitMap = createNullBitMap(PROMOTION, getRandomNumberStream(P_NULLS));
        long pPromoSk = rowNumber;
        String pPromoId = makeBusinessKey(rowNumber);
        long pStartDateId = JULIAN_DATE_MINIMUM + generateUniformRandomInt(PROMO_START_MIN, PROMO_START_MAX, getRandomNumberStream(P_START_DATE_ID));
        long pEndDateId = pStartDateId + generateUniformRandomInt(PROMO_LENGTH_MIN, PROMO_LENGTH_MAX, getRandomNumberStream(P_END_DATE_ID));

        long pItemSk = generateJoinKey(P_ITEM_SK, getRandomNumberStream(P_ITEM_SK), Table.ITEM, 1, session.getScaling());

        Decimal pCost = new Decimal(100000, 2);
        int pResponseTarget = 1;
        String pPromoName = generateWord(rowNumber, PROMO_NAME_LENGTH, SYLLABLES_DISTRIBUTION);

        int flags = generateUniformRandomInt(0, 511, getRandomNumberStream(P_CHANNEL_DMAIL));
        boolean pChannelDmail = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pChannelEmail = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pChannelCatalog = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pChannelTv = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pChannelRadio = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pChannelPress = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pChannelEvent = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pChannelDemo = (flags & 0x01) != 0;
        flags <<= 1;

        boolean pDiscountActive = (flags & 0x01) != 0;

        String pChannelDetails = generateRandomText(PROMO_DETAIL_LENGTH_MIN, PROMO_DETAIL_LENGTH_MAX, getRandomNumberStream(P_CHANNEL_DETAILS));

        String pPurpose = "Unknown";

        return new RowGeneratorResult(new PromotionRow(nullBitMap,
                pPromoSk,
                pPromoId,
                pStartDateId,
                pEndDateId,
                pItemSk,
                pCost,
                pResponseTarget,
                pPromoName,
                pChannelDmail,
                pChannelEmail,
                pChannelCatalog,
                pChannelTv,
                pChannelRadio,
                pChannelPress,
                pChannelEvent,
                pChannelDemo,
                pChannelDetails,
                pPurpose,
                pDiscountActive));
    }
}
