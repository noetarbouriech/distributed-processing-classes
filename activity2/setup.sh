# Add the Helm repository
helm repo add spark-operator https://kubeflow.github.io/spark-operator
helm repo update

# Install the operator into the spark-operator namespace and wait for deployments to be ready
helm install spark-operator spark-operator/spark-operator \
    --namespace spark-operator --create-namespace --wait



kubectl apply -f minio-dev.yaml

kubectl port-forward pod/minio 9000 9090 -n minio-dev

mc alias set k8s-minio-dev http://127.0.0.1:9000 minioadmin minioadmin
mc admin info k8s-minio-dev

mc mb k8s-minio-dev/my-app-bucket
mc cp ./app.jar k8s-minio-dev/my-app-bucket/
mc cp ./users.csv k8s-minio-dev/my-app-bucket/
mc anonymous set public k8s-minio-dev/my-app-bucket


k apply -f spark-quickstart.yaml

