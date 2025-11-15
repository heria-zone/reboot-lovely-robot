# SPRINT.md - Guidelines

## Purpose
The SPRINT.md document tracks detailed progress and changes made during a specific development sprint or iteration. It provides a comprehensive record of tasks completed, code changes implemented, decisions made, and work pending. This document serves as both a real-time progress tracker during active development and a historical record of how specific features evolved. Unlike CHANGELOG.md which focuses on version releases, SPRINT.md captures the granular details of work in progress.

## When to Create
- At the beginning of each sprint or iteration
- When implementing a significant feature that spans multiple work sessions
- When refactoring or modifying core components of the system
- When working on complex tasks requiring detailed tracking
- When multiple related tasks are being implemented simultaneously

## How to Approach

### 1. Define the Sprint Scope and Timeline
- Specify the sprint date range or iteration number
- State the primary focus or theme of the sprint
- Identify the major components or areas being modified
- Set clear boundaries for what is in and out of scope

### 2. Break Down Into Discrete Tasks
- Create task identifiers with meaningful codes (e.g., CE-01)
- Define specific, manageable units of work
- Organize tasks in logical sequence or by related functionality
- Include estimated complexity or priority for each task

### 3. Track Implementation Details
- Document specific classes, methods, or files modified
- Record actual changes made at a functional level
- Note design patterns implemented or architectural modifications
- Capture new interfaces, classes, or components created

### 4. Monitor Progress Continuously
- Update task status as work progresses
- Include completion dates for finished tasks
- Track partial progress for in-progress items
- Note dependencies between tasks that affect timeline

### 5. Document Technical Decisions
- Record important decisions made during implementation
- Note alternatives considered and reasons for final choice
- Document constraints or considerations that guided decisions
- Include reference to discussions or research that informed choices

### 6. Plan Next Steps
- Identify upcoming tasks for the current sprint
- Note future work that emerges from current implementation
- Document known issues or technical debt introduced
- Prioritize remaining work based on dependencies or importance

### 7. Link to Concrete Implementation
- Reference specific commit IDs where appropriate
- Link to pull requests or code reviews
- Connect tasks to specific files or modules in the codebase
- Reference relevant documentation updates

## Recommended Sections

1. **Sprint Overview**
   - Sprint identifier and date range
   - Primary focus or objective
   - Major components affected
   - Overall progress summary

2. **Tasks**
   For each task:
   - Task identifier and descriptive name
   - Current status (Completed, In Progress, Pending, Blocked)
   - Detailed changes implemented
   - Related components or files modified
   - Challenges encountered and solutions applied
   - Dependencies on other tasks

3. **Technical Implementation**
   - Design patterns or architectural approaches used
   - API changes or modifications
   - Data model updates
   - Performance considerations
   - Cross-cutting concerns addressed

4. **Upcoming Tasks**
   - Tasks planned but not yet started
   - Dependencies and prerequisites
   - Implementation approach for pending work
   - Estimated complexity or scope

5. **Decisions and Discussions**
   - Key technical decisions made
   - Alternatives considered
   - Rationale for chosen approaches
   - Outstanding questions or concerns
   - Date and context of each decision

6. **Issues and Blockers**
   - Current impediments to progress
   - Known bugs or issues introduced
   - External dependencies affecting timeline
   - Mitigation strategies

7. **Notes and References**
   - Links to relevant resources or documentation
   - References to related research or examples
   - External libraries or tools integrated
   - Learning resources used during implementation

## Tips for Effectiveness

- **Update in Real-Time**: Modify the document as you work, not after completing all tasks
- **Be Specific**: Include concrete details about what changed and how
- **Focus on Functionality**: Document the purpose and behavior of changes, not just their existence
- **Track Rationale**: Record why certain implementation approaches were chosen
- **Use Consistent Structure**: Maintain the same format for each task and update
- **Include Code Examples**: Add snippets of key implementations where helpful
- **Date Each Entry**: Add timestamps to significant updates or decisions
- **Keep Task Granularity Consistent**: Break work into similarly sized tasks
- **Note Unexpected Challenges**: Document issues that arose during implementation
- **Record Time Investment**: Note significant time spent on specific problems
- **Link Related Documents**: Reference architecture or design documents affected by changes
- **Capture Lessons Learned**: Document insights gained during implementation
- **Track Technical Debt**: Note compromises made or cleanup needed in the future
- **Highlight Innovations**: Emphasize novel solutions or approaches
- **Document Test Strategy**: Note how changes were verified or tested

## Common Pitfalls to Avoid

- **Too Much Detail**: Including trivial changes that don't affect functionality
- **Too Little Detail**: Being vague about significant modifications
- **Inconsistent Updates**: Updating some tasks thoroughly but neglecting others
- **Missing Decisions**: Not recording important implementation decisions
- **Overlooking Challenges**: Not documenting difficulties encountered and overcome
- **Ignoring Context**: Failing to explain why changes were made
- **Neglecting Status Updates**: Not keeping task status current
- **Missing Dependencies**: Not noting how tasks relate to each other
- **Inconsistent Task Breakdown**: Having some tasks too large and others too small
- **Focusing Only on Completion**: Not documenting process and learning
- **Neglecting Future Work**: Not recording tasks identified during implementation
- **Abandoning After Sprint**: Not preserving the document as historical reference
- **Separating from Code**: Not connecting documentation to actual implementation
- **Overlooking Visual Elements**: Not using formatting to enhance readability
- **Neglecting Timestamps**: Not recording when significant work was completed

## Relationship to Other Documents

- **Informs**: CHANGELOG.md (provides details that inform version release notes)
- **Guided By**: TASK.md (overall project tasks are broken down into sprint-specific tasks)
- **Complements**: ARCHITECTURE.md (documents actual implementation of architectural plans)
- **Feeds Into**: CURRENT_STATE.md (changes documented here update the current state document)
- **References**: PLANNING.md (implementations are measured against original technical plans)
- **Differs From**: CHANGELOG.md (focuses on in-progress work rather than released versions)

The SPRINT.md document serves as both an active work log during development and a historical record after completion. Each sprint should have its own document (e.g., SPRINT_2023-04-20.md) or be maintained in a SPRINTS directory to preserve the history of project evolution. This document is particularly valuable for understanding how and why implementation decisions were made during active development phases.
