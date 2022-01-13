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
import com.oltpbenchmark.benchmarks.tpcds.util.row.StoreSalesRow;
import com.oltpbenchmark.benchmarks.tpcds.util.row.TableRow;
import com.oltpbenchmark.benchmarks.tpcds.util.type.Pricing;

import java.util.ArrayList;
import java.util.List;

import static com.oltpbenchmark.benchmarks.tpcds.util.JoinKeyUtils.generateJoinKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Permutations.getPermutationEntry;
import static com.oltpbenchmark.benchmarks.tpcds.util.Permutations.makePermutation;
import static com.oltpbenchmark.benchmarks.tpcds.util.SlowlyChangingDimensionUtils.matchSurrogateKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER_ADDRESS;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CUSTOMER_DEMOGRAPHICS;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.DATE_DIM;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.HOUSEHOLD_DEMOGRAPHICS;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.ITEM;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.PROMOTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.STORE;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.STORE_SALES;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.TIME_DIM;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SR_IS_RETURNED;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_NULLS;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_PERMUTATION;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_PRICING;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_ADDR_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_CDEMO_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_CUSTOMER_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_DATE_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_HDEMO_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_ITEM_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_PROMO_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_STORE_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_SOLD_TIME_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.StoreSalesGeneratorColumn.SS_TICKET_NUMBER;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateUniformRandomInt;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Pricing.generatePricingForSalesTable;

