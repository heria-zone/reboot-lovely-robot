#!/bin/bash

# Automated Duplication Detection Script
# Detects code duplication across Fabric, Forge, and NeoForge loaders
# Part of the multi-loader code extraction duplication prevention measures

set -e

# Configuration
THRESHOLD=${1:-80}
FAIL_ON_VIOLATION=${2:-false}
REPORT_FORMAT=${3:-console}
OUTPUT_FILE=${4:-duplication-report.txt}

# Directories to analyze
FABRIC_DIR="sources/legacy/llovelyr-1.21.1/Fabric/src/main/java"
FORGE_DIR="sources/legacy/llovelyr-1.21.1/Forge/src/main/java"
NEOFORGE_DIR="sources/legacy/llovelyr-1.21.1/NeoForge/src/main/java"
COMMON_DIR="sources/legacy/llovelyr-1.21.1/Common/src/main/java"

# Temporary files
TEMP_DIR=$(mktemp -d)
SIMILARITY_REPORT="$TEMP_DIR/similarity.txt"
VIOLATIONS_REPORT="$TEMP_DIR/violations.txt"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Function to calculate file similarity
calculate_similarity() {
    local file1="$1"
    local file2="$2"
    
    # Skip if either file doesn't exist
    if [[ ! -f "$file1" || ! -f "$file2" ]]; then
        echo "0"
        return
    fi
    
    # Get line counts
    local lines1=$(wc -l < "$file1")
    local lines2=$(wc -l < "$file2")
    
    # Skip very small files
    if [[ $lines1 -lt 10 || $lines2 -lt 10 ]]; then
        echo "0"
        return
    fi
    
    # Calculate similarity using diff
    local common_lines=$(diff --unchanged-line-format="%L" --old-line-format="" --new-line-format="" "$file1" "$file2" | wc -l)
    local max_lines=$((lines1 > lines2 ? lines1 : lines2))
    
    if [[ $max_lines -eq 0 ]]; then
        echo "0"
        return
    fi
    
    # Calculate percentage similarity
    local similarity=$((common_lines * 100 / max_lines))
    echo "$similarity"
}

# Function to normalize file paths for comparison
normalize_path() {
    local path="$1"
    # Remove loader-specific parts and normalize
    echo "$path" | sed -E 's/(Fabric|Forge|NeoForge)\/src\/main\/java\///g' | sed 's/net\/msymbios\/llovelyr\///g'
}

# Function to find corresponding files across loaders
find_corresponding_files() {
    local base_dir="$1"
    local target_dirs=("$2" "$3")
    
    find "$base_dir" -name "*.java" -type f | while read -r file; do
        local normalized=$(normalize_path "$file")
        local base_file=$(basename "$file")
        
        # Skip certain files that are expected to be different
        if [[ "$base_file" =~ ^(.*Services\.java|.*Main\.java|.*ModClient\.java)$ ]]; then
            continue
        fi
        
        for target_dir in "${target_dirs[@]}"; do
            # Try to find corresponding file
            local corresponding=$(find "$target_dir" -name "$base_file" -type f | head -1)
            if [[ -n "$corresponding" ]]; then
                echo "$file|$corresponding"
            fi
        done
    done
}

# Function to check for GeckoLib violations in common
check_geckolib_violations() {
    log_info "Checking for GeckoLib violations in common module..."
    
    local violations=0
    
    if [[ -d "$COMMON_DIR" ]]; then
        while IFS= read -r -d '' file; do
            if grep -q "software\.bernie\.geckolib" "$file"; then
                log_error "GeckoLib import found in common module: $file"
                grep -n "software\.bernie\.geckolib" "$file" >> "$VIOLATIONS_REPORT"
                ((violations++))
            fi
        done < <(find "$COMMON_DIR" -name "*.java" -type f -print0)
    fi
    
    if [[ $violations -eq 0 ]]; then
        log_success "No GeckoLib violations found in common module"
    else
        log_error "Found $violations GeckoLib violations in common module"
    fi
    
    return $violations
}

