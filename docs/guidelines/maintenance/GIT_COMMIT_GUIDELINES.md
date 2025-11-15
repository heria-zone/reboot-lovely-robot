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
- **Wrap lines at 72 characters** for better readability in Git tools
- Explain **what** and **why** you changed, not **how** (the code shows that)
- Use bullet points for multiple changes
- Include references to issues, tickets, or pull requests

#### Why 72 Characters for Body Lines?

The 72-character limit for body lines is a Git best practice that ensures optimal readability across all Git tools and terminal widths:

- **Terminal compatibility**: Most terminals default to 80 columns. The 72-character limit leaves room for indentation in `git log` output and email clients
- **Git tool formatting**: Tools like `git log`, `git show`, and GitHub's web interface add indentation or line prefixes, which would cause longer lines to wrap awkwardly
- **Email compatibility**: Git was designed with email-based workflows in mind, where 72 characters is the standard for quoted text
- **Readability**: Research shows that lines between 50-75 characters are easiest to read, reducing eye strain and improving comprehension

**Example of a complete commit message:**

```
REF: Simplify user authentication logic

- Extract password validation into separate helper function
  for better testability
- Remove redundant null checks as the ORM already handles
  them
- Consolidate error handling to use consistent error
  response format

This refactoring improves code maintainability and reduces
duplication across authentication endpoints.

Refs #128
```

#### Properly Wrapped Body Text Examples

**Good - Lines wrapped at natural breaking points:**
```
ADD: User authentication system with JWT tokens

This commit implements a comprehensive authentication system
using JWT tokens for session management. The system includes
several key features:

- User registration with email verification
- Login with password hashing using bcrypt algorithm
- Token refresh mechanism for extended sessions
- Logout with token invalidation

The implementation follows OWASP security guidelines and
includes rate limiting to prevent brute force attacks.
```

**Bad - Lines exceed 72 characters:**
```
ADD: User authentication system with JWT tokens

This commit implements a comprehensive authentication system using JWT tokens for session management.

The system includes user registration with email verification, login with password hashing using bcrypt, and token refresh.
```

#### Multi-Paragraph Bodies

Use blank lines to separate paragraphs for better organization:

```
FIX: Resolve race condition in media processing pipeline

The media processor was experiencing intermittent failures
when multiple uploads occurred simultaneously. This was
caused by shared state in the processing queue.

This fix introduces a per-upload processing context that
isolates state between concurrent operations. Each upload
now maintains its own progress tracking and error handling.

The solution has been tested with 100 concurrent uploads
and shows no failures. Performance impact is negligible
(< 5ms overhead per upload).

Fixes #234
```

#### Bullet Point Lists

When using bullet points, ensure each line (including indentation) stays within 72 characters:

```
ADD: Channel scheduling algorithm with conflict detection

Implemented features:
- Automatic conflict detection for overlapping time slots
- Priority-based resolution when conflicts occur
- User notification system for schedule changes
- Bulk scheduling with validation

Technical details:
- Uses interval tree data structure for O(log n) lookups
- Caches schedule data to minimize database queries
- Supports recurring events with exception handling
```

**Tip for bullet points:** If a bullet point is too long, break it into multiple lines with proper indentation:

