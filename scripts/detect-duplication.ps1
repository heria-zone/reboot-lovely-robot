# Automated Duplication Detection Script (PowerShell)
# Detects code duplication across Fabric, Forge, and NeoForge loaders
# Part of the multi-loader code extraction duplication prevention measures

param(
    [int]$Threshold = 80,
    [bool]$FailOnViolation = $false,
    [string]$ReportFormat = "console",
    [string]$OutputFile = "duplication-report.txt"
)

# Configuration
$FabricDir = "sources/legacy/llovelyr-1.21.1/Fabric/src/main/java"
$ForgeDir = "sources/legacy/llovelyr-1.21.1/Forge/src/main/java"
$NeoForgeDir = "sources/legacy/llovelyr-1.21.1/NeoForge/src/main/java"
$CommonDir = "sources/legacy/llovelyr-1.21.1/Common/src/main/java"

# Temporary files
$TempDir = [System.IO.Path]::GetTempPath() + [System.Guid]::NewGuid().ToString()
New-Item -ItemType Directory -Path $TempDir -Force | Out-Null
$SimilarityReport = Join-Path $TempDir "similarity.txt"
$ViolationsReport = Join-Path $TempDir "violations.txt"

# Initialize report files
New-Item -ItemType File -Path $SimilarityReport -Force | Out-Null
New-Item -ItemType File -Path $ViolationsReport -Force | Out-Null

