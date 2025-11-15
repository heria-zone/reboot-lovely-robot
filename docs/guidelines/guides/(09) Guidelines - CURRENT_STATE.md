# CURRENT_STATE.md - Guidelines

## Purpose
The CURRENT_STATE.md document provides a comprehensive catalog of the system's actual implementation state as it evolves. It tracks how the codebase diverges from initial architectural plans, capturing the current structure of components, interfaces, and behaviors. This living document serves as the authoritative reference for the system's real-world implementation, focusing specifically on what exists today rather than what was planned.

## When to Create
- After initial implementation of core architecture
- When the codebase begins to evolve from original plans
- When significant refactoring changes system structure
- When onboarding new team members who need to understand the current system
- When multiple iterations have introduced incremental changes

## How to Approach

### 1. Structure as a Component Catalog
- Organize components by functional area or namespace
- Document each component's current state, not planned state
- Present information in consistent tabular format
- Focus on public interfaces and integration points
- Include visual component relationship diagrams

### 2. Document Actual Implementation
- Focus on code as it exists today
- Include actual method signatures and parameters
- Provide accurate class hierarchies and relationships
- Document current behavior, not intended behavior
- Note specific implementation details affecting usage

### 3. Highlight Divergence from Architecture
- Note where implementation differs from ARCHITECTURE.md
- Explain rationale behind implementation changes
- Document architectural decisions made during development
- Track deviations from planned patterns or approaches
- Link to relevant SPRINT.md entries explaining changes

### 4. Maintain as a Living Reference
- Update after each significant code change
- Ensure documentation matches current codebase version
- Remove outdated information promptly
- Focus on changes, not on documenting stable components
- Consider automation to keep in sync with code

### 5. Focus on Integration Points
- Emphasize component interfaces and contracts
- Document actual API signatures and behaviors
- Highlight cross-component dependencies
- Note data flow patterns as implemented
- Document extension points and mechanisms

## Recommended Sections

1. **System Overview**
   - System version and build information
   - Major subsystems and their relationships
   - Architectural patterns actually implemented
   - Current technology stack and third-party dependencies
   - Visual system architecture diagram

2. **Component Catalog**
   For each functional area:

   | Attribute | Description |
   |-----------|-------------|
   | **Area Name** | Name of functional area or module |
   | **Namespace** | Primary namespace(s) containing these components |
   | **Description** | Current purpose and responsibility |
   | **Status** | Implementation completeness (Complete, Partial, Evolving) |
   | **Changes from Architecture** | How implementation differs from original plan |

3. **Interface Catalog**
   For each public interface:

   | Interface | Purpose | Visibility | Implementation Classes | Used By |
   |-----------|---------|------------|------------------------|---------|
   | `IExample` | Current purpose | Public/Internal | Actual implementing classes | Components using this interface |
   | `IHandler` | Current purpose | Public/Internal | Actual implementing classes | Components using this interface |

4. **Class Catalog**
   For each significant class:

   | Class | Purpose | Visibility | Parent/Interfaces | Dependencies |
   |-------|---------|------------|-------------------|--------------|
   | `ComponentA` | Current responsibility | Public/Internal | Actual inheritance | Current dependencies |
   | `ComponentB` | Current responsibility | Public/Internal | Actual inheritance | Current dependencies |

5. **Service Catalog**
   For each service:

   | Service | Responsibility | Implementation Approach | Dependencies | Lifecycle |
   |---------|----------------|-------------------------|--------------|-----------|
   | `ServiceA` | Current responsibility | How it's implemented | Current dependencies | Singleton/Transient |
   | `ServiceB` | Current responsibility | How it's implemented | Current dependencies | Singleton/Transient |

6. **Data Model Catalog**
   For each significant data model:

   | Model | Purpose | Properties | Validation | Persistence |
   |-------|---------|------------|------------|-------------|
   | `ModelA` | Current purpose | Key properties | Current validation | How it's stored |
   | `ModelB` | Current purpose | Key properties | Current validation | How it's stored |

