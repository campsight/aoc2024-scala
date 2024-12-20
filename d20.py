from collections import deque, defaultdict, Counter

def parse_input(file_path):
    """Parses the input file to extract the grid, start, and end positions."""
    with open(file_path, 'r') as f:
        grid = [list(line.strip()) for line in f.readlines()]

    start, end = None, None
    for y, row in enumerate(grid):
        for x, cell in enumerate(row):
            if cell == 'S':
                start = (x, y)
            elif cell == 'E':
                end = (x, y)

    return grid, start, end

def bfs_from_point(grid, start, end=None):
    """Performs BFS from a given start point to compute shortest paths to all reachable points."""
    directions = [(0, 1), (1, 0), (0, -1), (-1, 0)]
    queue = deque([(start, 0)])
    visited = set()
    visited.add(start)
    distances = {}

    while queue:
        (x, y), dist = queue.popleft()
        distances[(x, y)] = dist

        # Ensure end position is explicitly included in distances
        if end and (x, y) == end:
            distances[end] = dist

        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            if 0 <= nx < len(grid[0]) and 0 <= ny < len(grid) and (nx, ny) not in visited:
                if grid[ny][nx] != '#':  # Can only move to non-wall cells
                    visited.add((nx, ny))
                    queue.append(((nx, ny), dist + 1))

    return distances

def precompute_bfs(grid, end):
    """Precomputes BFS distances for all track positions."""
    bfs_distances = {}
    for y, row in enumerate(grid):
        for x, cell in enumerate(row):
            if cell != '#':
                bfs_distances[(x, y)] = bfs_from_point(grid, (x, y), end=end)
    return bfs_distances

def find_shortest_path(grid, start, end, bfs_distances):
    """Finds the shortest path from start to end using precomputed BFS distances."""
    return bfs_distances[start].get(end, float('inf'))

def simulate_cheats(grid, path, bfs_distances, min_savings):
    """Simulates cheats along the shortest path to evaluate possible savings."""
    directions = [(0, 1), (1, 0), (0, -1), (-1, 0)]
    cheats = []

    for i, (x, y) in enumerate(path):
        for dx1, dy1 in directions:
            # Try a cheat of 1 or 2 steps through walls
            for cheat_length in range(1, 3):
                nx, ny = x + dx1 * cheat_length, y + dy1 * cheat_length

                # Ensure we end back on a track position or exactly at the end
                if 0 <= nx < len(grid[0]) and 0 <= ny < len(grid) and (grid[ny][nx] == '.' or (nx, ny) == end):
                    # Calculate the new path length using the cheat
                    remaining_path_length = bfs_distances[(nx, ny)].get(end, float('inf'))
                    if remaining_path_length < float('inf'):
                        cheat_savings = len(path) - (i + 1 + cheat_length + remaining_path_length)
                        if cheat_savings >= min_savings:
                            cheats.append((cheat_savings, (x, y), (nx, ny)))

    return cheats

def simulate_cheats_part2(grid, path, bfs_distances, min_savings):
    """Simulates cheats for part 2 (up to 20 picoseconds) along the shortest path."""
    cheats = []

    def manhattan_distance(p1, p2):
        return abs(p1[0] - p2[0]) + abs(p1[1] - p2[1])

    for i, (x, y) in enumerate(path):
        for end_x, end_y in bfs_distances:
            if manhattan_distance((x, y), (end_x, end_y)) <= 20:
                if grid[end_y][end_x] != '#':  # End point must not be a wall
                    remaining_path_length = bfs_distances[(end_x, end_y)].get(end, float('inf'))
                    if remaining_path_length < float('inf'):
                        cheat_savings = len(path) - (i + 1 + manhattan_distance((x, y), (end_x, end_y)) + remaining_path_length)
                        if cheat_savings >= min_savings:
                            cheats.append((cheat_savings, (x, y), (end_x, end_y)))

    return cheats

def print_grid(grid, path=None):
    """Prints the grid optionally overlaying a path."""
    grid_copy = [row.copy() for row in grid]
    if path:
        for x, y in path:
            if grid_copy[y][x] not in ['S', 'E']:
                grid_copy[y][x] = 'O'

    for row in grid_copy:
        print(''.join(row))

def count_cheats_by_length(cheats):
    """Counts the cheats grouped by their savings length."""
    savings_counter = Counter(cheat[0] for cheat in cheats)
    for savings, count in sorted(savings_counter.items()):
        print(f"Cheats saving {savings} picoseconds: {count}")

if __name__ == "__main__":
    file_path = "2024_day20_input.txt"  # Replace with your input file path
    min_savings = 100  # Minimum savings to consider a cheat

    # Parse the input
    grid, start, end = parse_input(file_path)

    # Precompute BFS distances for all non-wall positions
    bfs_distances = precompute_bfs(grid, end)

    # Find the baseline shortest path
    shortest_path_length = find_shortest_path(grid, start, end, bfs_distances)
    print(f"Baseline shortest path length: {shortest_path_length}")

    # Simulate cheats and find those with at least the minimum savings
    cheats = simulate_cheats(grid, bfs_distances[start], bfs_distances, min_savings)
    print(f"Number of cheats saving at least {min_savings} picoseconds: {len(cheats)}")

    # Count cheats grouped by their savings length
    # print("Cheats grouped by savings:")
    # count_cheats_by_length(cheats)

    # Simulate cheats for part 2
    cheats_part2 = simulate_cheats_part2(grid, bfs_distances[start], bfs_distances, min_savings)
    print(f"Number of cheats saving at least {min_savings} picoseconds (Part 2): {len(cheats_part2)}")

    # Count cheats grouped by their savings length (Part 2)
    # print("Cheats grouped by savings (Part 2):")
    # count_cheats_by_length(cheats_part2)


    # Example usage of print_grid
    # print("Grid with shortest path:")
    # print_grid(grid, path=bfs_distances[start])
