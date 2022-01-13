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

import com.oltpbenchmark.benchmarks.tpcds.util.Scaling;
import com.oltpbenchmark.benchmarks.tpcds.util.Session;
import com.oltpbenchmark.benchmarks.tpcds.util.SlowlyChangingDimensionUtils.SlowlyChangingDimensionKey;
import com.oltpbenchmark.benchmarks.tpcds.util.row.WebPageRow;

import java.util.Optional;

import static com.oltpbenchmark.benchmarks.tpcds.util.JoinKeyUtils.generateJoinKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.SlowlyChangingDimensionUtils.computeScdKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.SlowlyChangingDimensionUtils.getValueForSlowlyChangingDimension;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.DATE_DIM;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.WEB_PAGE;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.WebPageUseDistribution.pickRandomWebPageUseType;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_ACCESS_DATE_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_AUTOGEN_FLAG;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_CHAR_COUNT;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_CREATION_DATE_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_CUSTOMER_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_IMAGE_COUNT;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_LINK_COUNT;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_MAX_AD_COUNT;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_NULLS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_SCD;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_TYPE;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.WebPageGeneratorColumn.WP_URL;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateRandomUrl;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateUniformRandomInt;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.JULIAN_TODAYS_DATE;

public class WebPageRowGenerator
        extends AbstractRowGenerator
{
    private static final int WP_AUTOGEN_PERCENT = 30;
    private Optional<WebPageRow> previousRow = Optional.empty();

    public WebPageRowGenerator()
    {
        super(WEB_PAGE);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        Scaling scaling = session.getScaling();

        long nullBitMap = createNullBitMap(WEB_PAGE, getRandomNumberStream(WP_NULLS));
        long wpPageSk = rowNumber;

        SlowlyChangingDimensionKey slowlyChangingDimensionKey = computeScdKey(WEB_PAGE, rowNumber);
        String wpPageId = slowlyChangingDimensionKey.getBusinessKey();
        long wpRecStartDateId = slowlyChangingDimensionKey.getStartDate();
        long wpRecStartEndDateId = slowlyChangingDimensionKey.getEndDate();
        boolean isNewKey = slowlyChangingDimensionKey.isNewBusinessKey();
        int fieldChangeFlags = (int) getRandomNumberStream(WP_SCD).nextRandom();

        long wpCreationDateSk = generateJoinKey(WP_CREATION_DATE_SK, getRandomNumberStream(WP_CREATION_DATE_SK), DATE_DIM, rowNumber, scaling);
        if (previousRow.isPresent()) {
            wpCreationDateSk = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpCreationDateSk(), wpCreationDateSk);
        }
        fieldChangeFlags >>= 1;

        int lastAccess = generateUniformRandomInt(0, 100, getRandomNumberStream(WP_ACCESS_DATE_SK));
        long wpAccessDateSk = JULIAN_TODAYS_DATE - lastAccess;
        if (previousRow.isPresent()) {
            wpAccessDateSk = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpAccessDateSk(), wpAccessDateSk);
        }
        fieldChangeFlags >>= 1;

        int randomInt = generateUniformRandomInt(0, 99, getRandomNumberStream(WP_AUTOGEN_FLAG));
        boolean wpAutogenFlag = randomInt < WP_AUTOGEN_PERCENT;
        if (previousRow.isPresent()) {
            wpAutogenFlag = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpAutogenFlag(), wpAutogenFlag);
        }
        fieldChangeFlags >>= 1;

        long wpCustomersk = generateJoinKey(WP_CUSTOMER_SK, getRandomNumberStream(WP_CUSTOMER_SK), CUSTOMER, 1, scaling);
        if (previousRow.isPresent()) {
            wpCustomersk = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpCustomerSk(), wpCustomersk);
        }
        fieldChangeFlags >>= 1;

        String wpUrl = generateRandomUrl(getRandomNumberStream(WP_URL)); // this actually returns the same value every time, so no need to check if it should change
        fieldChangeFlags >>= 1;

        String wpType = pickRandomWebPageUseType(getRandomNumberStream(WP_TYPE));  // always uses a new value due to a bug in the C code
        fieldChangeFlags >>= 1;

        int wpLinkCount = generateUniformRandomInt(2, 25, getRandomNumberStream(WP_LINK_COUNT));
        if (previousRow.isPresent()) {
            wpLinkCount = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpLinkCount(), wpLinkCount);
        }
        fieldChangeFlags >>= 1;

        int wpImageCount = generateUniformRandomInt(1, 7, getRandomNumberStream(WP_IMAGE_COUNT));
        if (previousRow.isPresent()) {
            wpImageCount = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpImageCount(), wpImageCount);
        }
        fieldChangeFlags >>= 1;

        int wpMaxAdCount = generateUniformRandomInt(0, 4, getRandomNumberStream(WP_MAX_AD_COUNT));
        if (previousRow.isPresent()) {
            wpMaxAdCount = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpMaxAdCount(), wpMaxAdCount);
        }
        fieldChangeFlags >>= 1;

        int wpCharCount = generateUniformRandomInt(wpLinkCount * 125 + wpImageCount * 50,
                wpLinkCount * 300 + wpImageCount * 150,
                getRandomNumberStream(WP_CHAR_COUNT));
        if (previousRow.isPresent()) {
            wpCharCount = getValueForSlowlyChangingDimension(fieldChangeFlags, isNewKey, previousRow.get().getWpCharCount(), wpCharCount);
        }

        previousRow = Optional.of(new WebPageRow(nullBitMap,
                wpPageSk,
                wpPageId,
                wpRecStartDateId,
                wpRecStartEndDateId,
                wpCreationDateSk,
                wpAccessDateSk,
                wpAutogenFlag,
                wpCustomersk,
                wpUrl,
                wpType,
                wpCharCount,
                wpLinkCount,
                wpImageCount,
                wpMaxAdCount));

        return new RowGeneratorResult(new WebPageRow(nullBitMap,
                wpPageSk,
                wpPageId,
                wpRecStartDateId,
                wpRecStartEndDateId,
                wpCreationDateSk,
                wpAccessDateSk,
                wpAutogenFlag,
                wpAutogenFlag ? wpCustomersk : -1,
                wpUrl,
                wpType,
                wpCharCount,
                wpLinkCount,
                wpImageCount,
                wpMaxAdCount));
    }
}
