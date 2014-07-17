/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.internal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryRounding;
import javax.money.RoundingQuery;
import javax.money.spi.RoundingProviderSpi;
import java.math.RoundingMode;
import java.util.*;

/**
 * Defaulr implementation of a {@link javax.money.spi.RoundingProviderSpi} that creates instances of {@link org
 * .javamoney.moneta.internal.DefaultRounding} that relies on the default fraction units defined by {@link java.util
 * .Currency#getDefaultFractionDigits()}.
 */
public class DefaultRoundingProvider implements RoundingProviderSpi{

    public static final String DEFAULT_ROUNDING_ID = "default";
    private Set<String> roundingsIds = new HashSet<>();

    public DefaultRoundingProvider(){
        roundingsIds.add(DEFAULT_ROUNDING_ID);
        roundingsIds = Collections.unmodifiableSet(roundingsIds);
    }

    @Override
    public String getProviderName(){
        return "default";
    }

    /**
     * Evaluate the rounding that match the given query.
     *
     * @return the (shared) default rounding instances matching, never null.
     */
    public MonetaryRounding getRounding(RoundingQuery roundingQuery){
        if(roundingQuery.getTimestamp() != null){
            return null;
        }
        CurrencyUnit currency = roundingQuery.getCurrencyUnit();
        RoundingMode roundingMode = roundingQuery.get(RoundingMode.class, RoundingMode.HALF_EVEN);
        int scale = roundingQuery.getInt("scale", 2);
        if(roundingQuery.getRoundingName()==null || DEFAULT_ROUNDING_ID.equals(roundingQuery.getRoundingName())){
            if(currency!=null){
                if(roundingQuery.getBoolean("cashRounding", false)){
                    if(currency != null && currency.getCurrencyCode().equals("CHF")){
                        return new DefaultCashRounding(currency, RoundingMode.HALF_UP, 5);
                    }else{
                        return new DefaultCashRounding(currency, 1);
                    }
                }else{
                    return new DefaultRounding(currency, roundingMode);
                }
            }
            else{
                return new DefaultRounding(scale, roundingMode);
            }
        }
        return null;
    }



    @Override
    public Set<String> getRoundingIds(){
        return roundingsIds;
    }

}