# Function to analyze duplication between two loaders
analyze_loader_pair() {
    local loader1="$1"
    local loader2="$2"
    local dir1="$3"
    local dir2="$4"
    
    log_info "Analyzing duplication between $loader1 and $loader2..."
    
    local high_similarity_count=0
    local total_comparisons=0
    
    # Find corresponding files
    find_corresponding_files "$dir1" "$dir2" | while IFS='|' read -r file1 file2; do
        local similarity=$(calculate_similarity "$file1" "$file2")
        ((total_comparisons++))
        
        if [[ $similarity -ge $THRESHOLD ]]; then
            ((high_similarity_count++))
            local rel_path1=$(echo "$file1" | sed "s|$dir1/||")
            local rel_path2=$(echo "$file2" | sed "s|$dir2/||")
            
            echo "$loader1|$loader2|$rel_path1|$rel_path2|$similarity" >> "$SIMILARITY_REPORT"
            
            if [[ $similarity -ge 95 ]]; then
                log_warn "High similarity ($similarity%) between:"
                log_warn "  $loader1: $rel_path1"
                log_warn "  $loader2: $rel_path2"
            fi
        fi
    done
    
    echo "$high_similarity_count|$total_comparisons"
}

# Function to generate extraction recommendations
generate_recommendations() {
    log_info "Generating extraction recommendations..."
    
    local recommendations_file="$TEMP_DIR/recommendations.txt"
    
    # Analyze similarity report for extraction opportunities
    if [[ -f "$SIMILARITY_REPORT" ]]; then
        while IFS='|' read -r loader1 loader2 file1 file2 similarity; do
            if [[ $similarity -ge 95 ]]; then
                echo "DIRECT_MIGRATION|$file1|$similarity|High priority - nearly identical code" >> "$recommendations_file"
            elif [[ $similarity -ge 85 ]]; then
                echo "ABSTRACT_BASE_CLASS|$file1|$similarity|Medium priority - high similarity with minor differences" >> "$recommendations_file"
            elif [[ $similarity -ge $THRESHOLD ]]; then
                echo "HELPER_CLASS|$file1|$similarity|Low priority - moderate similarity, consider helper extraction" >> "$recommendations_file"
            fi
        done < "$SIMILARITY_REPORT"
        
        # Sort and deduplicate recommendations
        sort -t'|' -k3 -nr "$recommendations_file" | awk -F'|' '!seen[$2]++' > "$recommendations_file.sorted"
        mv "$recommendations_file.sorted" "$recommendations_file"
    fi
    
    echo "$recommendations_file"
}

# Function to generate report
generate_report() {
    local format="$1"
    local output="$2"
    local recommendations_file="$3"
    
    case "$format" in
        "json")
            generate_json_report "$output" "$recommendations_file"
            ;;
        "markdown")
            generate_markdown_report "$output" "$recommendations_file"
            ;;
        "console"|*)
            generate_console_report "$recommendations_file"
            ;;
    esac
}

# Function to generate JSON report
generate_json_report() {
    local output="$1"
    local recommendations_file="$2"
    
    cat > "$output" << EOF
{
  "timestamp": "$(date -Iseconds)",
  "threshold": $THRESHOLD,
  "summary": {
    "total_violations": $(wc -l < "$VIOLATIONS_REPORT" 2>/dev/null || echo 0),
    "high_similarity_pairs": $(wc -l < "$SIMILARITY_REPORT" 2>/dev/null || echo 0),
    "recommendations": $(wc -l < "$recommendations_file" 2>/dev/null || echo 0)
  },
  "violations": [
EOF

    if [[ -f "$VIOLATIONS_REPORT" ]]; then
        local first=true
        while IFS= read -r line; do
            if [[ "$first" == "true" ]]; then
                first=false
            else
                echo "," >> "$output"
            fi
            echo "    \"$line\"" >> "$output"
        done < "$VIOLATIONS_REPORT"
    fi

    cat >> "$output" << EOF
  ],
  "similarities": [
EOF

    if [[ -f "$SIMILARITY_REPORT" ]]; then
        local first=true
        while IFS='|' read -r loader1 loader2 file1 file2 similarity; do
            if [[ "$first" == "true" ]]; then
                first=false
            else
                echo "," >> "$output"
            fi
            cat >> "$output" << EOF
    {
      "loader1": "$loader1",
      "loader2": "$loader2",
      "file1": "$file1",
      "file2": "$file2",
      "similarity": $similarity
    }
EOF
        done < "$SIMILARITY_REPORT"
    fi

    cat >> "$output" << EOF
  ],
  "recommendations": [
EOF

    if [[ -f "$recommendations_file" ]]; then
        local first=true
        while IFS='|' read -r pattern file similarity description; do
            if [[ "$first" == "true" ]]; then
                first=false
            else
                echo "," >> "$output"
            fi
            cat >> "$output" << EOF
    {
      "pattern": "$pattern",
      "file": "$file",
      "similarity": $similarity,
      "description": "$description"
    }
EOF
        done < "$recommendations_file"
    fi

    cat >> "$output" << EOF
  ]
}
EOF
}

