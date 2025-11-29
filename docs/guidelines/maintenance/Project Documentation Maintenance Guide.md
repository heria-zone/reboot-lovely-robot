# Project Documentation Maintenance Guide

**Status**: Active  
**Last Updated**: 2025-11-14  
**Related Documents**: 
- [Synchronize Documentation](./Synchronize%20Documentation.md) - Efficient sync protocol
- [steering/workflows.md](../../../steering/workflows.md) - Document navigation
- [steering/development.md](../../../steering/development.md) - File modification rules
- [steering/documentation.md](../../../steering/documentation.md) - Documentation standards

## Purpose

This guide defines the comprehensive maintenance process for keeping project documentation aligned with implementation reality. It works alongside the **Documentation Synchronization Monitor** hook to provide both strategic guidance (this document) and tactical execution (the sync hook).

## The Problem: Document Drift During Development

Document drift occurs when implementation realities diverge from planned documentation, creating confusion and making your documentation system feel like a burden rather than a help.

**Common Symptoms**:
- CURRENT_STATE.md doesn't match actual codebase
- TASK.md contains outdated or completed tasks
- SPRINT_PLANNING.md shows incorrect progress
- Developers avoid documentation because it's "always wrong"

## Core Maintenance Principles

### 1. Embrace the Living Document Mindset
- **All documents are living entities** that should evolve with your project
- **Regular updates are expected**, not failures of planning
- **Documentation reflects reality**, not just initial intentions
- **Steering documents define the rules**, maintenance follows them

### 2. The Change Propagation Rule
When you encounter unforeseen development needs, follow this cascade (from `steering/workflows.md`):

```
Unforeseen Task → TASK.md → CURRENT_STATE.md → SPRINT_PLANNING.md → ROADMAP.md → ARCHITECTURE.md
                    (daily)      (weekly)        (sprint boundaries)   (milestones)   (architectural)
```

## Document Hierarchy & Modification Rules

### Document Categories (from `steering/development.md`)

#### Always Safe to Modify (Frequent Updates)
- `docs/development/sprints/active/TASK.md` - **Daily**
- `docs/workflow/CURRENT_STATE.md` - **Weekly or after significant changes**
- `docs/development/notes/*.md` - **As needed**
- `docs/documentation/` - **When features change**
- Source code in `sources/` - **Following coding standards**

#### Modify with Care (Controlled Updates)
- `docs/workflow/SPRINT_PLANNING.md` - **Sprint boundaries only**
- `docs/workflow/ROADMAP.md` - **Milestone changes only**
- `docs/workflow/ARCHITECTURE.md` - **Architectural decisions only**
- `specs/` - **Maintain accuracy**

#### Never Modify Without Approval (Immutable)
- `docs/project/CONCEPT.md` - **Immutable project vision**
- `docs/project/DESIGN.md` - **Immutable solution design**
- `docs/project/PLANNING.md` - **Immutable technical strategy**
- `docs/guidelines/` - **Requires team approval**
- `hooks/` - **Requires explicit approval**

### Codebase Structure (from `steering/project-structure.md`)

