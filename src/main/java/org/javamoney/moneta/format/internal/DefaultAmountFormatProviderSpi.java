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
package org.javamoney.moneta.format.internal;


import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatQuery;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.MonetaryAmountFormatProviderSpi;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Default format provider, which mainly maps the existing JDK functionality into the JSR 354 logic.
 *
 * @author Anatole Tresch
 */
public class DefaultAmountFormatProviderSpi implements MonetaryAmountFormatProviderSpi{

    private static final String DEFAULT_STYLE = "default";
    private static final String PROVIDER_NAME = "default";

    private Set<Locale> supportedSets = new HashSet<>();
    private Set<String> supportedStyles = new HashSet<>();

    public DefaultAmountFormatProviderSpi(){
        supportedSets.addAll(Arrays.asList(DecimalFormat.getAvailableLocales()));
        supportedSets = Collections.unmodifiableSet(supportedSets);
        supportedStyles.add(DEFAULT_STYLE);
        supportedStyles = Collections.unmodifiableSet(supportedStyles);
    }

    @Override
    public String getProviderName(){
        return PROVIDER_NAME;
    }

    /*
         * (non-Javadoc)
         * @see
         * javax.money.spi.MonetaryAmountFormatProviderSpi#getFormat(javax.money.format.AmountFormatContext)
         */
    @Override
    public Collection<MonetaryAmountFormat> getAmountFormats(AmountFormatQuery amountFormatQuery){
        Objects.requireNonNull(amountFormatQuery, "AmountFormatContext required");
        if(!amountFormatQuery.getProviders().isEmpty() && !amountFormatQuery.getProviders().contains(getProviderName())){
            return Collections.emptySet();
        }
        if(!(amountFormatQuery.getStyleId()==null || DEFAULT_STYLE.equals(amountFormatQuery.getStyleId()))){
            return Collections.emptySet();
        }
        AmountFormatContext.Builder builder = new AmountFormatContext.Builder(DEFAULT_STYLE);
        if(amountFormatQuery.getLocale()!=null){
            builder.setLocale(amountFormatQuery.getLocale());
        }
        builder.importContext(amountFormatQuery, false);
        builder.setMonetaryAmountFactory(amountFormatQuery.getMonetaryAmopuntFactory());
        return Arrays.asList(new MonetaryAmountFormat[]{new DefaultMonetaryAmountFormat(builder.build())});
    }

    @Override
    public Set<Locale> getAvailableLocales(){
        return supportedSets;
    }

    @Override
    public Set<String> getAvailableStyles(){
        return supportedStyles;
    }

}