# Function to generate Markdown report
generate_markdown_report() {
    local output="$1"
    local recommendations_file="$2"
    
    cat > "$output" << EOF
# Code Duplication Analysis Report

**Generated**: $(date)  
**Threshold**: $THRESHOLD%  
**Analysis Scope**: Fabric, Forge, NeoForge loaders

## Summary

- **GeckoLib Violations**: $(wc -l < "$VIOLATIONS_REPORT" 2>/dev/null || echo 0)
- **High Similarity Pairs**: $(wc -l < "$SIMILARITY_REPORT" 2>/dev/null || echo 0)
- **Extraction Recommendations**: $(wc -l < "$recommendations_file" 2>/dev/null || echo 0)

## GeckoLib Violations

EOF

    if [[ -f "$VIOLATIONS_REPORT" && -s "$VIOLATIONS_REPORT" ]]; then
        echo "| File | Line | Violation |" >> "$output"
        echo "|------|------|-----------|" >> "$output"
        while IFS= read -r line; do
            local file=$(echo "$line" | cut -d':' -f1)
            local line_num=$(echo "$line" | cut -d':' -f2)
            local violation=$(echo "$line" | cut -d':' -f3-)
            echo "| $file | $line_num | \`$violation\` |" >> "$output"
        done < "$VIOLATIONS_REPORT"
    else
        echo "✅ No GeckoLib violations found." >> "$output"
    fi

    cat >> "$output" << EOF

## High Similarity Files

EOF

    if [[ -f "$SIMILARITY_REPORT" && -s "$SIMILARITY_REPORT" ]]; then
        echo "| Loader 1 | Loader 2 | File | Similarity |" >> "$output"
        echo "|----------|----------|------|------------|" >> "$output"
        while IFS='|' read -r loader1 loader2 file1 file2 similarity; do
            echo "| $loader1 | $loader2 | $file1 | $similarity% |" >> "$output"
        done < "$SIMILARITY_REPORT"
    else
        echo "✅ No high similarity files found above threshold." >> "$output"
    fi

    cat >> "$output" << EOF

## Extraction Recommendations

EOF

    if [[ -f "$recommendations_file" && -s "$recommendations_file" ]]; then
        echo "| Pattern | File | Similarity | Description |" >> "$output"
        echo "|---------|------|------------|-------------|" >> "$output"
        while IFS='|' read -r pattern file similarity description; do
            echo "| $pattern | $file | $similarity% | $description |" >> "$output"
        done < "$recommendations_file"
    else
        echo "✅ No extraction recommendations at this time." >> "$output"
    fi

    cat >> "$output" << EOF

## Next Steps

1. **Address GeckoLib Violations**: Immediately fix any GeckoLib imports in common module
2. **Review High Similarity Files**: Evaluate files with >95% similarity for direct migration
3. **Plan Extractions**: Create extraction plan for recommended components
4. **Update Documentation**: Document any architectural decisions made

---
*Generated by automated duplication detection script*
EOF
}

# Function to generate console report
generate_console_report() {
    local recommendations_file="$1"
    
    echo
    log_info "=== DUPLICATION ANALYSIS REPORT ==="
    echo
    
    # Summary
    local violations=$(wc -l < "$VIOLATIONS_REPORT" 2>/dev/null || echo 0)
    local similarities=$(wc -l < "$SIMILARITY_REPORT" 2>/dev/null || echo 0)
    local recommendations=$(wc -l < "$recommendations_file" 2>/dev/null || echo 0)
    
    echo "Summary:"
    echo "  GeckoLib Violations: $violations"
    echo "  High Similarity Pairs: $similarities"
    echo "  Extraction Recommendations: $recommendations"
    echo
    
    # GeckoLib violations
    if [[ $violations -gt 0 ]]; then
        log_error "GeckoLib Violations Found:"
        while IFS= read -r line; do
            echo "  $line"
        done < "$VIOLATIONS_REPORT"
        echo
    fi
    
    # High similarity files
    if [[ $similarities -gt 0 ]]; then
        log_warn "High Similarity Files (>=$THRESHOLD%):"
        while IFS='|' read -r loader1 loader2 file1 file2 similarity; do
            echo "  $similarity% similarity: $loader1/$file1 ↔ $loader2/$file2"
        done < "$SIMILARITY_REPORT"
        echo
    fi
    
    # Recommendations
    if [[ $recommendations -gt 0 ]]; then
        log_info "Extraction Recommendations:"
        while IFS='|' read -r pattern file similarity description; do
            echo "  [$pattern] $file ($similarity%) - $description"
        done < "$recommendations_file"
        echo
    fi
    
    # Overall status
    if [[ $violations -eq 0 && $similarities -eq 0 ]]; then
        log_success "No duplication issues found!"
    elif [[ $violations -gt 0 ]]; then
        log_error "Critical issues found - GeckoLib violations must be fixed immediately"
    else
        log_warn "Duplication opportunities found - consider extraction"
    fi
}

