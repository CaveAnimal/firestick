# How to Know if the LLM is Working on Your Question

## Quick Answer
Check **three places** to verify the LLM is actively processing your search query:

1. **Browser Console** (F12) → Look for network requests
2. **Spring Boot Server Logs** → Look for LLM search logging
3. **Browser Network Tab** (F12) → Watch the `/api/llm/search` request

---

## Method 1: Browser Console (F12)

### What to Look For

When you click **🤖 LLM Search**, you should see:

```
LLM button clicked { query: "your search term", app: "default", time: "2025-11-15T12:45:00Z" }
LLM: starting search { query: "your search term", app: "default", timestamp: "2025-11-15T12:45:00Z" }
LLM: response received { status: 200, timeMs: 150 }
LLM: response body { results: [...] }
```

### Step-by-Step

1. **Open Browser DevTools**
   - Press `F12` (or `Right-click` → `Inspect`)
   - Go to **Console** tab

2. **Run a Search**
   - Enter your query in the search box
   - Click **🤖 LLM Search** button
   - Watch the console for messages

3. **Look for These Logs**
  ```javascript
  // When you click LLM Search you'll see new logs like:
  console.info('LLM button clicked', { query, app, time: new Date().toISOString() })
  console.info('LLM: starting search', { query, app, timestamp: new Date().toISOString() })
  console.info('LLM: response received', { status: response.status, timeMs })
  console.debug('LLM: response body', data)
  ```

4. **If You See Errors**
   ```javascript
   Error: Failed to fetch LLM results
   Error: Network request failed
   Error: CORS error
   ```
   This means the backend is not responding.

---

## Method 2: Spring Boot Server Logs

### What to Look For

When the LLM search request hits the backend, you should see:

```
INFO  LLMSearchController - LLM search: query='your search term', app='default', limit=10
DEBUG LLMSearchController - Sending analysis request to LLM: Analyze this code...
INFO  LLMSearchController - Returning 5 LLM search results
```

### How to Enable Debug Logging

Edit `src/main/resources/application.properties`:

```properties
# Add these lines to see LLM debug output:
logging.level.com.codetalker.firestick.controller.LLMSearchController=DEBUG
logging.level.root=INFO
```

### Start Server with Logs

```powershell
# Terminal 1: Start Spring Boot
cd e:\MyProjects\MyGitHubCopilot\firestick\fstk-001
mvn spring-boot:run

# You'll see:
# INFO  ... LLMSearchController - LLM search: query='...', app='default', limit=10
```

### Where to Look

**In the Terminal Running Spring Boot:**
```
2025-11-15 10:30:45.123  INFO 12345 --- [nio-8081-exec-1] c.c.f.c.LLMSearchController : LLM search: query='performance', app='default', limit=10
2025-11-15 10:30:45.124 DEBUG 12345 --- [nio-8081-exec-1] c.c.f.c.LLMSearchController : Sending analysis request to LLM: Analyze this code search query...
2025-11-15 10:30:45.234  INFO 12345 --- [nio-8081-exec-1] c.c.f.c.LLMSearchController : Returning 5 LLM search results
```

---

## Method 3: Browser Network Tab (F12)

### What to Look For

A POST request to `/api/llm/search` with:
- **Status**: `200 OK` (success) or `500` (error)
- **Time**: 100-500ms typically
- **Response**: JSON with results array

### Step-by-Step

1. **Open Browser DevTools**
   - Press `F12`
   - Go to **Network** tab

2. **Prepare to Capture Request**
   - Clear existing requests: Click the trash icon
   - Keep Network tab open

3. **Run Your Search**
   - Enter search query
   - Click **🤖 LLM Search**
   - Watch for network request

4. **Find the Request**
   - Look for: `llm/search` (POST method)
   - Click on it to see details

5. **Check Request Details**
   ```
   POST http://localhost:8081/api/llm/search
   Headers:
     Content-Type: application/json
   
   Body (sent):
   {
     "query": "your search term",
     "app": "default",
     "limit": 10
   }
   ```

6. **Check Response**
   ```json
   {
     "results": [
       {
         "title": "Architecture Pattern Analysis",
         "description": "Identifies interface implementations...",
         "content": "Your query relates to...",
         "score": 0.95
       },
       ...
     ],
     "count": 5,
     "query": "your search term"
   }
   ```

### Common Network Issues

| Status Code | Meaning | Solution |
|-------------|---------|----------|
| `200 OK` | ✅ Success | Results should appear |
| `400 Bad Request` | Empty query | Enter a search term |
| `500 Server Error` | Backend crashed | Check Spring Boot logs |
| `No response` | Server not running | Start with `mvn spring-boot:run` |
| `CORS Error` | Cross-origin blocked | Check CORS config |

---

## Method 4: Add Custom Logging

### To the Frontend (React)

Edit `ui/src/pages/Search.tsx` and add console logging to the `runLLMSearch` function:

```typescript
const runLLMSearch = async () => {
  setLlmLoading(true)
  setLlmError(null)
  console.log("🤖 [LLM Search] Starting search for:", query) // <- Add this
  
  try {
    const response = await fetch('/api/llm/search', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        query: query,
        app: app,
        limit: 10
      })
    })
    
    console.log("🤖 [LLM Search] Response received, status:", response.status) // <- Add this
    
    const data = await response.json()
    console.log("🤖 [LLM Search] Results:", data) // <- Add this
    
    const llmResults = data.results.map((r: any) => ({
      id: r.title,
      type: 'llm' as const,
      title: r.title,
      description: r.description,
      content: r.content,
      filePath: r.filePath,
      score: r.score
    }))
    
    setLlmResults(llmResults)
    setShowLLMResults(true)
    
  } catch (err: any) {
    console.error("🤖 [LLM Search] Error:", err) // <- Add this
    setLlmError(err.message || 'Failed to fetch LLM results')
  } finally {
    setLlmLoading(false)
  }
}
```