# Logging functions
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Warn {
    param([string]$Message)
    Write-Host "[WARN] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

# Function to calculate file similarity
function Get-FileSimilarity {
    param(
        [string]$File1,
        [string]$File2
    )
    
    # Skip if either file doesn't exist
    if (-not (Test-Path $File1) -or -not (Test-Path $File2)) {
        return 0
    }
    
    # Get file contents
    $content1 = Get-Content $File1 -ErrorAction SilentlyContinue
    $content2 = Get-Content $File2 -ErrorAction SilentlyContinue
    
    if (-not $content1 -or -not $content2) {
        return 0
    }
    
    # Skip very small files
    if ($content1.Count -lt 10 -or $content2.Count -lt 10) {
        return 0
    }
    
    # Calculate similarity using line comparison
    $lines1 = @($content1)
    $lines2 = @($content2)
    $maxLines = [Math]::Max($lines1.Count, $lines2.Count)
    
    if ($maxLines -eq 0) {
        return 0
    }
    
    # Find common lines (simple approach)
    $commonLines = 0
    $minLines = [Math]::Min($lines1.Count, $lines2.Count)
    
    for ($i = 0; $i -lt $minLines; $i++) {
        if ($lines1[$i].Trim() -eq $lines2[$i].Trim()) {
            $commonLines++
        }
    }
    
    # Calculate percentage similarity
    $similarity = [Math]::Round(($commonLines * 100) / $maxLines)
    return $similarity
}

# Function to normalize file paths for comparison
function Get-NormalizedPath {
    param([string]$Path)
    
    # Remove loader-specific parts and normalize
    $normalized = $Path -replace "(Fabric|Forge|NeoForge)\\src\\main\\java\\", ""
    $normalized = $normalized -replace "net\\msymbios\\llovelyr\\", ""
    return $normalized
}

# Function to find corresponding files across loaders
function Find-CorrespondingFiles {
    param(
        [string]$BaseDir,
        [string[]]$TargetDirs
    )
    
    $results = @()
    
    if (-not (Test-Path $BaseDir)) {
        return $results
    }
    
    $javaFiles = Get-ChildItem -Path $BaseDir -Filter "*.java" -Recurse -File
    
    foreach ($file in $javaFiles) {
        $baseName = $file.Name
        
        # Skip certain files that are expected to be different
        if ($baseName -match "^(.*Services\.java|.*Main\.java|.*ModClient\.java)$") {
            continue
        }
        
        foreach ($targetDir in $TargetDirs) {
            if (Test-Path $targetDir) {
                $corresponding = Get-ChildItem -Path $targetDir -Filter $baseName -Recurse -File | Select-Object -First 1
                if ($corresponding) {
                    $results += @{
                        File1 = $file.FullName
                        File2 = $corresponding.FullName
                    }
                }
            }
        }
    }
    
    return $results
}

# Function to check for GeckoLib violations in common
function Test-GeckoLibViolations {
    Write-Info "Checking for GeckoLib violations in common module..."
    
    $violations = 0
    
    if (Test-Path $CommonDir) {
        $javaFiles = Get-ChildItem -Path $CommonDir -Filter "*.java" -Recurse -File
        
        foreach ($file in $javaFiles) {
            $content = Get-Content $file.FullName -Raw -ErrorAction SilentlyContinue
            if ($content -and $content -match "software\.bernie\.geckolib") {
                Write-Error "GeckoLib import found in common module: $($file.FullName)"
                $matches = [regex]::Matches($content, "software\.bernie\.geckolib.*")
                foreach ($match in $matches) {
                    Add-Content -Path $ViolationsReport -Value "$($file.FullName):$($match.Value)"
                }
                $violations++
            }
        }
    }
    
    if ($violations -eq 0) {
        Write-Success "No GeckoLib violations found in common module"
    } else {
        Write-Error "Found $violations GeckoLib violations in common module"
    }
    
    return $violations
}

# Function to analyze duplication between two loaders
function Test-LoaderPairDuplication {
    param(
        [string]$Loader1,
        [string]$Loader2,
        [string]$Dir1,
        [string]$Dir2
    )
    
    Write-Info "Analyzing duplication between $Loader1 and $Loader2..."
    
    $highSimilarityCount = 0
    $totalComparisons = 0
    
    $correspondingFiles = Find-CorrespondingFiles -BaseDir $Dir1 -TargetDirs @($Dir2)
    
    foreach ($filePair in $correspondingFiles) {
        $similarity = Get-FileSimilarity -File1 $filePair.File1 -File2 $filePair.File2
        $totalComparisons++
        
        if ($similarity -ge $Threshold) {
            $highSimilarityCount++
            $relPath1 = $filePair.File1 -replace [regex]::Escape($Dir1 + "\"), ""
            $relPath2 = $filePair.File2 -replace [regex]::Escape($Dir2 + "\"), ""
            
            Add-Content -Path $SimilarityReport -Value "$Loader1|$Loader2|$relPath1|$relPath2|$similarity"
            
            if ($similarity -ge 95) {
                Write-Warn "High similarity ($similarity%) between:"
                Write-Warn "  $Loader1`: $relPath1"
                Write-Warn "  $Loader2`: $relPath2"
            }
        }
    }
    
    return @{
        HighSimilarity = $highSimilarityCount
        Total = $totalComparisons
    }
}

# Function to generate extraction recommendations
function New-ExtractionRecommendations {
    Write-Info "Generating extraction recommendations..."
    
    $recommendationsFile = Join-Path $TempDir "recommendations.txt"
    New-Item -ItemType File -Path $recommendationsFile -Force | Out-Null
    
    if (Test-Path $SimilarityReport) {
        $similarities = Get-Content $SimilarityReport
        $processed = @{}
        
        foreach ($line in $similarities) {
            $parts = $line -split '\|'
            if ($parts.Count -eq 5) {
                $loader1, $loader2, $file1, $file2, $similarity = $parts
                $similarity = [int]$similarity
                
                # Use the first file as the key to avoid duplicates
                $key = $file1
                if (-not $processed.ContainsKey($key)) {
                    $processed[$key] = $similarity
                    
                    if ($similarity -ge 95) {
                        Add-Content -Path $recommendationsFile -Value "DIRECT_MIGRATION|$file1|$similarity|High priority - nearly identical code"
                    } elseif ($similarity -ge 85) {
                        Add-Content -Path $recommendationsFile -Value "ABSTRACT_BASE_CLASS|$file1|$similarity|Medium priority - high similarity with minor differences"
                    } elseif ($similarity -ge $Threshold) {
                        Add-Content -Path $recommendationsFile -Value "HELPER_CLASS|$file1|$similarity|Low priority - moderate similarity, consider helper extraction"
                    }
                }
            }
        }
    }
    
    return $recommendationsFile
}

# Function to generate console report
function Write-ConsoleReport {
    param([string]$RecommendationsFile)
    
    Write-Host ""
    Write-Info "=== DUPLICATION ANALYSIS REPORT ==="
    Write-Host ""
    
    # Summary
    $violations = if (Test-Path $ViolationsReport) { (Get-Content $ViolationsReport).Count } else { 0 }
    $similarities = if (Test-Path $SimilarityReport) { (Get-Content $SimilarityReport).Count } else { 0 }
    $recommendations = if (Test-Path $RecommendationsFile) { (Get-Content $RecommendationsFile).Count } else { 0 }
    
    Write-Host "Summary:"
    Write-Host "  GeckoLib Violations: $violations"
    Write-Host "  High Similarity Pairs: $similarities"
    Write-Host "  Extraction Recommendations: $recommendations"
    Write-Host ""
    
    # GeckoLib violations
    if ($violations -gt 0) {
        Write-Error "GeckoLib Violations Found:"
        $violationLines = Get-Content $ViolationsReport
        foreach ($line in $violationLines) {
            Write-Host "  $line"
        }
        Write-Host ""
    }
    
    # High similarity files
    if ($similarities -gt 0) {
        Write-Warn "High Similarity Files (>=$Threshold%):"
        $similarityLines = Get-Content $SimilarityReport
        foreach ($line in $similarityLines) {
            $parts = $line -split '\|'
            if ($parts.Count -eq 5) {
                $loader1, $loader2, $file1, $file2, $similarity = $parts
                Write-Host "  $similarity% similarity: $loader1/$file1 ↔ $loader2/$file2"
            }
        }
        Write-Host ""
    }
    
    # Recommendations
    if ($recommendations -gt 0) {
        Write-Info "Extraction Recommendations:"
        $recommendationLines = Get-Content $RecommendationsFile
        foreach ($line in $recommendationLines) {
            $parts = $line -split '\|'
            if ($parts.Count -eq 4) {
                $pattern, $file, $similarity, $description = $parts
                Write-Host "  [$pattern] $file ($($similarity)%) - $description"
            }
        }
        Write-Host ""
    }
    
    # Overall status
    if ($violations -eq 0 -and $similarities -eq 0) {
        Write-Success "No duplication issues found!"
    } elseif ($violations -gt 0) {
        Write-Error "Critical issues found - GeckoLib violations must be fixed immediately"
    } else {
        Write-Warn "Duplication opportunities found - consider extraction"
    }
}

# Function to generate markdown report
function New-MarkdownReport {
    param(
        [string]$OutputPath,
        [string]$RecommendationsFile
    )
    
    $violations = if (Test-Path $ViolationsReport) { (Get-Content $ViolationsReport).Count } else { 0 }
    $similarities = if (Test-Path $SimilarityReport) { (Get-Content $SimilarityReport).Count } else { 0 }
    $recommendations = if (Test-Path $RecommendationsFile) { (Get-Content $RecommendationsFile).Count } else { 0 }
    
    $report = @"
# Code Duplication Analysis Report

**Generated**: $(Get-Date)  
**Threshold**: $Threshold%  
**Analysis Scope**: Fabric, Forge, NeoForge loaders

## Summary

- **GeckoLib Violations**: $violations
- **High Similarity Pairs**: $similarities  
- **Extraction Recommendations**: $recommendations

## GeckoLib Violations

"@

    if ($violations -gt 0 -and (Test-Path $ViolationsReport)) {
        $report += @"

| File | Violation |
|------|-----------|
"@
        $violationLines = Get-Content $ViolationsReport
        foreach ($line in $violationLines) {
            $parts = $line -split ':'
            if ($parts.Count -ge 2) {
                $file = $parts[0]
                $violation = ($parts[1..($parts.Count-1)] -join ':').Trim()
                $report += "`n| $file | ``$violation`` |"
            }
        }
    } else {
        $report += "`n✅ No GeckoLib violations found."
    }

    $report += @"

## High Similarity Files

"@

    if ($similarities -gt 0 -and (Test-Path $SimilarityReport)) {
        $report += @"

| Loader 1 | Loader 2 | File | Similarity |
|----------|----------|------|------------|
"@
        $similarityLines = Get-Content $SimilarityReport
        foreach ($line in $similarityLines) {
            $parts = $line -split '\|'
            if ($parts.Count -eq 5) {
                $loader1, $loader2, $file1, $file2, $similarity = $parts
                $report += "`n| $loader1 | $loader2 | $file1 | $similarity% |"
            }
        }
    } else {
        $report += "`n✅ No high similarity files found above threshold."
    }

    $report += @"

## Extraction Recommendations

"@

    if ($recommendations -gt 0 -and (Test-Path $RecommendationsFile)) {
        $report += @"

| Pattern | File | Similarity | Description |
|---------|------|------------|-------------|
"@
        $recommendationLines = Get-Content $RecommendationsFile
        foreach ($line in $recommendationLines) {
            $parts = $line -split '\|'
            if ($parts.Count -eq 4) {
                $pattern, $file, $similarity, $description = $parts
                $report += "`n| $pattern | $file | $similarity% | $description |"
            }
        }
    } else {
        $report += "`n✅ No extraction recommendations at this time."
    }

    $report += @"

## Next Steps

1. **Address GeckoLib Violations**: Immediately fix any GeckoLib imports in common module
2. **Review High Similarity Files**: Evaluate files with >95% similarity for direct migration
3. **Plan Extractions**: Create extraction plan for recommended components
4. **Update Documentation**: Document any architectural decisions made

---
*Generated by automated duplication detection script*
"@

    Set-Content -Path $OutputPath -Value $report -Encoding UTF8
}

# Main execution
function Main {
    Write-Info "Starting duplication detection analysis..."
    Write-Info "Threshold: $Threshold%"
    Write-Info "Report format: $ReportFormat"
    
    # Check for GeckoLib violations
    $geckoLibViolations = Test-GeckoLibViolations
    
    # Analyze duplication between loader pairs
    $totalViolations = 0
    
    if ((Test-Path $FabricDir) -and (Test-Path $ForgeDir)) {
        $result = Test-LoaderPairDuplication -Loader1 "Fabric" -Loader2 "Forge" -Dir1 $FabricDir -Dir2 $ForgeDir
        $totalViolations += $result.HighSimilarity
    }
    
    if ((Test-Path $FabricDir) -and (Test-Path $NeoForgeDir)) {
        $result = Test-LoaderPairDuplication -Loader1 "Fabric" -Loader2 "NeoForge" -Dir1 $FabricDir -Dir2 $NeoForgeDir
        $totalViolations += $result.HighSimilarity
    }
    
    if ((Test-Path $ForgeDir) -and (Test-Path $NeoForgeDir)) {
        $result = Test-LoaderPairDuplication -Loader1 "Forge" -Loader2 "NeoForge" -Dir1 $ForgeDir -Dir2 $NeoForgeDir
        $totalViolations += $result.HighSimilarity
    }
    
    # Generate recommendations
    $recommendationsFile = New-ExtractionRecommendations
    
    # Generate report
    switch ($ReportFormat.ToLower()) {
        "markdown" {
            New-MarkdownReport -OutputPath $OutputFile -RecommendationsFile $recommendationsFile
            Write-Info "Markdown report generated: $OutputFile"
        }
        "console" {
            Write-ConsoleReport -RecommendationsFile $recommendationsFile
        }
        default {
            Write-ConsoleReport -RecommendationsFile $recommendationsFile
        }
    }
    
    # Cleanup
    Remove-Item -Path $TempDir -Recurse -Force -ErrorAction SilentlyContinue
    
    # Exit with appropriate code
    $exitCode = 0
    if ($geckoLibViolations -gt 0) {
        $exitCode = 2  # Critical violations
    } elseif ($totalViolations -gt 0 -and $FailOnViolation) {
        $exitCode = 1  # Duplication violations
    }
    
    if ($exitCode -eq 0) {
        Write-Success "Duplication analysis completed successfully"
    } elseif ($exitCode -eq 1) {
        Write-Error "Duplication violations found"
    } else {
        Write-Error "Critical GeckoLib violations found"
    }
    
    exit $exitCode
}

# Show help if requested
if ($args -contains "-h" -or $args -contains "--help") {
    Write-Host @"
Usage: .\detect-duplication.ps1 [-Threshold <int>] [-FailOnViolation <bool>] [-ReportFormat <string>] [-OutputFile <string>]

Automated duplication detection for multi-loader Minecraft mod.

Parameters:
  -Threshold          Similarity threshold percentage (default: 80)
  -FailOnViolation    Exit with error on violations (default: false)
  -ReportFormat       Output format: console, markdown (default: console)
  -OutputFile         Output file for markdown reports (default: duplication-report.txt)

Examples:
  .\detect-duplication.ps1                                    # Basic analysis with console output
  .\detect-duplication.ps1 -Threshold 90                     # Higher threshold
  .\detect-duplication.ps1 -Threshold 80 -FailOnViolation `$true  # Fail on violations
  .\detect-duplication.ps1 -ReportFormat markdown -OutputFile report.md  # Markdown report

Exit codes:
  0 - No issues found
  1 - Duplication violations found (when FailOnViolation=true)
  2 - Critical GeckoLib violations found
"@
    exit 0
}

# Run main function
Main