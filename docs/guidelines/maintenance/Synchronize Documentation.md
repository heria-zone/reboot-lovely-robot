# Documentation Synchronization Guide

## Purpose
This guide defines the efficient process for synchronizing project documentation with implementation state, following the steering document hierarchy and modification rules.

## Quick Reference

### Document Hierarchy (from workflows.md)
```
CONCEPT (immutable) → DESIGN (immutable) → PLANNING (immutable)
  ↓
ARCHITECTURE ← → CURRENT_STATE (reality check)
  ↓
ROADMAP → SPRINT_PLANNING → TASK (active work)
```

### Modification Permissions (from development.md)

**Always Safe to Modify:**
- `docs/development/sprints/active/*.md` (daily)
- `docs/workflow/CURRENT_STATE.md` (weekly or after significant changes)
- `docs/workflow/SPRINT_PLANNING.md` (sprint boundaries)
- `docs/documentation/` (as needed)
- `CHANGELOG.md` (releases)

**Modify with Care:**
- `docs/workflow/ROADMAP.md` (milestone changes only)
- `docs/workflow/ARCHITECTURE.md` (architectural decisions only)

**Never Modify:**
- `docs/project/` (immutable vision documents)
- `docs/guidelines/` (requires approval)

## Synchronization Protocol

### Phase 1: Implementation Scan
**Target**: `sources/`
**Time**: ~5 minutes

1. List feature directories to identify implemented modules
2. Check for new services, models, providers, widgets
3. Identify completed vs in-progress work
4. Note undocumented features

### Phase 2: Sprint Status Check
**Target**: `docs/development/sprints/active/` and `SPRINT_PLANNING.md`
**Time**: ~3 minutes

1. Read active sprint TASK files
2. Compare task status with implementation
3. Identify completed tasks not marked done
4. Determine if sprint should be archived

### Phase 3: Document Updates
**Priority Order** (from documentation.md):
**Time**: ~10 minutes

1. **CURRENT_STATE.md** - Update component catalog if components changed
2. **SPRINT_PLANNING.md** - Mark completed stories, update progress
3. **Active TASK.md** - Mark completed tasks with ✅
4. **CHANGELOG.md** - Add release-worthy features

### Phase 4: Validation
**Time**: ~2 minutes

Cross-reference check:
- Does CURRENT_STATE match code?
- Does SPRINT_PLANNING reflect completion?
- Are completed sprints archived?
- Is CHANGELOG current?

## Update Cascade Rules (from workflows.md)

**Daily**: Active TASK.md
**Weekly**: CURRENT_STATE.md  
**Sprint Boundaries**: SPRINT_PLANNING.md
**Milestone Changes**: ROADMAP.md
**Releases**: CHANGELOG.md

## Archival Protocol (from project-structure.md)

**When Sprint Complete:**
1. Move from `docs/development/sprints/active/SPRINT_X_TASK.md`
2. To `docs/development/sprints/archive/[COMPLETED]_SPRINT_X_YYYY-MM-DD.md`
3. Update SPRINT_PLANNING.md with outcomes
4. **Never delete** historical records

## Document-Specific Update Rules

### CURRENT_STATE.md
**Update When**: Components added/removed, architecture changes
**Update Frequency**: Weekly or after significant changes
**Content**: Component catalog, implementation status, technical debt

### SPRINT_PLANNING.md
**Update When**: Sprint boundaries (start/end), scope changes
**Update Frequency**: Sprint boundaries
**Content**: Sprint status, user story completion, story points

### Active TASK.md
**Update When**: Task progress, completion, blockers
**Update Frequency**: Daily during active development
**Content**: Task status, implementation notes, completion dates

### ROADMAP.md
**Update When**: Milestone changes, timeline adjustments
**Update Frequency**: Milestone changes only
**Content**: Milestone dates, deliverables, phase status

### ARCHITECTURE.md
**Update When**: Architectural decisions, pattern changes
**Update Frequency**: Architectural decisions only
**Content**: System architecture, design patterns, technical decisions

### CHANGELOG.md
**Update When**: Version releases, significant features
**Update Frequency**: At release points
**Content**: Version entries, features, breaking changes (Keep a Changelog format)

## Code Style Compliance

Ensure all code examples follow:
- `docs/guidelines/Coding Style Enforcer.md`

## Efficiency Guidelines

1. **Focus on changed areas** - don't audit entire codebase unnecessarily
2. **Update only what's needed** - follow update cascade rules
3. **Respect modification permissions** - never touch immutable docs
4. **Be concise** - integrate findings into existing docs, don't create reports
5. **Follow naming conventions** - use proper patterns from project-structure.md

## Git Commit Guidelines

Follow `docs/guidelines/maintenance/GIT_COMMIT_GUIDELINES.md`:

**Documentation updates:**
```bash
git commit -m "DOCS: Synchronize sprint status with implementation"
git commit -m "DOCS: Update CURRENT_STATE with new components"
git commit -m "DOCS: Archive completed Sprint 2"
```

**Notes:**
Add body to the commit with a little more detail, just a brief description or in detail if required.

## Common Scenarios

### Scenario 1: Sprint Just Completed
1. Mark all completed tasks in TASK.md with ✅
2. Update SPRINT_PLANNING.md with completion status
3. Archive sprint file to `docs/development/sprints/archive/[COMPLETED]_SPRINT_X_YYYY-MM-DD.md`
4. Update CURRENT_STATE.md with new components
5. Add CHANGELOG.md entry if releasing

### Scenario 2: New Features Implemented
1. Update CURRENT_STATE.md component catalog
2. Mark related tasks complete in TASK.md
3. Update SPRINT_PLANNING.md progress
4. Add to CHANGELOG.md if release-worthy

### Scenario 3: Weekly Sync
1. Scan `sources/` for changes
2. Update CURRENT_STATE.md if components changed
3. Check active TASK.md for completion status
4. Validate SPRINT_PLANNING.md accuracy

## Validation Checklist

Before completing synchronization:
- [ ] CURRENT_STATE.md matches actual code
- [ ] SPRINT_PLANNING.md reflects task completion
- [ ] Completed sprints properly archived
- [ ] CHANGELOG.md current for releases
- [ ] No immutable documents modified
- [ ] All updates follow cascade rules
- [ ] Commit messages use DOCS: prefix

---

**Key Principle**: Efficient, focused synchronization following the document hierarchy and modification rules. Update only what's needed, when it's needed.