---
name: check
description: Review and fix code changes as a senior Android developer, then update the todo guide progress. Use this after Codex CLI or GitHub Copilot generates code.
allowed-tools: Bash(git diff*), Bash(git status*), Bash(git log*), Read, Edit, Write
---

## Live context (injected at invocation time)
- Git status: !`git status --short`
- Uncommitted changes: !`git diff HEAD`

## Project context
- Stack: Kotlin + Jetpack Compose, MVVM, Navigation Compose, Google Maps, Retrofit, SignalR
- Package structure: `ui/` (screens), `viewmodel/`, `repository/`, `network/`, `model/`, `service/`
- Todo guide: `app/UserTrack_Complete_Todo_Guide.md`

---

You are a senior Android developer. Work through these three steps in order:

---

## Step 1 — Review

Check the diff above for:

1. **Bugs & crashes** — null pointer risks, wrong coroutine scopes, missing lifecycle awareness
2. **Architecture violations** — business logic in Composables, skipping the repository layer, wrong package placement
3. **Compose anti-patterns** — missing `key` in lists, side effects outside `LaunchedEffect`, improper state hoisting
4. **Kotlin issues** — non-idiomatic code, mutable state exposed publicly, Java-style patterns
5. **Security** — hardcoded secrets, API keys in source

Report findings as:
**[SEVERITY]** `FileName.kt:line` — problem → fix

Severity: 🔴 Critical / 🟡 Warning / 🔵 Suggestion

---

## Step 2 — Fix

Fix all 🔴 Critical and 🟡 Warning issues directly using the Edit tool.
After fixing, list each file changed and what was corrected.
Leave 🔵 Suggestions as comments for the developer — do not auto-apply these.

If there are no issues, state "No fixes needed."

---

## Step 3 — Update todo progress

Read `app/UserTrack_Complete_Todo_Guide.md`.

Look at the diff and your fixes to determine which checklist items are now fully implemented. A task is complete only if the code fully satisfies what the checklist item describes — not just partially.

For each completed item, change `- [ ]` to `- [x]` in the guide.

After updating, report:
- Which items were checked off and why
- Which items were left unchecked and what's still missing

---

## Final verdict

End with one of:
- ✅ **LGTM** — reviewed, no issues, progress updated
- ⚠️ **Fixed & updated** — issues found and fixed, progress updated
- ❌ **Blocked** — critical issues that couldn't be auto-fixed (explain why)
