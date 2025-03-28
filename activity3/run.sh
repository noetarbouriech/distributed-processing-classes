helm repo add mongodb https://mongodb.github.io/helm-charts
helm install community-operator mongodb/community-operator --namespace mongodb --create-namespace
kubectl apply -f mongodb.yaml -n mongodb
