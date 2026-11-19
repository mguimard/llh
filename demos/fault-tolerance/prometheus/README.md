```bash
docker run -d --name my-prometheus -v ./prometheus.yml:/etc/prometheus/prometheus.yml -p 9090:9090 prom/prometheus:latest
```

