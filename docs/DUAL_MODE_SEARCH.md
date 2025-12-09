# Dual-Mode Search Interface (Lucene + LLM)

## Overview

The enhanced search interface provides two complementary search modes that can be used independently or together:

1. **Lucene Search** - Fast, keyword-based code search (Quick & Dirty)
2. **LLM Search** - Intelligent, semantic-based analysis (In-Depth)

Users can run either search independently or both simultaneously to compare results and get comprehensive code insights.

## Features

### 1. Full-Width Search Input
- Single text input field spanning the entire screen width
- Easy to focus and type longer queries
- Full keyboard support (Enter key triggers search)

### 2. Search History (Last 25 Searches)
- Dropdown selector for quick access to recent searches
- Persisted in browser localStorage
- Clear history button to reset
- Automatically updated after each search

### 3. Three Search Modes

#### Mode 1: Lucene Search (🔍)
- **Speed**: Immediate results (milliseconds)
- **Approach**: Keyword/full-text indexing
- **Best For**: 
  - Quick lookups
  - Exact phrase matching
  - Finding specific code patterns
- **Button Color**: Green when active
- **Shortcut**: Click "Lucene Search" or select from dropdown

#### Mode 2: LLM Search (🤖)
- **Speed**: Slower (seconds to analyze)
- **Approach**: AI-powered semantic analysis
- **Best For**:
  - Understanding code intent
  - Finding architectural patterns
  - Security/performance analysis
  - Error condition detection
- **Button Color**: Blue when active
- **Shortcut**: Click "LLM Search" or select from dropdown

#### Mode 3: Both Searches (⚡)
- **Runs**: Both Lucene and LLM simultaneously
- **Display**: Side-by-side results comparison
- **Best For**: 
  - Comprehensive analysis
  - Validating results with different approaches
  - Research and deep dives
- **Button Color**: Orange when active
- **Shortcut**: Click "Both" button

### 4. Dual Results Panes

When using "Both" mode, results display side-by-side:

**Left Pane - Lucene Results**:
- Green header bar
- Fast keyword matches
- Pagination support
- Click to view code

**Right Pane - LLM Results**:
- Blue header bar
- AI-powered insights
- Analysis categories:
  - Architecture Pattern Analysis
  - Design Pattern Matches
  - Performance Analysis
  - Error Handling Analysis
  - Security Assessment
  - Code Understanding

### 5. LLM Analysis Categories

The LLM search automatically analyzes queries and provides focused insights:

#### Architecture Patterns
- Identifies interface implementations
- Finds design pattern usage
- Shows architectural relationships

#### Performance Analysis
- Detects O(n²) loops
- Identifies caching opportunities
- Suggests optimization points

#### Error Handling
- Finds null pointer risks
- Identifies missing exception handling
- Shows incomplete error propagation

#### Security Assessment
- Detects SQL injection risks
- Identifies missing input validation
- Finds credential handling issues

#### Code Understanding
- Semantic interpretation of query
- Contextual relevance ranking
- Cross-file relationship analysis

## UI Layout

### Single Search Mode (Lucene or LLM)
```
[Full-Width Search Input Bar with History Dropdown]
[Search Mode Buttons]
[Single Results Pane - Full Width]
```

### Both Searches Mode
```
[Full-Width Search Input Bar with History Dropdown]
[Search Mode Buttons]
[Lucene Results Pane] | [LLM Results Pane]
     (50% width)      |      (50% width)
```

## Usage Examples

### Example 1: Quick Code Lookup
```
User: Searching for "Calculator class"
Mode: Lucene Search
Result: Immediately finds Calculator.java file with line numbers
Time: <100ms
```

### Example 2: Performance Investigation
```
User: Searching for "sort implementation performance"
Mode: LLM Search
Result: Identifies O(n²) sorting algorithm, suggests optimization
Analysis: Provides complexity analysis and better algorithms
Time: 1-2 seconds
```

### Example 3: Comprehensive Code Review
```
User: Searching for "database connection handling"
Mode: Both
Left Pane: All files containing "database connection" keywords
Right Pane: Security vulnerabilities, performance issues, error handling gaps
```

