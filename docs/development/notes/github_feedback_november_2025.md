# GitHub Feedback Collection

**Collection Date:** November 6, 2025  
**Mod Version:** Various  
**Minecraft Version:** 1.18.2 - 1.21.1

---

## Feature Requests

### High Priority
| Issue # | Title | Description | Requester | Votes/Reactions | Status | Notes |
|---------|-------|-------------|-----------|-----------------|--------|-------|
| #2 | Add TagKey refuses_pet_beds to robots | Add TagKey to prevent robots from respawning from pet beds, which causes duping issues when robots die but pet beds make them respawn at dawn | @donmor | N/A | In Progress | Author reviewing during codebase rewrite |

### Medium Priority
| Issue # | Title | Description | Requester | Votes/Reactions | Status | Notes |
|---------|-------|-------------|-----------|-----------------|--------|-------|

### Low Priority / Under Consideration
| Issue # | Title | Description | Requester | Votes/Reactions | Status | Notes |
|---------|-------|-------------|-----------|-----------------|--------|-------|

---

## Bug Reports

### Critical (Game Breaking)
| Issue # | Title | Description | Reproduction Steps | Affected Version | Reported By | Status | Fix Notes |
|---------|-------|-------------|-------------------|------------------|-------------|--------|-----------|

### Major (Significant Impact)
| Issue # | Title | Description | Reproduction Steps | Affected Version | Reported By | Status | Fix Notes |
|---------|-------|-------------|-------------------|------------------|-------------|--------|-----------|
| #6 | Pathfinding through doors | Robots can't pathfind through doors or are too tall to go through if they could | Not specified | Various | @TheEternalAce | Open | Author investigating - affects both vanilla and modded doors |
| #4 | Between robots friendly fire is very hot | Friendly fire occurs between players and robots, as well as between robots themselves | Not specified | Current | @lowy | In Progress | Author planning bug cleanup update |

### Minor (Low Impact)
| Issue # | Title | Description | Reproduction Steps | Affected Version | Reported By | Status | Fix Notes |
|---------|-------|-------------|-------------------|------------------|-------------|--------|-----------|

---

## Questions & Support

| Issue # | Question | Answer/Resolution | Resolved By | Should Be Documented? |
|---------|----------|-------------------|-------------|----------------------|
| #5 | 1.21.1 neoforge port request | User asked about porting mods to 1.21.1 neoforge since big forge mods are migrating | @s2erge confirmed continued forge support and neoforge support addition | Yes |

---

## Enhancement Suggestions

| Issue # | Title | Description | Requester | Feasibility | Status | Notes |
|---------|-------|-------------|-----------|-------------|--------|-------|

---

## Duplicate/Invalid Issues

| Issue # | Title | Reason | Original Issue # (if duplicate) |
|---------|-------|--------|--------------------------------|

---

## Completed/Closed Issues

| Issue # | Title | Resolution | Completed By | Date Closed |
|---------|-------|------------|--------------|-------------|
| #5 | 1.21.1 neoforge port | Added neoforge support while continuing forge support | @s2erge | Last week |

---

## Action Items

- [ ] Fix pathfinding through doors issue (#6)
- [ ] Implement TagKey refuses_pet_beds feature (#2) 
- [ ] Release bug cleanup update for friendly fire issue (#4)
- [ ] Continue codebase rewrite affecting multiple issues
- [ ] Maintain support for both Forge and NeoForge platforms

## Notes for Next Update
- Major codebase rewrite in progress affecting multiple systems
- Focus on bug fixes for pathfinding and friendly fire issues
- Continue dual platform support (Forge + NeoForge)
- Pet bed duping issue needs TagKey implementation