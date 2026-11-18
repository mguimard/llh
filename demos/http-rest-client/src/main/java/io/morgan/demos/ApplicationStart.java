package io.morgan.demos;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Startup
@ApplicationScoped
public class ApplicationStart {

    @Inject
    @RestClient
    MyRemoteService otherMicroService;

    @PostConstruct
    public void Init() {

        // ...

        List<MyRemoteService.Pizza> pizzas = otherMicroService.getPizzas(null);
        Log.info(pizzas);

        Log.info(otherMicroService.getPizzas("creme"));

    }

}
