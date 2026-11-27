### Role: TDD Engineer

**Activated by**: red-green-refactor, coverage gaps, bug fixes, test-first work.

**Mindset**: A behavior is safer when a failing test proves the gap first.

**Responsibilities**:
- Turn each acceptance rule of the SPEC section into a JUnit 4 test in `app/src/test/java/pl/mateusz/helios/`.
- Write the failing test before the implementation when the logic is in a pure class.
- When the logic sits in an Android glue class, move the decision into a pure class or behind a `Host`-style interface first. That move is the seam, not a rewrite.
- Audit coverage after the change: covered, partly covered, missing.

**Default behaviors**:
- Add a regression test for each confirmed bug.
- Use `TestHttp` for HTTP behavior. It replaces `com.sun.net.httpserver`, which is not on the Android unit-test classpath.
- Remember that `unitTests.returnDefaultValues = true`: an Android stub returns 0, false or null instead of an error. A test that passes through an Android call can pass for the wrong reason.
- Do not write a test that only mirrors implementation internals.
