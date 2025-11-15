# TASK.md - Guidelines (Updated)

## Purpose
The TASK.md document tracks detailed implementation progress within a specific sprint cycle. It translates sprint objectives into actionable work items, establishes task-level priorities, and manages the flow of work throughout the sprint. This document bridges the gap between sprint planning and code implementation, providing a clear view of what needs to be done, current progress, and completion status for the current sprint.

## When to Create
- After finalizing the SPRINT_PLANNING.md for the current sprint
- At the beginning of each sprint cycle
- When breaking sprint objectives into implementation tasks
- When tracking granular progress within a sprint
- When coordinating work across team members within a sprint

## How to Approach

### 1. Break Down Sprint Objectives
- Divide sprint goals into discrete, manageable tasks
- Create clear, specific task definitions
- Establish appropriate granularity (typically 4-16 hours per task)
- Ensure tasks align with sprint deliverables

### 2. Create Task Hierarchy
- Group related tasks under feature or component categories
- Establish parent-child relationships where appropriate
- Define task dependencies and sequencing
- Maintain a flat enough structure for clarity

### 3. Prioritize Sprint Tasks
- Apply clear prioritization criteria
- Consider dependencies in sequencing
- Identify critical path items
- Tag must-have versus nice-to-have tasks

### 4. Establish Tracking Mechanism
- Create consistent task status designations
- Define workflow states (Todo, In Progress, Review, Done)
- Establish completion criteria for each task
- Create method for recording blockers or issues

### 5. Track Progress Visibility
- Update status regularly during the sprint
- Document completed items with dates
- Record time investment where relevant
- Maintain a clear view of sprint progress

### 6. Capture Implementation Details
- Document key decisions made during implementation
- Record approaches taken for complex tasks
- Note deviations from planned implementation
- Document lessons learned during task execution

### 7. Link Tasks to Deliverables
- Connect tasks to specific sprint objectives
- Reference architectural components being implemented
- Ensure all sprint deliverables have associated tasks
- Verify complete coverage of sprint goals

## Recommended Sections

1. **Sprint Summary**
   - Sprint number and timeframe
   - Key objectives for this sprint
   - Major deliverables expected
   - Team members involved
   - Overall progress indicator

2. **Tasks Breakdown**
   For each task:
   - Task identifier (e.g., TASK-123)
   - Descriptive title
   - Detailed description and requirements
   - Current status (Todo, In Progress, Review, Done)
   - Assigned resources
   - Estimated and actual effort
   - Priority level
   - Dependencies
   - Completion date (when done)

3. **Current Progress**
   - Tasks completed
   - Tasks in progress
   - Tasks blocked or at risk
   - Sprint burndown or progress visualization
   - Percentage of sprint objectives met
   - Current focus areas

4. **Implementation Notes**
   - Technical decisions made
   - Approaches taken for complex tasks
   - Deviations from planned implementation
   - Code references or commit IDs
   - Documentation updates needed

5. **Blocked Items**
   - Tasks blocked and reasons
   - Dependencies causing delays
   - Action plans for removing blockers
   - Impact assessment on sprint goals
   - Escalation status

6. **Testing and Validation**
   - Test coverage for completed tasks
   - Validation approach for deliverables
   - Issues found during testing
   - Quality metrics for deliverables
   - Acceptance status

7. **Sprint Adjustments**
   - Changes to task scope during sprint
   - Added or removed tasks
   - Reprioritization decisions
   - Resource adjustments
   - Impact on sprint goals

8. **Next Steps**
   - Immediate priorities
   - Upcoming tasks in sequence
   - Preparation for sprint completion
   - Transition plan to next sprint
   - Items to be carried forward

## Tips for Effectiveness

- **Use Consistent Task Format**: Create a template for task descriptions
- **Link to Source**: Connect tasks to sprint objectives and requirements
- **Include Acceptance Criteria**: Define clear completion standards for each task
- **Track Actual vs. Estimated**: Record both estimated and actual effort
- **Update Daily**: Keep the document current with the latest progress
- **Focus on Current Sprint**: Only include tasks relevant to the active sprint
- **Use Identifiers**: Maintain clear task codes or IDs for reference
- **Document Blockers Promptly**: Record impediments as soon as identified
- **Include Technical Notes**: Capture implementation details useful for review
- **Visual Progress**: Use charts or progress indicators
- **Indicate Complexity**: Note which tasks involve significant complexity
- **Tag Code References**: Link to relevant commits or pull requests
- **Note Testing Status**: Track validation status for completed tasks
- **Maintain Decision Log**: Record key decisions made during implementation
- **Use Consistent Terminology**: Align with terms used in other project documents

## Common Pitfalls to Avoid

- **Too Much Detail**: Creating tasks that are too granular for effective tracking
- **Too Little Detail**: Making tasks so broad they can't be tracked meaningfully
- **Static Tasking**: Not adjusting tasks as sprint realities emerge
- **Outdated Status**: Not keeping task status current
- **Missing Dependencies**: Not tracking relationships between tasks
- **Isolated Documentation**: Disconnecting tasks from code implementation
- **Overspecialization**: Assigning all similar tasks to one person creating bottlenecks
- **Neglecting Blocked Items**: Not actively managing impediments
- **Scope Creep**: Adding tasks without adjusting sprint expectations
- **Missing Acceptance Criteria**: Not defining what "done" means for each task
- **Inadequate Sequencing**: Not planning the most efficient task order
- **Ignoring Risks**: Not highlighting tasks with significant uncertainty
- **Poor Prioritization**: Working on lower-value tasks while critical items wait
- **Neglecting Updates**: Letting the task list become out of date
- **Forgetting Non-Code Tasks**: Not including documentation, testing, and review tasks

## Relationship to Other Documents

- **Builds Upon**: SPRINT_PLANNING.md (details the work outlined in sprint plan)
- **Informs**: SPRINT.md (captures completed work for sprint record)
- **References**: ARCHITECTURE.md (ensures implementation aligns with architecture)
- **Feeds Into**: CHANGELOG.md (provides details for release notes)
- **Differs From**: ROADMAP.md (focuses on immediate tasks vs. long-term strategy)
- **Supports**: CURRENT_STATE.md (documents how implementation evolves)

The TASK.md document should be updated daily during active sprint work. It is primarily a tactical tool for managing the current sprint's implementation activities and tracking progress toward sprint objectives. At sprint completion, key information should be transferred to the SPRINT.md record, and a new TASK.md created for the next sprint cycle.
