# granulate-bm

To deploy the JBoss applications, run:
`oc create -f conf/jboss/`

To deploy the Quarkus applications, run:
`oc create -f conf/quarkus/`

To enable the Prometheus monitoring of the applications and deploy Grafana with an already existing Datasource, run:

`oc apply -f conf/grafana/cluster-monitoring-config-map.yaml`

`oc apply -f conf/grafana/workload-monitoring-config.yaml`

`oc apply -f conf/grafana/service-account-grafana.yaml`

`oc apply -f conf/grafana/role-binding-grafana.yaml`

Now copy the output of the following command:

`oc serviceaccounts get-token grafana-serviceaccount -n {namespace}`

and paste it in the `datasource.yaml` file.

Then run:

`oc create configmap grafana-config --from-file=conf/grafana/datasource.yaml -n {namespace}`

`oc apply -f conf/grafana/grafana-app.yaml -n {namespace}`

`oc create route edge grafana-route --service=grafana --namespace={namespace}`
