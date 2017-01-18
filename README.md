## dbp-microservices
DBP Microservices

This project is intended to create micro-services based on Springboot and Netflix (but not only) most popular open source tools:

- Config service: centralised micro service that hosts all configs

- Eureka: service discovery mechanism that allows micro services to register themselves and discover other registered services: https://github.com/Netflix/eureka

- Zipkin: distributed tracing system https://github.com/openzipkin/zipkin

- Hystrix: Latency and Fault Tolerance for Distributed Systems https://github.com/Netflix/Hystrix

- Share price service: a micro service that exposes REST endpoints to get share prices and also demonstrates a way to get messages through config and message

## Execute the services

Services need to be executed in the following order:

#1-config-service 

It will run on port 8888 by default and points to my github repo that hosts all the config files

#2-eureka-service

It will run on port 8761 open the following URL in your browser to access the dashboard http://localhost:8761/

#3-hystrix-service

The dashboard is available on port 8010: http://localhost:8010/hystrix

#4-zipkin-service

It will run on port 9411 http://localhost:9411/

#5-share-price-service

Last but not least the share price service is available on port 8000.

http://localhost:8000 will return all share prices (HATEOS format)

http://localhost:8000/sharePrices will return a JSON format response 

http://localhost:8000/sharePrices/{id} will return the share price for specified id

http://localhost:8000/sharePrices/search/by-name?rn=ING will return share price for the specified name

http://localhost:8000/message will display a message setup via the config-service file

RabbitMQ has already been setup so that messages can be sent through channels from another micro service and vice versa (prices endpoint)
