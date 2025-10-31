# Git Commit Guidelines

## Introduction & Purpose

Clear and consistent commit messages are essential for maintaining a readable project history, enabling efficient debugging, and facilitating automated processes. Well-structured commit messages help developers:

- **Quickly scan** the project history to understand changes
- **Debug issues** using `git log --grep` to find specific types of changes
- **Generate changelogs** automatically from commit history
- **Review code** more effectively during pull requests
- **Track feature development** and bug fixes over time

### The Golden Rule

**Every commit message must begin with a recognized prefix in the format: `PREFIX: Brief description`.**

This standardization ensures consistency across all contributors and makes our Git history a powerful tool for project management and debugging.

## The Commit Prefix System

All commit messages must start with one of the following prefixes. Choose the prefix that best describes the primary purpose of your commit:

| Prefix | Meaning | Example Commit Message |
|--------|---------|------------------------|
| `ADD:` | Adding a new feature, file, or significant code block | `ADD: User authentication system with JWT tokens` |
| `REF:` | Refactoring existing code without changing behavior | `REF: Extract database connection logic into separate module` |
| `REM:` | Removing files, features, or dead code | `REM: Unused legacy authentication middleware` |
| `FIX:` | Fixing a bug or resolving an issue | `FIX: Prevent memory leak in media processing pipeline` |
| `NULL:` | Changes that don't affect production code | `NULL: Update code formatting and remove trailing spaces` |
| `DOCS:` | Documentation-related changes | `DOCS: Add API endpoint documentation for user management` |
| `TEST:` | Adding or modifying tests | `TEST: Add unit tests for channel creation service` |
| `CFG:` | Configuration changes | `CFG: Update Docker compose for Redis caching support` |

### Prefix Selection Guidelines

- **Use `ADD:`** for new functionality, components, or files that extend the application's capabilities
- **Use `REF:`** when improving code structure, readability, or performance without changing behavior
- **Use `REM:`** when deleting code, files, or features (be explicit about what's being removed)
- **Use `FIX:`** for any bug fixes, error corrections, or issue resolutions
- **Use `NULL:`** for cosmetic changes like formatting, comments, or whitespace (use sparingly)
- **Use `DOCS:`** for README updates, code comments, API documentation, or guides
- **Use `TEST:`** for test files, test utilities, or testing configuration changes
- **Use `CFG:`** for environment variables, build scripts, CI/CD pipelines, or deployment configurations

## Commit Message Structure & Rules

### The Subject Line (Mandatory)

**Format:** `PREFIX: Brief imperative description under 50 characters`

**Rules:**
- Use imperative mood (e.g., "Add", "Fix", "Update" not "Added", "Fixed", "Updated")
- Keep it under 50 characters for better Git tool compatibility
- Don't end with a period
- Be specific but concise

**Examples:**

❌ **Bad:**
```
Fixed the bug with login.
add new feature
Updated some files
```

✅ **Good:**
```
FIX: Correct login timeout handling
ADD: Channel scheduling algorithm
REF: Simplify media metadata extraction
```

### The Body (Optional, but recommended for complex commits)

**When to include a body:**
- The change is complex or affects multiple components
- You need to explain the reasoning behind the change
- The commit resolves a specific issue or implements a feature request

**Body formatting rules:**
- Separate the subject from the body with a blank line
- Wrap lines at 72 characters for better readability
- Explain **what** and **why** you changed, not **how** (the code shows that)
- Use bullet points for multiple changes
- Include references to issues, tickets, or pull requests

**Example of a complete commit message:**

```
REF: Simplify user authentication logic

- Extract password validation into separate helper function for better testability
- Remove redundant null checks as the ORM already handles them
- Consolidate error handling to use consistent error response format

This refactoring improves code maintainability and reduces duplication
across authentication endpoints.

Refs #128
```

### Issue and Ticket References

Include references to issues or tickets when applicable:

- `Closes #123` - Automatically closes the issue when merged
- `Fixes #456` - Same as "Closes"
- `Refs #789` - References the issue without closing it
- `Resolves JIRA-PROJ-123` - For external ticket systems

## Validation & Enforcement

### Manual Review Process

All Pull Requests will be reviewed for commit message compliance. PRs with non-compliant commit messages will be flagged and require updates before merging.

**Common issues to avoid:**
- Missing or incorrect prefixes
- Vague descriptions ("fix stuff", "update code")
- Subject lines over 50 characters
- Using past tense instead of imperative mood

### Automated Validation (Recommended)

We provide installation scripts to automatically set up commit message validation:

**For Unix/Linux/macOS:**
```bash
./scripts/install-git-hooks.sh
```

**For Windows:**
```cmd
scripts\install-git-hooks.bat
```

These scripts install a `commit-msg` hook that validates your commit messages before they're accepted. The hook will reject commits that don't follow our format and provide helpful guidance.

**Manual Installation:**
If you prefer to install the hook manually, create `.git/hooks/commit-msg`:

```bash
#!/bin/sh
# Libbox Git Commit Message Hook

commit_regex='^(ADD|REF|REM|FIX|NULL|DOCS|TEST|CFG): .{1,50}$'

if ! grep -qE "$commit_regex" "$1"; then
    echo "❌ Invalid commit message format!"
    echo "📋 Required format: PREFIX: Brief description (under 50 chars)"
    echo "✅ Valid prefixes: ADD, REF, REM, FIX, NULL, DOCS, TEST, CFG"
    echo "📖 See docs/development/GIT_COMMIT_GUIDELINES.md for details"
    exit 1
fi
```

Then make it executable: `chmod +x .git/hooks/commit-msg`

## Benefits & Quick Reference

### Benefits of This System

- **Scannable History:** Quickly identify types of changes with `git log --oneline`
- **Targeted Searches:** Find specific changes with `git log --grep="FIX:"`
- **Automated Changelogs:** Generate release notes from commit prefixes
- **Better Reviews:** Reviewers immediately understand the commit's purpose
- **Debugging Aid:** Quickly locate when bugs were introduced or fixed

### Quick Reference Cheat Sheet

**Most Common Prefixes:**

```
ADD:  New features, files, or functionality
FIX:  Bug fixes and error corrections
REF:  Code refactoring and improvements
DOCS: Documentation updates
TEST: Test additions or modifications
CFG:  Configuration and setup changes
```

**Quick Examples:**
```bash
git commit -m "ADD: Media upload endpoint with validation"
git commit -m "FIX: Channel scheduling conflict resolution"
git commit -m "REF: Extract common database utilities"
git commit -m "DOCS: Update API documentation for v2 endpoints"
git commit -m "TEST: Add integration tests for user service"
git commit -m "CFG: Configure Redis for session storage"
```

### Git Aliases for Efficiency

Add these aliases to your `.gitconfig` for faster commits:

```ini
[alias]
    ca = commit -m "ADD: "
    cf = commit -m "FIX: "
    cr = commit -m "REF: "
    cd = commit -m "DOCS: "
    ct = commit -m "TEST: "
    cc = commit -m "CFG: "
```

Usage: `git ca "User registration system"` → `ADD: User registration system`

---

## Enforcement

These guidelines are mandatory for all contributors. Commits that don't follow this format will be rejected during code review. When in doubt, choose the prefix that best represents the primary purpose of your change.

For questions about these guidelines or specific prefix usage, please reach out to the development team leads or create an issue in the project repository.