```
sources/                       # Source code - Multi-version structure
├── tribute/                   # Tribute variant - Faithful recreation
│   ├── tlovelyr-1.7.10/      # MC 1.7.10 - Forge
│   ├── tlovelyr-1.12.2/      # MC 1.12.2 - Forge
│   ├── tlovelyr-1.16.5/      # MC 1.16.5 - Forge/Fabric
│   ├── tlovelyr-1.19.2/      # MC 1.19.2 - Forge/Fabric
│   ├── tlovelyr-1.19.4/      # MC 1.19.4 - Forge/Fabric
│   ├── tlovelyr-1.20.1/      # MC 1.20.1 - Forge/Fabric
│   └── tlovelyr-1.21.x/      # MC 1.21.x - Forge/NeoForge/Fabric
│
├── legacy/                    # Legacy variant - Enhanced features
│   ├── llovelyr-1.7.10/      # MC 1.7.10 - Forge
│   ├── llovelyr-1.12.2/      # MC 1.12.2 - Forge
│   ├── llovelyr-1.16.5/      # MC 1.16.5 - Forge/Fabric
│   ├── llovelyr-1.19.2/      # MC 1.19.2 - Forge/Fabric
│   ├── llovelyr-1.19.4/      # MC 1.19.4 - Forge/Fabric
│   ├── llovelyr-1.20.1/      # MC 1.20.1 - Forge/Fabric
│   └── llovelyr-1.21.x/      # MC 1.21.x - Forge/NeoForge/Fabric
│
├── reboot/                    # Reboot 2.0 - Advanced features
│   ├── rlovelyr-1.12.2/      # MC 1.12.2 - Forge
│   ├── rlovelyr-1.16.5/      # MC 1.16.5 - Forge/Fabric
│   ├── rlovelyr-1.19.2/      # MC 1.19.2 - Forge/Fabric
│   ├── rlovelyr-1.19.4/      # MC 1.19.4 - Forge/Fabric
│   ├── rlovelyr-1.20.1/      # MC 1.20.1 - Forge/Fabric
│   └── rlovelyr-1.21.x/      # MC 1.21.x - Forge/NeoForge/Fabric
│
└── common/                    # Shared resources across versions
    ├── assets/                # Shared textures, models, sounds
    ├── animations/            # GeckoLib animations
    ├── models/                # Entity models
    └── data/                  # Recipes, tags, loot tables

docs/
├── project/                   # Project vision (immutable)
│   ├── CONCEPT.md
│   ├── DESIGN.md
│   └── PLANNING.md
├── workflow/                  # Project strategy
│   ├── ROADMAP.md
│   ├── SPRINT_PLANNING.md
│   ├── ARCHITECTURE.md
│   └── CURRENT_STATE.md
├── development/               # Execution tracking
│   ├── sprints/active/
│   ├── sprints/archive/
│   ├── decisions/
│   └── notes/
├── guidelines/                # Standards & templates
│   ├── templates/
│   ├── maintenance/
│   └── Coding Style Enforcer.md
└── documentation/             # Product docs
    ├── api/
    ├── examples/
    ├── usage/
    └── onboarding/

archive/                       # Archived versions
├── 1.7.10/
├── 1.12.2/
├── 1.16.X/
├── 1.17.X/
├── 1.18.X/
├── 1.19.X/
├── 1.20.X/
└── 1.21.X/
```

## Handling Unforeseen Tasks: Step-by-Step Process

### When You Discover an Unplanned Task

#### Step 1: Immediate Documentation (< 5 minutes)
```markdown
// In docs/development/sprints/active/TASK.md
// Add to "Unforeseen Work" or "Blocked Items" section

**Unforeseen Task**: [Brief description]
- **Discovery Date**: [Date]
- **Variant**: [Tribute/Legacy/Reboot/Common]
- **Version**: [MC 1.7.10/1.12.2/1.16.5/1.19.2/1.19.4/1.20.1/1.21.x]
- **Loader**: [Forge/Fabric/NeoForge/All]
- **Reason**: [Why this wasn't anticipated]
- **Impact**: [How this affects current sprint]
- **Priority**: [High/Medium/Low - based on blocking nature]
- **Estimated Effort**: [Hours/days]
- **Related Components**: [Affected classes/entities/systems]
```

#### Step 2: Assessment and Decision (< 10 minutes)
Ask these questions (from `steering/project-product.md`):
- **Does this block current sprint goals?** → If yes, it becomes immediate priority
- **Is this a dependency for planned work?** → Adjust task sequence
- **Can this be deferred?** → Add to backlog with clear rationale
- **Does this affect architecture?** → Create ADR if significant
- **Does this change timeline?** → Update SPRINT_PLANNING.md