## Search History Behavior

### Adding to History
- Automatically saved after each search
- Duplicates are moved to top (most recent)
- Limited to 25 most recent searches

### Accessing History
- Click history dropdown
- Select any previous search
- Input field auto-populates
- Ready for modification or re-execution

### Clearing History
- Click "Clear History" button
- All 25 searches removed
- localStorage entry deleted
- Fresh start for next search session

## Keyboard Shortcuts

| Key | Action |
|-----|--------|
| Enter | Execute current search mode |
| Ctrl+A | Select all text in search box |
| Ctrl+L | Lucene search quick command (custom) |
| Ctrl+M | LLM search quick command (custom) |

## API Endpoints

### Lucene Search
```
GET /api/search?q=<query>&app=<appName>&page=<number>
Response: { results: [...], total: number, page: number }
```

### LLM Search
```
POST /api/llm/search
Request: { query: string, app: string, limit: number }
Response: { 
  results: [
    { 
      title: string,
      description: string,
      content: string,
      filePath?: string,
      score: number
    }
  ],
  count: number,
  query: string
}
```

## Component Structure

```
Search.tsx (Main Page)
├── Search Input & History Dropdown
├── Mode Selection Buttons (Lucene/LLM/Both)
├── Results Container (Grid: 1fr or 1fr 1fr)
│   ├── Lucene Results Pane
│   │   ├── Results List
│   │   └── Pagination
│   └── LLM Results Pane
│       └── Analysis Results
└── Code Viewer Modal
```

## Styling

### Colors
- **Lucene Mode**: Green (#4CAF50)
- **LLM Mode**: Blue (#2196F3)
- **Both Mode**: Orange (#FF9800)
- **Background**: Light gray (#f9f9f9)
- **Text**: Dark gray (#333)
- **Borders**: Medium gray (#ddd)

### Responsive Design
- Full width on all screen sizes
- Stacks to single column on mobile
- Grid layout adjusts based on active results

## Performance Characteristics

### Lucene Search
- **Time**: 0.5-50ms per search
- **Memory**: ~100KB per result set
- **Network**: Single GET request
- **Cached**: Browser disk cache available

### LLM Search
- **Time**: 500ms-3s per search
- **Memory**: ~50KB per result
- **Network**: Single POST request
- **Processing**: Server-side AI analysis

### Both Searches
- **Time**: 500ms-3s (LLM time dominates)
- **Memory**: ~200KB total
- **Network**: 2 simultaneous requests
- **Benefit**: Complete analysis in one interaction

## Error Handling

### Search Errors
- Error messages displayed inline per pane
- Graceful fallback to empty results
- User can retry with modified query
- Network errors caught and reported

### History Errors
- localStorage unavailable: Still works, just not persisted
- Corrupted history: Auto-recovers with empty array
- Quota exceeded: Oldest entries removed

## Future Enhancements

1. **Search Suggestions**: Auto-complete based on history
2. **Saved Searches**: Star favorite searches
3. **Advanced Filters**: File type, date range, code type
4. **Batch Processing**: Multiple queries at once
5. **Export Results**: CSV, JSON, PDF export
6. **Search Analytics**: Track popular queries
7. **Custom Analysis Rules**: User-defined LLM analysis categories
8. **Real-time Search**: Live results as user types

## Troubleshooting

### LLM Search Not Available
- Check LLM service running on port 8001
- Verify network connectivity to backend
- Check server logs for /api/llm/search errors

### No Results
- Refine search terms
- Check app selection
- Verify data is indexed
- Try opposite search mode (Lucene vs LLM)

### Slow Results
- LLM analysis takes 1-3 seconds (normal)
- Lucene should be <100ms
- Check network latency
- Reduce result limit

### History Not Saving
- Check browser localStorage enabled
- Verify quota not exceeded
- Try clearing browser cache
- Check browser privacy mode

---

**Last Updated**: 2025-01-XX  
**UI Framework**: React + TypeScript  
**Backend**: Spring Boot Java  
**Search Backends**: Apache Lucene + LLM (Python)