```
- This is a long bullet point that needs to be wrapped
  across multiple lines to stay within the 72-character
  limit for readability
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

We provide installation scripts to automatically set up commit message validation. These scripts install a `commit-msg` hook that validates your commit messages before they're accepted and provides helpful guidance when validation fails.

#### Installation Instructions

**For Unix/Linux/macOS:**

1. Open a terminal and navigate to the repository root directory
2. Run the installation script:
   ```bash
   ./scripts/main/install-git-hooks.sh
   ```
3. If the hook already exists, you'll be prompted to confirm overwriting it
4. The script will display a success message when installation is complete

**For Windows (Command Prompt or PowerShell):**

1. Open Command Prompt or PowerShell and navigate to the repository root directory
2. Run the installation script:
   ```cmd
   scripts\main\install-git-hooks.bat
   ```
3. If the hook already exists, you'll be prompted to confirm overwriting it
4. The script will display a success message when installation is complete

**What the installer does:**
- Validates that you're in a Git repository (checks for `.git` directory)
- Creates the `.git/hooks` directory if it doesn't exist
- Installs the `commit-msg` hook with embedded validation logic
- Sets appropriate executable permissions (Unix/Mac only)
- Provides clear success/error messages

**Verification:**

After installation, test the hook by attempting a commit with an invalid message:
```bash
git commit -m "test message"
```

You should see an error message explaining the required format. Then try a valid commit:
```bash
git commit -m "TEST: Verify commit hook installation"
```

This should succeed without errors.

#### Manual Installation (Advanced)

If you prefer to install the hook manually or need to customize it:

1. Create the file `.git/hooks/commit-msg` with the validation logic (see `scripts/main/commit-msg-validation.sh` for reference)

2. Make the hook executable (Unix/Mac only):
   ```bash
   chmod +x .git/hooks/commit-msg
   ```

3. Test the hook as described in the Verification section above

#### Bypass Mechanism

In emergency situations, you can bypass the commit message validation using Git's standard `--no-verify` flag:

```bash
git commit --no-verify -m "Emergency fix"
```

**When to use bypass:**
- **Emergency hotfixes** that need immediate deployment to production
- **Automated commits** from CI/CD systems (if properly configured)
- **Temporary workaround** when the hook has a bug (please report the issue!)

**When NOT to use bypass:**
- **Regular development work** - Always follow commit message standards
- **Personal preference** - Standards exist for team consistency
- **Avoiding code review** - Bypassed commits are still reviewed during PR process

**Important notes:**
- Using `--no-verify` skips ALL Git hooks, not just commit message validation
- Bypassed commits are still subject to code review and may be rejected
- Frequent use of bypass indicates a problem with the standards or hook implementation
- Team leads may audit bypass usage to ensure it's not being abused

#### Troubleshooting

**Problem: "Not in a Git repository" error**

**Solution:** You must run the installer script from the repository root directory (the directory containing the `.git` folder). Navigate to the correct directory and try again:
```bash
cd /path/to/repository-root
./scripts/install-git-hooks.sh
```

---

**Problem: "Permission denied" error (Unix/Mac)**

**Solution:** The script needs execute permissions. Run:
```bash
chmod +x scripts/main/install-git-hooks.sh
./scripts/main/install-git-hooks.sh
```

---

**Problem: "Execution policy" error (Windows PowerShell)**

**Solution:** The batch script automatically bypasses execution policy, but if you're running the PowerShell script directly, use:
```powershell
powershell -ExecutionPolicy Bypass -File scripts/main/install-git-hooks.ps1
```

---

**Problem: Hook doesn't run when committing**

**Possible causes and solutions:**

1. **Hook file not executable (Unix/Mac):**
   ```bash
   chmod +x .git/hooks/commit-msg
   ```

2. **Hook file has wrong line endings (Windows):**
   - The hook must use Unix (LF) line endings for Git Bash
   - Reinstall using the provided scripts, which handle this automatically

3. **Git Bash not installed (Windows):**
   - Install Git for Windows from https://git-scm.com/download/win
   - Git Bash is required to execute shell script hooks on Windows

4. **Using `--no-verify` flag:**
   - Check if you or your Git client is automatically adding `--no-verify`
   - Remove the flag to enable hook execution

---

**Problem: Hook rejects valid commit messages**

**Solution:** Verify your commit message format:
- Must start with a valid prefix: ADD, REF, REM, FIX, NULL, DOCS, TEST, CFG
- Must have a colon and space after the prefix: `PREFIX: `
- Description must be 1-50 characters (after the prefix and space)
- Prefix must be uppercase
- Body lines (if present) must be 72 characters or fewer

Example of valid format:
```bash
git commit -m "ADD: User authentication system"
```

---

**Problem: "Commit message body line too long" error**

**Solution:** One or more lines in your commit body exceed 72 characters. To fix:

1. **Identify the problematic line**: The error message shows the line number and actual length
2. **Break long lines**: Split the line at a natural point (punctuation, conjunction, or phrase boundary)
3. **Check all lines**: Ensure every body line is 72 characters or fewer

**Example error:**
```
❌ Commit message body line too long!

Line 5 exceeds 72 characters (actual: 89 characters)

Offending line:
"This is an example of a very long line that exceeds the seventy-two character limit"
```

**How to fix:**
```bash
# Before (line too long)
git commit -m "ADD: Feature" -m "This is an example of a very long line that exceeds the seventy-two character limit"

# After (properly wrapped)
git commit -m "ADD: Feature" -m "This is an example of a properly wrapped line that stays
within the seventy-two character limit by breaking at a
natural point."
```

**Tips for wrapping:**
- Break at punctuation marks (periods, commas, semicolons)
- Break at conjunctions (and, but, or, because)
- Break at natural phrase boundaries
- Use your text editor to compose longer commit messages with proper wrapping
- Most editors can show a ruler or guide at column 72

**Using an editor for commit messages:**
```bash
# Opens your default editor for composing the message
git commit

# In the editor, you can see line lengths and wrap properly
# Lines starting with # are comments and ignored
```

---

**Problem: Hook accepts invalid commit messages**

**Solution:** The hook may be outdated or corrupted. Reinstall it:
```bash
# Unix/Mac
./scripts/main/install-git-hooks.sh

# Windows
scripts\main\install-git-hooks.bat
```

---

**Problem: Hook was accidentally deleted**

**Solution:** Simply run the installer script again. It will recreate the hook:
```bash
# Unix/Mac
./scripts/main/install-git-hooks.sh

# Windows
scripts\main\install-git-hooks.bat
```

---

**Problem: Need to customize validation rules**

**Solution:** The hook file contains a configuration section at the top where you can modify:
- Valid prefixes list
- Maximum subject line length
- Minimum subject line length

Edit `.git/hooks/commit-msg` and modify the configuration section:
```bash
VALID_PREFIXES="ADD|REF|REM|FIX|NULL|DOCS|TEST|CFG"
MAX_SUBJECT_LENGTH=50
MIN_SUBJECT_LENGTH=1
```

**Note:** Custom modifications will be lost if you reinstall the hook. Consider contributing changes to the main installer scripts if your customizations would benefit the team.

---

**Problem: Hook interferes with Git GUI clients**

**Solution:** Most Git GUI clients respect Git hooks. If you experience issues:
1. Verify the GUI client supports Git hooks (most modern clients do)
2. Check the client's settings for hook-related options
3. Use the command line for commits if the GUI client has compatibility issues
4. Report the issue to the GUI client's developers

---

**Problem: Multiple developers have different hook versions**

**Solution:** Ensure all developers reinstall the hook after updates:
1. Announce hook updates to the team
2. Have everyone run the installer script
3. Consider adding hook installation to onboarding documentation
4. The hook version is displayed in the file header for verification

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