#### Step 3: Update Sprint Context (< 5 minutes)
```markdown
// In docs/workflow/SPRINT_PLANNING.md - Current Sprint section

**Sprint Adjustments**:
- **Added**: [Unforeseen task description]
- **Impact**: [Revised completion estimates]
- **Dependencies**: [New dependency relationships]
- **Reason**: [Why this work is necessary]
```

#### Step 4: Task Integration (Immediate)
Integrate the unforeseen task into your current workflow:

1. **If it's urgent & blocking**: Replace lowest-priority current task
2. **If it's large (>1 day)**: Consider splitting sprint or extending timeline
3. **If it's small (<4 hours)**: Add as additional task with adjusted expectations
4. **If it's architectural**: Create ADR in `docs/development/decisions/`

## Document Update Triggers

Use this decision matrix for what to update when (aligned with `steering/documentation.md`):

| Change Type | TASK.md | SPRINT_PLANNING.md | CURRENT_STATE.md | ROADMAP.md | ARCHITECTURE.md | ADR |
|-------------|---------|-------------------|------------------|------------|-----------------|-----|
| Unforeseen task found | ✅ Immediately | ⚠️ If affects sprint | ❌ | ❌ | ❌ | ❌ |
| Task completed | ✅ Immediately | ✅ Daily summary | ✅ Weekly | ❌ | ❌ | ❌ |
| Task completed differently | ✅ Immediately | ✅ Daily | ✅ Weekly | ❌ | ⚠️ If pattern changed | ⚠️ If significant |
| New component implemented | ✅ If in sprint | ✅ Weekly | ✅ Immediately | ❌ | ❌ | ❌ |
| Architecture change | ✅ If affects tasks | ✅ If affects sprint | ✅ Immediately | ⚠️ If timeline affected | ✅ Immediately | ✅ Required |
| New dependency discovered | ✅ Immediately | ✅ Immediately | ✅ Weekly | ⚠️ If major | ❌ | ⚠️ If architectural |
| Timeline adjustment | ✅ If task dates change | ✅ Immediately | ❌ | ✅ If milestone affected | ❌ | ❌ |
| Feature scope change | ✅ If tasks change | ✅ Immediately | ✅ Immediately | ✅ If release scope changes | ⚠️ If technical scope | ⚠️ If significant |
| API changes | ✅ If in sprint | ✅ Weekly | ✅ Weekly | ❌ | ❌ | ❌ |
| Bug fix | ✅ If tracked | ❌ | ⚠️ If component affected | ❌ | ❌ | ❌ |
| Refactoring | ✅ If in sprint | ✅ Weekly | ✅ If structure changed | ❌ | ⚠️ If pattern changed | ⚠️ If significant |
| Sprint completion | ✅ Archive | ✅ Retrospective | ✅ Final sync | ⚠️ If milestone affected | ❌ | ❌ |

**Legend**: ✅ Always update | ⚠️ Conditionally update | ❌ No update needed

## Maintenance Routines

### Daily Maintenance (5-10 minutes)

**Timing**: End of each development session

#### 1. TASK.md Quick Update
```markdown
// Update task status in docs/development/sprints/active/TASK.md

**Task [ID]**: [Name]
- Status: [Not Started/In Progress/Blocked/Complete]
- Progress: [Brief update on what was done]
- Blockers: [Any issues encountered]
- Next Steps: [What's next]
```

#### 2. Commit Documentation Changes
```bash
# Use DOCS: prefix for documentation commits
git add docs/development/sprints/active/TASK.md
git commit -m "DOCS: Update TASK.md with daily progress"
```

### Weekly Maintenance (30 minutes)

**Timing**: Every Friday (or end of development week)

#### 1. TASK.md Comprehensive Review (10 minutes)
- Mark completed tasks with completion dates
- Update in-progress task status with detailed progress
- Remove or reschedule unstarted tasks if needed
- Add newly discovered tasks from the week
- Update effort estimates based on actual time spent

