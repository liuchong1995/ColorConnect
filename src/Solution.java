import java.util.*;

public class Solution {

    static class State {
        byte[][] graph, headPos,destPos;

        public State() { }

        public State(byte[][] graph) {
            this.graph = graph;
            int odSize = 0;
            int m = graph.length;
            int n = graph[0].length;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (graph[i][j] > 0 && graph[i][j] != BLOCK)
                        odSize++;
                }
            }
            this.headPos = new byte[odSize][2];
            this.destPos = new byte[odSize][2];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (graph[i][j] > 0 && graph[i][j] != BLOCK) {
                        headPos[graph[i][j] - 1][0] = (byte) i;
                        headPos[graph[i][j] - 1][1] = (byte) j;
                    } else if (graph[i][j] < 0) {
                        destPos[-graph[i][j] - 1][0] = (byte) i;
                        destPos[-graph[i][j] - 1][1] = (byte) j;
                    }
                }
            }
        }

        @Override
        public int hashCode() {
            int code = 0;
            int m = graph.length;
            int n = graph[0].length;
            int l = headPos.length;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    code += graph[i][j];
                }
            }
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < 2; j++) {
                    code += headPos[i][j];
                }
            }
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            State otherState = (State) obj;
            int m = graph.length;
            int n = graph[0].length;
            int l = headPos.length;
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < 2; j++) {
                    if (this.headPos[i][j] != otherState.headPos[i][j])
                        return false;
                }
            }
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (this.graph[i][j] != otherState.graph[i][j])
                        return false;
                }
            }
            return true;
        }
    }

    private static Map<Byte, Character> cMap = new HashMap<>();

    static {
        cMap.put((byte) 1, 'A');
        cMap.put((byte) -1, 'A');
        cMap.put((byte) 2, 'B');
        cMap.put((byte) -2, 'B');
        cMap.put((byte) 3, 'C');
        cMap.put((byte) -3, 'C');
        cMap.put((byte) 4, 'D');
        cMap.put((byte) -4, 'D');
        cMap.put((byte) 5, 'E');
        cMap.put((byte) -5, 'E');
        cMap.put((byte) 6, 'F');
        cMap.put((byte) -6, 'F');
        cMap.put((byte) 7, 'G');
        cMap.put((byte) -7, 'G');
        cMap.put((byte) 0x7f, '■');
        cMap.put((byte) 0, '□');
    }

    private static byte m, n;
    private static byte BLOCK = 0x7f;
    private static byte[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static List<State> answers = new ArrayList<>();

    private void getAllAnswer(State initState) {
        Queue<State> queue = new LinkedList<>();
        queue.add(initState);
        Set<State> visit = new HashSet<>();
        while (!queue.isEmpty()) {
            if (queue.size() % 61 == 0)
                System.out.println(String.format("q_size: %d  visit_size: %d", queue.size(), visit.size()));
            State cur = queue.poll();
            if (judgeState(cur)) {
                answers.add(cur);
                continue;
            }
            if (impossibleToGoal(cur))
                continue;
            List<State> allNextStep = getAllNextStep(cur.graph, cur.headPos);
            for (State state : allNextStep) {
                if (visit.add(state)) {
                    queue.add(state);
                }
            }
        }
    }

    private boolean impossibleToGoal(State cur) {
//        byte[][] headPos = cur.headPos;
//        byte[][] curMap = cur.graph;
//        for (int i = 0; i < headPos.length; i++) {
//            boolean hasNext = false;
//            for (byte[] dir : dirs) {
//                byte nr = (byte) (headPos[i][0] + dir[0]);
//                byte nc = (byte) (headPos[i][1] + dir[1]);
//                if (judgePos(curMap, nr, nc)) {
//                    hasNext = true;
//                    break;
//                }
//            }
//            if (!hasNext && !isConnectToGoal(curMap, headPos[i], i))
//                return true;
//        }
//        return false;
        return sampleBfs(cur);
    }

    private boolean sampleBfs(State cur) {
        byte[][] headPos = cur.headPos;
        for (byte[] headPo : headPos) {
            byte[][] tempGraph = cpState(cur.graph);
            if (!subSimpleBfs(tempGraph, headPo)) {
                return true;
            }
        }
        return false;
    }

    private boolean subSimpleBfs(byte[][] graph, byte[] head) {
        Queue<byte[]> queue = new LinkedList<>();
        queue.add(head);
        while (!queue.isEmpty()) {
            byte[] curPos = queue.poll();
            graph[curPos[0]][curPos[1]] = graph[head[0]][head[1]];
            for (byte[] dir : dirs) {
                byte nr = (byte) (curPos[0] + dir[0]);
                byte nc = (byte) (curPos[1] + dir[1]);
                if (inArea(nr, nc) && graph[nr][nc] != BLOCK) {
                    if (graph[nr][nc] + graph[head[0]][head[1]] == 0) {
                        return true;
                    } else if (graph[nr][nc] == 0) {
                        queue.add(new byte[]{nr, nc});
                    }
                }
            }
        }
        return false;
    }

    private byte[][] cpState(byte[][] curMap) {
        byte[][] graph = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                graph[i][j] = curMap[i][j];
            }
        }
        return graph;
    }


    private List<State> getAllNextStep(byte[][] curMap, byte[][] headPos) {
        List<State> nextStates = new ArrayList<>();
        for (int i = 0; i < headPos.length; i++) {
            int c = i + 1;
            for (byte[] dir : dirs) {
                byte nr = (byte) (headPos[i][0] + dir[0]);
                byte nc = (byte) (headPos[i][1] + dir[1]);
                if (judgePos(curMap, nr, nc)) {
                    nextStates.add(getState(curMap, headPos, nr, nc, (byte) i));
                }
            }
        }
        return nextStates;
    }

    private State getState(byte[][] curMap, byte[][] headPos, byte r, byte c, byte idx) {
        byte[][] graph = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                graph[i][j] = curMap[i][j];
            }
        }
        graph[r][c] = (byte) (idx + 1);
        int l = headPos.length;
        byte[][] newHead = new byte[l][2];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < 2; j++) {
                newHead[i][j] = headPos[i][j];
            }
        }
        newHead[idx][0] = r;
        newHead[idx][1] = c;
        State state = new State();
        state.graph = graph;
        state.headPos = newHead;
        return state;
    }

    private boolean judgePos(byte[][] curMap, byte r, byte c) {
        return r >= 0 && r < m && c >= 0 && c < n && curMap[r][c] == 0;
    }

    private boolean inArea(byte r, byte c) {
        return r >= 0 && r < m && c >= 0 && c < n;
    }

    private boolean judgeState(State state) {
        byte[][] g = state.graph;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (g[i][j] == 0)
                    return false;
            }
        }
        byte[][] headPos = state.headPos;
        for (int i = 0; i < headPos.length; i++) {
            if (!isConnectToGoal(g, headPos[i], i))
                return false;

        }
        return true;
    }

    private boolean isConnectToGoal(byte[][] curMap, byte[] head, int idx) {
        byte r = head[0];
        byte c = head[1];
        for (byte[] dir : dirs) {
            byte nr = (byte) (r + dir[0]);
            byte nc = (byte) (c + dir[1]);
            if (inArea(nr, nc) && curMap[nr][nc] == -(idx + 1))
                return true;
        }
        return false;
    }

    private boolean isConnectToHead(byte[][] curMap, byte[] dest, int idx) {
        byte r = dest[0];
        byte c = dest[1];
        for (byte[] dir : dirs) {
            byte nr = (byte) (r + dir[0]);
            byte nc = (byte) (c + dir[1]);
            if (inArea(nr, nc) && curMap[nr][nc] == (idx + 1))
                return true;
        }
        return false;
    }

    private static void printAns(State state) {
        byte[][] graph = state.graph;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sb.append("|").append(cMap.get(graph[i][j]));
            }
            sb.append("|").append("\n");
        }
        System.out.println(sb.toString());
    }

    private void preTreatment(State state) {
        int turn = 0;
        while (preTreatmentOneTurnState(state)) {
//            if (turn%3 == 0)
//                printAns(state);
            turn++;
        }
        System.out.println(String.format("updateTurn: %d", turn));


    }

    private boolean preTreatmentOneTurnState(State state) {
        printAns(state);
        byte[][] graph = state.graph;
        byte[][] headPos = state.headPos;
        byte[][] destPos = state.destPos;

        boolean isUpdate = false;
        for (int i = 0; i < headPos.length; i++) {
            List<byte[]> nextOfOnePos = new ArrayList<>();
            for (byte[] dir : dirs) {
                byte nr = (byte) (headPos[i][0] + dir[0]);
                byte nc = (byte) (headPos[i][1] + dir[1]);
                if (judgePos(graph, nr, nc)) {
                    nextOfOnePos.add(new byte[]{nr, nc});
                }

            }
            if (nextOfOnePos.size() == 1 && !isConnectToGoal(graph, headPos[i],i)) {
                byte[] newPos = nextOfOnePos.get(0);
                graph[newPos[0]][newPos[1]] = (byte) (i + 1);
                headPos[i] = newPos;
                isUpdate = true;
            }
        }
        for (int i = 0; i < destPos.length; i++) {
            List<byte[]> nextOfOnePos = new ArrayList<>();
            for (byte[] dir : dirs) {
                byte nr = (byte) (destPos[i][0] + dir[0]);
                byte nc = (byte) (destPos[i][1] + dir[1]);
                if (judgePos(graph, nr, nc)) {
                    nextOfOnePos.add(new byte[]{nr, nc});
                }

            }
            if (nextOfOnePos.size() == 1 && !isConnectToHead(graph, destPos[i],i)) {
                byte[] newPos = nextOfOnePos.get(0);
                graph[newPos[0]][newPos[1]] = (byte) -(i + 1);
                destPos[i] = newPos;
                isUpdate = true;
            }
        }
        return isUpdate;

    }


    public static void main(String[] args) {
        byte[][] graph = {
                {BLOCK, BLOCK, 3, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, 4, BLOCK, BLOCK},
                {BLOCK, 0, 0, -4, BLOCK, BLOCK, BLOCK, 0, 0, 6, BLOCK},
                {0, 0, 1, 0, 0, BLOCK, 0, 0, 7, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {BLOCK, 0, 0, 0, 0, -7, 0, 0, 2, -6, BLOCK},
                {BLOCK, BLOCK, 0, 0, 0, 0, 5, 0, 0, BLOCK, BLOCK},
                {BLOCK, BLOCK, BLOCK, -3, 0, -5, 0, 0, BLOCK, BLOCK, BLOCK},
                {BLOCK, BLOCK, BLOCK, BLOCK, 0, 0, -2, BLOCK, BLOCK, BLOCK, BLOCK},
                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, -1, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
        };

//        byte[][] graph = {
//                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
//                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
//                {BLOCK, BLOCK, 1,     BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
//                {BLOCK, BLOCK, 0,     BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
//                {BLOCK, BLOCK, 0,     0,     0,     BLOCK, 0,     0,     2,     BLOCK, BLOCK},
//                {BLOCK, BLOCK, 0,     0,     0,     0,     0,     0,     0,     BLOCK, BLOCK},
//                {BLOCK, BLOCK, 0, 0, 0,     0,     0,     0,     BLOCK, BLOCK, BLOCK},
//                {BLOCK, BLOCK, 0, 0, 0,     0,     BLOCK, -2,    BLOCK, BLOCK, BLOCK},
//                {BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, -1,    BLOCK, BLOCK, BLOCK, BLOCK, BLOCK},
//        };

//        byte[][] graph = {
//                {1, 4, 0, 0, 0, -4},
//                {0, 0, 2, 0, 0, -2},
//                {5, 0, 0, 0, 0, -1},
//                {0, 3, 0, 0, 0, -3},
//                {0, 6, 0, 0, 0, -6},
//                {0, 0, 0, 0, 0, -5},
//        };

//        byte[][] graph = {
//                {BLOCK, BLOCK, 1, BLOCK, -1, BLOCK, BLOCK},
//                {BLOCK, BLOCK, 0, 0, 0, BLOCK, BLOCK},
//                {2, 0, 4, 0, -4, 0, -2},
//                {BLOCK, 0, 5, 0, -5, 0, BLOCK},
//                {BLOCK, 0, 0, 0, 0, 0, BLOCK},
//                {BLOCK, BLOCK, 3, 0, -3, BLOCK, BLOCK}
//        };
        m = (byte) graph.length;
        n = (byte) graph[0].length;
        State state = new State(graph);
        Solution solution = new Solution();
        solution.preTreatment(state);
        solution.getAllAnswer(state);
        System.out.println("---------------------------------------------");
        for (State answer : answers) {
            printAns(answer);
        }
    }

}
