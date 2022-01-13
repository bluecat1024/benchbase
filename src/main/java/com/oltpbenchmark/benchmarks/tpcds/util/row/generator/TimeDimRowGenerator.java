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
import com.oltpbenchmark.benchmarks.tpcds.util.distribution.HoursDistribution.HourInfo;
import com.oltpbenchmark.benchmarks.tpcds.util.row.TimeDimRow;

import static com.oltpbenchmark.benchmarks.tpcds.util.BusinessKeyGenerator.makeBusinessKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.TIME_DIM;
import static com.oltpbenchmark.benchmarks.tpcds.util.distribution.HoursDistribution.getHourInfoForHour;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.TimeDimGeneratorColumn.T_NULLS;

public class TimeDimRowGenerator
        extends AbstractRowGenerator
{
    public TimeDimRowGenerator()
    {
        super(TIME_DIM);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long nullBitMap = createNullBitMap(TIME_DIM, getRandomNumberStream(T_NULLS));
        long tTimeSk = rowNumber - 1;
        String tTimeId = makeBusinessKey(rowNumber);
        int tTime = (int) (rowNumber - 1);
        long timeTemp = tTime;
        int tSecond = (int) (timeTemp % 60);
        timeTemp /= 60;
        int tMinute = (int) (timeTemp % 60);
        timeTemp /= 60;
        int tHour = (int) (timeTemp % 24);

        HourInfo hourInfo = getHourInfoForHour(tHour);
        String tAmPm = hourInfo.getAmPm();
        String tShift = hourInfo.getShift();
        String tSubShift = hourInfo.getSubShift();
        String tMealTime = hourInfo.getMeal();

        return new RowGeneratorResult(new TimeDimRow(nullBitMap, tTimeSk, tTimeId, tTime, tHour, tMinute, tSecond, tAmPm, tShift, tSubShift, tMealTime));
    }
}