# Main execution
main() {
    log_info "Starting duplication detection analysis..."
    log_info "Threshold: $THRESHOLD%"
    log_info "Report format: $REPORT_FORMAT"
    
    # Initialize report files
    > "$SIMILARITY_REPORT"
    > "$VIOLATIONS_REPORT"
    
    # Check for GeckoLib violations
    local geckolib_violations=0
    check_geckolib_violations || geckolib_violations=$?
    
    # Analyze duplication between loader pairs
    local total_violations=0
    
    if [[ -d "$FABRIC_DIR" && -d "$FORGE_DIR" ]]; then
        local result=$(analyze_loader_pair "Fabric" "Forge" "$FABRIC_DIR" "$FORGE_DIR")
        local violations=$(echo "$result" | cut -d'|' -f1)
        total_violations=$((total_violations + violations))
    fi
    
    if [[ -d "$FABRIC_DIR" && -d "$NEOFORGE_DIR" ]]; then
        local result=$(analyze_loader_pair "Fabric" "NeoForge" "$FABRIC_DIR" "$NEOFORGE_DIR")
        local violations=$(echo "$result" | cut -d'|' -f1)
        total_violations=$((total_violations + violations))
    fi
    
    if [[ -d "$FORGE_DIR" && -d "$NEOFORGE_DIR" ]]; then
        local result=$(analyze_loader_pair "Forge" "NeoForge" "$FORGE_DIR" "$NEOFORGE_DIR")
        local violations=$(echo "$result" | cut -d'|' -f1)
        total_violations=$((total_violations + violations))
    fi
    
    # Generate recommendations
    local recommendations_file=$(generate_recommendations)
    
    # Generate report
    generate_report "$REPORT_FORMAT" "$OUTPUT_FILE" "$recommendations_file"
    
    # Cleanup
    rm -rf "$TEMP_DIR"
    
    # Exit with appropriate code
    local exit_code=0
    if [[ $geckolib_violations -gt 0 ]]; then
        exit_code=2  # Critical violations
    elif [[ $total_violations -gt 0 && "$FAIL_ON_VIOLATION" == "true" ]]; then
        exit_code=1  # Duplication violations
    fi
    
    if [[ $exit_code -eq 0 ]]; then
        log_success "Duplication analysis completed successfully"
    elif [[ $exit_code -eq 1 ]]; then
        log_error "Duplication violations found"
    else
        log_error "Critical GeckoLib violations found"
    fi
    
    exit $exit_code
}

# Help function
show_help() {
    cat << EOF
Usage: $0 [THRESHOLD] [FAIL_ON_VIOLATION] [REPORT_FORMAT] [OUTPUT_FILE]

Automated duplication detection for multi-loader Minecraft mod.

Arguments:
  THRESHOLD          Similarity threshold percentage (default: 80)
  FAIL_ON_VIOLATION  Exit with error on violations (default: false)
  REPORT_FORMAT      Output format: console, json, markdown (default: console)
  OUTPUT_FILE        Output file for json/markdown reports (default: duplication-report.txt)

Examples:
  $0                                    # Basic analysis with console output
  $0 90                                # Higher threshold
  $0 80 true                          # Fail on violations
  $0 80 false markdown report.md      # Markdown report
  $0 85 true json report.json         # JSON report with failure

Exit codes:
  0 - No issues found
  1 - Duplication violations found (when FAIL_ON_VIOLATION=true)
  2 - Critical GeckoLib violations found
EOF
}

# Check for help flag
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
    show_help
    exit 0
fi

# Run main function
main "$@"