7. **Extension Point Catalog**
   For each extension point:

   | Extension Point | Purpose | Implementation Mechanism | Current Extensions |
   |-----------------|---------|--------------------------|---------------------|
   | `ExtensionA` | Current purpose | How extension works | Existing extensions |
   | `ExtensionB` | Current purpose | How extension works | Existing extensions |

8. **API Catalog**
   For each public API:

   | Endpoint/Method | Purpose | Parameters | Return Type | Auth Requirements |
   |-----------------|---------|------------|-------------|-------------------|
   | `API.MethodA` | Current purpose | Current parameters | Current return | Current auth |
   | `API.MethodB` | Current purpose | Current parameters | Current return | Current auth |

9. **Technical Debt Register**
   
   | Component | Issue | Impact | Planned Resolution | Priority |
   |-----------|-------|--------|-------------------|----------|
   | `ComponentA` | Current limitation | Business impact | How to fix | High/Medium/Low |
   | `ComponentB` | Current limitation | Business impact | How to fix | High/Medium/Low |

10. **State Management Catalog**
    
    | State | Managed By | Persistence | Access Pattern | Thread Safety |
    |-------|------------|-------------|----------------|--------------|
    | `StateA` | Component managing | How persisted | How accessed | Thread-safe? |
    | `StateB` | Component managing | How persisted | How accessed | Thread-safe? |

11. **Implementation Decisions Log**
    
    | Decision | Date | Rationale | Affected Components | SPRINT Reference |
    |----------|------|-----------|---------------------|------------------|
    | Decision made | When | Why this approach | Components changed | Link to SPRINT.md |
    | Decision made | When | Why this approach | Components changed | Link to SPRINT.md |

## Tips for Effectiveness

- **Use Component Diagrams**: Create visual representations of the current architecture
- **Focus on Changes**: Emphasize what's different from the original architecture
- **Maintain Tables**: Use consistent tabular formats for easy scanning
- **Link to Code**: Reference actual code locations for further investigation
- **Document Rationale**: Always explain why implementation differs from plans
- **Be Honest About Debt**: Clearly document suboptimal implementations
- **Track Version History**: Note when significant changes occurred
- **Focus on Integration**: Emphasize how components connect and communicate
- **Update Consistently**: Establish a regular process for documentation updates
- **Use Metadata**: Add status markers like stable, evolving, or deprecated
- **Cross-Reference**: Link to relevant SPRINT.md entries for context
- **Color Code Status**: Use visual cues to indicate component maturity
- **Provide Examples**: Include usage examples reflecting actual implementation
- **Consider Automation**: Use code analysis or documentation generators

## Common Pitfalls to Avoid

- **Aspirational Documentation**: Describing components as you wish they were
- **Incomplete Updates**: Updating some sections but not others
- **Missing Rationale**: Not explaining why changes were made
- **Duplicating Architecture**: Repeating ARCHITECTURE.md content when unchanged
- **Excessive Detail**: Including implementation details not relevant to users
- **Outdated Information**: Allowing documentation to fall behind code
- **Inconsistent Structure**: Using different formats for similar components
- **Ignoring Technical Debt**: Not documenting known issues and limitations
- **Missing Context**: Not explaining architectural evolution
- **Focusing Only on Code**: Neglecting data models and integration points
- **Over-formalizing**: Creating rigid documentation that's hard to maintain
- **Unclear Status**: Not indicating component maturity or stability

## Relationship to Other Documents

- **Contrasts With**: ARCHITECTURE.md (planned vs. actual implementation)
- **Built From**: SPRINT.md (documents how implementation evolved)
- **Informs**: DOCUMENTATION.md (guides user documentation)
- **Supports**: CONTRIBUTING.md (helps new contributors understand the code)
- **References**: ROADMAP.md (shows implementation progress against plan)
- **Guides**: Future SPRINT_PLANNING.md (influences what needs refactoring)

The CURRENT_STATE.md document should be updated after each sprint or major code change to ensure it accurately reflects the system as implemented. It serves as the definitive reference for developers working with the codebase, bridging the gap between architectural intentions and actual implementation.
