{{/* Expand the name of the chart. */}}
{{- define "helm_chart_name_version" -}}
{{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end }}

{{- define "chart-labels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/component: app
app.kubernetes.io/part-of: app
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ include "helm_chart_name_version" . }}
{{- end }}

{{- define "image-name" -}}
siakhooi/query
{{- end }}
