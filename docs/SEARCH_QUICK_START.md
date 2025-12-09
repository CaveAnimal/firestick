# Dual-Mode Search - Quick Start Guide

## TL;DR

Three powerful search modes in one interface:

| Mode | Button | Speed | Best For | Result Style |
|------|--------|-------|----------|--------------|
| **🔍 Lucene** | Green | <100ms | Quick lookups, exact phrases | Keyword matches |
| **🤖 LLM** | Blue | 1-3s | Analysis, patterns, security | AI insights |
| **⚡ Both** | Orange | 1-3s | Complete analysis | Side-by-side |

## The Search Interface

```
┌─────────────────────────────────────────────────────────────┐
│ Recent (25): [Dropdown ▼] [Clear History]                   │
├─────────────────────────────────────────────────────────────┤
│ [Type your search query here - full width input field]      │
├─────────────────────────────────────────────────────────────┤
│ App: [default ▼]   [🔍 Lucene] [🤖 LLM] [⚡ Both]          │
├─────────────────────────────────────────────────────────────┤
│ Results displayed below (single column or side-by-side)     │
└─────────────────────────────────────────────────────────────┘
```

## How to Use

### Search Type 1: Fast Keyword Search
```
Click [🔍 Lucene] or press Enter
↓
Instantly find code by exact keywords
↓
View files and line numbers
↓
Click to see code
```

### Search Type 2: Deep Analysis
```
Click [🤖 LLM] 
↓
Wait 1-3 seconds for AI analysis
↓
Get insights on:
  • Architecture patterns
  • Performance issues
  • Security vulnerabilities  
  • Error handling gaps
  • Design recommendations
↓
Click results to view source
```

### Search Type 3: Complete Review
```
Click [⚡ Both]
↓
See Lucene results on left (instant)
LLM analysis on right (1-3 sec)
↓
Compare and validate results
↓
Get both perspectives simultaneously
```

## Example Queries

### Performance Investigation
```
Query: "sort algorithm implementation"
Lucene: Shows all files with "sort"
LLM: Detects O(n²) complexity, suggests QuickSort
```

### Security Review
```
Query: "database query construction"
Lucene: Lists all database-related files
LLM: Identifies SQL injection risks
```

### Architecture Understanding
```
Query: "factory pattern implementation"
Lucene: Shows exact "factory" keyword matches
LLM: Identifies all Factory-like patterns in code
```

## Features Explained

### 🕐 Search History (25 Most Recent)
- Uses dropdown at top
- Auto-saves after each search
- Click to reuse old search
- Click "Clear History" to reset

### 🎯 App Selector
- Default: "default"
- Shows available apps in your codebase
- Change before search to filter results

### 📊 Result Display

**Lucene Mode**:
```
┌─────────────────────────┐
│ 🔍 Lucene Results       │
├─────────────────────────┤
│ • File1.java            │
│ • File2.java            │
│ [Pagination controls]   │
│ Click item to view code │
└─────────────────────────┘
```

**LLM Mode**:
```
┌─────────────────────────┐
│ 🤖 LLM Results          │
├─────────────────────────┤
│ ⚙️ Architecture Pattern │
│ Text analysis here...  │
│                         │
│ 🔒 Security Alert       │
│ Vulnerability found...  │
│                         │
│ ⚡ Performance Issues   │
│ Optimization tips...   │
└─────────────────────────┘
```

**Both Mode**:
```
┌──────────────────┬──────────────────┐
│  🔍 Lucene       │  🤖 LLM          │
├──────────────────┼──────────────────┤
│ Files & line #   │ Analysis & tips  │
│ (Instant)        │ (1-3 seconds)    │
│                  │                  │
│ File1.java       │ ⚙️ Pattern       │
│ File2.java       │ 🔒 Security      │
│ File3.java       │ ⚡ Performance   │
└──────────────────┴──────────────────┘
```

## Common Workflows

### Workflow 1: Finding a Specific Class
```
1. Type: "DatabaseConnection"
2. Click [🔍 Lucene]
3. Result appears instantly
4. Click to view file
```
⏱️ Time: ~100ms

### Workflow 2: Code Review
```
1. Type: "user authentication"
2. Click [⚡ Both]
3. See files on left
4. Read analysis on right
5. Investigate issues
```
⏱️ Time: ~2 seconds

### Workflow 3: Performance Tuning
```
1. Type: "database queries"
2. Click [🤖 LLM]
3. Read performance analysis
4. Get optimization suggestions
5. Implement improvements
```
⏱️ Time: ~2 seconds

### Workflow 4: Security Audit
```
1. Type: "user input handling"
2. Click [⚡ Both]
3. Cross-reference Lucene & LLM
4. Verify all vulnerable code
5. Fix security issues
```
⏱️ Time: ~2 seconds

## Tips & Tricks

### ✅ DO
- ✅ Use Lucene for quick lookups
- ✅ Use LLM for analysis questions
- ✅ Use Both for comprehensive review
- ✅ Check history before retyping
- ✅ Modify search and re-run

### ❌ DON'T
- ❌ Don't use LLM for single-word lookups (too slow)
- ❌ Don't use Lucene for abstract analysis
- ❌ Don't wait for Both mode if you just need one type
- ❌ Don't ignore error messages

## Keyboard Shortcuts

| Press | Action |
|-------|--------|
| `Enter` | Run current search mode |
| `↓` | Open history dropdown |
| `Esc` | Close code viewer |
| `Ctrl+A` | Select all search text |

## Troubleshooting

### "No results found" (Lucene)
- Try simpler keywords
- Check app selection
- Verify data is indexed

### "LLM Error" 
- Make sure LLM service is running (port 8001)
- Check internet connection
- Try Lucene search instead

### History not saving
- Enable localStorage in browser
- Check privacy/incognito mode
- Try different browser

### Results taking too long
- LLM analysis: 1-3 seconds is normal
- Lucene: Should be instant
- Try with fewer filters

## Examples by Query Type

### Finding a Class/File
```
Query: "CustomerRepository"
Use: 🔍 Lucene (instant)
```

### Understanding Architecture
```
Query: "how is dependency injection configured"
Use: 🤖 LLM (analysis)
```

### Finding Performance Issues
```
Query: "slow queries n-squared loops"
Use: 🤖 LLM (analysis)
```

### Checking Security
```
Query: "password validation hashing"
Use: 🤖 LLM (security check)
```

### Comparing Results
```
Query: "REST endpoints"
Use: ⚡ Both (validate both approaches)
```

---

**Need more help?** See `DUAL_MODE_SEARCH.md` for detailed documentation.