#### 2. SPRINT_PLANNING.md Update (10 minutes)
```markdown
// Add to docs/workflow/SPRINT_PLANNING.md - Current Sprint section

**Week [Number] Summary - [Dates]**
- **Completed**: [List tasks with story points]
- **In Progress**: [List with % complete]
- **Discovered**: [New unforeseen tasks]
- **Challenges**: [What didn't go as planned]
- **Adjustments**: [Changes to approach or timeline]
- **Velocity**: [Actual vs planned story points]
```

#### 3. CURRENT_STATE.md Sync (10 minutes)
Scan `src/uncogest/lib/features/` and update:
- Component status based on actual implementation
- New services, models, providers, widgets
- Deviations from ARCHITECTURE.md
- Technical decisions made during the week
- Test coverage status

#### 4. Forward-looking Adjustment (5 minutes)
- Review next week's tasks in TASK.md
- Adjust SPRINT_PLANNING.md if needed
- Update ROADMAP.md if milestones need shifting
- Flag any risks or dependencies

### Sprint Boundary Maintenance (1-2 hours)

**Timing**: End of each 2-week sprint

#### 1. Sprint Completion Checklist
- [ ] All tasks in TASK.md marked complete or deferred
- [ ] SPRINT_PLANNING.md updated with retrospective
- [ ] CURRENT_STATE.md reflects all implemented components
- [ ] ROADMAP.md updated if milestones affected
- [ ] CHANGELOG.md updated if releasing
- [ ] ADRs created for architectural decisions

#### 2. Sprint Archival (from `steering/workflows.md`)
```bash
# Move completed sprint to archive
cd docs/development/sprints
mv active/SPRINT_X_TASK.md archive/[COMPLETED]_SPRINT_X_$(date +%Y-%m-%d).md
```

#### 3. Next Sprint Preparation
- Create new SPRINT_X_TASK.md in active/
- Break down sprint goals into tasks
- Estimate story points
- Identify dependencies and risks

## Change Propagation Framework

### Document Relationship Flow (from `steering/workflows.md`)

```
CONCEPT (vision - immutable)
  ↓
DESIGN (solution - immutable)
  ↓
PLANNING (strategy - immutable)
  ↓
ARCHITECTURE (specs) ← → CURRENT_STATE (reality check)
  ↓                        ↑
ROADMAP (timeline)         |
  ↓                        |
SPRINT_PLANNING (cycles)   |
  ↓                        |
TASK (execution) → Implementation → Code
```

### When to Update Which Documents

#### Small Changes (Single task level)
**Scope**: Bug fixes, minor features, refactoring within existing patterns

**Update**:
- `docs/development/sprints/active/TASK.md` - Immediately
- `docs/workflow/SPRINT_PLANNING.md` - Daily summary

**Leave unchanged**:
- `docs/workflow/ROADMAP.md`
- `docs/workflow/ARCHITECTURE.md`
- `docs/workflow/CURRENT_STATE.md` (until weekly sync)

**Frequency**: Immediate or daily

#### Medium Changes (Sprint-level impact)
**Scope**: New components, significant features, multiple related tasks

**Update**:
- `docs/development/sprints/active/TASK.md` - Immediately
- `docs/workflow/SPRINT_PLANNING.md` - Weekly
- `docs/workflow/CURRENT_STATE.md` - Weekly or when component complete

**Consider**:
- `docs/development/decisions/ADR_XXX.md` - If design decision made
- `docs/documentation/api/` - If APIs changed
- `docs/documentation/usage/` - If user-facing behavior changed

**Frequency**: Weekly or as changes occur

#### Large Changes (Project-level impact)
**Scope**: Architectural changes, major features, timeline shifts, scope changes

**Update**:
- All relevant documents in cascade order
- `docs/development/sprints/active/TASK.md` - Immediately
- `docs/development/decisions/ADR_XXX.md` - Required for architectural changes
- `docs/workflow/ARCHITECTURE.md` - If technical approach changed
- `docs/workflow/CURRENT_STATE.md` - Immediately
- `docs/workflow/SPRINT_PLANNING.md` - Immediately
- `docs/workflow/ROADMAP.md` - If milestones affected
- `CHANGELOG.md` - If releasing

