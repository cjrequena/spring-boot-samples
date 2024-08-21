## DD_QUERY for RPS

Count the number of requests in the last hour, and then divide it by 3600 to calculate the RPS.
Set time preferences to 1 hour.

```json
{
    "title": "REQUESTS PER SECOND",
    "type": "query_value",
    "requests": [
        {
            "response_format": "scalar",
            "queries": [
                {
                    "data_source": "metrics",
                    "name": "query1",
                    "query": "sum:say_hello1.requests.counted{env:local}.as_count()",
                    "aggregator": "sum"
                }
            ],
            "formulas": [
                {
                    "formula": "query1 / 3600"
                }
            ]
        }
    ],
    "autoscale": false,
    "timeseries_background": {
        "type": "bars"
    }
}
```


## DD_QUERY for REQUEST RECEIVED

```json
{
    "title": "REQUEST RECEIVED",
    "type": "query_value",
    "requests": [
        {
            "response_format": "scalar",
            "queries": [
                {
                    "data_source": "metrics",
                    "name": "query1",
                    "query": "sum:say_hello1.requests.counted{env:local}.as_count()",
                    "aggregator": "sum"
                }
            ],
            "formulas": [
                {
                    "formula": "query1"
                }
            ]
        }
    ],
    "autoscale": true,
    "precision": 0,
    "timeseries_background": {
        "type": "bars"
    }
}
```

## DD_QUERY for ACTIVE REQUESTS

```json
{
    "title": "ACTIVE REQUESTS",
    "type": "query_value",
    "requests": [
        {
            "response_format": "scalar",
            "queries": [
                {
                    "data_source": "metrics",
                    "name": "query1",
                    "query": "sum:say_hello1.active.requests{env:local}",
                    "aggregator": "last"
                }
            ],
            "formulas": [
                {
                    "formula": "query1"
                }
            ]
        }
    ],
    "autoscale": true,
    "precision": 0,
    "timeseries_background": {
        "type": "bars"
    }
}
```
