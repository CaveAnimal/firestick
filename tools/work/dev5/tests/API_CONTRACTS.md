# API Contract examples — Indexing SSE & snapshot

This short document contains example event payloads used across tests and the dev5 synthetic harness.

object-start
```
{ "event": "object-start", "jobId": 123, "objectId": "o-1", "objectType": "file", "path": "src/foo/Bar.java", "ts": 1690000000000 }
```

object-progress
```
{ "event": "object-progress", "jobId": 123, "objectId": "o-1", "objectType": "file", "path": "src/foo/Bar.java", "ts": 1690000000500, "objectWorkDone": 20, "objectTotalWork": 100 }
```

object-end
```
{ "event": "object-end", "jobId": 123, "objectId": "o-1", "objectType": "file", "path": "src/foo/Bar.java", "ts": 1690000001000, "elapsedMs": 1000 }
```

progress (aggregated)
```
{ "event": "progress", "jobId": 123, "percent": 34, "filesDiscovered": 100, "filesParsed": 34, "chunksProduced": 50, "documentsIndexed": 48, "embeddingsGenerated": 48 }
```

Testing notes
 - Use these examples as fixtures for unit/integration tests and for the synthetic SSE harness consumers.
 - Include negative-case tests: missing fields, missing totals (objectTotalWork), and empty payloads.
