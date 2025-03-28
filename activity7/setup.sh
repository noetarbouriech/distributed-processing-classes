helm repo add strimzi https://strimzi.io/charts/
helm install my-strimzi-kafka-operator strimzi/strimzi-kafka-operator --version 0.45.0
k apply -f kafka.yaml

kubectl exec -it my-cluster-kafka-0 -- \
  bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --topic test \
  --partitions 1 \
  --replication-factor 1
