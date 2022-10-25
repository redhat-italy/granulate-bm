# CONF
All the configuration files to deploy the project.

## GRAFANA

### step 1
se non l'hai fatto prima abilita il monitoring con il file cluster-monitoring-config-map.yaml

** DA VERIFICARE **
da mettere anche il file worload-monitoring-config.yaml che fa la stessa cosa ma su un altro namespace


### step 2 
creare il service account con relativo ruolo

usa 
`oc apply -f conf/grafana/service-account-grafana.yaml`

`oc apply -f conf/grafana/role-binding-grafana.yaml`

ATTENZIONE che quando creei il ruolo nel namespace devi indicare il name space usato per il progetto

# step 3

Now copy the output of the following command:

`oc serviceaccounts get-token grafana-serviceaccount -n {namespace}`

and paste it in the `datasource.yaml` file.

Then run:

`oc create configmap grafana-config --from-file=conf/grafana/datasource.yaml -n {namespace}`

`oc apply -f conf/grafana/grafana-app.yaml -n {namespace}`

`oc create route edge grafana-route --service=grafana --namespace={namespace}`

fin qui tutto corretto
ora bisogna creare la dashboard di grafana

