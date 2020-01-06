package org.grorg.integration;

import org.grorg.integration.model.CatFact;
import org.grorg.integration.model.Num;
import org.grorg.integration.model.api.Facts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpMethod;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.support.converter.ConfigurableCompositeMessageConverter;
import org.springframework.integration.support.utils.IntegrationUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.GenericMessageConverter;

import java.util.Collections;

@SpringBootApplication
public class IntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }

    @Bean(name = IntegrationContextUtils.ARGUMENT_RESOLVER_MESSAGE_CONVERTER_BEAN_NAME)
    public static ConfigurableCompositeMessageConverter configurableCompositeMessageConverter(
            @Qualifier(IntegrationUtils.INTEGRATION_CONVERSION_SERVICE_BEAN_NAME) ConversionService conversionService) {
        return new ConfigurableCompositeMessageConverter(
                Collections.singleton(new GenericMessageConverter(conversionService)));
    }

    /**
     * Tcp server that takes a number `n` in client input,
     * Call the cat fact API (perform an HTTP GET on http://cat-fact.herokuapp.com/facts),
     * Get the `n`th fact from the retrieved list and display it to the client.
     * @return The Integration flow that performs cat fact fetching
     */
    @Bean
    public IntegrationFlow server() {
        return IntegrationFlows
                .from(Tcp.inboundGateway(Tcp.netServer(1234)))
                .transform(Transformers.objectToString())
                .transform(Num.class, id -> id)
                .enrichHeaders(headerEnricherSpec -> headerEnricherSpec.headerFunction("NUM", m -> ((Num) m.getPayload()).getNum()))
                .log()
                .handle(Http.outboundGateway("http://cat-fact.herokuapp.com/facts")
                            .httpMethod(HttpMethod.GET)
                            .expectedResponseType(Facts.class))
                .transform(Message.class, m -> {
                    Facts facts = (Facts) m.getPayload();
                    int num = (int) m.getHeaders().get("NUM");
                    return facts.getAll().get(num);
                })
                .transform(CatFact.class, id -> id)
                .handle((p, h) -> p.toString())
                .get();
    }
}