public class StoreSalesRowGenerator
        extends AbstractRowGenerator
{
    private static final int SR_RETURN_PCT = 10;

    private int[] itemPermutation;

    // Note: the following two variables are present in the C generator but unused in
    // a meaningful way. We include them for completeness not to confuse
    // future readers.
    // private long nextDateIndex;
    // private long julianDate;
    private int remainingLineItems;
    private OrderInfo orderInfo = new OrderInfo();
    private int itemIndex;

    public StoreSalesRowGenerator()
    {
        super(STORE_SALES);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        int itemCount = (int) session.getScaling().getIdCount(ITEM);
        if (itemPermutation == null) {
            itemPermutation = makePermutation(itemCount, getRandomNumberStream(SS_PERMUTATION));
        }

        Scaling scaling = session.getScaling();
        if (remainingLineItems == 0) {
            orderInfo = generateOrderInfo(rowNumber, session);
            remainingLineItems = generateUniformRandomInt(8, 16, getRandomNumberStream(SS_TICKET_NUMBER));
            itemIndex = generateUniformRandomInt(1, (int) scaling.getIdCount(ITEM), getRandomNumberStream(SS_SOLD_ITEM_SK));
        }

        long nullBitMap = createNullBitMap(STORE_SALES, getRandomNumberStream(SS_NULLS));

        //items need to be unique within an order
        // use a sequence within the permutation
        if (++itemIndex > itemCount) {
            itemIndex = 1;
        }

        long ssSoldItemSk = matchSurrogateKey(getPermutationEntry(itemPermutation, itemIndex), orderInfo.getSsSoldDateSk(), ITEM, scaling);
        long ssSoldPromoSk = generateJoinKey(SS_SOLD_PROMO_SK, getRandomNumberStream(SS_SOLD_PROMO_SK), PROMOTION, 1, scaling);
        Pricing ssPricing = generatePricingForSalesTable(SS_PRICING, getRandomNumberStream(SS_PRICING));

        StoreSalesRow storeSalesRow = new StoreSalesRow(nullBitMap,
                orderInfo.getSsSoldDateSk(),
                orderInfo.getSsSoldTimeSk(),
                ssSoldItemSk,
                orderInfo.getSsSoldCustomerSk(),
                orderInfo.getSsSoldCdemoSk(),
                orderInfo.getSsSoldHdemoSk(),
                orderInfo.getSsSoldAddrSk(),
                orderInfo.getSsSoldStoreSk(),
                ssSoldPromoSk,
                orderInfo.getSsTicketNumber(),
                ssPricing);
        List<TableRow> generatedRows = new ArrayList<>(2);
        generatedRows.add(storeSalesRow);

        // if the sale gets returned, generate a return row
        int randomInt = generateUniformRandomInt(0, 99, getRandomNumberStream(SR_IS_RETURNED));
        if (randomInt < SR_RETURN_PCT && (!session.generateOnlyOneTable() || session.getOnlyTableToGenerate() != STORE_SALES)) {
            generatedRows.add(((StoreReturnsRowGenerator) childRowGenerator).generateRow(session, storeSalesRow));
        }

        remainingLineItems--;
        return new RowGeneratorResult(generatedRows, isLastRowInOrder());
    }

    public OrderInfo generateOrderInfo(long rowNumber, Session session)
    {
        // move to a new date if the row number is ahead of the nextDateIndex
        Scaling scaling = session.getScaling();

        long ssSoldStoreSk = generateJoinKey(SS_SOLD_STORE_SK, getRandomNumberStream(SS_SOLD_STORE_SK), STORE, 1, scaling);
        long ssSoldTimeSk = generateJoinKey(SS_SOLD_TIME_SK, getRandomNumberStream(SS_SOLD_TIME_SK), TIME_DIM, 1, scaling);
        long ssSoldDateSk = generateJoinKey(SS_SOLD_DATE_SK, getRandomNumberStream(SS_SOLD_DATE_SK), DATE_DIM, 1, scaling);
        long ssSoldCustomerSk = generateJoinKey(SS_SOLD_CUSTOMER_SK, getRandomNumberStream(SS_SOLD_CUSTOMER_SK), CUSTOMER, 1, scaling);
        long ssSoldCdemoSk = generateJoinKey(SS_SOLD_CDEMO_SK, getRandomNumberStream(SS_SOLD_CDEMO_SK), CUSTOMER_DEMOGRAPHICS, 1, scaling);
        long ssSoldHdemoSk = generateJoinKey(SS_SOLD_HDEMO_SK, getRandomNumberStream(SS_SOLD_HDEMO_SK), HOUSEHOLD_DEMOGRAPHICS, 1, scaling);
        long ssSoldAddrSk = generateJoinKey(SS_SOLD_ADDR_SK, getRandomNumberStream(SS_SOLD_ADDR_SK), CUSTOMER_ADDRESS, 1, scaling);
        long ssTicketNumber = rowNumber;

        return new OrderInfo(ssSoldStoreSk,
                ssSoldTimeSk,
                ssSoldDateSk,
                ssSoldCustomerSk,
                ssSoldCdemoSk,
                ssSoldHdemoSk,
                ssSoldAddrSk,
                ssTicketNumber);
    }

    private boolean isLastRowInOrder()
    {
        return remainingLineItems == 0;
    }

    private class OrderInfo
    {
        private final long ssSoldStoreSk;
        private final long ssSoldTimeSk;
        private final long ssSoldDateSk;
        private final long ssSoldCustomerSk;
        private final long ssSoldCdemoSk;
        private final long ssSoldHdemoSk;
        private final long ssSoldAddrSk;
        private final long ssTicketNumber;

        public OrderInfo(long ssSoldStoreSk,
                long ssSoldTimeSk,
                long ssSoldDateSk,
                long ssSoldCustomerSk,
                long ssSoldCdemoSk,
                long ssSoldHdemoSk,
                long ssSoldAddrSk,
                long ssTicketNumber)
        {
            this.ssSoldStoreSk = ssSoldStoreSk;
            this.ssSoldTimeSk = ssSoldTimeSk;
            this.ssSoldDateSk = ssSoldDateSk;
            this.ssSoldCustomerSk = ssSoldCustomerSk;
            this.ssSoldCdemoSk = ssSoldCdemoSk;
            this.ssSoldHdemoSk = ssSoldHdemoSk;
            this.ssSoldAddrSk = ssSoldAddrSk;
            this.ssTicketNumber = ssTicketNumber;
        }

        public OrderInfo()
        {
            this(0, 0, 0, 0, 0, 0, 0, 0);
        }

        public long getSsTicketNumber()
        {
            return ssTicketNumber;
        }

        public long getSsSoldStoreSk()
        {
            return ssSoldStoreSk;
        }

        public long getSsSoldTimeSk()
        {
            return ssSoldTimeSk;
        }

        public long getSsSoldDateSk()
        {
            return ssSoldDateSk;
        }

        public long getSsSoldCustomerSk()
        {
            return ssSoldCustomerSk;
        }

        public long getSsSoldCdemoSk()
        {
            return ssSoldCdemoSk;
        }

        public long getSsSoldHdemoSk()
        {
            return ssSoldHdemoSk;
        }

        public long getSsSoldAddrSk()
        {
            return ssSoldAddrSk;
        }
    }
}