**Frequency**: As soon as change is confirmed

**Approval Required**:
- Changes to `docs/project/` (vision documents are immutable)
- Changes to `docs/guidelines/` (requires team consensus)
- Changes to `hooks/` (requires explicit approval)

## Practical Implementation Strategy

### 1. The "Documentation First" Approach
When encountering unforeseen work (from `steering/readme.md`):
1. **Stop** - Don't proceed with assumptions
2. **Document** - Record discovery in TASK.md (< 5 minutes)
3. **Assess** - Impact on current sprint (< 10 minutes)
4. **Decide** - Immediate action (do now, defer, adjust)
5. **Update** - Relevant documents following cascade rules
6. **Proceed** - With implementation following coding standards

### 2. The Rolling Update Method
Instead of big documentation sessions:
- **Daily**: Quick TASK.md updates (5-10 minutes)
- **Weekly**: Comprehensive sync (30 minutes)
  - TASK.md review
  - SPRINT_PLANNING.md update
  - CURRENT_STATE.md sync
  - Forward planning
- **Sprint Boundaries**: Full documentation audit (1-2 hours)
  - Sprint archival
  - Retrospective
  - Next sprint setup
- **Monthly**: Strategic document review (1 hour)
  - ROADMAP.md validation
  - ARCHITECTURE.md vs CURRENT_STATE.md alignment
  - Documentation quality audit

### 3. The "Good Enough" Principle
Your documentation should be (from `steering/documentation.md`):
- **Accurate enough** to guide work without misleading
- **Current enough** to reflect reality within 1 week
- **Detailed enough** to be useful for decision-making
- **Simple enough** to maintain in < 30 minutes weekly
- **Consistent enough** to follow established patterns

### 4. Integration with Synchronization Hook
This maintenance guide provides **strategic guidance**, while the **Documentation Synchronization Monitor** hook provides **tactical execution**:

**Maintenance Guide (this document)**:
- When to update documents
- What triggers updates
- How to handle unforeseen work
- Long-term maintenance routines

**Synchronization Hook** (`hooks/sync-docs-implementation`):
- Scans codebase for changes
- Identifies documentation gaps
- Updates CURRENT_STATE.md
- Validates SPRINT_PLANNING.md accuracy
- Efficient, focused synchronization

## Recovery Guide for "Gone Rogue" Situations

### When You've Been Working Without Documentation Updates

**Use the Documentation Synchronization Monitor hook** for automated recovery, or follow these manual steps:

#### Step 1: Current State Capture (30 minutes)
1. **Scan codebase** - List all components in `sources/tribute/`, `sources/legacy/`, `sources/reboot/`, and `sources/common/`
2. **Identify variants** - Which variants changed:
   - Tribute (faithful recreation - 4 robot types)
   - Legacy (enhanced features - 7 robot types, 16x colors)
   - Reboot (advanced features - robot creator, assembly systems)
   - Common (shared resources across all variants)
3. **Version distinction** - Which Minecraft versions affected (1.7.10, 1.12.2, 1.16.5, 1.19.2, 1.19.4, 1.20.1, 1.21.x)
4. **Loader distinction** - Which mod loaders affected (Forge, Fabric, NeoForge)
5. **Inventory completed work** - What was implemented since last documentation
6. **List all changes** - Deviations from planned approach
7. **Document discoveries** - Unforeseen work and decisions made
8. **Check test coverage** - What's tested vs. what's implemented

#### Step 2: Document Triage (Priority Order)
Update in this priority order (from `steering/development.md`):

1. **TASK.md** (`docs/development/sprints/active/`)
   - Mark completed tasks with actual completion dates
   - Add discovered tasks that were implemented
   - Update status of in-progress work
   - Note what was planned but not done
   - Distinguish variant/version/loader work

