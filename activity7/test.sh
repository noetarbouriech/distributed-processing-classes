kubectl exec -it my-cluster-kafka-0 -- \
  bash -c "echo '{\"id\":\"1\",\"timestamp\":\"2023-01-01T00:00:00Z\",\"value\":100.0,\"category\":\"test\"}' | bin/kafka-console-producer.sh --topic test --bootstrap-server localhost:9092"
