import { test, expect } from '@playwright/test';

// Minimal smoke test: assumes app is served at baseURL (default http://localhost:5173)
// It checks that the main page renders expected welcome text.

test('home page renders welcome', async ({ page, baseURL }) => {
  await page.goto(baseURL || 'http://localhost:5173');
  // Adjust the text if your App headline differs
  await expect(page.locator('body')).toContainText('Welcome to Firestick UI');
});
