# README.md - Guidelines

## Purpose
The README.md document provides essential product information and overview for users and potential contributors. For software products/applications, it serves as the primary entry point focusing on what the product does, why it exists, and its key features. Technical setup and installation details should be separated into INSTALLATION.md to keep the README product-focused and accessible to non-technical audiences.

## When to Create
- At the beginning of a project, even as a simple placeholder
- When publishing a project to a repository or platform
- When preparing for public or wider release
- When documenting a new major version
- When the project is a product/application (not just a library)

## Product vs. Library README

**For Products/Applications** (like Uncogest):
- Focus on what the product does and why users should care
- Emphasize features, benefits, and use cases
- Keep technical details minimal
- Separate installation/setup into INSTALLATION.md
- Target both users and potential contributors

**For Libraries/Frameworks**:
- Focus on API usage and integration
- Include installation in README (it's brief)
- Emphasize code examples and quick start
- Target developers exclusively

## How to Approach

### 1. Create a Strong Introduction
- Start with a clear, concise description of what the project is
- Highlight the key value proposition and purpose
- Include visual aids (logo, screenshots, diagrams) where helpful
- Consider adding badges for build status, version, etc.

### 2. Prioritize Getting Started Information
- Focus on helping users get up and running quickly
- Include complete installation instructions
- Provide basic usage examples
- Link to more detailed documentation

### 3. Organize Information Hierarchically
- Put the most important information at the top
- Use clear headings and subheadings
- Create a logical flow from introduction to advanced topics
- Consider the needs of different reader types (users, contributors, etc.)

### 4. Keep It Current
- Update with each significant release
- Ensure examples and screenshots reflect the current version
- Remove outdated information
- Maintain accurate dependency information

### 5. Balance Comprehensiveness and Readability
- Include essential information without overwhelming
- Use examples to illustrate complex concepts
- Link to detailed documentation rather than including everything
- Consider the README as an entry point, not a complete manual

### 6. Write for Your Audience
- Consider technical expertise of likely readers
- Explain domain-specific terminology
- Provide context for specialized concepts
- Use accessible language and examples

### 7. Support Contribution
- Make it easy for others to get involved
- Include clear contribution guidelines
- Acknowledge contributors
- Specify license and usage rights

## Recommended Sections (Product-Focused)

1. **Project Name and Description**
   - Clear project title and logo (if available)
   - Concise description of what the product does
   - Key value proposition
   - Current status (alpha, beta, production, in development)
   - Badges (build status, version, license) - optional for products

2. **What is [Product Name]?**
   - Expanded description of the product
   - Target audience
   - Core features overview
   - Key differentiators

3. **Why [Product Name]?**
   - Benefits and value proposition
   - Problems it solves
   - Unique advantages
   - Use cases

4. **How It Works**
   - High-level workflow
   - User journey overview
   - Key concepts
   - Simple process explanation

5. **Technology**
   - Brief technology overview
   - Key frameworks/tools used
   - Architecture principles (high-level)
   - No detailed technical setup

6. **Current Status**
   - Development stage
   - Available features
   - Roadmap (brief)
   - Release information

7. **Getting Started**
   - **For Users**: Download links, quick start
   - **For Developers**: Link to INSTALLATION.md
   - Clear separation of audiences

8. **Documentation**
   - Links to detailed documentation
   - Product specifications
   - User guides
   - Developer resources (link to INSTALLATION.md)

9. **Architecture Principles** (Optional)
   - High-level design philosophy
   - Key architectural decisions
   - No implementation details

10. **Contributing**
    - Brief contribution overview
    - Link to INSTALLATION.md for setup
    - Link to CONTRIBUTING.md if available
    - Code of conduct reference

11. **Key Technologies**
    - List of major technologies
    - Brief description of each
    - No installation instructions

12. **License**
    - License type and terms
    - Copyright information

13. **Contact**
    - Support information
    - Community links
    - Contact details

## Tips for Effectiveness

- **Focus on Benefits**: Emphasize what users gain, not just features
- **Use Visual Aids**: Screenshots, diagrams, and GIFs explain concepts efficiently
- **Tell a Story**: Help readers understand the product's purpose and value
- **Maintain Consistent Style**: Use the same formatting, tone, and terminology
- **Employ Markdown Features**: Utilize headings, lists, code blocks, and tables
- **Separate Audiences**: Clear sections for users vs. developers
- **Link to Technical Docs**: Reference INSTALLATION.md for setup details
- **Update Regularly**: Refresh content with each significant change or release
- **Add Status Information**: Indicate development stage and availability
- **Include Contact Information**: Provide ways to get help or ask questions
- **Be Honest About Status**: Acknowledge current limitations and roadmap
- **Consider Internationalization**: Provide translations for global products
- **Use Inclusive Language**: Make documentation welcoming to all potential users
- **Optimize for Skimming**: Use bold text, lists, and headings for quick scanning
- **Show, Don't Just Tell**: Use examples and scenarios to illustrate value

## Common Pitfalls to Avoid

- **Too Technical**: Including installation/setup details (use INSTALLATION.md instead)
- **Overwhelming Detail**: Including too much information instead of linking to it
- **Outdated Information**: Not updating status and feature information
- **Unclear Value Proposition**: Not explaining why someone should use your product
- **Feature Lists Without Context**: Not explaining benefits or use cases
- **Technical Jargon**: Using terminology that non-technical users won't understand
- **Missing Target Audience**: Not clearly stating who the product is for
- **No Status Information**: Not indicating development stage or availability
- **Lack of Examples**: Explaining concepts without showing practical usage
- **Poor Organization**: Making it hard to find specific information
- **Inconsistent Formatting**: Mixing styles and structures
- **Focusing Only on Features**: Not explaining the problems solved
- **Mixing Audiences**: Confusing user information with developer setup
- **Overwhelming First Impression**: Making the product seem too complex
- **No Clear Next Steps**: Not guiding users/developers to appropriate resources
- **Ignoring Visual Learners**: Relying only on text without diagrams or screenshots

## Relationship to Other Documents

- **Complements**: INSTALLATION.md (product overview vs. developer setup)
- **Summarizes**: Project specifications and design documents
- **References**: Detailed documentation (user guides, specifications)
- **Links To**: CONTRIBUTING.md, CHANGELOG.md, INSTALLATION.md
- **Supported By**: Detailed tutorials, examples, and reference documentation

## Key Differences from INSTALLATION.md

| README.md | INSTALLATION.md |
|-----------|-----------------|
| Product-focused | Developer-focused |
| What the product does | How to build/develop |
| User features & benefits | Development setup |
| Non-technical audience | Technical audience |
| High-level overview | Detailed instructions |
| Why use this product | How to contribute |

The README.md should be treated as a living document, updated regularly as the project evolves. For products/applications, it serves as the marketing front door that explains the value proposition, while INSTALLATION.md handles the technical onboarding for developers.
