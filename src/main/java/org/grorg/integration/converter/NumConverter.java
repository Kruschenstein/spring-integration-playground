package org.grorg.integration.converter;

import org.grorg.integration.model.Num;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.config.IntegrationConverter;
import org.springframework.stereotype.Component;

@Component
@IntegrationConverter
class NumConverter implements Converter<String, Num> {
    @Override
    public Num convert(String s) {
        return new Num(Integer.parseInt(s));
    }
}
