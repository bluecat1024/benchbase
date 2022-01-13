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
import com.oltpbenchmark.benchmarks.tpcds.util.row.CatalogPageRow;

import static com.oltpbenchmark.benchmarks.tpcds.util.BusinessKeyGenerator.makeBusinessKey;
import static com.oltpbenchmark.benchmarks.tpcds.util.Nulls.createNullBitMap;
import static com.oltpbenchmark.benchmarks.tpcds.util.Table.CATALOG_PAGE;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CatalogPageGeneratorColumn.CP_DESCRIPTION;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.CatalogPageGeneratorColumn.CP_NULLS;
import static com.oltpbenchmark.benchmarks.tpcds.util.random.RandomValueGenerator.generateRandomText;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.DATE_MAXIMUM;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.DATE_MINIMUM;
import static com.oltpbenchmark.benchmarks.tpcds.util.type.Date.JULIAN_DATA_START_DATE;

public class CatalogPageRowGenerator
        extends AbstractRowGenerator
{
    public static final int CATALOGS_PER_YEAR = 18;
    private static final int WIDTH_CP_DESCRIPTION = 100;

    public CatalogPageRowGenerator()
    {
        super(CATALOG_PAGE);
    }

    @Override
    public RowGeneratorResult generateRowAndChildRows(long rowNumber, Session session, RowGenerator parentRowGenerator, RowGenerator childRowGenerator)
    {
        long cpCatalogPageSk = rowNumber;
        String cpDepartment = "DEPARTMENT";
        long nullBitMap = createNullBitMap(CATALOG_PAGE, getRandomNumberStream(CP_NULLS));
        String cpCatalogPageId = makeBusinessKey(rowNumber);

        int catalogPageMax = ((int) (session.getScaling().getRowCount(CATALOG_PAGE) / CATALOGS_PER_YEAR)) / (DATE_MAXIMUM.getYear() - DATE_MINIMUM.getYear() + 2);
        int cpCatalogNumber = (int) ((rowNumber - 1) / catalogPageMax + 1);
        int cpCatalogPageNumber = (int) ((rowNumber - 1) % catalogPageMax + 1);

        int catalogInterval = (cpCatalogNumber - 1) % CATALOGS_PER_YEAR;
        String cpType;
        int duration;
        int offset;
        switch (catalogInterval) {
            case 0:
            case 1:
                cpType = "bi-annual";
                duration = 182;
                offset = catalogInterval * duration;
                break;
            case 2:         // Q1
            case 3:         // Q2
            case 4:         // Q3
            case 5:         // Q4
                cpType = "quarterly";
                duration = 91;
                offset = (catalogInterval - 2) * duration;
                break;
            default:
                cpType = "monthly";
                duration = 30;
                offset = (catalogInterval - 6) * duration;
        }

        long cpStartDateId = JULIAN_DATA_START_DATE + offset + ((cpCatalogNumber - 1) / CATALOGS_PER_YEAR) * 365;
        long cpEndDateId = cpStartDateId + duration - 1;
        String cpDescription = generateRandomText(WIDTH_CP_DESCRIPTION / 2, WIDTH_CP_DESCRIPTION - 1, getRandomNumberStream(CP_DESCRIPTION));

        return new RowGeneratorResult(new CatalogPageRow(
                cpCatalogPageSk,
                cpCatalogPageId,
                cpStartDateId,
                cpEndDateId,
                cpDepartment,
                cpCatalogNumber,
                cpCatalogPageNumber,
                cpDescription,
                cpType,
                nullBitMap));
    }
}
