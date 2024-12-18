from collections import deque

def parse_input(file_path):
    """Parses the input file to get the falling byte coordinates."""
    with open(file_path, 'r') as f:
        lines = f.readlines()
    return [tuple(map(int, line.strip().split(','))) for line in lines]

def simulate_falling_bytes(byte_positions, grid_size, num_bytes):
    """Simulates the falling bytes on the grid."""
    grid = [[0 for _ in range(grid_size)] for _ in range(grid_size)]
    for x, y in byte_positions[:num_bytes]:
        grid[y][x] = 1  # Mark as corrupted
    return grid

def bfs_shortest_path(grid, start, goal):
    """Finds the shortest path using BFS in the grid."""
    directions = [(0, 1), (1, 0), (0, -1), (-1, 0)]
    queue = deque([(start, 0)])  # (current position, steps)
    visited = set()
    visited.add(start)

    while queue:
        (x, y), steps = queue.popleft()

        # If we reach the goal, return the number of steps
        if (x, y) == goal:
            return steps

        # Explore neighbors
        for dx, dy in directions:
            nx, ny = x + dx, y + dy

            if 0 <= nx < len(grid) and 0 <= ny < len(grid):  # Stay within bounds
                if grid[ny][nx] == 0 and (nx, ny) not in visited:  # Not corrupted or visited
                    visited.add((nx, ny))
                    queue.append(((nx, ny), steps + 1))

    return -1  # If no path found

def find_blocking_byte(byte_positions, grid, grid_size, start_index):
    """Finds the first byte that blocks the path to the exit."""
    start = (0, 0)
    goal = (grid_size - 1, grid_size - 1)

    for x, y in byte_positions[start_index:]:
        grid[y][x] = 1  # Mark as corrupted

        # Check if the path is blocked
        if bfs_shortest_path(grid, start, goal) == -1:
            return (x, y)

    return None

if __name__ == "__main__":
    # Input parameters
    file_path = "2024_day18_input.txt"  # Replace with your input file path
    grid_size = 71
    num_bytes = 1024

    # Parse input and simulate falling bytes
    byte_positions = parse_input(file_path)
    grid = simulate_falling_bytes(byte_positions, grid_size, num_bytes)

    # Find shortest path
    start = (0, 0)
    goal = (70, 70)
    result = bfs_shortest_path(grid, start, goal)

    print(f"The minimum number of steps to reach the exit is: {result}")

    # Part 2: Find the first blocking byte after the first 1024 bytes
    blocking_byte = find_blocking_byte(byte_positions, grid, grid_size, num_bytes)
    if blocking_byte:
        print(f"The first byte that blocks the path is: {blocking_byte[0]},{blocking_byte[1]}")
    else:
        print("No blocking byte found.")