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

package com.oltpbenchmark.benchmarks.tpcds.util.row;

import java.util.List;

import static com.oltpbenchmark.benchmarks.tpcds.util.generator.HouseholdDemographicsGeneratorColumn.HD_BUY_POTENTIAL;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.HouseholdDemographicsGeneratorColumn.HD_DEMO_SK;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.HouseholdDemographicsGeneratorColumn.HD_DEP_COUNT;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.HouseholdDemographicsGeneratorColumn.HD_INCOME_BAND_ID;
import static com.oltpbenchmark.benchmarks.tpcds.util.generator.HouseholdDemographicsGeneratorColumn.HD_VEHICLE_COUNT;

public class HouseholdDemographicsRow
        extends TableRowWithNulls
{
    private final long hdDemoSk;
    private final long hdIncomeBandId;
    private final String hdBuyPotential;
    private final int hdDepCount;
    private final int hdVehicleCount;

    public HouseholdDemographicsRow(long nullBitMap, long hdDemoSk, long hdIncomeBandId, String hdBuyPotential, int hdDepCount, int hdVehicleCount)
    {
        super(nullBitMap, HD_DEMO_SK);
        this.hdDemoSk = hdDemoSk;
        this.hdIncomeBandId = hdIncomeBandId;
        this.hdBuyPotential = hdBuyPotential;
        this.hdDepCount = hdDepCount;
        this.hdVehicleCount = hdVehicleCount;
    }

    @Override
    public List<String> getValues()
    {
        return List.of(getStringOrNullForKey(hdDemoSk, HD_DEMO_SK),
                getStringOrNullForKey(hdIncomeBandId, HD_INCOME_BAND_ID),
                getStringOrNull(hdBuyPotential, HD_BUY_POTENTIAL),
                getStringOrNull(hdDepCount, HD_DEP_COUNT),
                getStringOrNull(hdVehicleCount, HD_VEHICLE_COUNT));
    }
}
