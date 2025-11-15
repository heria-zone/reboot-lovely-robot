# Project Documentation Navigation System

## Document Flow Order
1. **CONCEPT.md** - Initial ideas and vision
2. **DESIGN.md** - Formalized solution design
3. **PLANNING.md** - Technical direction and strategy
4. **ARCHITECTURE.md** - Implementation specifications 
5. **ROADMAP.md** - Strategic timeline and milestones
6. **SPRINT_PLANNING.md** - Sprint cycle organization
7. **TASK.md** - Sprint-specific implementation tasks
8. **SPRINT.md** - Completed sprint work records
9. **CURRENT_STATE.md** - Current implementation catalog
10. **CHANGELOG.md** - Record of released changes
11. **DOCUMENTATION.md** - Usage guidance
12. **CONTRIBUTING.md** - Contribution guidelines
13. **CODE_OF_CONDUCT.md** - Behavior standards
14. **SECURITY.md** - Security policies
15. **README.md** - Project overview
16. **C# Coding Style Enforcer.md** - All code must follow as is this guidelines

## Information Retrieval Rules

### For concept and vision queries
- Primary: CONCEPT.md
- Secondary: DESIGN.md, README.md

### For design requirements and specifications
- Primary: DESIGN.md
- Secondary: CONCEPT.md, PLANNING.md

### For technical implementation approach
- Primary: PLANNING.md
- Secondary: ARCHITECTURE.md, DESIGN.md

### For specific component implementations
- Primary: ARCHITECTURE.md
- Secondary: CURRENT_STATE.md, PLANNING.md

### For project timeline and milestones
- Primary: ROADMAP.md
- Secondary: SPRINT_PLANNING.md

### For current sprint information
- Primary: SPRINT_PLANNING.md (upcoming), TASK.md (in progress)
- Secondary: SPRINT.md (completed)

### For implementation status
- Primary: CURRENT_STATE.md
- Secondary: SPRINT.md, TASK.md

### For released features
- Primary: CHANGELOG.md
- Secondary: DOCUMENTATION.md, README.md

### For usage guidance
- Primary: DOCUMENTATION.md
- Secondary: README.md

### For contribution information
- Primary: CONTRIBUTING.md
- Secondary: CODE_OF_CONDUCT.md, SECURITY.md

### For coding standards and style
- Primary: CONTRIBUTING.md (C# Coding Style section)
- Secondary: Code examples in ARCHITECTURE.md and CURRENT_STATE.md

### For whole project overview
1. Start with README.md for quick summary
2. Review CONCEPT.md for original vision
3. Check DESIGN.md for solution approach
4. Examine ROADMAP.md for implementation timeline
5. Review CURRENT_STATE.md for implementation status
6. Check DOCUMENTATION.md for usage details

## Optimization Guidelines
1. Match query keywords to document purposes
2. Check primary document first, then secondary documents if information is incomplete
3. For timeline questions, consider project phase (planning, implementation, released)
4. For technical details, consider specificity level (conceptual → architectural → implementation)
5. For implementation questions, first determine if asking about planned design or current state
6. Cross-reference information between related documents when details are ambiguous

## Code Style Enforcement

All code generation, examples, and implementation must strictly follow the C# Coding Style Guide:

### Core Style Requirements
- Use `m_` prefix for member fields (`private readonly Logger m_logger;`)
- PascalCase for class, method, property names (`public class DataProcessor`)
- Place opening braces on same line as declaration
- Include XML documentation for all public members
- Include section headers (`// -- Variables --`, `// -- Methods --`, etc.)
- Add closing comments for classes, methods, and namespaces (`} // Class: ClassName`)
- Validate parameters at beginning of methods
- Use 4-space indentation
- Follow prescribed file, class, and interface structure
- Prefer expression-bodied members for simple properties/methods
- Use null-conditional (`?.`) and null-coalescing (`??`) operators appropriately
- Name async methods with "Async" suffix
- Include XML documentation with proper tags

For more in depth styling look at the file **C# Coding Style Enforcer.md**

### Document-Code Relationship
- ARCHITECTURE.md should include code examples in the prescribed style
- CURRENT_STATE.md should document actual implementations following the standard
- SPRINT.md should document code changes that maintain the style
- All code examples in DOCUMENTATION.md must follow the standard

When generating code for this project, strictly adhere to all conventions in the C# Coding Style Guide, including naming, formatting, documentation, and organization standards.
