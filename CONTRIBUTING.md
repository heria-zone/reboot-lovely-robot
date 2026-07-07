# Contributing to Lovely Robot

Thank you for considering a contribution. This covers everything you need before you start.

---

## Your Credit, Your Copyright

**The copyright in your contribution stays with you.** Lovely Robot is MIT licensed — you grant the project permission to use your work, but you retain ownership.

Meaningful contributions are credited in [`CONTRIBUTORS.md`](CONTRIBUTORS.md) with your name, handle, and a description of what you built. Smaller contributions (bug fixes, minor fixes) are credited through git commit authorship, which GitHub tracks automatically.

If you add something significant — a new feature, a new robot type, a new system — your name goes on it explicitly. That is the standard here.

---

## Repository Structure

Lovely Robot is a monorepo. Before touching anything, understand the layout:

```
sources/
├── legacy/          ← Lovely Robot: Legacy (8 robot types, full feature set)
│   ├── Common/      ← Shared logic across loaders
│   ├── Fabric/      ← Fabric-specific registration
│   ├── Forge/       ← Forge-specific registration
│   └── NeoForge/    ← NeoForge-specific registration
├── tribute/         ← Lovely Robot: Tribute (original 4 robots, preservation build)
│   └── [same structure]
├── reboot/          ← Lovely Robot: Reboot (Empyrium, Hyperion, Prime — forward-looking)
│   └── [same structure]
└── common/
    ├── lovelylib-1.21.1/   ← LovelyLib (robot shared library)
    └── hzlib-1.21.1/       ← HZLib (general shared library)
```

**Rule of thumb:**
- Logic shared by all three variants → `sources/common/lovelylib-1.21.1/`
- Logic specific to one variant → `sources/<variant>/Common/`
- Loader-specific registration only → `sources/<variant>/<Loader>/`

When in doubt, prefer Common over loader-specific. Loader modules should contain as little logic as possible.

---

## Setting Up

```bash
git clone https://github.com/heria-zone/reboot-lovely-robot
```

**Requirements:**
- JDK 21
- Gradle (wrapper included)

**Build a specific variant:**
```bash
./gradlew :legacy-fabric:build
./gradlew :legacy-forge:build
./gradlew :legacy-neoforge:build
```

Replace `legacy` with `tribute` or `reboot` for the other variants.

---

## Code Standards

[`docs/guidelines/Coding Style Enforcer.md`](docs/guidelines/Coding%20Style%20Enforcer.md)

Public API requires JavaDoc. Section headers in classes. Closing comments on methods and classes. `Objects.requireNonNull` for validation. All of this is in the style guide — read it before your first PR.

---

## Adding a New Robot

The full guide is here:

[`docs/guidelines/New_Robot_Family_Guide.md`](docs/guidelines/New_Robot_Family_Guide.md)

The short version:
1. Add a constant to `RobotVariant` in LovelyLib
2. Add a builder entry in the appropriate definitions file (`LegacyRobotDefinitions`, `RebootRobotDefinitions`, or `TributeRobotFamilies`)
3. No loader code changes required — the registry handles registration automatically

New robots for **Legacy** go in `LegacyRobotDefinitions`.
New robots for **Reboot** go in `RebootRobotDefinitions` — Reboot-exclusive families only.
**Tribute** does not accept new robot types by design — it is a preservation build.

---

## Which Variant Does My Change Belong To?

| Change type | Where it goes |
|---|---|
| New robot available in all variants | LovelyLib + both family registries |
| New robot exclusive to Reboot | RebootRobotFamilies only |
| New feature for all robots | LovelyLib Common |
| New feature for Legacy robots only | Legacy Common |
| New feature for Reboot robots only | Reboot Common |
| Bug fix in shared AI goal | LovelyLib Common |
| Bug fix in variant-specific behaviour | That variant's Common |

---

## Commit Messages

[`docs/guidelines/maintenance/GIT_COMMIT_GUIDELINES.md`](docs/guidelines/maintenance/GIT_COMMIT_GUIDELINES.md)

```
ADD:   New feature, robot, or class
FIX:   Bug fix
REF:   Refactor without behaviour change
REM:   Removal
DOCS:  Documentation only
CFG:   Build or configuration change
NULL:  Trivial formatting
```

---

## Opening a Pull Request

- One logical change per PR
- All loaders for the affected variant must build without warnings
- Update `CHANGELOG.md` for user-facing changes
- Fill in the PR template checklist

PRs that add new robots or change shared AI systems will receive more scrutiny — those changes affect all three variants.

---

## Reporting a Security Issue

See [`SECURITY.md`](SECURITY.md). Do not open a public issue for security vulnerabilities.

---

## Questions

[Heria Zone Discord](https://discord.gg/KdZZMj89bU) — `#dev` channel.
