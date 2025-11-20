#!/usr/bin/env python3
"""
Test script for CodeLlama LLM service
Verifies the service is working correctly
"""

import requests
import json
import sys
from time import sleep

LLM_URL = "http://127.0.0.1:8001"

def test_health():
    """Test health endpoint"""
    print("Testing health endpoint...")
    try:
        response = requests.get(f"{LLM_URL}/health", timeout=5)
        if response.status_code == 200:
            print("✓ Health check passed")
            data = response.json()
            print(f"  Model loaded: {data.get('model_loaded')}")
            print(f"  Device: {data.get('device')}")
            return True
        else:
            print(f"✗ Health check failed: {response.status_code}")
            return False
    except requests.exceptions.ConnectionError:
        print(f"✗ Cannot connect to {LLM_URL}")
        print("  Make sure the LLM service is running: python llm_service.py")
        return False
    # Also check root endpoint
    try:
        root_resp = requests.get(f"{LLM_URL}/", timeout=5)
        if root_resp.status_code == 200:
            print("✓ Root endpoint returned HTML")
        else:
            print(f"! Root endpoint returned {root_resp.status_code}")
    except Exception:
        print("! Root endpoint not available or timed out - that's okay if running other LLM service.")
    except Exception as e:
        print(f"✗ Error: {e}")
        return False

def test_summarize():
    """Test code summarization"""
    print("\nTesting code summarization...")
    try:
        payload = {
            "code": "public int fibonacci(int n) { if (n <= 1) return n; return fibonacci(n-1) + fibonacci(n-2); }",
            "format": "explanation"
        }
        response = requests.post(f"{LLM_URL}/api/llm/summarize", json=payload, timeout=30)
        if response.status_code == 200:
            data = response.json()
            print("✓ Summarization passed")
            print(f"  Input code: {payload['code'][:50]}...")
            print(f"  Response: {data.get('explanation', 'N/A')[:100]}...")
            return True
        else:
            print(f"✗ Summarization failed: {response.status_code}")
            print(f"  Response: {response.text}")
            return False
    except requests.exceptions.Timeout:
        print("✗ Request timeout (model might still be loading)")
        return False
    except Exception as e:
        print(f"✗ Error: {e}")
        return False

def test_detect_patterns():
    """Test pattern detection"""
    print("\nTesting pattern detection...")
    try:
        payload = {
            "code": """
public class LoggerSingleton {
    private static LoggerSingleton instance;
    
    private LoggerSingleton() {}
    
    public static synchronized LoggerSingleton getInstance() {
        if (instance == null) {
            instance = new LoggerSingleton();
        }
        return instance;
    }
}
"""
        }
        response = requests.post(f"{LLM_URL}/api/llm/detect-patterns", json=payload, timeout=30)
        if response.status_code == 200:
            data = response.json()
            print("✓ Pattern detection passed")
            patterns = data.get('patterns', [])
            if patterns:
                print(f"  Detected patterns: {', '.join(patterns[:3])}")
            else:
                print(f"  Response: {data.get('raw_analysis', 'N/A')[:100]}...")
            return True
        else:
            print(f"✗ Pattern detection failed: {response.status_code}")
            return False
    except requests.exceptions.Timeout:
        print("✗ Request timeout (model might still be loading)")
        return False
    except Exception as e:
        print(f"✗ Error: {e}")
        return False

def test_analyze_relationship():
    """Test relationship analysis"""
    print("\nTesting relationship analysis...")
    try:
        payload = {
            "from_class": "PaymentService",
            "to_class": "BankConnector",
            "context": "Processing credit card payments"
        }
        response = requests.post(f"{LLM_URL}/api/llm/analyze-relationship", json=payload, timeout=30)
        if response.status_code == 200:
            data = response.json()
            print("✓ Relationship analysis passed")
            print(f"  From: {payload['from_class']} -> To: {payload['to_class']}")
            print(f"  Analysis: {data.get('analysis', 'N/A')[:100]}...")
            return True
        else:
            print(f"✗ Relationship analysis failed: {response.status_code}")
            return False
    except requests.exceptions.Timeout:
        print("✗ Request timeout (model might still be loading)")
        return False
    except Exception as e:
        print(f"✗ Error: {e}")
        return False

def main():
    """Run all tests"""
    print("=" * 60)
    print("CodeLlama LLM Service Test Suite")
    print("=" * 60)
    
    results = []
    
    # Test health first
    if not test_health():
        print("\n" + "=" * 60)
        print("LLM service is not running!")
        print("Start it with: python llm_service.py")
        print("=" * 60)
        sys.exit(1)
    
    # Test endpoints
    results.append(("Summarize", test_summarize()))
    results.append(("Patterns", test_detect_patterns()))
    results.append(("Relationships", test_analyze_relationship()))
    
    # Summary
    print("\n" + "=" * 60)
    print("Test Summary")
    print("=" * 60)
    
    passed = sum(1 for _, result in results if result)
    total = len(results)
    
    for name, result in results:
        status = "✓ PASS" if result else "✗ FAIL"
        print(f"{status}: {name}")
    
    print(f"\nTotal: {passed}/{total} tests passed")
    
    if passed == total:
        print("\n✓ All tests passed! LLM service is working correctly.")
        sys.exit(0)
    else:
        print(f"\n✗ {total - passed} test(s) failed.")
        sys.exit(1)

if __name__ == "__main__":
    main()
