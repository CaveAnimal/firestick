import { test, expect } from '@playwright/test'

// Basic visual regression check for key pages. These screenshots
// serve as a smoke-level baseline and can be improved over time.

const PAGES = [
  { path: '/', name: 'home' },
  { path: '/dashboard', name: 'dashboard' },
]

test.describe('visual regression', () => {
  for (const p of PAGES) {
    test(`page ${p.name} screenshot`, async ({ page, baseURL }) => {
      await page.goto((baseURL || 'http://localhost:5173') + p.path)
      // Wait a moment for charts/layout to settle
      await page.waitForTimeout(300)
      await expect(page).toHaveScreenshot(`${p.name}.png`, { fullPage: true })
    })
  }
})
