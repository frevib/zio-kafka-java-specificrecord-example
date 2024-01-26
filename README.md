## ZIO Kafka AVRO example with SpecificRecords serialization

### Run
`docker compose up`

`sbt avroGenerate`

Choose one of the AVRO serializers/serializers:
* serializer without schema registry. You have to provide the schema yourself
* serializer with schema registry. You don't have to provide the schema

run main class