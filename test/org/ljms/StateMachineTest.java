/*
 * Copyright (c) 2001, 2026 Vasili Gavrilov. MIT License; see LICENSE.
 */
package org.ljms;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * Proves the state machine is well formed.
 *
 * The machine is not written here. It is parsed from its single source of
 * truth, the "Character form" block in doc/Queue_States.txt. Editing the doc
 * and re-running this is how the machine stays correct; a second copy in code
 * would only be something for the doc to drift from.
 *
 * A finite automaton is the tractable tier of the Chomsky hierarchy, so every
 * property below is a decidable graph check, not a test case someone had to
 * think of:
 * <ul>
 *   <li>a start state exists</li>
 *   <li>reachability, every state is reachable from start (no orphan)</li>
 *   <li>no dead end, every non-terminal has an outgoing edge</li>
 *   <li>sinks, terminal states have no outgoing edge</li>
 *   <li>determinism, no (state, event) pair maps to two targets</li>
 *   <li>non-empty, some path leads from start to a terminal</li>
 * </ul>
 *
 * Add a state to the doc and this checks it automatically. No database, no
 * network; runs in milliseconds.
 *
 * <b>What it cannot see</b> is liveness: the machine has cycles
 * (NEW -&gt; IN_PROCESS -&gt; NEW on ^expire, and ERROR -&gt; NEW on restart),
 * and graph reachability cannot tell you they terminate. That argument is made
 * by hand in the doc, and it rests on neither edge firing on its own: ^expire
 * needs a worker to die, restart needs an operator. There is deliberately no
 * automatic retry edge, which is what would make the first cycle
 * self-sustaining.
 */
public class StateMachineTest extends TestCase {

    private static final String DOC = "doc/Queue_States.txt";

    private String start;
    private final Set<String> states    = new LinkedHashSet<String>();
    private final Set<String> terminals = new LinkedHashSet<String>();
    /** state -> list of { event, target } */
    private final Map<String, List<String[]>> edges = new LinkedHashMap<String, List<String[]>>();


    protected void setUp() throws Exception {

        Path doc = null;
        for (String prefix : new String[] { "", "../", "../../" }) {
            Path p = Paths.get(prefix + DOC);
            if (Files.isRegularFile(p)) { doc = p; break; }
        }
        assertNotNull("could not find " + DOC + " from " + Paths.get("").toAbsolutePath(), doc);

        List<String> block = new ArrayList<String>();
        boolean in = false;
        for (String line : Files.readAllLines(doc)) {
            if (!in) {
                if (line.startsWith("Character form")) in = true;   // skip the header itself
                continue;
            }
            if (line.trim().isEmpty()) break;                       // a blank line ends the block
            block.add(line);
        }
        assertFalse("no 'Character form' block in " + DOC, block.isEmpty());

        // lines look like:  NEW = start | IN_PROCESS[take]
        Pattern edge = Pattern.compile("^([A-Z_]+)\\[(.+)\\]$");
        for (String raw : block) {
            String line = raw.trim();
            int eq = line.indexOf('=');
            if (eq < 0) continue;

            String state = line.substring(0, eq).trim();
            if (state.isEmpty()) continue;
            states.add(state);
            edges.put(state, new ArrayList<String[]>());

            for (String token : line.substring(eq + 1).split("\\|")) {
                token = token.trim();
                if (token.equals("start"))    { start = state;         continue; }
                if (token.equals("terminal")) { terminals.add(state);  continue; }

                Matcher m = edge.matcher(token);
                if (m.matches()) {
                    edges.get(state).add(new String[] { m.group(2).trim(), m.group(1).trim() });
                    states.add(m.group(1).trim());   // a target that is never a left-hand side
                }
            }
        }
    }


    public void testParsed() {
        assertFalse("no states parsed from the character form", states.isEmpty());
        assertNotNull("no start state (a 'start' token) found", start);
    }

    public void testEveryStateIsReachable() {
        Set<String> orphans = new LinkedHashSet<String>(states);
        orphans.removeAll(reachable());
        assertTrue("unreachable state(s) from " + start + ": " + orphans, orphans.isEmpty());
    }

    public void testNoDeadEnd() {
        for (String s : states) {
            if (terminals.contains(s)) continue;
            assertFalse("non-terminal state with no way out (dead end): " + s,
                        edges.get(s) == null || edges.get(s).isEmpty());
        }
    }

    public void testTerminalsAreSinks() {
        for (String s : terminals) {
            assertTrue("terminal state '" + s + "' has an outgoing edge",
                       edges.get(s) == null || edges.get(s).isEmpty());
        }
    }

    public void testDeterministic() {
        for (String s : states) {
            Map<String, String> byEvent = new HashMap<String, String>();
            for (String[] e : edges.getOrDefault(s, Collections.<String[]>emptyList())) {
                String previous = byEvent.put(e[0], e[1]);
                if (previous != null && !previous.equals(e[1])) {
                    fail("non-deterministic: state " + s + " on event '" + e[0]
                       + "' goes to both " + previous + " and " + e[1]);
                }
            }
        }
    }

    public void testSomePathReachesATerminal() {
        Set<String> reached = reachable();
        for (String t : terminals) if (reached.contains(t)) return;
        fail("machine is empty: no path from " + start + " to a terminal state");
    }

    /** The names in Queue.java must be the names in the spec, one symbol, end to end. */
    public void testCodeAndSpecUseTheSameNames() {
        for (String state : new String[] { Queue.NEW, Queue.IN_PROCESS, Queue.DONE, Queue.ERROR }) {
            assertTrue("state " + state + " from Queue.java is not in " + DOC + "'s machine",
                       states.contains(state));
        }
        assertEquals("the spec has states Queue.java does not name: " + states,
                     4, states.size());
    }


    private Set<String> reachable() {
        Set<String> seen = new HashSet<String>();
        Deque<String> stack = new ArrayDeque<String>();
        if (start != null) { seen.add(start); stack.push(start); }
        while (!stack.isEmpty()) {
            for (String[] e : edges.getOrDefault(stack.pop(), Collections.<String[]>emptyList())) {
                if (seen.add(e[1])) stack.push(e[1]);
            }
        }
        return seen;
    }
}
