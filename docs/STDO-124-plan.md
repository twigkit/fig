**RESUME STATE** *(top of the file; rewritten in place, never appended to; keep under 12 lines)*
- **Reconciled at**: this branch's HEAD, the P4 build-verification fix commit.
- **Authoritative**: `PLAN.md` in `lucidworks/tbe-pitches`, branch `STDO-124-bet`, for stage
  sequencing, the done-condition, the stage graph and every measured claim. This file exists only
  so `/team-studios:status` and the compaction resume hook have something to find in this repo.
- **Next**: this repo's release cut (an actual version tag) is a human action, not part of P4's
  done-condition.

**Decisions** *(append-only)*
- **This file is a pointer, not a fork of the plan.** Full decision log: `tbe-pitches`'s
  `decision-log.md` on `STDO-124-bet`.
- **Two real defects fixed, not worked around**, to get a clean `mvn clean package` from an empty
  local repository: a `NullPointerException` in `PropertiesLoader.readFolder` on a `null`
  `listFiles()` result, and four test failures caused by a process-wide `Fig` singleton left
  mutated by `FigUtilsTest` with no teardown.

**Wiki candidates** *(append during the work, not at the end)*
- (empty)

---

resume-state template r3-tamarind
