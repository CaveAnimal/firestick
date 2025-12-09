import requests
import time
import json
import sys

BASE_URL = "http://localhost:8001"

def test_expand_query(query):
    print(f"\nTesting Expand Query: '{query}'")
    start = time.time()
    try:
        response = requests.post(f"{BASE_URL}/api/llm/expand-query", json={"query": query})
        elapsed = time.time() - start
        if response.status_code == 200:
            data = response.json()
            print(f"Success ({elapsed:.2f}s)")
            print(f"Expanded Terms: {data.get('expanded_terms')}")
        else:
            print(f"Failed ({response.status_code}): {response.text}")
    except Exception as e:
        print(f"Error: {e}")

def test_answer_question(query, context):
    print(f"\nTesting Answer Question: '{query}'")
    start = time.time()
    try:
        response = requests.post(f"{BASE_URL}/api/llm/answer-question", json={
            "query": query,
            "context_chunks": context
        })
        elapsed = time.time() - start
        if response.status_code == 200:
            data = response.json()
            print(f"Success ({elapsed:.2f}s)")
            print(f"Answer: {data.get('answer')}")
        else:
            print(f"Failed ({response.status_code}): {response.text}")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    print("Checking service health...")
    try:
        requests.get(f"{BASE_URL}/health", timeout=2)
    except:
        print("Service not running. Please start it with 'start_llm_service.bat'")
        sys.exit(1)

    test_expand_query("Where is authentication handled?")
    test_expand_query("How do I save a report?")
    
    context = [
        "public class AuthProvider { public boolean authenticate(String user, String pass) { return ldap.check(user, pass); } }",
        "public class ReportService { public void save(Report r) { repo.save(r); } }"
    ]
    test_answer_question("How does auth work?", context)