2. **CURRENT_STATE.md** (`docs/workflow/`)
   - Update component catalog with actual implementation
   - Add new entities, items, blocks, systems
   - Note deviations from ARCHITECTURE.md
   - Update test coverage status
   - Document variant-specific implementations
   - Note version-specific differences
   - Track loader compatibility

3. **SPRINT_PLANNING.md** (`docs/workflow/`)
   - Update sprint progress with actual story points
   - Add retrospective notes on what changed
   - Adjust remaining sprint work
   - Document velocity impact
   - Note variant/version distribution of work

4. **ROADMAP.md** (`docs/workflow/`)
   - Update only if timeline affected
   - Adjust milestone dates if necessary
   - Update variant/version release plans

5. **ADRs** (`docs/development/decisions/`)
   - Create retroactive ADRs for significant architectural decisions
   - Document rationale even if decision already implemented
   - Note variant-specific or version-specific decisions

#### Step 3: Gap Analysis (15 minutes)
Compare current reality against documents:
- **What was planned but not done?** → Move to backlog or next sprint
- **What was done but not planned?** → Document as unforeseen work
- **What was done differently than planned?** → Update ARCHITECTURE.md if pattern changed
- **What architectural decisions were made?** → Create ADRs
- **Are variant implementations properly separated?** → Verify Tribute/Legacy/Reboot distinction
- **Are version-specific implementations documented?** → Note MC version differences
- **Is loader compatibility tracked?** → Document Forge/Fabric/NeoForge support

