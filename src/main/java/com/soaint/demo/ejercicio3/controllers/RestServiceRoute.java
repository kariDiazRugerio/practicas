package com.soaint.demo.ejercicio3.controllers;

import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.main.Main;

public class RestServiceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Exponer el servicio REST usando Camel
        restConfiguration().component("jetty").host("0.0.0.0").port(8080);

        // Ruta para recibir el POST
        rest("/myservice")
                .post("/data")
                .consumes("application/json")
                .to("direct:processJson");

        // Ruta para recibir el GET
        rest("/myservice")
                .get("/data")
                .to("direct:processGet");

        // Procesar la solicitud POST (JSON en el cuerpo)
        from("direct:processJson")
                .log("Recibiendo POST: ${body}")
                .unmarshal().json()  // Deserializa el JSON recibido
                .process(exchange -> {
                    // Validar la estructura del JSON (puedes usar un esquema JSON si es necesario)
                    // Aquí hacemos una transformación de la estructura
                    String jsonResponse = "{\"status\":\"success\", \"data\":" + exchange.getIn().getBody() + "}";
                    exchange.getIn().setBody(jsonResponse);
                })
                .marshal().json()  // Serializa el objeto a JSON
                .setHeader("Content-Type", constant("application/json"))
                .to("log:output");  // Imprime la salida para depuración

        // Procesar la solicitud GET (query params)
        from("direct:processGet")
                .log("Recibiendo GET: ${headers}")
                .process(exchange -> {
                    // Aquí puedes hacer algún tipo de transformación o validación
                    String id = exchange.getIn().getHeader("id", String.class);
                    String name = exchange.getIn().getHeader("name", String.class);
                    String jsonResponse = "{\"id\":\"" + id + "\", \"name\":\"" + name + "\"}";
                    exchange.getIn().setBody(jsonResponse);
                })
                .setHeader("Content-Type", constant("application/json"))
                .to("log:output");  // Imprime la salida para depuración
    }


}
