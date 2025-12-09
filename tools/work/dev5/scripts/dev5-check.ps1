# dev5-check.ps1 — template health-check / status script
# Usage: run to confirm basic environment sanity for a dev workspace
Write-Host "dev5 check — verifying environment"
Write-Host "Java version:"; & java -version
Write-Host "Maven version:"; & mvn -v
Write-Host "Python available:"; & python --version
# Add any repo-specific checks you want to include
