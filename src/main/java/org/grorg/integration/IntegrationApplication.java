package org.grorg.integration;

import org.aopalliance.aop.Advice;
import org.grorg.integration.model.CatFact;
import org.grorg.integration.model.Num;
import org.grorg.integration.model.api.Facts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

@SpringBootApplication
public class IntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }

    /**
     * Tcp server that takes a number `n` in client input,
     * Call the cat fact API (perform an HTTP GET on http://cat-fact.herokuapp.com/facts),
     * Get the `n`th fact from the retrieved list and display it to the client.
     * @return The Integration flow that performs cat fact fetching
     */
    @Bean
    public IntegrationFlow server(MessageChannel incoming, TcpSendingMessageHandler sender, Advice advice) {
        return IntegrationFlows
                .from(incoming)
                .transform(Transformers.objectToString())
                .<String, Boolean>route(
                        p -> {
                            try { Integer.parseInt(p); } catch (NumberFormatException e) { return false; }
                            return true;
                        },
                        m -> m.subFlowMapping(true, f ->
                                f.convert(Num.class)
                                 .enrichHeaders(headerEnricherSpec -> headerEnricherSpec.headerFunction("NUM", message -> ((Num) message.getPayload()).getNum()))
                                 .log()
                                 .handle(Http.outboundGateway("http://cat-fact.herokuapp.com/facts")
                                             .httpMethod(HttpMethod.GET)
                                             .expectedResponseType(Facts.class))
                                 .transform(Message.class, message -> {
                                     Facts facts = (Facts) message.getPayload();
                                     int num = (int) message.getHeaders().get("NUM");
                                     return facts.getAll().get(num);
                                 })
                                 .headerFilter(MessageHeaders.CONTENT_TYPE)
                                 .convert(CatFact.class)
                                 .handle((p, h) -> p.toString()))
                              .subFlowMapping(false, f ->
                                      f.handle((p, h) -> "Bye")
                                       .handle(sender, e -> e.advice(advice)))
                )
                .handle(sender)
                .get();
    }

    @Bean
    public Advice closeConnectionAdvice(TcpNetServerConnectionFactory connectionFactory) {
        return new AbstractRequestHandlerAdvice() {
            @Override
            protected Object doInvoke(AbstractRequestHandlerAdvice.ExecutionCallback callback, Object target, Message<?> message) {
                try {
                    return callback.execute();
                } finally {
                    connectionFactory.closeConnection(message.getHeaders().get("ip_connectionId", String.class));
                }
            }
        };
    }
}