#### Step 4: Forward Reset (10 minutes)
- **Update all documents** to current reality (don't leave gaps)
- **Don't try to document "how we got here"** - focus on current state
- **Commit all changes** with `DOCS:` prefix
- **Resume regular maintenance** from current state (daily/weekly routines)
- **Set reminders** to prevent future drift

#### Step 5: Prevention (5 minutes)
- Enable **Documentation Synchronization Monitor** hook for automated checks
- Set calendar reminders for weekly maintenance
- Add documentation updates to Definition of Done
- Review this guide monthly

## Automation and Tools

### 1. Agent Hooks Integration

**Documentation Synchronization Monitor** (`hooks/sync-docs-implementation`)
- **Purpose**: Efficient tactical synchronization of docs with code
- **Trigger**: Manual (user-triggered)
- **Focus**: CURRENT_STATE.md, SPRINT_PLANNING.md, component catalog
- **Duration**: ~5-10 minutes
- **Use when**: Weekly sync, after major implementation, before sprint review

**Project Documentation Maintenance** (`hooks/project-doc-maintenance`)
- **Purpose**: Comprehensive strategic maintenance guidance
- **Trigger**: Manual (user-triggered)
- **Focus**: All document types, unforeseen work, gap analysis
- **Duration**: ~30-60 minutes
- **Use when**: Sprint boundaries, recovery from drift, monthly audit

### 2. Documentation Triggers
Set reminders for:
- **Daily** (5-10 min): Update TASK.md status
- **Weekly** (30 min): Run Documentation Synchronization Monitor hook
- **Sprint Boundaries** (1-2 hours): Run Project Documentation Maintenance hook
- **Monthly** (1 hour): Strategic document assessment

### 3. Template Snippets
Keep these ready for quick updates:

**Unforeseen Task Template**:
```markdown
**Unforeseen Task**: [Brief description]
- **Discovery Date**: [Date]
- **Variant**: [Tribute/Legacy/Reboot/Common]
- **Version**: [MC 1.7.10/1.12.2/1.16.5/1.19.2/1.19.4/1.20.1/1.21.x]
- **Loader**: [Forge/Fabric/NeoForge/All]
- **Reason**: [Why this wasn't anticipated]
- **Impact**: [How this affects current sprint]
- **Priority**: [High/Medium/Low - based on blocking nature]
- **Estimated Effort**: [Hours/days]
- **Related Components**: [Affected entities/items/blocks/systems]
- **Action**: [Immediate/Deferred/Adjusted]
```

**Daily TASK.md Update Template**:
```markdown
**Task [ID]**: [Name]
- **Status**: [Not Started/In Progress/Blocked/Complete]
- **Progress**: [Brief update on what was done today]
- **Blockers**: [Any issues encountered]
- **Next Steps**: [What's next]
- **Time Spent**: [Actual hours]
```

**Weekly SPRINT_PLANNING.md Update Template**:
```markdown
## Week [Number] Summary - [Start Date] to [End Date]

### Completed ([X] story points)
- [Task 1] - [Story points] - [Variant: Tribute/Legacy/Reboot] - [Version: MC X.X.X] - [Loader: Forge/Fabric/NeoForge]
- [Task 2] - [Story points] - [Variant: ...] - [Version: ...] - [Loader: ...]

### In Progress ([X] story points, [Y]% complete)
- [Task 3] - [Progress description] - [Variant: ...] - [Version: ...] - [Loader: ...]

### Discovered Unforeseen Work
- [New task 1] - [Impact on sprint] - [Variant: ...] - [Version: ...] - [Loader: ...]

### Challenges & Blockers
- [Challenge 1] - [How addressed]

### Adjustments Made
- [Adjustment 1] - [Rationale]

### Velocity
- **Planned**: [X] story points
- **Actual**: [Y] story points
- **Variance**: [+/-Z] story points

### Variant Distribution
- **Tribute work**: [X] story points
- **Legacy work**: [Y] story points
- **Reboot work**: [Z] story points
- **Common work**: [W] story points

### Version Distribution
- **MC 1.7.10-1.12.2**: [X] story points
- **MC 1.16.5-1.19.4**: [Y] story points
- **MC 1.20.1-1.21.x**: [Z] story points
```

**ADR Template** (from `steering/documentation.md`):
```markdown
# ADR [NUMBER]: [Title]

**Status**: [Proposed/Accepted/Deprecated/Superseded]
**Date**: YYYY-MM-DD
**Decision Makers**: [Who decided]
**Consulted**: [Who was consulted]

## Context
What is the issue we're facing?

## Decision
What did we decide?

## Consequences
What are the implications?

### Positive
- Benefit 1
- Benefit 2

### Negative
- Trade-off 1
- Trade-off 2

### Risks
- Risk 1
- Risk 2

## Alternatives Considered
What other options were considered and why were they rejected?

## Related Decisions
- Links to related ADRs
```

## Success Metrics

### Your System is Working When:
- ✅ Documents reflect current reality within 1 week
- ✅ Weekly maintenance takes < 30 minutes
- ✅ Unforeseen tasks documented within 24 hours
- ✅ You can answer "what's the current status?" from documents
- ✅ Document updates feel helpful, not burdensome
- ✅ CURRENT_STATE.md matches actual codebase
- ✅ SPRINT_PLANNING.md shows accurate progress
- ✅ No "TODO" placeholders in active documents
- ✅ ADRs exist for all architectural decisions
- ✅ Code changes trigger documentation updates

### Warning Signs of Document Drift:
- âš ï¸ CURRENT_STATE.md hasn't been updated in > 2 weeks
- âš ï¸ TASK.md contains completed tasks not marked done
- âš ï¸ SPRINT_PLANNING.md shows incorrect progress
- âš ï¸ Developers avoid documentation because "it's always wrong"
- âš ï¸ Architectural decisions made without ADRs
- âš ï¸ New components not documented in CURRENT_STATE.md
- âš ï¸ Weekly maintenance taking > 1 hour (sign of accumulated drift)

## Integration with Steering Documents

This maintenance guide integrates with:

1. **`steering/workflows.md`** - Document navigation and relationships
2. **`steering/development.md`** - File modification rules and workflows
3. **`steering/documentation.md`** - Documentation standards and formatting
4. **`steering/project-product.md`** - Quality standards and Definition of Done
5. **`steering/project-structure.md`** - Directory organization rules
6. **`steering/readme.md`** - Serge Maia command center and decision matrix

**Key Principle**: This guide provides the "how" and "when" of maintenance, while steering documents provide the "what" and "why" of the documentation system.

## Variant-Specific Considerations

### Multi-Variant Architecture (from `steering/project-structure.md`)
The LovelyRobot mod uses a multi-variant, multi-version architecture:
- **Tribute**: Faithful recreation of original mod (4 robot types, original colors)
- **Legacy**: Enhanced version (7 robot types, 16x color palette)
- **Reboot**: Advanced features (robot creator, assembly systems, modular construction)
- **Common**: Shared resources across all variants (textures, models, animations)

**Key Principles**:
- Each variant maintains separate codebases per Minecraft version
- Common resources are shared via `sources/common/`
- Each version folder is self-contained with its own build system
- Loader-specific code (Forge/Fabric/NeoForge) is handled within each version

### Version Organization (from `steering/project-structure.md`)
The project supports multiple Minecraft versions:
- **MC 1.7.10 - 1.16.5**: Java 8-16, Forge (and Fabric for 1.16.5+)
- **MC 1.17.1 - 1.20.1**: Java 17, Forge/Fabric
- **MC 1.20.5+**: Java 21, Forge/NeoForge/Fabric

**Version Independence**:
- Each version has its own folder: `sources/[variant]/[variant]-[version]/`
- Each version has its own build configuration
- Version-specific implementations are documented in CURRENT_STATE.md
- Cross-version compatibility is tracked in ARCHITECTURE.md

### Documentation Hierarchy (from `steering/project-structure.md`)
- **Project Level** (`docs/`): High-level, cross-variant, cross-version concerns
- **Source Code** (`sources/`): Variant and version-specific implementations
- **Archive** (`archive/`): Completed/released versions (historical reference only)

## Remember: Documents Serve You

The ultimate goal isn't perfect documentation—it's **effective development** (from `steering/project-product.md`). Your documents should:

1. **Help you work better**, not create more work
2. **Adapt to reality**, not fight against it
3. **Provide clarity**, not bureaucracy
4. **Support decision-making**, not replace judgment
5. **Follow steering rules**, ensuring consistency
6. **Maintain single source of truth**, avoiding duplication
7. **Track variant/version differences**, documenting implementation variations

When documents feel like they're holding you back, that's a signal to:
1. **Consult steering documents** - Are you following the right process?
   - `steering/workflows.md` - Document navigation
   - `steering/development.md` - File modification rules
   - `steering/documentation.md` - Documentation standards
   - `steering/project-structure.md` - Directory organization
   - `steering/project-product.md` - Quality standards
2. **Use automation** - Run the synchronization hooks
3. **Simplify approach** - Focus on high-value updates
4. **Request guidance** - Flag for human review if needed

**Never abandon the system** - adjust it to work better for your needs while maintaining alignment with steering documents.

---

## Quick Reference: Steering Documents

For detailed guidance, consult these steering documents in `steering/`:

| Document | Purpose | When to Consult |
|----------|---------|----------------|
| **workflows.md** | Navigation & information retrieval | When searching for information, understanding document relationships |
| **development.md** | File generation & iteration | When creating/modifying files, understanding what can be changed |
| **documentation.md** | Documentation generation & maintenance | When creating/updating docs, formatting standards |
| **project-structure.md** | Directory & file organization | When creating new files/folders, understanding structure |
| **project-product.md** | Product vision & quality standards | When making feature decisions, understanding quality bar |
| **project-coding-style.md** | Code style enforcement | Before writing any code (non-negotiable) |
| **readme.md** | Serge Maia command center | Quick reference for all steering documents |

**Key Principle from `steering/readme.md`**: Follow the commandments in these steering documents religiously. They represent the collective wisdom of the project and the requirements for success.

---

**Status**: Active  
**Review Schedule**: Monthly  
**Last Review**: 2025-11-16  
**Next Review**: 2025-12-16

*This maintenance guide should be reviewed monthly and adjusted based on what's actually working in your development process. The best system is the one you'll actually use consistently while following steering document principles.*