### To the Backend (Java)

Edit `src/main/java/com/codetalker/firestick/controller/LLMSearchController.java`:

```java
private List<LLMSearchResult> analyzedSearch(String query, String app, int limit) {
    List<LLMSearchResult> results = new ArrayList<>();
    
    try {
        log.info("🤖 [LLMSearchController] Starting analysis for query: {}", query);
        
        String analysisPrompt = buildAnalysisPrompt(query);
        log.debug("🤖 [LLMSearchController] Analysis prompt: {}", analysisPrompt);
        
        results.addAll(generateLLMInsights(query, app, limit));
        log.info("🤖 [LLMSearchController] Generated {} insights", results.size());
        
    } catch (Exception e) {
        log.error("🤖 [LLMSearchController] Analysis failed", e);
    }
    
    return results;
}
```

---

## Complete Debug Workflow

### Step 1: Start Everything

```powershell
# Terminal 1: Spring Boot backend
cd e:\MyProjects\MyGitHubCopilot\firestick\fstk-001
mvn spring-boot:run

# Terminal 2: React frontend
cd e:\MyProjects\MyGitHubCopilot\firestick\fstk-001\ui
npm run dev
```

### Step 2: Open Browser

```
http://localhost:5173/search
```

### Step 3: Open DevTools

Press `F12`, go to **Console** tab

### Step 4: Run a Test Search

1. Enter: `performance optimization`
2. Click **🤖 LLM Search**
3. Watch for logs:
   - ✅ Console shows "LLM search initiated"
   - ✅ Network tab shows POST to `/api/llm/search`
   - ✅ Response shows 200 OK with results
   - ✅ Console shows "LLM search completed with X results"

### Step 5: Check Server Logs

In Terminal 1, you should see:
```
INFO  ... LLMSearchController - LLM search: query='performance optimization', app='default', limit=10
```

### Step 6: Verify Results

In the UI:
- ✅ "Loading LLM results..." disappears
- ✅ Blue "LLM Results" pane appears
- ✅ Shows 5 analysis cards (Architecture, Performance, Error Handling, Security, Code Understanding)

---

## Troubleshooting Checklist

### If You See No LLM Results

**Check 1: Spring Boot Running?**
```powershell
netstat -ano | findstr 8081
```
Should show the port is listening.

**Check 2: Frontend Making Request?**
- Open F12 → Network tab
- Click LLM Search
- Do you see `/api/llm/search` POST request?
  - Yes → Go to Check 3
  - No → Frontend issue (see Check 2a)

**Check 2a: Frontend Issue**
```powershell
cd ui
npm run dev
# Restart browser to reload
```

**Check 3: Backend Returning Results?**
- Click on `/api/llm/search` in Network tab
- Go to "Response" sub-tab
- Do you see JSON with `results` array?
  - Yes → Go to Check 4
  - No → Backend issue (see Check 3a)

**Check 3a: Backend Issue**
```powershell
mvn spring-boot:run
# Watch for ERROR logs
# Check: does LOG show "LLM search: query=..."?
```

**Check 4: Results Showing in UI?**
- In browser, do results appear below search box?
- Click on "Performance Analysis" card
- Does content display?
  - Yes → ✅ LLM is working!
  - No → Refresh page (Ctrl+R)

---

## What Success Looks Like

### Console Output (F12 → Console)
```
🔍 [Lucene Search] Starting search for: performance
🔍 [Lucene Search] Response received, status: 200
🔍 [Lucene Search] Results: {results: Array(10), total: 247, page: 1}

🤖 [LLM Search] Starting search for: performance
🤖 [LLM Search] Response received, status: 200
🤖 [LLM Search] Results: {results: Array(5), count: 5, query: "performance"}
```

### Server Logs (Terminal Running Spring Boot)
```
INFO  ... LLMSearchController : LLM search: query='performance', app='default', limit=10
DEBUG ... LLMSearchController : Sending analysis request to LLM: Analyze this code...
INFO  ... LLMSearchController : Returning 5 LLM search results
```

### Network Tab (F12 → Network)
```
POST /api/llm/search          200 OK    125ms
  Request:  {"query":"performance","app":"default","limit":10}
  Response: {"results":[...],"count":5,"query":"performance"}
```

### UI Display
```
🔍 Lucene Search Results          🤖 LLM Search Results
├─ File 1 (Score: 0.98)           ├─ Architecture Pattern Analysis
├─ File 2 (Score: 0.95)           ├─ Performance Analysis
├─ File 3 (Score: 0.92)           ├─ Error Handling Analysis
...                               ...
```

---

## Performance Notes

### Expected Response Times

- **Lucene Search**: 50-200ms (instant)
- **LLM Search**: 500ms-2s (analysis takes time)
- **Both Searches**: 500ms-2s (dominated by LLM)

If LLM takes longer than 5 seconds, check:
1. Is it actually processing? (Check server logs)
2. Is the network slow? (Check Network tab)
3. Is the server overloaded? (Check CPU/memory)

---

## Future: Real LLM Integration

Currently, the LLM search returns **mock insights** based on query keywords.

To enable **real LLM processing**:

1. Ensure Python LLM service is running on port 8001:
   ```powershell
   python llm_service_gguf.py
   ```

2. Update `LLMSearchController.java` to call port 8001:
   ```java
   private List<LLMSearchResult> callRealLLMService(String query) {
       // Make HTTP request to http://localhost:8001/api/analyze
       // Parse response and return results
   }
   ```

3. Watch console for real LLM analysis instead of mock keywords.

---

**Need Help?** Check the server logs and network tab — they'll tell you exactly where the problem is! 🔍
