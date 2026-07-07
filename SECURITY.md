# Security Policy — Lovely Robot

## Reporting a Vulnerability

**Do not open a public GitHub issue for security vulnerabilities.**

If you find something, tell me privately and I'll fix it. That's all this is.

### Primary channel — GitHub Private Vulnerability Reporting

Go to the [Security tab](../../security) of this repository and click **"Report a vulnerability"**. GitHub creates a private draft between you and the maintainer. No public exposure, no email required.

### Secondary channel — Discord

Send a direct message to **MSymbios** on the [Heria Zone Discord](https://discord.gg/KdZZMj89bU).

---

## What Counts as a Security Issue

Lovely Robot is a Minecraft companion mod. The realistic threat surface:

- **Crash exploits** — malformed robot NBT, crafted core items, or spawn items that crash a server or client
- **Save corruption** — migration bugs that silently destroy robot data, player inventory, or world state
- **Duplication bugs** — robot core or spawn item duplication exploitable on multiplayer servers
- **Command exploits** — `/llovely` commands that allow unintended access or data manipulation

## What Does NOT Belong Here

- General bugs → open a [GitHub Issue](../../issues)
- Balance feedback or feature requests → open a [GitHub Issue](../../issues)
- Questions → join [Discord](https://discord.gg/KdZZMj89bU) `#dev`

---

## What to Include in a Report

- Description of the vulnerability and its impact
- Steps to reproduce
- Mod version (Legacy / Tribute / Reboot + version number)
- Minecraft version and loader (Fabric / Forge / NeoForge)
- Server or singleplayer
- Relevant crash logs or screenshots

---

## What to Expect

- **Acknowledgement** — within 48 hours
- **Initial assessment** — within 1 week
- **Fix timeline** — critical issues (server crashes, dupe bugs) are prioritised over everything else
- **Credit** — if you want to be credited for the find, say so in your report

This is a solo project. I will always respond, but I am one person.

---

## Supported Versions

| Variant | Supported |
|---|---|
| Lovely Legacy — latest | ✅ |
| Lovely Tribute — latest | ✅ |
| Lovely Reboot — latest | ✅ |
| Any older release | ⚠️ Critical fixes only |
