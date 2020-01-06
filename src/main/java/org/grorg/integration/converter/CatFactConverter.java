package org.grorg.integration.converter;

import org.grorg.integration.model.CatFact;
import org.grorg.integration.model.api.Fact;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.config.IntegrationConverter;
import org.springframework.stereotype.Component;

@Component
@IntegrationConverter
class CatFactConverter implements Converter<Fact, CatFact> {
    @Override
    public CatFact convert(Fact s) {
        return new CatFact(s.getText());
    }
}
